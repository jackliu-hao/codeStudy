/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.net.JarURLConnection;
/*     */ import java.net.URISyntaxException;
/*     */ import java.net.URL;
/*     */ import java.net.URLConnection;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.DirectoryStream;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.Paths;
/*     */ import java.util.Date;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import org.xnio.IoUtils;
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
/*     */ public class URLResource
/*     */   implements Resource, RangeAwareResource
/*     */ {
/*     */   private final URL url;
/*     */   private final String path;
/*     */   private boolean connectionOpened = false;
/*     */   private Date lastModified;
/*     */   private Long contentLength;
/*     */   
/*     */   @Deprecated
/*     */   public URLResource(URL url, URLConnection connection, String path) {
/*  61 */     this(url, path);
/*     */   }
/*     */   
/*     */   public URLResource(URL url, String path) {
/*  65 */     this.url = url;
/*  66 */     this.path = path;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  71 */     return this.path;
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLastModified() {
/*  76 */     openConnection();
/*  77 */     return this.lastModified;
/*     */   }
/*     */   
/*     */   private void openConnection() {
/*  81 */     if (!this.connectionOpened) {
/*  82 */       this.connectionOpened = true;
/*  83 */       URLConnection connection = null;
/*     */       try {
/*     */         try {
/*  86 */           connection = this.url.openConnection();
/*  87 */         } catch (IOException e) {
/*  88 */           this.lastModified = null;
/*  89 */           this.contentLength = null;
/*     */           return;
/*     */         } 
/*  92 */         if (this.url.getProtocol().equals("jar")) {
/*  93 */           connection.setUseCaches(false);
/*  94 */           URL jar = ((JarURLConnection)connection).getJarFileURL();
/*  95 */           this.lastModified = new Date((new File(jar.getFile())).lastModified());
/*     */         } else {
/*  97 */           this.lastModified = new Date(connection.getLastModified());
/*     */         } 
/*  99 */         this.contentLength = Long.valueOf(connection.getContentLengthLong());
/*     */       } finally {
/* 101 */         if (connection != null) {
/*     */           try {
/* 103 */             IoUtils.safeClose(connection.getInputStream());
/* 104 */           } catch (IOException iOException) {}
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLastModifiedString() {
/* 114 */     return DateUtils.toDateString(getLastModified());
/*     */   }
/*     */ 
/*     */   
/*     */   public ETag getETag() {
/* 119 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 124 */     String path = this.url.getPath();
/* 125 */     if (path.endsWith("/")) {
/* 126 */       path = path.substring(0, path.length() - 1);
/*     */     }
/* 128 */     int sepIndex = path.lastIndexOf("/");
/* 129 */     if (sepIndex != -1) {
/* 130 */       path = path.substring(sepIndex + 1);
/*     */     }
/* 132 */     return path;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 137 */     Path file = getFilePath();
/* 138 */     if (file != null)
/* 139 */       return Files.isDirectory(file, new java.nio.file.LinkOption[0]); 
/* 140 */     if (this.url.getPath().endsWith("/")) {
/* 141 */       return true;
/*     */     }
/* 143 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Resource> list() {
/* 148 */     List<Resource> result = new LinkedList<>();
/* 149 */     Path file = getFilePath();
/*     */     try {
/* 151 */       if (file != null) {
/* 152 */         try (DirectoryStream<Path> stream = Files.newDirectoryStream(file)) {
/* 153 */           for (Path child : stream) {
/* 154 */             result.add(new URLResource(child.toUri().toURL(), child.toString()));
/*     */           }
/*     */         } 
/*     */       }
/* 158 */     } catch (IOException e) {
/* 159 */       throw new RuntimeException(e);
/*     */     } 
/* 161 */     return result;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType(MimeMappings mimeMappings) {
/* 166 */     String fileName = getName();
/* 167 */     int index = fileName.lastIndexOf('.');
/* 168 */     if (index != -1 && index != fileName.length() - 1) {
/* 169 */       return mimeMappings.getMimeType(fileName.substring(index + 1));
/*     */     }
/* 171 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
/* 176 */     serveImpl(sender, exchange, -1L, -1L, false, completionCallback);
/*     */   }
/*     */ 
/*     */   
/*     */   public void serveImpl(final Sender sender, final HttpServerExchange exchange, final long start, final long end, final boolean range, final IoCallback completionCallback) {
/*     */     class ServerTask
/*     */       implements Runnable, IoCallback
/*     */     {
/*     */       private InputStream inputStream;
/*     */       private byte[] buffer;
/* 186 */       long toSkip = start;
/* 187 */       long remaining = end - start + 1L;
/*     */ 
/*     */       
/*     */       public void run() {
/* 191 */         if (range && this.remaining == 0L) {
/*     */           
/* 193 */           IoUtils.safeClose(this.inputStream);
/* 194 */           completionCallback.onComplete(exchange, sender);
/*     */           return;
/*     */         } 
/* 197 */         if (this.inputStream == null) {
/*     */           try {
/* 199 */             this.inputStream = URLResource.this.url.openStream();
/* 200 */           } catch (IOException e) {
/* 201 */             exchange.setStatusCode(500);
/*     */             return;
/*     */           } 
/* 204 */           this.buffer = new byte[1024];
/*     */         } 
/*     */         try {
/* 207 */           int res = this.inputStream.read(this.buffer);
/* 208 */           if (res == -1) {
/*     */             
/* 210 */             IoUtils.safeClose(this.inputStream);
/* 211 */             completionCallback.onComplete(exchange, sender);
/*     */             return;
/*     */           } 
/* 214 */           int bufferStart = 0;
/* 215 */           int length = res;
/* 216 */           if (range && this.toSkip > 0L) {
/*     */ 
/*     */             
/* 219 */             while (this.toSkip > res) {
/* 220 */               this.toSkip -= res;
/* 221 */               res = this.inputStream.read(this.buffer);
/* 222 */               if (res == -1) {
/*     */                 
/* 224 */                 IoUtils.safeClose(this.inputStream);
/* 225 */                 completionCallback.onComplete(exchange, sender);
/*     */                 return;
/*     */               } 
/*     */             } 
/* 229 */             bufferStart = (int)this.toSkip;
/* 230 */             length = (int)(length - this.toSkip);
/* 231 */             this.toSkip = 0L;
/*     */           } 
/* 233 */           if (range && length > this.remaining) {
/* 234 */             length = (int)this.remaining;
/*     */           }
/* 236 */           sender.send(ByteBuffer.wrap(this.buffer, bufferStart, length), this);
/* 237 */         } catch (IOException e) {
/* 238 */           onException(exchange, sender, e);
/*     */         } 
/*     */       }
/*     */ 
/*     */ 
/*     */       
/*     */       public void onComplete(HttpServerExchange exchange, Sender sender) {
/* 245 */         if (exchange.isInIoThread()) {
/* 246 */           exchange.dispatch(this);
/*     */         } else {
/* 248 */           run();
/*     */         } 
/*     */       }
/*     */ 
/*     */       
/*     */       public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 254 */         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/* 255 */         IoUtils.safeClose(this.inputStream);
/* 256 */         if (!exchange.isResponseStarted()) {
/* 257 */           exchange.setStatusCode(500);
/*     */         }
/* 259 */         completionCallback.onException(exchange, sender, exception);
/*     */       }
/*     */     };
/*     */     
/* 263 */     ServerTask serveTask = new ServerTask();
/* 264 */     if (exchange.isInIoThread()) {
/* 265 */       exchange.dispatch(serveTask);
/*     */     } else {
/* 267 */       serveTask.run();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Long getContentLength() {
/* 273 */     openConnection();
/* 274 */     return this.contentLength;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCacheKey() {
/* 279 */     return this.url.toString();
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() {
/* 284 */     Path path = getFilePath();
/* 285 */     return (path != null) ? path.toFile() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getFilePath() {
/* 290 */     if (this.url.getProtocol().equals("file")) {
/*     */       try {
/* 292 */         return Paths.get(this.url.toURI());
/* 293 */       } catch (URISyntaxException e) {
/* 294 */         return null;
/*     */       } 
/*     */     }
/* 297 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getResourceManagerRoot() {
/* 302 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getResourceManagerRootPath() {
/* 307 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/* 312 */     return this.url;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback completionCallback) {
/* 317 */     serveImpl(sender, exchange, start, end, true, completionCallback);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRangeSupported() {
/* 322 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\URLResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */