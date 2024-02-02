package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.cache.LRUCache;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class CachingResourceManager implements ResourceManager {
   private final long maxFileSize;
   private final ResourceManager underlyingResourceManager;
   private final DirectBufferCache dataCache;
   private final LRUCache<String, Object> cache;
   private final int maxAge;

   public CachingResourceManager(int metadataCacheSize, long maxFileSize, DirectBufferCache dataCache, ResourceManager underlyingResourceManager, int maxAge) {
      this.maxFileSize = maxFileSize;
      this.underlyingResourceManager = underlyingResourceManager;
      this.dataCache = dataCache;
      this.cache = new LRUCache(metadataCacheSize, maxAge);
      this.maxAge = maxAge;
      if (underlyingResourceManager.isResourceChangeListenerSupported()) {
         try {
            underlyingResourceManager.registerResourceChangeListener(new ResourceChangeListener() {
               public void handleChanges(Collection<ResourceChangeEvent> changes) {
                  Iterator var2 = changes.iterator();

                  while(var2.hasNext()) {
                     ResourceChangeEvent change = (ResourceChangeEvent)var2.next();
                     CachingResourceManager.this.invalidate(change.getResource());
                  }

               }
            });
         } catch (Exception var8) {
            UndertowLogger.ROOT_LOGGER.couldNotRegisterChangeListener(var8);
         }
      }

   }

   public CachedResource getResource(String p) throws IOException {
      if (p == null) {
         return null;
      } else {
         String path;
         if (p.startsWith("/")) {
            path = p.substring(1);
         } else {
            path = p;
         }

         Object res = this.cache.get(path);
         if (res instanceof NoResourceMarker) {
            NoResourceMarker marker = (NoResourceMarker)res;
            long nextCheck = marker.getNextCheckTime();
            if (nextCheck <= 0L) {
               return null;
            }

            long time = System.currentTimeMillis();
            if (time <= nextCheck) {
               return null;
            }

            marker.setNextCheckTime(time + (long)this.maxAge);
            if (this.underlyingResourceManager.getResource(path) == null) {
               return null;
            }

            this.cache.remove(path);
         } else if (res != null) {
            CachedResource resource = (CachedResource)res;
            if (resource.checkStillValid()) {
               return resource;
            }

            this.invalidate(path);
         }

         Resource underlying = this.underlyingResourceManager.getResource(path);
         if (underlying == null) {
            this.cache.add(path, new NoResourceMarker(this.maxAge > 0 ? System.currentTimeMillis() + (long)this.maxAge : -1L));
            return null;
         } else {
            CachedResource resource = new CachedResource(this, underlying, path);
            this.cache.add(path, resource);
            return resource;
         }
      }
   }

   public boolean isResourceChangeListenerSupported() {
      return this.underlyingResourceManager.isResourceChangeListenerSupported();
   }

   public void registerResourceChangeListener(ResourceChangeListener listener) {
      this.underlyingResourceManager.registerResourceChangeListener(listener);
   }

   public void removeResourceChangeListener(ResourceChangeListener listener) {
      this.underlyingResourceManager.removeResourceChangeListener(listener);
   }

   public void invalidate(String path) {
      if (path.startsWith("/")) {
         path = path.substring(1);
      }

      Object entry = this.cache.remove(path);
      if (entry instanceof CachedResource) {
         ((CachedResource)entry).invalidate();
      }

   }

   DirectBufferCache getDataCache() {
      return this.dataCache;
   }

   public long getMaxFileSize() {
      return this.maxFileSize;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public void close() throws IOException {
      try {
         if (this.dataCache != null) {
            Set<Object> keys = this.dataCache.getAllKeys();
            Iterator var2 = keys.iterator();

            while(var2.hasNext()) {
               Object key = var2.next();
               if (key instanceof CachedResource.CacheKey && ((CachedResource.CacheKey)key).manager == this) {
                  this.dataCache.remove(key);
               }
            }
         }
      } finally {
         this.underlyingResourceManager.close();
      }

   }

   private static final class NoResourceMarker {
      volatile long nextCheckTime;

      private NoResourceMarker(long nextCheckTime) {
         this.nextCheckTime = nextCheckTime;
      }

      public long getNextCheckTime() {
         return this.nextCheckTime;
      }

      public void setNextCheckTime(long nextCheckTime) {
         this.nextCheckTime = nextCheckTime;
      }

      // $FF: synthetic method
      NoResourceMarker(long x0, Object x1) {
         this(x0);
      }
   }
}
