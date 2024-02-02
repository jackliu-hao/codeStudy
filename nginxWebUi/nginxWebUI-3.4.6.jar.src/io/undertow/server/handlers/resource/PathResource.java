/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.MalformedURLException;
/*     */ import java.net.URL;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Date;
/*     */ import java.util.List;
/*     */ import org.xnio.IoUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathResource
/*     */   implements RangeAwareResource
/*     */ {
/*     */   private final Path file;
/*     */   private final String path;
/*     */   private final ETag eTag;
/*     */   private final PathResourceManager manager;
/*     */   
/*     */   public PathResource(Path file, PathResourceManager manager, String path, ETag eTag) {
/*  42 */     this.file = file;
/*  43 */     this.path = path;
/*  44 */     this.manager = manager;
/*  45 */     this.eTag = eTag;
/*     */   }
/*     */   
/*     */   public PathResource(Path file, PathResourceManager manager, String path) {
/*  49 */     this(file, manager, path, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  54 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLastModified() {
/*     */     try {
/*  60 */       if (Files.isSymbolicLink(this.file) && Files.notExists(this.file, new java.nio.file.LinkOption[0])) {
/*  61 */         return null;
/*     */       }
/*  63 */       return new Date(Files.getLastModifiedTime(this.file, new java.nio.file.LinkOption[0]).toMillis());
/*  64 */     } catch (IOException e) {
/*  65 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLastModifiedString() {
/*  71 */     return DateUtils.toDateString(getLastModified());
/*     */   }
/*     */ 
/*     */   
/*     */   public ETag getETag() {
/*  76 */     return this.eTag;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  81 */     if (this.file.getFileName() != null) {
/*  82 */       return this.file.getFileName().toString();
/*     */     }
/*  84 */     return this.file.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/*  89 */     return Files.isDirectory(this.file, new java.nio.file.LinkOption[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Resource> list() {
/*  94 */     List<Resource> resources = new ArrayList<>();
/*  95 */     try (DirectoryStream<Path> stream = Files.newDirectoryStream(this.file)) {
/*  96 */       for (Path child : stream) {
/*  97 */         resources.add(new PathResource(child, this.manager, this.path + this.file.getFileSystem().getSeparator() + child.getFileName().toString()));
/*     */       }
/*  99 */     } catch (IOException e) {
/* 100 */       throw new RuntimeException(e);
/*     */     } 
/* 102 */     return resources;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType(MimeMappings mimeMappings) {
/* 107 */     String fileName = this.file.getFileName().toString();
/* 108 */     int index = fileName.lastIndexOf('.');
/* 109 */     if (index != -1 && index != fileName.length() - 1) {
/* 110 */       return mimeMappings.getMimeType(fileName.substring(index + 1));
/*     */     }
/* 112 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serve(Sender sender, HttpServerExchange exchange, IoCallback callback) {
/* 117 */     serveImpl(sender, exchange, -1L, -1L, callback, false);
/*     */   }
/*     */   
/*     */   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback callback) {
/* 121 */     serveImpl(sender, exchange, start, end, callback, true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void serveImpl(final Sender sender, final HttpServerExchange exchange, final long start, final long end, final IoCallback callback, final boolean range) {
/*     */     BaseFileTask task;
/*     */     class ServerTask
/*     */       extends BaseFileTask
/*     */       implements IoCallback
/*     */     {
/*     */       private PooledByteBuffer pooled;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       long remaining;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       ServerTask() {
/* 149 */         super(param1Boolean, param1Long1, param1HttpServerExchange, param1IoCallback, param1Sender);
/*     */ 
/*     */ 
/*     */         
/* 153 */         this.remaining = end - start + 1L;
/*     */       }
/*     */       
/*     */       public void run() {
/* 157 */         if (range && this.remaining == 0L) {
/*     */           
/* 159 */           if (this.pooled != null) {
/* 160 */             this.pooled.close();
/* 161 */             this.pooled = null;
/*     */           } 
/* 163 */           IoUtils.safeClose(this.fileChannel);
/* 164 */           callback.onComplete(exchange, sender);
/*     */           return;
/*     */         } 
/* 167 */         if (this.fileChannel == null) {
/* 168 */           if (!openFile()) {
/*     */             return;
/*     */           }
/* 171 */           this.pooled = exchange.getConnection().getByteBufferPool().allocate();
/*     */         } 
/* 173 */         if (this.pooled != null) {
/* 174 */           ByteBuffer buffer = this.pooled.getBuffer();
/*     */           try {
/* 176 */             buffer.clear();
/* 177 */             int res = this.fileChannel.read(buffer);
/* 178 */             if (res == -1) {
/*     */               
/* 180 */               this.pooled.close();
/* 181 */               IoUtils.safeClose(this.fileChannel);
/* 182 */               callback.onComplete(exchange, sender);
/*     */               return;
/*     */             } 
/* 185 */             buffer.flip();
/* 186 */             if (range) {
/* 187 */               if (buffer.remaining() > this.remaining) {
/* 188 */                 buffer.limit((int)(buffer.position() + this.remaining));
/*     */               }
/* 190 */               this.remaining -= buffer.remaining();
/*     */             } 
/* 192 */             sender.send(buffer, this);
/* 193 */           } catch (IOException e) {
/* 194 */             onException(exchange, sender, e);
/*     */           } 
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 202 */         if (exchange.isInIoThread()) {
/* 203 */           exchange.dispatch(this);
/*     */         } else {
/* 205 */           run();
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 211 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 212 */         if (this.pooled != null) {
/* 213 */           this.pooled.close();
/* 214 */           this.pooled = null;
/*     */         } 
/* 216 */         IoUtils.safeClose(this.fileChannel);
/* 217 */         if (!exchange.isResponseStarted()) {
/* 218 */           exchange.setStatusCode(500);
/*     */         }
/* 220 */         callback.onException(exchange, sender, exception);
/*     */       } };
/*     */     class TransferTask extends BaseFileTask {
/*     */       TransferTask() {
/* 224 */         super(param1Boolean, param1Long, param1HttpServerExchange, param1IoCallback, param1Sender);
/*     */       }
/*     */       
/*     */       public void run() {
/* 228 */         if (!openFile()) {
/*     */           return;
/*     */         }
/* 231 */         sender.transferFrom(this.fileChannel, new IoCallback()
/*     */             {
/*     */               public void onComplete(HttpServerExchange exchange, Sender sender) {
/*     */                 try {
/* 235 */                   IoUtils.safeClose(PathResource.TransferTask.this.fileChannel);
/*     */                 } finally {
/* 237 */                   callback.onComplete(exchange, sender);
/*     */                 } 
/*     */               }
/*     */ 
/*     */               
/*     */               public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/*     */                 try {
/* 244 */                   IoUtils.safeClose(PathResource.TransferTask.this.fileChannel);
/*     */                 } finally {
/* 246 */                   callback.onException(exchange, sender, exception);
/*     */                 } 
/*     */               }
/*     */             });
/*     */       }
/*     */     };
/*     */     
/*     */     try {
/* 254 */       task = (this.manager.getTransferMinSize() > Files.size(this.file) || range) ? new ServerTask() : new TransferTask();
/* 255 */     } catch (IOException e) {
/* 256 */       throw new RuntimeException(e);
/*     */     } 
/* 258 */     if (exchange.isInIoThread()) {
/* 259 */       exchange.dispatch(task);
/*     */     } else {
/* 261 */       task.run();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getContentLength() {
/*     */     try {
/* 268 */       if (Files.isSymbolicLink(this.file) && Files.notExists(this.file, new java.nio.file.LinkOption[0])) {
/* 269 */         return null;
/*     */       }
/* 271 */       return Long.valueOf(Files.size(this.file));
/* 272 */     } catch (IOException e) {
/* 273 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCacheKey() {
/* 279 */     return this.file.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() {
/* 284 */     return this.file.toFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getFilePath() {
/* 289 */     return this.file;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getResourceManagerRoot() {
/* 294 */     return this.manager.getBasePath().toFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getResourceManagerRootPath() {
/* 299 */     return this.manager.getBasePath();
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/*     */     try {
/* 305 */       return this.file.toUri().toURL();
/* 306 */     } catch (MalformedURLException e) {
/* 307 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRangeSupported() {
/* 313 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\PathResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */