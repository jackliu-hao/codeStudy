package io.undertow.server.handlers.resource;

import io.undertow.UndertowLogger;
import io.undertow.io.IoCallback;
import io.undertow.io.Sender;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.cache.DirectBufferCache;
import io.undertow.server.handlers.cache.LimitedBufferSlicePool;
import io.undertow.server.handlers.cache.ResponseCachingSender;
import io.undertow.util.DateUtils;
import io.undertow.util.ETag;
import io.undertow.util.MimeMappings;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;

public class CachedResource implements Resource, RangeAwareResource {
   private final CacheKey cacheKey;
   private final CachingResourceManager cachingResourceManager;
   private final Resource underlyingResource;
   private final boolean directory;
   private final Date lastModifiedDate;
   private final String lastModifiedDateString;
   private final ETag eTag;
   private final String name;
   private volatile long nextMaxAgeCheck;

   public CachedResource(CachingResourceManager cachingResourceManager, Resource underlyingResource, String path) {
      this.cachingResourceManager = cachingResourceManager;
      this.underlyingResource = underlyingResource;
      this.directory = underlyingResource.isDirectory();
      this.lastModifiedDate = underlyingResource.getLastModified();
      if (this.lastModifiedDate != null) {
         this.lastModifiedDateString = DateUtils.toDateString(this.lastModifiedDate);
      } else {
         this.lastModifiedDateString = null;
      }

      this.eTag = underlyingResource.getETag();
      this.name = underlyingResource.getName();
      this.cacheKey = new CacheKey(cachingResourceManager, underlyingResource.getCacheKey());
      if (cachingResourceManager.getMaxAge() > 0) {
         this.nextMaxAgeCheck = System.currentTimeMillis() + (long)cachingResourceManager.getMaxAge();
      } else {
         this.nextMaxAgeCheck = -1L;
      }

   }

   public String getPath() {
      return this.underlyingResource.getPath();
   }

   public Date getLastModified() {
      return this.lastModifiedDate;
   }

   public String getLastModifiedString() {
      return this.lastModifiedDateString;
   }

   public ETag getETag() {
      return this.eTag;
   }

   public String getName() {
      return this.name;
   }

   public boolean isDirectory() {
      return this.directory;
   }

   public List<Resource> list() {
      return this.underlyingResource.list();
   }

   public String getContentType(MimeMappings mimeMappings) {
      return this.underlyingResource.getContentType(mimeMappings);
   }

   public void invalidate() {
      DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
      if (dataCache != null) {
         dataCache.remove(this.cacheKey);
      }

   }

   public boolean checkStillValid() {
      if (this.nextMaxAgeCheck > 0L) {
         long time = System.currentTimeMillis();
         if (time > this.nextMaxAgeCheck) {
            this.nextMaxAgeCheck = time + (long)this.cachingResourceManager.getMaxAge();
            if (!this.underlyingResource.getLastModified().equals(this.lastModifiedDate)) {
               return false;
            }
         }
      }

      return true;
   }

   public void serve(Sender sender, HttpServerExchange exchange, IoCallback completionCallback) {
      DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
      if (dataCache == null) {
         this.underlyingResource.serve(sender, exchange, completionCallback);
      } else {
         DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
         Long length = this.getContentLength();
         if (length != null && length <= this.cachingResourceManager.getMaxFileSize()) {
            if (existing != null && existing.enabled() && existing.reference()) {
               UndertowLogger.REQUEST_LOGGER.tracef("Serving resource %s from the buffer cache to %s", this.name, exchange);
               boolean ok = false;

               ByteBuffer[] buffers;
               try {
                  LimitedBufferSlicePool.PooledByteBuffer[] pooled = existing.buffers();
                  buffers = new ByteBuffer[pooled.length];
                  int i = 0;

                  while(true) {
                     if (i >= buffers.length) {
                        ok = true;
                        break;
                     }

                     buffers[i] = pooled[i].getBuffer().duplicate();
                     ++i;
                  }
               } finally {
                  if (!ok) {
                     existing.dereference();
                  }

               }

               sender.send((ByteBuffer[])buffers, (IoCallback)(new DereferenceCallback(existing, completionCallback)));
            } else {
               Sender newSender = sender;
               DirectBufferCache.CacheEntry entry;
               if (existing == null) {
                  entry = dataCache.add(this.cacheKey, length.intValue(), this.cachingResourceManager.getMaxAge());
               } else {
                  entry = existing;
               }

               if (entry != null && entry.buffers().length != 0 && entry.claimEnable()) {
                  if (entry.reference()) {
                     newSender = new ResponseCachingSender(sender, entry, length);
                  } else {
                     entry.disable();
                  }
               }

               this.underlyingResource.serve((Sender)newSender, exchange, completionCallback);
            }

         } else {
            this.underlyingResource.serve(sender, exchange, completionCallback);
         }
      }
   }

   public Long getContentLength() {
      DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
      if (dataCache == null) {
         return this.underlyingResource.getContentLength();
      } else {
         DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
         return existing != null && existing.enabled() ? (long)existing.size() : this.underlyingResource.getContentLength();
      }
   }

   public String getCacheKey() {
      return this.cacheKey.cacheKey;
   }

   public File getFile() {
      return this.underlyingResource.getFile();
   }

   public Path getFilePath() {
      return this.underlyingResource.getFilePath();
   }

   public File getResourceManagerRoot() {
      return this.underlyingResource.getResourceManagerRoot();
   }

   public Path getResourceManagerRootPath() {
      return this.underlyingResource.getResourceManagerRootPath();
   }

   public URL getUrl() {
      return this.underlyingResource.getUrl();
   }

   public void serveRange(Sender sender, HttpServerExchange exchange, long start, long end, IoCallback completionCallback) {
      DirectBufferCache dataCache = this.cachingResourceManager.getDataCache();
      if (dataCache == null) {
         ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
      } else {
         DirectBufferCache.CacheEntry existing = dataCache.get(this.cacheKey);
         Long length = this.getContentLength();
         if (length != null && length <= this.cachingResourceManager.getMaxFileSize()) {
            if (existing != null && existing.enabled() && existing.reference()) {
               boolean ok = false;

               ByteBuffer[] buffers;
               try {
                  LimitedBufferSlicePool.PooledByteBuffer[] pooled = existing.buffers();
                  buffers = new ByteBuffer[pooled.length];
                  int i = 0;

                  while(true) {
                     if (i >= buffers.length) {
                        ok = true;
                        break;
                     }

                     buffers[i] = pooled[i].getBuffer().duplicate();
                     ++i;
                  }
               } finally {
                  if (!ok) {
                     existing.dereference();
                  }

               }

               long endTarget = end + 1L;
               long startDec = start;
               long endCount = 0L;
               ByteBuffer[] var19 = buffers;
               int var20 = buffers.length;

               for(int var21 = 0; var21 < var20; ++var21) {
                  ByteBuffer b = var19[var21];
                  if (endCount == endTarget) {
                     b.limit(b.position());
                  } else {
                     if (endCount + (long)b.remaining() < endTarget) {
                        endCount += (long)b.remaining();
                     } else {
                        b.limit((int)((long)b.position() + (endTarget - endCount)));
                        endCount = endTarget;
                     }

                     if ((long)b.remaining() >= startDec) {
                        b.position((int)((long)b.position() + startDec));
                        startDec = 0L;
                     } else {
                        startDec -= (long)b.remaining();
                        b.position(b.limit());
                     }
                  }
               }

               sender.send((ByteBuffer[])buffers, (IoCallback)(new DereferenceCallback(existing, completionCallback)));
            } else {
               ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
            }

         } else {
            ((RangeAwareResource)this.underlyingResource).serveRange(sender, exchange, start, end, completionCallback);
         }
      }
   }

   public boolean isRangeSupported() {
      return this.underlyingResource instanceof RangeAwareResource && ((RangeAwareResource)this.underlyingResource).isRangeSupported();
   }

   static final class CacheKey {
      final CachingResourceManager manager;
      final String cacheKey;

      CacheKey(CachingResourceManager manager, String cacheKey) {
         this.manager = manager;
         this.cacheKey = cacheKey;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            CacheKey cacheKey1 = (CacheKey)o;
            if (this.cacheKey != null) {
               if (!this.cacheKey.equals(cacheKey1.cacheKey)) {
                  return false;
               }
            } else if (cacheKey1.cacheKey != null) {
               return false;
            }

            if (this.manager != null) {
               if (this.manager.equals(cacheKey1.manager)) {
                  return true;
               }
            } else if (cacheKey1.manager == null) {
               return true;
            }

            return false;
         } else {
            return false;
         }
      }

      public int hashCode() {
         int result = this.manager != null ? this.manager.hashCode() : 0;
         result = 31 * result + (this.cacheKey != null ? this.cacheKey.hashCode() : 0);
         return result;
      }
   }

   private static class DereferenceCallback implements IoCallback {
      private final DirectBufferCache.CacheEntry entry;
      private final IoCallback callback;

      DereferenceCallback(DirectBufferCache.CacheEntry entry, IoCallback callback) {
         this.entry = entry;
         this.callback = callback;
      }

      public void onComplete(HttpServerExchange exchange, Sender sender) {
         try {
            this.entry.dereference();
         } finally {
            this.callback.onComplete(exchange, sender);
         }

      }

      public void onException(HttpServerExchange exchange, Sender sender, IOException exception) {
         UndertowLogger.REQUEST_IO_LOGGER.ioException(exception);

         try {
            this.entry.dereference();
         } finally {
            this.callback.onException(exchange, sender, exception);
         }

      }
   }
}
