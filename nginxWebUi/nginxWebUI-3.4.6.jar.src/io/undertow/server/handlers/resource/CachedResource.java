/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.io.IoCallback;
/*     */ import io.undertow.io.Sender;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.cache.DirectBufferCache;
/*     */ import io.undertow.server.handlers.cache.LimitedBufferSlicePool;
/*     */ import io.undertow.server.handlers.cache.ResponseCachingSender;
/*     */ import io.undertow.util.DateUtils;
/*     */ import io.undertow.util.ETag;
/*     */ import io.undertow.util.MimeMappings;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.net.URL;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.file.Path;
/*     */ import java.util.Date;
/*     */ import java.util.List;
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
/*     */ public class CachedResource
/*     */   implements Resource, RangeAwareResource
/*     */ {
/*     */   private final CacheKey cacheKey;
/*     */   private final CachingResourceManager cachingResourceManager;
/*     */   private final Resource underlyingResource;
/*     */   private final boolean directory;
/*     */   private final Date lastModifiedDate;
/*     */   private final String lastModifiedDateString;
/*     */   private final ETag eTag;
/*     */   private final String name;
/*     */   private volatile long nextMaxAgeCheck;
/*     */   
/*     */   public CachedResource(CachingResourceManager cachingResourceManager, Resource underlyingResource, String path) {
/*  56 */     this.cachingResourceManager = cachingResourceManager;
/*  57 */     this.underlyingResource = underlyingResource;
/*  58 */     this.directory = underlyingResource.isDirectory();
/*  59 */     this.lastModifiedDate = underlyingResource.getLastModified();
/*  60 */     if (this.lastModifiedDate != null) {
/*  61 */       this.lastModifiedDateString = DateUtils.toDateString(this.lastModifiedDate);
/*     */     } else {
/*  63 */       this.lastModifiedDateString = null;
/*     */     } 
/*  65 */     this.eTag = underlyingResource.getETag();
/*  66 */     this.name = underlyingResource.getName();
/*  67 */     this.cacheKey = new CacheKey(cachingResourceManager, underlyingResource.getCacheKey());
/*  68 */     if (cachingResourceManager.getMaxAge() > 0) {
/*  69 */       this.nextMaxAgeCheck = System.currentTimeMillis() + cachingResourceManager.getMaxAge();
/*     */     } else {
/*  71 */       this.nextMaxAgeCheck = -1L;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String getPath() {
/*  77 */     return this.underlyingResource.getPath();
/*     */   }
/*     */ 
/*     */   
/*     */   public Date getLastModified() {
/*  82 */     return this.lastModifiedDate;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getLastModifiedString() {
/*  87 */     return this.lastModifiedDateString;
/*     */   }
/*     */ 
/*     */   
/*     */   public ETag getETag() {
/*  92 */     return this.eTag;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  97 */     return this.name;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirectory() {
/* 102 */     return this.directory;
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Resource> list() {
/* 107 */     return this.underlyingResource.list();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getContentType(MimeMappings mimeMappings) {
/* 112 */     return this.underlyingResource.getContentType(mimeMappings);
/*     */   }
/*     */   
/*     */   public void invalidate() {
/* 116 */     DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
/* 117 */     if (dataCache != null) {
/* 118 */       dataCache.remove(this.cacheKey);
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean checkStillValid() {
/* 123 */     if (this.nextMaxAgeCheck > 0L) {
/* 124 */       long time = System.currentTimeMillis();
/* 125 */       if (time > this.nextMaxAgeCheck) {
/* 126 */         this.nextMaxAgeCheck = time + this.cachingResourceManager.getMaxAge();
/* 127 */         if (!this.underlyingResource.getLastModified().equals(this.lastModifiedDate)) {
/* 128 */           return false;
/*     */         }
/*     */       } 
/*     */     } 
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
/* 137 */     DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
/* 138 */     if (dataCache == null) {
/* 139 */       this.underlyingResource.serve(sender, exchange, completionCallback);
/*     */       
/*     */       return;
/*     */     } 
/* 143 */     DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
/* 144 */     Long length = getContentLength();
/*     */     
/* 146 */     if (length == null || length.longValue() > this.cachingResourceManager.getMaxFileSize()) {
/* 147 */       this.underlyingResource.serve(sender, exchange, completionCallback);
/*     */       
/*     */       return;
/*     */     } 
/* 151 */     if (existing == null || !existing.enabled() || !existing.reference()) {
/* 152 */       ResponseCachingSender responseCachingSender; DirectBufferCache.CacheEntry entry; Sender newSender = sender;
/*     */ 
/*     */       
/* 155 */       if (existing == null) {
/* 156 */         entry = dataCache.add(this.cacheKey, length.intValue(), this.cachingResourceManager.getMaxAge());
/*     */       } else {
/* 158 */         entry = existing;
/*     */       } 
/*     */       
/* 161 */       if (entry != null && (entry.buffers()).length != 0 && entry.claimEnable()) {
/* 162 */         if (entry.reference()) {
/* 163 */           responseCachingSender = new ResponseCachingSender(sender, entry, length.longValue());
/*     */         } else {
/* 165 */           entry.disable();
/*     */         } 
/*     */       }
/* 168 */       this.underlyingResource.serve((Sender)responseCachingSender, exchange, completionCallback);
/*     */     } else {
/* 170 */       ByteBuffer[] buffers; UndertowLogger.REQUEST_LOGGER.tracef("Serving resource %s from the buffer cache to %s", this.name, exchange);
/*     */ 
/*     */       
/* 173 */       boolean ok = false;
/*     */       try {
/* 175 */         LimitedBufferSlicePool.PooledByteBuffer[] pooled = existing.buffers();
/* 176 */         buffers = new ByteBuffer[pooled.length];
/* 177 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 179 */           buffers[i] = pooled[i].getBuffer().duplicate();
/*     */         }
/* 181 */         ok = true;
/*     */       } finally {
/* 183 */         if (!ok) {
/* 184 */           existing.dereference();
/*     */         }
/*     */       } 
/* 187 */       sender.send(buffers, new DereferenceCallback(existing, completionCallback));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Long getContentLength() {
/* 195 */     DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
/* 196 */     if (dataCache == null) {
/* 197 */       return this.underlyingResource.getContentLength();
/*     */     }
/* 199 */     DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
/* 200 */     if (existing == null || !existing.enabled()) {
/* 201 */       return this.underlyingResource.getContentLength();
/*     */     }
/*     */     
/* 204 */     return Long.valueOf(existing.size());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCacheKey() {
/* 209 */     return this.cacheKey.cacheKey;
/*     */   }
/*     */ 
/*     */   
/*     */   public File getFile() {
/* 214 */     return this.underlyingResource.getFile();
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getFilePath() {
/* 219 */     return this.underlyingResource.getFilePath();
/*     */   }
/*     */ 
/*     */   
/*     */   public File getResourceManagerRoot() {
/* 224 */     return this.underlyingResource.getResourceManagerRoot();
/*     */   }
/*     */ 
/*     */   
/*     */   public Path getResourceManagerRootPath() {
/* 229 */     return this.underlyingResource.getResourceManagerRootPath();
/*     */   }
/*     */ 
/*     */   
/*     */   public URL getUrl() {
/* 234 */     return this.underlyingResource.getUrl();
/*     */   }
/*     */ 
/*     */   
/*     */   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback completionCallback) {
/* 239 */     DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
/* 240 */     if (dataCache == null) {
/* 241 */       ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
/*     */       
/*     */       return;
/*     */     } 
/* 245 */     DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
/* 246 */     Long length = getContentLength();
/*     */     
/* 248 */     if (length == null || length.longValue() > this.cachingResourceManager.getMaxFileSize()) {
/* 249 */       ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
/*     */       
/*     */       return;
/*     */     } 
/* 253 */     if (existing == null || !existing.enabled() || !existing.reference()) {
/*     */ 
/*     */       
/* 256 */       ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
/*     */     } else {
/*     */       ByteBuffer[] buffers;
/*     */       
/* 260 */       boolean ok = false;
/*     */       try {
/* 262 */         LimitedBufferSlicePool.PooledByteBuffer[] pooled = existing.buffers();
/* 263 */         buffers = new ByteBuffer[pooled.length];
/* 264 */         for (int i = 0; i < buffers.length; i++)
/*     */         {
/* 266 */           buffers[i] = pooled[i].getBuffer().duplicate();
/*     */         }
/* 268 */         ok = true;
/*     */       } finally {
/* 270 */         if (!ok) {
/* 271 */           existing.dereference();
/*     */         }
/*     */       } 
/* 274 */       long endTarget = end + 1L;
/* 275 */       long startDec = start;
/* 276 */       long endCount = 0L;
/*     */       
/* 278 */       for (ByteBuffer b : buffers) {
/* 279 */         if (endCount == endTarget) {
/* 280 */           b.limit(b.position());
/*     */         } else {
/* 282 */           if (endCount + b.remaining() < endTarget) {
/* 283 */             endCount += b.remaining();
/*     */           } else {
/* 285 */             b.limit((int)(b.position() + endTarget - endCount));
/* 286 */             endCount = endTarget;
/*     */           } 
/* 288 */           if (b.remaining() >= startDec) {
/* 289 */             b.position((int)(b.position() + startDec));
/* 290 */             startDec = 0L;
/*     */           } else {
/* 292 */             startDec -= b.remaining();
/* 293 */             b.position(b.limit());
/*     */           } 
/*     */         } 
/*     */       } 
/* 297 */       sender.send(buffers, new DereferenceCallback(existing, completionCallback));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRangeSupported() {
/* 305 */     return (this.underlyingResource instanceof RangeAwareResource && ((RangeAwareResource)this.underlyingResource).isRangeSupported());
/*     */   }
/*     */   
/*     */   private static class DereferenceCallback
/*     */     implements IoCallback {
/*     */     private final DirectBufferCache.CacheEntry entry;
/*     */     private final IoCallback callback;
/*     */     
/*     */     DereferenceCallback(DirectBufferCache.CacheEntry entry, IoCallback callback) {
/* 314 */       this.entry = entry;
/* 315 */       this.callback = callback;
/*     */     }
/*     */ 
/*     */     
/*     */     public void onComplete(HttpServerExchange exchange, Sender sender) {
/*     */       try {
/* 321 */         this.entry.dereference();
/*     */       } finally {
/* 323 */         this.callback.onComplete(exchange, sender);
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
/* 329 */       UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);
/*     */       try {
/* 331 */         this.entry.dereference();
/*     */       } finally {
/* 333 */         this.callback.onException(exchange, sender, exception);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static final class CacheKey
/*     */   {
/*     */     final CachingResourceManager manager;
/*     */     final String cacheKey;
/*     */     
/*     */     CacheKey(CachingResourceManager manager, String cacheKey) {
/* 344 */       this.manager = manager;
/* 345 */       this.cacheKey = cacheKey;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 350 */       if (this == o) return true; 
/* 351 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 353 */       CacheKey cacheKey1 = (CacheKey)o;
/*     */       
/* 355 */       if ((this.cacheKey != null) ? !this.cacheKey.equals(cacheKey1.cacheKey) : (cacheKey1.cacheKey != null)) return false; 
/* 356 */       if ((this.manager != null) ? !this.manager.equals(cacheKey1.manager) : (cacheKey1.manager != null)) return false;
/*     */       
/* 358 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 363 */       int result = (this.manager != null) ? this.manager.hashCode() : 0;
/* 364 */       result = 31 * result + ((this.cacheKey != null) ? this.cacheKey.hashCode() : 0);
/* 365 */       return result;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\CachedResource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */