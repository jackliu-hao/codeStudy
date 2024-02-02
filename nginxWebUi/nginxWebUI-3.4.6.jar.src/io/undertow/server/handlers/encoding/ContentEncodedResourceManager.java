/*     */ package io.undertow.server.handlers.encoding;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.resource.CachedResource;
/*     */ import io.undertow.server.handlers.resource.CachingResourceManager;
/*     */ import io.undertow.server.handlers.resource.Resource;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.ImmediateConduitFactory;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.Channels;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.ConduitStreamSinkChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ public class ContentEncodedResourceManager
/*     */ {
/*     */   private final Path encodedResourcesRoot;
/*     */   private final CachingResourceManager encoded;
/*     */   private final ContentEncodingRepository contentEncodingRepository;
/*     */   private final int minResourceSize;
/*     */   private final int maxResourceSize;
/*     */   private final Predicate encodingAllowed;
/*  62 */   private final ConcurrentMap<LockKey, Object> fileLocks = new ConcurrentHashMap<>();
/*     */   
/*     */   public ContentEncodedResourceManager(Path encodedResourcesRoot, CachingResourceManager encodedResourceManager, ContentEncodingRepository contentEncodingRepository, int minResourceSize, int maxResourceSize, Predicate encodingAllowed) {
/*  65 */     this.encodedResourcesRoot = encodedResourcesRoot;
/*  66 */     this.encoded = encodedResourceManager;
/*  67 */     this.contentEncodingRepository = contentEncodingRepository;
/*  68 */     this.minResourceSize = minResourceSize;
/*  69 */     this.maxResourceSize = maxResourceSize;
/*  70 */     this.encodingAllowed = encodingAllowed;
/*     */   }
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
/*     */   public ContentEncodedResource getResource(Resource resource, HttpServerExchange exchange) throws IOException {
/*  84 */     String path = resource.getPath();
/*  85 */     Path file = resource.getFilePath();
/*  86 */     if (file == null) {
/*  87 */       return null;
/*     */     }
/*  89 */     if ((this.minResourceSize > 0 && resource.getContentLength().longValue() < this.minResourceSize) || (this.maxResourceSize > 0 && resource
/*  90 */       .getContentLength().longValue() > this.maxResourceSize) || (this.encodingAllowed != null && 
/*  91 */       !this.encodingAllowed.resolve(exchange))) {
/*  92 */       return null;
/*     */     }
/*  94 */     AllowedContentEncodings encodings = this.contentEncodingRepository.getContentEncodings(exchange);
/*  95 */     if (encodings == null || encodings.isNoEncodingsAllowed()) {
/*  96 */       return null;
/*     */     }
/*  98 */     EncodingMapping encoding = encodings.getEncoding();
/*  99 */     if (encoding == null || encoding.getName().equals("identity")) {
/* 100 */       return null;
/*     */     }
/* 102 */     String newPath = path + ".undertow.encoding." + encoding.getName();
/* 103 */     CachedResource cachedResource = this.encoded.getResource(newPath);
/* 104 */     if (cachedResource != null) {
/* 105 */       return new ContentEncodedResource((Resource)cachedResource, encoding.getName());
/*     */     }
/* 107 */     LockKey key = new LockKey(path, encoding.getName());
/* 108 */     if (this.fileLocks.putIfAbsent(key, this) != null)
/*     */     {
/*     */       
/* 111 */       return null;
/*     */     }
/* 113 */     FileChannel targetFileChannel = null;
/* 114 */     FileChannel sourceFileChannel = null;
/*     */     
/*     */     try {
/* 117 */       cachedResource = this.encoded.getResource(newPath);
/* 118 */       if (cachedResource != null) {
/* 119 */         return new ContentEncodedResource((Resource)cachedResource, encoding.getName());
/*     */       }
/*     */       
/* 122 */       Path finalTarget = this.encodedResourcesRoot.resolve(newPath);
/* 123 */       Path tempTarget = this.encodedResourcesRoot.resolve(newPath);
/*     */ 
/*     */       
/* 126 */       OutputStream tmp = Files.newOutputStream(tempTarget, new OpenOption[0]);
/*     */       try {
/* 128 */         tmp.close();
/*     */       } finally {
/* 130 */         IoUtils.safeClose(tmp);
/*     */       } 
/*     */       
/* 133 */       targetFileChannel = FileChannel.open(tempTarget, new OpenOption[] { StandardOpenOption.READ, StandardOpenOption.WRITE });
/* 134 */       sourceFileChannel = FileChannel.open(file, new OpenOption[] { StandardOpenOption.READ });
/*     */       
/* 136 */       StreamSinkConduit conduit = (StreamSinkConduit)encoding.getEncoding().getResponseWrapper().wrap((ConduitFactory)new ImmediateConduitFactory((Conduit)new FileConduitTarget(targetFileChannel, exchange)), exchange);
/* 137 */       ConduitStreamSinkChannel targetChannel = new ConduitStreamSinkChannel(null, conduit);
/* 138 */       long transferred = sourceFileChannel.transferTo(0L, resource.getContentLength().longValue(), (WritableByteChannel)targetChannel);
/* 139 */       targetChannel.shutdownWrites();
/* 140 */       Channels.flushBlocking((SuspendableWriteChannel)targetChannel);
/* 141 */       if (transferred != resource.getContentLength().longValue()) {
/* 142 */         UndertowLogger.REQUEST_LOGGER.failedToWritePreCachedFile();
/*     */       }
/* 144 */       Files.move(tempTarget, finalTarget, new java.nio.file.CopyOption[0]);
/* 145 */       this.encoded.invalidate(newPath);
/* 146 */       CachedResource cachedResource1 = this.encoded.getResource(newPath);
/* 147 */       return new ContentEncodedResource((Resource)cachedResource1, encoding.getName());
/*     */     } finally {
/* 149 */       IoUtils.safeClose(targetFileChannel);
/* 150 */       IoUtils.safeClose(sourceFileChannel);
/* 151 */       this.fileLocks.remove(key);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class LockKey {
/*     */     private final String path;
/*     */     private final String encoding;
/*     */     
/*     */     private LockKey(String path, String encoding) {
/* 160 */       this.path = path;
/* 161 */       this.encoding = encoding;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 166 */       if (this == o) return true; 
/* 167 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 169 */       LockKey lockKey = (LockKey)o;
/*     */       
/* 171 */       if ((this.encoding != null) ? !this.encoding.equals(lockKey.encoding) : (lockKey.encoding != null)) return false; 
/* 172 */       if ((this.path != null) ? !this.path.equals(lockKey.path) : (lockKey.path != null)) return false;
/*     */       
/* 174 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 179 */       int result = (this.path != null) ? this.path.hashCode() : 0;
/* 180 */       result = 31 * result + ((this.encoding != null) ? this.encoding.hashCode() : 0);
/* 181 */       return result;
/*     */     }
/*     */   }
/*     */   
/*     */   private static final class FileConduitTarget implements StreamSinkConduit {
/*     */     private final FileChannel fileChannel;
/*     */     private final HttpServerExchange exchange;
/*     */     private WriteReadyHandler writeReadyHandler;
/*     */     private boolean writesResumed = false;
/*     */     
/*     */     private FileConduitTarget(FileChannel fileChannel, HttpServerExchange exchange) {
/* 192 */       this.fileChannel = fileChannel;
/* 193 */       this.exchange = exchange;
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferFrom(FileChannel fileChannel, long l, long l2) throws IOException {
/* 198 */       return this.fileChannel.transferFrom(fileChannel, l, l2);
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferFrom(StreamSourceChannel streamSourceChannel, long l, ByteBuffer byteBuffer) throws IOException {
/* 203 */       return IoUtils.transfer((ReadableByteChannel)streamSourceChannel, l, byteBuffer, this.fileChannel);
/*     */     }
/*     */ 
/*     */     
/*     */     public int write(ByteBuffer byteBuffer) throws IOException {
/* 208 */       return this.fileChannel.write(byteBuffer);
/*     */     }
/*     */ 
/*     */     
/*     */     public long write(ByteBuffer[] byteBuffers, int i, int i2) throws IOException {
/* 213 */       return this.fileChannel.write(byteBuffers, i, i2);
/*     */     }
/*     */ 
/*     */     
/*     */     public int writeFinal(ByteBuffer src) throws IOException {
/* 218 */       return Conduits.writeFinalBasic(this, src);
/*     */     }
/*     */ 
/*     */     
/*     */     public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 223 */       return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */     }
/*     */ 
/*     */     
/*     */     public void terminateWrites() throws IOException {
/* 228 */       this.fileChannel.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWriteShutdown() {
/* 233 */       return !this.fileChannel.isOpen();
/*     */     }
/*     */ 
/*     */     
/*     */     public void resumeWrites() {
/* 238 */       wakeupWrites();
/*     */     }
/*     */ 
/*     */     
/*     */     public void suspendWrites() {
/* 243 */       this.writesResumed = false;
/*     */     }
/*     */ 
/*     */     
/*     */     public void wakeupWrites() {
/* 248 */       if (this.writeReadyHandler != null) {
/* 249 */         this.writesResumed = true;
/* 250 */         while (this.writesResumed && this.writeReadyHandler != null) {
/* 251 */           this.writeReadyHandler.writeReady();
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isWriteResumed() {
/* 258 */       return this.writesResumed;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void awaitWritable() throws IOException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void awaitWritable(long l, TimeUnit timeUnit) throws IOException {}
/*     */ 
/*     */     
/*     */     public XnioIoThread getWriteThread() {
/* 271 */       return this.exchange.getIoThread();
/*     */     }
/*     */ 
/*     */     
/*     */     public void setWriteReadyHandler(WriteReadyHandler writeReadyHandler) {
/* 276 */       this.writeReadyHandler = writeReadyHandler;
/*     */     }
/*     */ 
/*     */     
/*     */     public void truncateWrites() throws IOException {
/* 281 */       this.fileChannel.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean flush() throws IOException {
/* 286 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public XnioWorker getWorker() {
/* 291 */       return this.exchange.getConnection().getWorker();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\encoding\ContentEncodedResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */