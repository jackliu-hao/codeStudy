package org.xnio;

import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.xnio._private.Messages;

/** @deprecated */
public final class ByteBufferSlicePool implements Pool<ByteBuffer> {
   private static final int LOCAL_LENGTH;
   private static final Queue<ByteBuffer> FREE_DIRECT_BUFFERS;
   private final Set<Ref> refSet;
   private final Queue<Slice> sliceQueue;
   private final BufferAllocator<ByteBuffer> allocator;
   private final int bufferSize;
   private final int buffersPerRegion;
   private final int threadLocalQueueSize;
   private final List<ByteBuffer> directBuffers;
   private final ThreadLocal<ThreadLocalCache> localQueueHolder;

   public ByteBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize, int threadLocalQueueSize) {
      this.refSet = Collections.synchronizedSet(new HashSet());
      this.localQueueHolder = new ThreadLocalCacheWrapper(this);
      if (bufferSize <= 0) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else if (maxRegionSize < bufferSize) {
         throw Messages.msg.parameterOutOfRange("bufferSize");
      } else {
         this.buffersPerRegion = maxRegionSize / bufferSize;
         this.bufferSize = bufferSize;
         this.allocator = allocator;
         this.sliceQueue = new ConcurrentLinkedQueue();
         this.threadLocalQueueSize = threadLocalQueueSize;
         if (allocator == BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR) {
            this.directBuffers = Collections.synchronizedList(new ArrayList());
         } else {
            this.directBuffers = null;
         }

      }
   }

   public ByteBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize) {
      this(allocator, bufferSize, maxRegionSize, LOCAL_LENGTH);
   }

   public ByteBufferSlicePool(int bufferSize, int maxRegionSize) {
      this(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, bufferSize, maxRegionSize);
   }

   public Pooled<ByteBuffer> allocate() {
      Slice slice;
      if (this.threadLocalQueueSize > 0) {
         ThreadLocalCache localCache = (ThreadLocalCache)this.localQueueHolder.get();
         if (localCache.outstanding != this.threadLocalQueueSize) {
            ++localCache.outstanding;
         }

         slice = (Slice)localCache.queue.poll();
         if (slice != null) {
            return new PooledByteBuffer(slice, slice.slice());
         }
      }

      Queue<Slice> sliceQueue = this.sliceQueue;
      slice = (Slice)sliceQueue.poll();
      if (slice != null) {
         return new PooledByteBuffer(slice, slice.slice());
      } else {
         synchronized(sliceQueue) {
            slice = (Slice)sliceQueue.poll();
            if (slice != null) {
               return new PooledByteBuffer(slice, slice.slice());
            } else {
               Slice newSlice = this.allocateSlices(this.buffersPerRegion, this.bufferSize);
               return new PooledByteBuffer(newSlice, newSlice.slice());
            }
         }
      }
   }

   private Slice allocateSlices(int buffersPerRegion, int bufferSize) {
      if (this.directBuffers != null) {
         ByteBuffer region = (ByteBuffer)FREE_DIRECT_BUFFERS.poll();

         Slice var4;
         try {
            if (region == null) {
               region = (ByteBuffer)this.allocator.allocate(buffersPerRegion * bufferSize);
               var4 = this.sliceAllocatedBuffer(region, buffersPerRegion, bufferSize);
               return var4;
            }

            var4 = this.sliceReusedBuffer(region, buffersPerRegion, bufferSize);
         } finally {
            this.directBuffers.add(region);
         }

         return var4;
      } else {
         return this.sliceAllocatedBuffer((ByteBuffer)this.allocator.allocate(buffersPerRegion * bufferSize), buffersPerRegion, bufferSize);
      }
   }

   private Slice sliceReusedBuffer(ByteBuffer region, int buffersPerRegion, int bufferSize) {
      int maxI = Math.min(buffersPerRegion, region.capacity() / bufferSize);
      int idx = bufferSize;

      for(int i = 1; i < maxI; ++i) {
         this.sliceQueue.add(new Slice(region, idx, bufferSize));
         idx += bufferSize;
      }

      if (maxI == 0) {
         return this.allocateSlices(buffersPerRegion, bufferSize);
      } else {
         if (maxI < buffersPerRegion) {
            this.sliceQueue.add(this.allocateSlices(buffersPerRegion - maxI, bufferSize));
         }

         return new Slice(region, 0, bufferSize);
      }
   }

   private Slice sliceAllocatedBuffer(ByteBuffer region, int buffersPerRegion, int bufferSize) {
      int idx = bufferSize;

      for(int i = 1; i < buffersPerRegion; ++i) {
         this.sliceQueue.add(new Slice(region, idx, bufferSize));
         idx += bufferSize;
      }

      return new Slice(region, 0, bufferSize);
   }

   public void clean() {
      ThreadLocalCache localCache = (ThreadLocalCache)this.localQueueHolder.get();
      if (!localCache.queue.isEmpty()) {
         localCache.queue.clear();
      }

      if (!this.sliceQueue.isEmpty()) {
         this.sliceQueue.clear();
      }

      if (this.directBuffers != null) {
         FREE_DIRECT_BUFFERS.addAll(this.directBuffers);
      }

   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   private ThreadLocalCache createThreadLocalCache() {
      return new ThreadLocalCache(this);
   }

   private void freeThreadLocalCache(ThreadLocalCache cache) {
      ArrayDeque<Slice> deque = cache.queue;

      for(Slice slice = (Slice)deque.poll(); slice != null; slice = (Slice)deque.poll()) {
         this.doFree(slice);
      }

   }

   private void doFree(Slice region) {
      if (this.threadLocalQueueSize > 0) {
         ThreadLocalCache localCache = (ThreadLocalCache)this.localQueueHolder.get();
         boolean cacheOk = false;
         if (localCache.outstanding > 0) {
            --localCache.outstanding;
            cacheOk = true;
         }

         ArrayDeque<Slice> localQueue = localCache.queue;
         if (localQueue.size() != this.threadLocalQueueSize && cacheOk) {
            localQueue.add(region);
         } else {
            this.sliceQueue.add(region);
         }
      } else {
         this.sliceQueue.add(region);
      }

   }

   static {
      String value = (String)AccessController.doPrivileged(new ReadPropertyAction("xnio.bufferpool.threadlocal.size", "12"));

      int val;
      try {
         val = Integer.parseInt(value);
      } catch (NumberFormatException var3) {
         val = 12;
      }

      LOCAL_LENGTH = val;
      FREE_DIRECT_BUFFERS = new ConcurrentLinkedQueue();
   }

   private static class ThreadLocalCacheWrapper extends ThreadLocal<ThreadLocalCache> {
      private final WeakReference<ByteBufferSlicePool> pool;

      ThreadLocalCacheWrapper(ByteBufferSlicePool pool) {
         this.pool = new WeakReference(pool);
      }

      protected ThreadLocalCache initialValue() {
         ByteBufferSlicePool pool = (ByteBufferSlicePool)this.pool.get();
         return pool != null ? pool.createThreadLocalCache() : null;
      }

      public void remove() {
         ByteBufferSlicePool pool = (ByteBufferSlicePool)this.pool.get();
         ThreadLocalCache cache = (ThreadLocalCache)this.get();
         if (pool != null && cache != null) {
            pool.freeThreadLocalCache(cache);
         }

         super.remove();
      }
   }

   static final class ThreadLocalCache {
      final WeakReference<ByteBufferSlicePool> pool;
      final ArrayDeque<Slice> queue;
      int outstanding = 0;

      ThreadLocalCache(ByteBufferSlicePool pool) {
         this.pool = new WeakReference(pool);
         this.queue = new ArrayDeque<Slice>(pool.threadLocalQueueSize) {
            protected void finalize() {
               ByteBufferSlicePool pool = (ByteBufferSlicePool)ThreadLocalCache.this.pool.get();
               if (pool != null) {
                  ArrayDeque<Slice> deque = ThreadLocalCache.this.queue;

                  for(Slice slice = (Slice)deque.poll(); slice != null; slice = (Slice)deque.poll()) {
                     pool.doFree(slice);
                  }

               }
            }
         };
      }
   }

   final class Ref extends AutomaticReference<ByteBuffer> {
      private final Slice region;

      private Ref(ByteBuffer referent, Slice region) {
         super(referent, AutomaticReference.PERMIT);
         this.region = region;
      }

      protected void free() {
         ByteBufferSlicePool.this.doFree(this.region);
         ByteBufferSlicePool.this.refSet.remove(this);
      }

      // $FF: synthetic method
      Ref(ByteBuffer x1, Slice x2, Object x3) {
         this(x1, x2);
      }
   }

   private static final class Slice {
      private final ByteBuffer parent;

      private Slice(ByteBuffer parent, int start, int size) {
         this.parent = (ByteBuffer)parent.duplicate().position(start).limit(start + size);
      }

      ByteBuffer slice() {
         return this.parent.slice();
      }

      // $FF: synthetic method
      Slice(ByteBuffer x0, int x1, int x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   private final class PooledByteBuffer implements Pooled<ByteBuffer> {
      private final Slice region;
      ByteBuffer buffer;

      PooledByteBuffer(Slice region, ByteBuffer buffer) {
         this.region = region;
         this.buffer = buffer;
      }

      public void discard() {
         ByteBuffer buffer = this.buffer;
         this.buffer = null;
         if (buffer != null) {
            ByteBufferSlicePool.this.refSet.add(ByteBufferSlicePool.this.new Ref(buffer, this.region));
         }

      }

      public void free() {
         ByteBuffer buffer = this.buffer;
         this.buffer = null;
         if (buffer != null) {
            ByteBufferSlicePool.this.doFree(this.region);
         }

      }

      public ByteBuffer getResource() {
         ByteBuffer buffer = this.buffer;
         if (buffer == null) {
            throw Messages.msg.bufferFreed();
         } else {
            return buffer;
         }
      }

      public void close() {
         this.free();
      }

      public String toString() {
         return "Pooled buffer " + this.buffer;
      }
   }
}
