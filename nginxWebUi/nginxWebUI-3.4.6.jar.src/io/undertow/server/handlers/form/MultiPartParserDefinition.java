/*     */ package io.undertow.server.handlers.form;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.UndertowOptions;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.ExchangeCompletionListener;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.MalformedMessageException;
/*     */ import io.undertow.util.MultipartParser;
/*     */ import io.undertow.util.SameThreadExecutor;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.FileOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.LinkOption;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.nio.file.attribute.FileAttribute;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.Executor;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MultiPartParserDefinition
/*     */   implements FormParserFactory.ParserDefinition<MultiPartParserDefinition>
/*     */ {
/*     */   public static final String MULTIPART_FORM_DATA = "multipart/form-data";
/*     */   private Executor executor;
/*     */   private Path tempFileLocation;
/*  70 */   private String defaultEncoding = StandardCharsets.ISO_8859_1.displayName();
/*     */   
/*  72 */   private long maxIndividualFileSize = -1L;
/*     */   
/*     */   private long fileSizeThreshold;
/*     */   
/*     */   public MultiPartParserDefinition() {
/*  77 */     this.tempFileLocation = Paths.get(System.getProperty("java.io.tmpdir"), new String[0]);
/*     */   }
/*     */   
/*     */   public MultiPartParserDefinition(Path tempDir) {
/*  81 */     this.tempFileLocation = tempDir;
/*     */   }
/*     */ 
/*     */   
/*     */   public FormDataParser create(HttpServerExchange exchange) {
/*  86 */     String mimeType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/*  87 */     if (mimeType != null && mimeType.startsWith("multipart/form-data")) {
/*  88 */       String boundary = Headers.extractQuotedValueFromHeader(mimeType, "boundary");
/*  89 */       if (boundary == null) {
/*  90 */         UndertowLogger.REQUEST_LOGGER.debugf("Could not find boundary in multipart request with ContentType: %s, multipart data will not be available", mimeType);
/*  91 */         return null;
/*     */       } 
/*  93 */       final MultiPartUploadHandler parser = new MultiPartUploadHandler(exchange, boundary, this.maxIndividualFileSize, this.fileSizeThreshold, this.defaultEncoding);
/*  94 */       exchange.addExchangeCompleteListener(new ExchangeCompletionListener()
/*     */           {
/*     */             public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
/*  97 */               IoUtils.safeClose(parser);
/*  98 */               nextListener.proceed();
/*     */             }
/*     */           });
/* 101 */       Long sizeLimit = (Long)exchange.getConnection().getUndertowOptions().get(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE);
/* 102 */       if (sizeLimit != null) {
/* 103 */         exchange.setMaxEntitySize(sizeLimit.longValue());
/*     */       }
/* 105 */       UndertowLogger.REQUEST_LOGGER.tracef("Created multipart parser for %s", exchange);
/*     */       
/* 107 */       return parser;
/*     */     } 
/*     */     
/* 110 */     return null;
/*     */   }
/*     */   
/*     */   public Executor getExecutor() {
/* 114 */     return this.executor;
/*     */   }
/*     */   
/*     */   public MultiPartParserDefinition setExecutor(Executor executor) {
/* 118 */     this.executor = executor;
/* 119 */     return this;
/*     */   }
/*     */   
/*     */   public Path getTempFileLocation() {
/* 123 */     return this.tempFileLocation;
/*     */   }
/*     */   
/*     */   public MultiPartParserDefinition setTempFileLocation(Path tempFileLocation) {
/* 127 */     this.tempFileLocation = tempFileLocation;
/* 128 */     return this;
/*     */   }
/*     */   
/*     */   public String getDefaultEncoding() {
/* 132 */     return this.defaultEncoding;
/*     */   }
/*     */   
/*     */   public MultiPartParserDefinition setDefaultEncoding(String defaultEncoding) {
/* 136 */     this.defaultEncoding = defaultEncoding;
/* 137 */     return this;
/*     */   }
/*     */   
/*     */   public long getMaxIndividualFileSize() {
/* 141 */     return this.maxIndividualFileSize;
/*     */   }
/*     */   
/*     */   public void setMaxIndividualFileSize(long maxIndividualFileSize) {
/* 145 */     this.maxIndividualFileSize = maxIndividualFileSize;
/*     */   }
/*     */   
/*     */   public void setFileSizeThreshold(long fileSizeThreshold) {
/* 149 */     this.fileSizeThreshold = fileSizeThreshold;
/*     */   }
/*     */   
/*     */   private final class MultiPartUploadHandler
/*     */     implements FormDataParser, MultipartParser.PartHandler {
/*     */     private final HttpServerExchange exchange;
/*     */     private final FormData data;
/* 156 */     private final List<Path> createdFiles = new ArrayList<>();
/*     */     
/*     */     private final long maxIndividualFileSize;
/*     */     private final long fileSizeThreshold;
/*     */     private String defaultEncoding;
/* 161 */     private final ByteArrayOutputStream contentBytes = new ByteArrayOutputStream();
/*     */     
/*     */     private String currentName;
/*     */     private String fileName;
/*     */     private Path file;
/*     */     private FileChannel fileChannel;
/*     */     private HeaderMap headers;
/*     */     private HttpHandler handler;
/*     */     private long currentFileSize;
/*     */     private final MultipartParser.ParseState parser;
/*     */     
/*     */     private MultiPartUploadHandler(HttpServerExchange exchange, String boundary, long maxIndividualFileSize, long fileSizeThreshold, String defaultEncoding) {
/* 173 */       this.exchange = exchange;
/* 174 */       this.maxIndividualFileSize = maxIndividualFileSize;
/* 175 */       this.defaultEncoding = defaultEncoding;
/* 176 */       this.fileSizeThreshold = fileSizeThreshold;
/* 177 */       this.data = new FormData(exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
/* 178 */       String charset = defaultEncoding;
/* 179 */       String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
/* 180 */       if (contentType != null) {
/* 181 */         String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
/* 182 */         if (value != null) {
/* 183 */           charset = value;
/*     */         }
/*     */       } 
/* 186 */       this.parser = MultipartParser.beginParse(exchange.getConnection().getByteBufferPool(), this, boundary.getBytes(StandardCharsets.US_ASCII), charset);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void parse(HttpHandler handler) throws Exception {
/* 193 */       if (this.exchange.getAttachment(FORM_DATA) != null) {
/* 194 */         handler.handleRequest(this.exchange);
/*     */         return;
/*     */       } 
/* 197 */       this.handler = handler;
/*     */ 
/*     */ 
/*     */       
/* 201 */       StreamSourceChannel requestChannel = this.exchange.getRequestChannel();
/* 202 */       if (requestChannel == null) {
/* 203 */         throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
/*     */       }
/* 205 */       if (MultiPartParserDefinition.this.executor == null) {
/* 206 */         this.exchange.dispatch(new NonBlockingParseTask((Executor)this.exchange.getConnection().getWorker(), requestChannel));
/*     */       } else {
/* 208 */         this.exchange.dispatch(MultiPartParserDefinition.this.executor, new NonBlockingParseTask(MultiPartParserDefinition.this.executor, requestChannel));
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public FormData parseBlocking() throws IOException {
/* 214 */       FormData existing = (FormData)this.exchange.getAttachment(FORM_DATA);
/* 215 */       if (existing != null) {
/* 216 */         return existing;
/*     */       }
/* 218 */       InputStream inputStream = this.exchange.getInputStream();
/* 219 */       if (inputStream == null) {
/* 220 */         throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
/*     */       }
/* 222 */       try (PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 223 */         ByteBuffer buf = pooled.getBuffer();
/*     */         while (true) {
/* 225 */           buf.clear();
/* 226 */           int c = inputStream.read(buf.array(), buf.arrayOffset(), buf.remaining());
/* 227 */           if (c == -1) {
/* 228 */             if (this.parser.isComplete()) {
/*     */               break;
/*     */             }
/* 231 */             throw UndertowMessages.MESSAGES.connectionTerminatedReadingMultiPartData();
/*     */           } 
/* 233 */           if (c != 0) {
/* 234 */             buf.limit(c);
/* 235 */             this.parser.parse(buf);
/*     */           } 
/*     */         } 
/* 238 */         this.exchange.putAttachment(FORM_DATA, this.data);
/* 239 */       } catch (MalformedMessageException e) {
/* 240 */         throw new IOException(e);
/*     */       } 
/* 242 */       return (FormData)this.exchange.getAttachment(FORM_DATA);
/*     */     }
/*     */ 
/*     */     
/*     */     public void beginPart(HeaderMap headers) {
/* 247 */       this.currentFileSize = 0L;
/* 248 */       this.headers = headers;
/* 249 */       String disposition = headers.getFirst(Headers.CONTENT_DISPOSITION);
/* 250 */       if (disposition != null && 
/* 251 */         disposition.startsWith("form-data")) {
/* 252 */         this.currentName = Headers.extractQuotedValueFromHeader(disposition, "name");
/* 253 */         this.fileName = Headers.extractQuotedValueFromHeaderWithEncoding(disposition, "filename");
/* 254 */         if (this.fileName != null && this.fileSizeThreshold == 0L) {
/*     */           try {
/* 256 */             if (MultiPartParserDefinition.this.tempFileLocation != null) {
/*     */               
/* 258 */               FileAttribute[] emptyFA = new FileAttribute[0];
/* 259 */               LinkOption[] emptyLO = new LinkOption[0];
/* 260 */               Path normalized = MultiPartParserDefinition.this.tempFileLocation.normalize();
/* 261 */               if (!Files.exists(normalized, new LinkOption[0])) {
/* 262 */                 int pathElementsCount = normalized.getNameCount();
/* 263 */                 Path tmp = normalized;
/* 264 */                 LinkedList<Path> dirsToGuard = new LinkedList<>();
/* 265 */                 for (int i = 0; i < pathElementsCount; i++) {
/* 266 */                   if (Files.exists(tmp, emptyLO)) {
/* 267 */                     if (!Files.isDirectory(tmp, emptyLO))
/*     */                     {
/*     */                       
/* 270 */                       throw UndertowMessages.MESSAGES.pathElementIsRegularFile(tmp);
/*     */                     }
/*     */                     break;
/*     */                   } 
/* 274 */                   dirsToGuard.addFirst(tmp);
/* 275 */                   tmp = tmp.getParent();
/*     */                 } 
/*     */                 
/*     */                 try {
/* 279 */                   Files.createDirectories(normalized, (FileAttribute<?>[])emptyFA);
/*     */                 } finally {
/* 281 */                   for (Path p : dirsToGuard) {
/*     */                     try {
/* 283 */                       p.toFile().deleteOnExit();
/* 284 */                     } catch (Exception e) {
/*     */                       break;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/* 289 */               } else if (!Files.isDirectory(normalized, emptyLO)) {
/* 290 */                 throw new IOException(UndertowMessages.MESSAGES.pathNotADirectory(normalized));
/*     */               } 
/* 292 */               this.file = Files.createTempFile(normalized, "undertow", "upload", (FileAttribute<?>[])new FileAttribute[0]);
/*     */             } else {
/* 294 */               this.file = Files.createTempFile("undertow", "upload", (FileAttribute<?>[])new FileAttribute[0]);
/*     */             } 
/* 296 */             this.createdFiles.add(this.file);
/* 297 */             this.fileChannel = FileChannel.open(this.file, new OpenOption[] { StandardOpenOption.READ, StandardOpenOption.WRITE });
/* 298 */           } catch (IOException e) {
/* 299 */             throw new RuntimeException(e);
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void data(ByteBuffer buffer) throws IOException {
/* 308 */       this.currentFileSize += buffer.remaining();
/* 309 */       if (this.maxIndividualFileSize > 0L && this.currentFileSize > this.maxIndividualFileSize) {
/* 310 */         throw UndertowMessages.MESSAGES.maxFileSizeExceeded(this.maxIndividualFileSize);
/*     */       }
/* 312 */       if (this.file == null && this.fileName != null && this.fileSizeThreshold < this.currentFileSize) {
/*     */         try {
/* 314 */           if (MultiPartParserDefinition.this.tempFileLocation != null) {
/* 315 */             this.file = Files.createTempFile(MultiPartParserDefinition.this.tempFileLocation, "undertow", "upload", (FileAttribute<?>[])new FileAttribute[0]);
/*     */           } else {
/* 317 */             this.file = Files.createTempFile("undertow", "upload", (FileAttribute<?>[])new FileAttribute[0]);
/*     */           } 
/* 319 */           this.createdFiles.add(this.file);
/*     */           
/* 321 */           FileOutputStream fileOutputStream = new FileOutputStream(this.file.toFile());
/* 322 */           this.contentBytes.writeTo(fileOutputStream);
/*     */           
/* 324 */           this.fileChannel = fileOutputStream.getChannel();
/* 325 */         } catch (IOException e) {
/* 326 */           throw new RuntimeException(e);
/*     */         } 
/*     */       }
/*     */       
/* 330 */       if (this.file == null) {
/* 331 */         while (buffer.hasRemaining()) {
/* 332 */           this.contentBytes.write(buffer.get());
/*     */         }
/*     */       } else {
/* 335 */         this.fileChannel.write(buffer);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void endPart() {
/* 341 */       if (this.file != null) {
/* 342 */         this.data.add(this.currentName, this.file, this.fileName, this.headers);
/* 343 */         this.file = null;
/* 344 */         this.contentBytes.reset();
/*     */         try {
/* 346 */           this.fileChannel.close();
/* 347 */           this.fileChannel = null;
/* 348 */         } catch (IOException e) {
/* 349 */           throw new RuntimeException(e);
/*     */         } 
/* 351 */       } else if (this.fileName != null) {
/* 352 */         this.data.add(this.currentName, Arrays.copyOf(this.contentBytes.toByteArray(), this.contentBytes.size()), this.fileName, this.headers);
/* 353 */         this.contentBytes.reset();
/*     */       } else {
/*     */ 
/*     */         
/*     */         try {
/* 358 */           String charset = this.defaultEncoding;
/* 359 */           String contentType = this.headers.getFirst(Headers.CONTENT_TYPE);
/* 360 */           if (contentType != null) {
/* 361 */             String cs = Headers.extractQuotedValueFromHeader(contentType, "charset");
/* 362 */             if (cs != null) {
/* 363 */               charset = cs;
/*     */             }
/*     */           } 
/*     */           
/* 367 */           this.data.add(this.currentName, new String(this.contentBytes.toByteArray(), charset), charset, this.headers);
/* 368 */         } catch (UnsupportedEncodingException e) {
/* 369 */           throw new RuntimeException(e);
/*     */         } 
/* 371 */         this.contentBytes.reset();
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public List<Path> getCreatedFiles() {
/* 377 */       return this.createdFiles;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() throws IOException {
/* 382 */       IoUtils.safeClose(this.fileChannel);
/*     */       
/* 384 */       final List<Path> files = new ArrayList<>(getCreatedFiles());
/* 385 */       this.exchange.getConnection().getWorker().execute(new Runnable()
/*     */           {
/*     */             public void run() {
/* 388 */               for (Path file : files) {
/* 389 */                 if (Files.exists(file, new LinkOption[0])) {
/*     */                   
/* 391 */                   try { Files.delete(file); }
/* 392 */                   catch (NoSuchFileException noSuchFileException) {  }
/* 393 */                   catch (IOException e)
/* 394 */                   { UndertowLogger.REQUEST_LOGGER.cannotRemoveUploadedFile(file); }
/*     */                 
/*     */                 }
/*     */               } 
/*     */             }
/*     */           });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void setCharacterEncoding(String encoding) {
/* 405 */       this.defaultEncoding = encoding;
/* 406 */       this.parser.setCharacterEncoding(encoding);
/*     */     }
/*     */     
/*     */     private final class NonBlockingParseTask
/*     */       implements Runnable {
/*     */       private final Executor executor;
/*     */       private final StreamSourceChannel requestChannel;
/*     */       
/*     */       private NonBlockingParseTask(Executor executor, StreamSourceChannel requestChannel) {
/* 415 */         this.executor = executor;
/* 416 */         this.requestChannel = requestChannel;
/*     */       }
/*     */ 
/*     */       
/*     */       public void run() {
/*     */         try {
/* 422 */           FormData existing = (FormData)MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.getAttachment(FormDataParser.FORM_DATA);
/* 423 */           if (existing != null) {
/* 424 */             MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.dispatch(SameThreadExecutor.INSTANCE, MultiPartParserDefinition.MultiPartUploadHandler.this.handler);
/*     */             return;
/*     */           } 
/* 427 */           PooledByteBuffer pooled = MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.getConnection().getByteBufferPool().allocate();
/*     */           try {
/*     */             while (true) {
/* 430 */               int c = this.requestChannel.read(pooled.getBuffer());
/* 431 */               if (c == 0) {
/* 432 */                 this.requestChannel.getReadSetter().set(new ChannelListener<StreamSourceChannel>()
/*     */                     {
/*     */                       public void handleEvent(StreamSourceChannel channel) {
/* 435 */                         channel.suspendReads();
/* 436 */                         MultiPartParserDefinition.MultiPartUploadHandler.NonBlockingParseTask.this.executor.execute(MultiPartParserDefinition.MultiPartUploadHandler.NonBlockingParseTask.this);
/*     */                       }
/*     */                     });
/* 439 */                 this.requestChannel.resumeReads(); return;
/*     */               } 
/* 441 */               if (c == -1) {
/* 442 */                 if (MultiPartParserDefinition.MultiPartUploadHandler.this.parser.isComplete()) {
/* 443 */                   MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.putAttachment(FormDataParser.FORM_DATA, MultiPartParserDefinition.MultiPartUploadHandler.this.data);
/* 444 */                   MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.dispatch(SameThreadExecutor.INSTANCE, MultiPartParserDefinition.MultiPartUploadHandler.this.handler);
/*     */                 } else {
/* 446 */                   UndertowLogger.REQUEST_IO_LOGGER.ioException(UndertowMessages.MESSAGES.connectionTerminatedReadingMultiPartData());
/* 447 */                   MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.setStatusCode(500);
/* 448 */                   MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.endExchange();
/*     */                 } 
/*     */                 return;
/*     */               } 
/* 452 */               pooled.getBuffer().flip();
/* 453 */               MultiPartParserDefinition.MultiPartUploadHandler.this.parser.parse(pooled.getBuffer());
/* 454 */               pooled.getBuffer().compact();
/*     */             }
/*     */           
/* 457 */           } catch (MalformedMessageException e) {
/* 458 */             UndertowLogger.REQUEST_IO_LOGGER.ioException((IOException)e);
/* 459 */             MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.setStatusCode(500);
/* 460 */             MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.endExchange();
/*     */           } finally {
/* 462 */             pooled.close();
/*     */           }
/*     */         
/* 465 */         } catch (Throwable e) {
/* 466 */           UndertowLogger.REQUEST_IO_LOGGER.debug("Exception parsing data", e);
/* 467 */           MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.setStatusCode(500);
/* 468 */           MultiPartParserDefinition.MultiPartUploadHandler.this.exchange.endExchange();
/*     */         } 
/*     */       }
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class FileTooLargeException
/*     */     extends IOException
/*     */   {
/*     */     public FileTooLargeException() {}
/*     */ 
/*     */     
/*     */     public FileTooLargeException(String message) {
/* 482 */       super(message);
/*     */     }
/*     */     
/*     */     public FileTooLargeException(String message, Throwable cause) {
/* 486 */       super(message, cause);
/*     */     }
/*     */     
/*     */     public FileTooLargeException(Throwable cause) {
/* 490 */       super(cause);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\MultiPartParserDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */