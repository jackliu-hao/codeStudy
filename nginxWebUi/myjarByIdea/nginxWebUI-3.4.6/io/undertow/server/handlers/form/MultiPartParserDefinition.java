package io.undertow.server.handlers.form;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.UndertowOptions;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import io.undertow.util.MalformedMessageException;
import io.undertow.util.MultipartParser;
import io.undertow.util.SameThreadExecutor;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import org.xnio.ChannelListener;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;

public class MultiPartParserDefinition implements FormParserFactory.ParserDefinition<MultiPartParserDefinition> {
   public static final String MULTIPART_FORM_DATA = "multipart/form-data";
   private Executor executor;
   private Path tempFileLocation;
   private String defaultEncoding;
   private long maxIndividualFileSize;
   private long fileSizeThreshold;

   public MultiPartParserDefinition() {
      this.defaultEncoding = StandardCharsets.ISO_8859_1.displayName();
      this.maxIndividualFileSize = -1L;
      this.tempFileLocation = Paths.get(System.getProperty("java.io.tmpdir"));
   }

   public MultiPartParserDefinition(Path tempDir) {
      this.defaultEncoding = StandardCharsets.ISO_8859_1.displayName();
      this.maxIndividualFileSize = -1L;
      this.tempFileLocation = tempDir;
   }

   public FormDataParser create(HttpServerExchange exchange) {
      String mimeType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
      if (mimeType != null && mimeType.startsWith("multipart/form-data")) {
         String boundary = Headers.extractQuotedValueFromHeader(mimeType, "boundary");
         if (boundary == null) {
            UndertowLogger.REQUEST_LOGGER.debugf("Could not find boundary in multipart request with ContentType: %s, multipart data will not be available", mimeType);
            return null;
         } else {
            final MultiPartUploadHandler parser = new MultiPartUploadHandler(exchange, boundary, this.maxIndividualFileSize, this.fileSizeThreshold, this.defaultEncoding);
            exchange.addExchangeCompleteListener(new ExchangeCompletionListener() {
               public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
                  IoUtils.safeClose((Closeable)parser);
                  nextListener.proceed();
               }
            });
            Long sizeLimit = (Long)exchange.getConnection().getUndertowOptions().get(UndertowOptions.MULTIPART_MAX_ENTITY_SIZE);
            if (sizeLimit != null) {
               exchange.setMaxEntitySize(sizeLimit);
            }

            UndertowLogger.REQUEST_LOGGER.tracef("Created multipart parser for %s", exchange);
            return parser;
         }
      } else {
         return null;
      }
   }

   public Executor getExecutor() {
      return this.executor;
   }

   public MultiPartParserDefinition setExecutor(Executor executor) {
      this.executor = executor;
      return this;
   }

   public Path getTempFileLocation() {
      return this.tempFileLocation;
   }

   public MultiPartParserDefinition setTempFileLocation(Path tempFileLocation) {
      this.tempFileLocation = tempFileLocation;
      return this;
   }

   public String getDefaultEncoding() {
      return this.defaultEncoding;
   }

   public MultiPartParserDefinition setDefaultEncoding(String defaultEncoding) {
      this.defaultEncoding = defaultEncoding;
      return this;
   }

   public long getMaxIndividualFileSize() {
      return this.maxIndividualFileSize;
   }

   public void setMaxIndividualFileSize(long maxIndividualFileSize) {
      this.maxIndividualFileSize = maxIndividualFileSize;
   }

   public void setFileSizeThreshold(long fileSizeThreshold) {
      this.fileSizeThreshold = fileSizeThreshold;
   }

   public static class FileTooLargeException extends IOException {
      public FileTooLargeException() {
      }

      public FileTooLargeException(String message) {
         super(message);
      }

      public FileTooLargeException(String message, Throwable cause) {
         super(message, cause);
      }

      public FileTooLargeException(Throwable cause) {
         super(cause);
      }
   }

   private final class MultiPartUploadHandler implements FormDataParser, MultipartParser.PartHandler {
      private final HttpServerExchange exchange;
      private final FormData data;
      private final List<Path> createdFiles;
      private final long maxIndividualFileSize;
      private final long fileSizeThreshold;
      private String defaultEncoding;
      private final ByteArrayOutputStream contentBytes;
      private String currentName;
      private String fileName;
      private Path file;
      private FileChannel fileChannel;
      private HeaderMap headers;
      private HttpHandler handler;
      private long currentFileSize;
      private final MultipartParser.ParseState parser;

      private MultiPartUploadHandler(HttpServerExchange exchange, String boundary, long maxIndividualFileSize, long fileSizeThreshold, String defaultEncoding) {
         this.createdFiles = new ArrayList();
         this.contentBytes = new ByteArrayOutputStream();
         this.exchange = exchange;
         this.maxIndividualFileSize = maxIndividualFileSize;
         this.defaultEncoding = defaultEncoding;
         this.fileSizeThreshold = fileSizeThreshold;
         this.data = new FormData(exchange.getConnection().getUndertowOptions().get(UndertowOptions.MAX_PARAMETERS, 1000));
         String charset = defaultEncoding;
         String contentType = exchange.getRequestHeaders().getFirst(Headers.CONTENT_TYPE);
         if (contentType != null) {
            String value = Headers.extractQuotedValueFromHeader(contentType, "charset");
            if (value != null) {
               charset = value;
            }
         }

         this.parser = MultipartParser.beginParse(exchange.getConnection().getByteBufferPool(), this, boundary.getBytes(StandardCharsets.US_ASCII), charset);
      }

      public void parse(HttpHandler handler) throws Exception {
         if (this.exchange.getAttachment(FORM_DATA) != null) {
            handler.handleRequest(this.exchange);
         } else {
            this.handler = handler;
            StreamSourceChannel requestChannel = this.exchange.getRequestChannel();
            if (requestChannel == null) {
               throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
            } else {
               if (MultiPartParserDefinition.this.executor == null) {
                  this.exchange.dispatch((Runnable)(new NonBlockingParseTask(this.exchange.getConnection().getWorker(), requestChannel)));
               } else {
                  this.exchange.dispatch(MultiPartParserDefinition.this.executor, (Runnable)(new NonBlockingParseTask(MultiPartParserDefinition.this.executor, requestChannel)));
               }

            }
         }
      }

      public FormData parseBlocking() throws IOException {
         FormData existing = (FormData)this.exchange.getAttachment(FORM_DATA);
         if (existing != null) {
            return existing;
         } else {
            InputStream inputStream = this.exchange.getInputStream();
            if (inputStream == null) {
               throw new IOException(UndertowMessages.MESSAGES.requestChannelAlreadyProvided());
            } else {
               try {
                  PooledByteBuffer pooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate();
                  Throwable var4 = null;

                  try {
                     ByteBuffer buf = pooled.getBuffer();

                     while(true) {
                        buf.clear();
                        int c = inputStream.read(buf.array(), buf.arrayOffset(), buf.remaining());
                        if (c == -1) {
                           if (!this.parser.isComplete()) {
                              throw UndertowMessages.MESSAGES.connectionTerminatedReadingMultiPartData();
                           }

                           this.exchange.putAttachment(FORM_DATA, this.data);
                           return (FormData)this.exchange.getAttachment(FORM_DATA);
                        }

                        if (c != 0) {
                           buf.limit(c);
                           this.parser.parse(buf);
                        }
                     }
                  } catch (Throwable var15) {
                     var4 = var15;
                     throw var15;
                  } finally {
                     if (pooled != null) {
                        if (var4 != null) {
                           try {
                              pooled.close();
                           } catch (Throwable var14) {
                              var4.addSuppressed(var14);
                           }
                        } else {
                           pooled.close();
                        }
                     }

                  }
               } catch (MalformedMessageException var17) {
                  throw new IOException(var17);
               }
            }
         }
      }

      public void beginPart(HeaderMap headers) {
         this.currentFileSize = 0L;
         this.headers = headers;
         String disposition = headers.getFirst(Headers.CONTENT_DISPOSITION);
         if (disposition != null && disposition.startsWith("form-data")) {
            this.currentName = Headers.extractQuotedValueFromHeader(disposition, "name");
            this.fileName = Headers.extractQuotedValueFromHeaderWithEncoding(disposition, "filename");
            if (this.fileName != null && this.fileSizeThreshold == 0L) {
               try {
                  if (MultiPartParserDefinition.this.tempFileLocation == null) {
                     this.file = Files.createTempFile("undertow", "upload");
                  } else {
                     FileAttribute[] emptyFA = new FileAttribute[0];
                     LinkOption[] emptyLO = new LinkOption[0];
                     Path normalized = MultiPartParserDefinition.this.tempFileLocation.normalize();
                     if (!Files.exists(normalized, new LinkOption[0])) {
                        int pathElementsCount = normalized.getNameCount();
                        Path tmp = normalized;
                        LinkedList<Path> dirsToGuard = new LinkedList();

                        for(int i = 0; i < pathElementsCount; ++i) {
                           if (Files.exists(tmp, emptyLO)) {
                              if (!Files.isDirectory(tmp, emptyLO)) {
                                 throw UndertowMessages.MESSAGES.pathElementIsRegularFile(tmp);
                              }
                              break;
                           }

                           dirsToGuard.addFirst(tmp);
                           tmp = tmp.getParent();
                        }

                        boolean var20 = false;

                        try {
                           var20 = true;
                           Files.createDirectories(normalized, emptyFA);
                           var20 = false;
                        } finally {
                           if (var20) {
                              Iterator var13 = dirsToGuard.iterator();

                              while(var13.hasNext()) {
                                 Path p = (Path)var13.next();

                                 try {
                                    p.toFile().deleteOnExit();
                                 } catch (Exception var21) {
                                    break;
                                 }
                              }

                           }
                        }

                        Iterator var25 = dirsToGuard.iterator();

                        while(var25.hasNext()) {
                           Path px = (Path)var25.next();

                           try {
                              px.toFile().deleteOnExit();
                           } catch (Exception var23) {
                              break;
                           }
                        }
                     } else if (!Files.isDirectory(normalized, emptyLO)) {
                        throw new IOException(UndertowMessages.MESSAGES.pathNotADirectory(normalized));
                     }

                     this.file = Files.createTempFile(normalized, "undertow", "upload");
                  }

                  this.createdFiles.add(this.file);
                  this.fileChannel = FileChannel.open(this.file, StandardOpenOption.READ, StandardOpenOption.WRITE);
               } catch (IOException var24) {
                  throw new RuntimeException(var24);
               }
            }
         }

      }

      public void data(ByteBuffer buffer) throws IOException {
         this.currentFileSize += (long)buffer.remaining();
         if (this.maxIndividualFileSize > 0L && this.currentFileSize > this.maxIndividualFileSize) {
            throw UndertowMessages.MESSAGES.maxFileSizeExceeded(this.maxIndividualFileSize);
         } else {
            if (this.file == null && this.fileName != null && this.fileSizeThreshold < this.currentFileSize) {
               try {
                  if (MultiPartParserDefinition.this.tempFileLocation != null) {
                     this.file = Files.createTempFile(MultiPartParserDefinition.this.tempFileLocation, "undertow", "upload");
                  } else {
                     this.file = Files.createTempFile("undertow", "upload");
                  }

                  this.createdFiles.add(this.file);
                  FileOutputStream fileOutputStream = new FileOutputStream(this.file.toFile());
                  this.contentBytes.writeTo(fileOutputStream);
                  this.fileChannel = fileOutputStream.getChannel();
               } catch (IOException var3) {
                  throw new RuntimeException(var3);
               }
            }

            if (this.file == null) {
               while(buffer.hasRemaining()) {
                  this.contentBytes.write(buffer.get());
               }
            } else {
               this.fileChannel.write(buffer);
            }

         }
      }

      public void endPart() {
         if (this.file != null) {
            this.data.add(this.currentName, this.file, this.fileName, this.headers);
            this.file = null;
            this.contentBytes.reset();

            try {
               this.fileChannel.close();
               this.fileChannel = null;
            } catch (IOException var4) {
               throw new RuntimeException(var4);
            }
         } else if (this.fileName != null) {
            this.data.add(this.currentName, Arrays.copyOf(this.contentBytes.toByteArray(), this.contentBytes.size()), this.fileName, this.headers);
            this.contentBytes.reset();
         } else {
            try {
               String charset = this.defaultEncoding;
               String contentType = this.headers.getFirst(Headers.CONTENT_TYPE);
               if (contentType != null) {
                  String cs = Headers.extractQuotedValueFromHeader(contentType, "charset");
                  if (cs != null) {
                     charset = cs;
                  }
               }

               this.data.add(this.currentName, new String(this.contentBytes.toByteArray(), charset), charset, this.headers);
            } catch (UnsupportedEncodingException var5) {
               throw new RuntimeException(var5);
            }

            this.contentBytes.reset();
         }

      }

      public List<Path> getCreatedFiles() {
         return this.createdFiles;
      }

      public void close() throws IOException {
         IoUtils.safeClose((Closeable)this.fileChannel);
         final List<Path> files = new ArrayList(this.getCreatedFiles());
         this.exchange.getConnection().getWorker().execute(new Runnable() {
            public void run() {
               Iterator var1 = files.iterator();

               while(var1.hasNext()) {
                  Path file = (Path)var1.next();
                  if (Files.exists(file, new LinkOption[0])) {
                     try {
                        Files.delete(file);
                     } catch (NoSuchFileException var4) {
                     } catch (IOException var5) {
                        UndertowLogger.REQUEST_LOGGER.cannotRemoveUploadedFile(file);
                     }
                  }
               }

            }
         });
      }

      public void setCharacterEncoding(String encoding) {
         this.defaultEncoding = encoding;
         this.parser.setCharacterEncoding(encoding);
      }

      // $FF: synthetic method
      MultiPartUploadHandler(HttpServerExchange x1, String x2, long x3, long x4, String x5, Object x6) {
         this(x1, x2, x3, x4, x5);
      }

      private final class NonBlockingParseTask implements Runnable {
         private final Executor executor;
         private final StreamSourceChannel requestChannel;

         private NonBlockingParseTask(Executor executor, StreamSourceChannel requestChannel) {
            this.executor = executor;
            this.requestChannel = requestChannel;
         }

         public void run() {
            try {
               FormData existing = (FormData)MultiPartUploadHandler.this.exchange.getAttachment(FormDataParser.FORM_DATA);
               if (existing != null) {
                  MultiPartUploadHandler.this.exchange.dispatch(SameThreadExecutor.INSTANCE, MultiPartUploadHandler.this.handler);
               } else {
                  PooledByteBuffer pooled = MultiPartUploadHandler.this.exchange.getConnection().getByteBufferPool().allocate();

                  try {
                     while(true) {
                        int c = this.requestChannel.read(pooled.getBuffer());
                        if (c == 0) {
                           this.requestChannel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
                              public void handleEvent(StreamSourceChannel channel) {
                                 channel.suspendReads();
                                 NonBlockingParseTask.this.executor.execute(NonBlockingParseTask.this);
                              }
                           });
                           this.requestChannel.resumeReads();
                           return;
                        }

                        if (c == -1) {
                           if (MultiPartUploadHandler.this.parser.isComplete()) {
                              MultiPartUploadHandler.this.exchange.putAttachment(FormDataParser.FORM_DATA, MultiPartUploadHandler.this.data);
                              MultiPartUploadHandler.this.exchange.dispatch(SameThreadExecutor.INSTANCE, MultiPartUploadHandler.this.handler);
                           } else {
                              UndertowLogger.REQUEST_IO_LOGGER.ioException(UndertowMessages.MESSAGES.connectionTerminatedReadingMultiPartData());
                              MultiPartUploadHandler.this.exchange.setStatusCode(500);
                              MultiPartUploadHandler.this.exchange.endExchange();
                           }
                           break;
                        }

                        pooled.getBuffer().flip();
                        MultiPartUploadHandler.this.parser.parse(pooled.getBuffer());
                        pooled.getBuffer().compact();
                     }
                  } catch (MalformedMessageException var8) {
                     UndertowLogger.REQUEST_IO_LOGGER.ioException(var8);
                     MultiPartUploadHandler.this.exchange.setStatusCode(500);
                     MultiPartUploadHandler.this.exchange.endExchange();
                     return;
                  } finally {
                     pooled.close();
                  }

               }
            } catch (Throwable var10) {
               UndertowLogger.REQUEST_IO_LOGGER.debug("Exception parsing data", var10);
               MultiPartUploadHandler.this.exchange.setStatusCode(500);
               MultiPartUploadHandler.this.exchange.endExchange();
            }
         }

         // $FF: synthetic method
         NonBlockingParseTask(Executor x1, StreamSourceChannel x2, Object x3) {
            this(x1, x2);
         }
      }
   }
}
