/*     */ package io.undertow.server.handlers.resource;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.handlers.cache.DirectBufferCache;
/*     */ import io.undertow.server.handlers.cache.LRUCache;
/*     */ import java.io.IOException;
/*     */ import java.util.Collection;
/*     */ import java.util.Set;
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
/*     */ public class CachingResourceManager
/*     */   implements ResourceManager
/*     */ {
/*     */   private final long maxFileSize;
/*     */   private final ResourceManager underlyingResourceManager;
/*     */   private final DirectBufferCache dataCache;
/*     */   private final LRUCache<String, Object> cache;
/*     */   private final int maxAge;
/*     */   
/*     */   public CachingResourceManager(int metadataCacheSize, long maxFileSize, DirectBufferCache dataCache, ResourceManager underlyingResourceManager, int maxAge) {
/*  57 */     this.maxFileSize = maxFileSize;
/*  58 */     this.underlyingResourceManager = underlyingResourceManager;
/*  59 */     this.dataCache = dataCache;
/*  60 */     this.cache = new LRUCache(metadataCacheSize, maxAge);
/*  61 */     this.maxAge = maxAge;
/*  62 */     if (underlyingResourceManager.isResourceChangeListenerSupported()) {
/*     */       try {
/*  64 */         underlyingResourceManager.registerResourceChangeListener(new ResourceChangeListener()
/*     */             {
/*     */               public void handleChanges(Collection<ResourceChangeEvent> changes) {
/*  67 */                 for (ResourceChangeEvent change : changes) {
/*  68 */                   CachingResourceManager.this.invalidate(change.getResource());
/*     */                 }
/*     */               }
/*     */             });
/*  72 */       } catch (Exception e) {
/*  73 */         UndertowLogger.ROOT_LOGGER.couldNotRegisterChangeListener(e);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   public CachedResource getResource(String p) throws IOException {
/*     */     String path;
/*  80 */     if (p == null) {
/*  81 */       return null;
/*     */     }
/*     */ 
/*     */     
/*  85 */     if (p.startsWith("/")) {
/*  86 */       path = p.substring(1);
/*     */     } else {
/*  88 */       path = p;
/*     */     } 
/*  90 */     Object res = this.cache.get(path);
/*  91 */     if (res instanceof NoResourceMarker) {
/*  92 */       NoResourceMarker marker = (NoResourceMarker)res;
/*  93 */       long nextCheck = marker.getNextCheckTime();
/*  94 */       if (nextCheck > 0L) {
/*  95 */         long time = System.currentTimeMillis();
/*  96 */         if (time > nextCheck) {
/*  97 */           marker.setNextCheckTime(time + this.maxAge);
/*  98 */           if (this.underlyingResourceManager.getResource(path) != null) {
/*  99 */             this.cache.remove(path);
/*     */           } else {
/* 101 */             return null;
/*     */           } 
/*     */         } else {
/* 104 */           return null;
/*     */         } 
/*     */       } else {
/* 107 */         return null;
/*     */       } 
/* 109 */     } else if (res != null) {
/* 110 */       CachedResource cachedResource = (CachedResource)res;
/* 111 */       if (cachedResource.checkStillValid()) {
/* 112 */         return cachedResource;
/*     */       }
/* 114 */       invalidate(path);
/*     */     } 
/*     */     
/* 117 */     Resource underlying = this.underlyingResourceManager.getResource(path);
/* 118 */     if (underlying == null) {
/* 119 */       this.cache.add(path, new NoResourceMarker((this.maxAge > 0) ? (System.currentTimeMillis() + this.maxAge) : -1L));
/* 120 */       return null;
/*     */     } 
/* 122 */     CachedResource resource = new CachedResource(this, underlying, path);
/* 123 */     this.cache.add(path, resource);
/* 124 */     return resource;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isResourceChangeListenerSupported() {
/* 129 */     return this.underlyingResourceManager.isResourceChangeListenerSupported();
/*     */   }
/*     */ 
/*     */   
/*     */   public void registerResourceChangeListener(ResourceChangeListener listener) {
/* 134 */     this.underlyingResourceManager.registerResourceChangeListener(listener);
/*     */   }
/*     */ 
/*     */   
/*     */   public void removeResourceChangeListener(ResourceChangeListener listener) {
/* 139 */     this.underlyingResourceManager.removeResourceChangeListener(listener);
/*     */   }
/*     */   
/*     */   public void invalidate(String path) {
/* 143 */     if (path.startsWith("/")) {
/* 144 */       path = path.substring(1);
/*     */     }
/* 146 */     Object entry = this.cache.remove(path);
/* 147 */     if (entry instanceof CachedResource) {
/* 148 */       ((CachedResource)entry).invalidate();
/*     */     }
/*     */   }
/*     */   
/*     */   DirectBufferCache getDataCache() {
/* 153 */     return this.dataCache;
/*     */   }
/*     */   
/*     */   public long getMaxFileSize() {
/* 157 */     return this.maxFileSize;
/*     */   }
/*     */   
/*     */   public int getMaxAge() {
/* 161 */     return this.maxAge;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/*     */     try {
/* 168 */       if (this.dataCache != null) {
/* 169 */         Set<Object> keys = this.dataCache.getAllKeys();
/* 170 */         for (Object key : keys) {
/* 171 */           if (key instanceof CachedResource.CacheKey && 
/* 172 */             ((CachedResource.CacheKey)key).manager == this) {
/* 173 */             this.dataCache.remove(key);
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } finally {
/*     */       
/* 179 */       this.underlyingResourceManager.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   private static final class NoResourceMarker
/*     */   {
/*     */     volatile long nextCheckTime;
/*     */     
/*     */     private NoResourceMarker(long nextCheckTime) {
/* 188 */       this.nextCheckTime = nextCheckTime;
/*     */     }
/*     */     
/*     */     public long getNextCheckTime() {
/* 192 */       return this.nextCheckTime;
/*     */     }
/*     */     
/*     */     public void setNextCheckTime(long nextCheckTime) {
/* 196 */       this.nextCheckTime = nextCheckTime;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\resource\CachingResourceManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */