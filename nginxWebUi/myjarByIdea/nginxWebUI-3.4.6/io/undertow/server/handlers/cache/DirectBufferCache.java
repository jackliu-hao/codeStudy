package io.undertow.server.handlers.cache;

import io.undertow.util.ConcurrentDirectDeque;
import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.xnio.BufferAllocator;

public class DirectBufferCache {
   private static final int SAMPLE_INTERVAL = 5;
   private final LimitedBufferSlicePool pool;
   private final ConcurrentMap<Object, CacheEntry> cache;
   private final ConcurrentDirectDeque<CacheEntry> accessQueue;
   private final int sliceSize;
   private final int maxAge;

   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory) {
      this(sliceSize, slicesPerPage, maxMemory, BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR);
   }

   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory, BufferAllocator<ByteBuffer> bufferAllocator) {
      this(sliceSize, slicesPerPage, maxMemory, bufferAllocator, -1);
   }

   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory, BufferAllocator<ByteBuffer> bufferAllocator, int maxAge) {
      this.sliceSize = sliceSize;
      this.pool = new LimitedBufferSlicePool(bufferAllocator, sliceSize, sliceSize * slicesPerPage, maxMemory / (sliceSize * slicesPerPage));
      this.cache = new ConcurrentHashMap(16);
      this.accessQueue = ConcurrentDirectDeque.newInstance();
      this.maxAge = maxAge;
   }

   public CacheEntry add(Object key, int size) {
      return this.add(key, size, this.maxAge);
   }

   public CacheEntry add(Object key, int size, int maxAge) {
      CacheEntry value = (CacheEntry)this.cache.get(key);
      if (value == null) {
         value = new CacheEntry(key, size, this, maxAge);
         CacheEntry result = (CacheEntry)this.cache.putIfAbsent(key, value);
         if (result != null) {
            value = result;
         } else {
            this.bumpAccess(value);
         }
      }

      return value;
   }

   public CacheEntry get(Object key) {
      CacheEntry cacheEntry = (CacheEntry)this.cache.get(key);
      if (cacheEntry == null) {
         return null;
      } else {
         long expires = cacheEntry.getExpires();
         if (expires != -1L && System.currentTimeMillis() > expires) {
            this.remove(key);
            return null;
         } else {
            if (cacheEntry.hit() % 5 == 0) {
               this.bumpAccess(cacheEntry);
               if (!cacheEntry.allocate()) {
                  int reclaimSize = cacheEntry.size();
                  Iterator var6 = this.accessQueue.iterator();

                  while(var6.hasNext()) {
                     CacheEntry oldest = (CacheEntry)var6.next();
                     if (oldest != cacheEntry) {
                        if (oldest.buffers().length > 0) {
                           reclaimSize -= oldest.size();
                        }

                        this.remove(oldest.key());
                        if (reclaimSize <= 0) {
                           break;
                        }
                     }
                  }

                  cacheEntry.allocate();
               }
            }

            return cacheEntry;
         }
      }
   }

   public Set<Object> getAllKeys() {
      return new HashSet(this.cache.keySet());
   }

   private void bumpAccess(CacheEntry cacheEntry) {
      Object prevToken = cacheEntry.claimToken();
      if (!Boolean.FALSE.equals(prevToken)) {
         if (prevToken != null) {
            this.accessQueue.removeToken(prevToken);
         }

         Object token = null;

         try {
            token = this.accessQueue.offerLastAndReturnToken(cacheEntry);
         } catch (Throwable var5) {
         }

         if (!cacheEntry.setToken(token) && token != null) {
            this.accessQueue.removeToken(token);
         }
      }

   }

   public void remove(Object key) {
      CacheEntry remove = (CacheEntry)this.cache.remove(key);
      if (remove != null) {
         Object old = remove.clearToken();
         if (old != null) {
            this.accessQueue.removeToken(old);
         }

         remove.dereference();
      }

   }

   public static final class CacheEntry {
      private static final LimitedBufferSlicePool.PooledByteBuffer[] EMPTY_BUFFERS = new LimitedBufferSlicePool.PooledByteBuffer[0];
      private static final LimitedBufferSlicePool.PooledByteBuffer[] INIT_BUFFERS = new LimitedBufferSlicePool.PooledByteBuffer[0];
      private static final Object CLAIM_TOKEN = new Object();
      private static final AtomicIntegerFieldUpdater<CacheEntry> hitsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "hits");
      private static final AtomicIntegerFieldUpdater<CacheEntry> refsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "refs");
      private static final AtomicIntegerFieldUpdater<CacheEntry> enabledUpdator = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "enabled");
      private static final AtomicReferenceFieldUpdater<CacheEntry, LimitedBufferSlicePool.PooledByteBuffer[]> bufsUpdater = AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, LimitedBufferSlicePool.PooledByteBuffer[].class, "buffers");
      private static final AtomicReferenceFieldUpdater<CacheEntry, Object> tokenUpdator = AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, Object.class, "accessToken");
      private final Object key;
      private final int size;
      private final DirectBufferCache cache;
      private final int maxAge;
      private volatile LimitedBufferSlicePool.PooledByteBuffer[] buffers;
      private volatile int refs;
      private volatile int hits;
      private volatile Object accessToken;
      private volatile int enabled;
      private volatile long expires;

      private CacheEntry(Object key, int size, DirectBufferCache cache, int maxAge) {
         this.buffers = INIT_BUFFERS;
         this.refs = 1;
         this.hits = 1;
         this.expires = -1L;
         this.key = key;
         this.size = size;
         this.cache = cache;
         this.maxAge = maxAge;
      }

      public int size() {
         return this.size;
      }

      public LimitedBufferSlicePool.PooledByteBuffer[] buffers() {
         return this.buffers;
      }

      public int hit() {
         int i;
         do {
            i = this.hits;
         } while(!hitsUpdater.weakCompareAndSet(this, i++, i));

         return i;
      }

      public Object key() {
         return this.key;
      }

      public boolean enabled() {
         return this.enabled == 2;
      }

      public void enable() {
         if (this.maxAge == -1) {
            this.expires = -1L;
         } else {
            this.expires = System.currentTimeMillis() + (long)this.maxAge;
         }

         this.enabled = 2;
      }

      public void disable() {
         this.enabled = 0;
      }

      public boolean claimEnable() {
         return enabledUpdator.compareAndSet(this, 0, 1);
      }

      public boolean reference() {
         int refs;
         do {
            refs = this.refs;
            if (refs < 1) {
               return false;
            }
         } while(!refsUpdater.compareAndSet(this, refs++, refs));

         return true;
      }

      public boolean dereference() {
         int refs;
         do {
            refs = this.refs;
            if (refs < 1) {
               return false;
            }
         } while(!refsUpdater.compareAndSet(this, refs--, refs));

         if (refs == 0) {
            this.destroy();
         }

         return true;
      }

      public boolean allocate() {
         if (this.buffers.length > 0) {
            return true;
         } else if (!bufsUpdater.compareAndSet(this, INIT_BUFFERS, EMPTY_BUFFERS)) {
            return true;
         } else {
            int reserveSize = this.size;
            int n = 1;

            DirectBufferCache bufferCache;
            for(bufferCache = this.cache; (reserveSize -= bufferCache.sliceSize) > 0; ++n) {
            }

            LimitedBufferSlicePool slicePool = bufferCache.pool;
            if (!slicePool.canAllocate(n)) {
               this.buffers = INIT_BUFFERS;
               return false;
            } else {
               LimitedBufferSlicePool.PooledByteBuffer[] buffers = new LimitedBufferSlicePool.PooledByteBuffer[n];

               for(int i = 0; i < n; ++i) {
                  LimitedBufferSlicePool.PooledByteBuffer allocate = slicePool.allocate();
                  if (allocate == null) {
                     while(true) {
                        --i;
                        if (i < 0) {
                           this.buffers = INIT_BUFFERS;
                           return false;
                        }

                        buffers[i].free();
                     }
                  }

                  buffers[i] = allocate;
               }

               this.buffers = buffers;
               return true;
            }
         }
      }

      private void destroy() {
         this.buffers = EMPTY_BUFFERS;
         LimitedBufferSlicePool.PooledByteBuffer[] var1 = this.buffers;
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            LimitedBufferSlicePool.PooledByteBuffer buffer = var1[var3];
            buffer.free();
         }

      }

      Object claimToken() {
         Object current;
         do {
            current = this.accessToken;
            if (current == CLAIM_TOKEN) {
               return Boolean.FALSE;
            }
         } while(!tokenUpdator.compareAndSet(this, current, CLAIM_TOKEN));

         return current;
      }

      boolean setToken(Object token) {
         return tokenUpdator.compareAndSet(this, CLAIM_TOKEN, token);
      }

      Object clearToken() {
         Object old = tokenUpdator.getAndSet(this, (Object)null);
         return old == CLAIM_TOKEN ? null : old;
      }

      long getExpires() {
         return this.expires;
      }

      // $FF: synthetic method
      CacheEntry(Object x0, int x1, DirectBufferCache x2, int x3, Object x4) {
         this(x0, x1, x2, x3);
      }
   }
}
