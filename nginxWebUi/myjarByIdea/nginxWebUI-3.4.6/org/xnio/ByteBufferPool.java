package org.xnio;

import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.wildfly.common.Assert;
import org.wildfly.common.cpu.CacheInfo;
import org.wildfly.common.function.ExceptionBiConsumer;
import org.wildfly.common.function.ExceptionBiFunction;
import org.wildfly.common.function.ExceptionConsumer;
import org.wildfly.common.function.ExceptionFunction;
import org.wildfly.common.function.ExceptionRunnable;
import org.wildfly.common.function.ExceptionSupplier;
import org.wildfly.common.function.Functions;

public abstract class ByteBufferPool {
   private static final boolean sliceLargeBuffers = Boolean.parseBoolean(System.getProperty("xnio.buffer.slice-large-buffers", "true"));
   private final ConcurrentLinkedQueue<ByteBuffer> masterQueue = new ConcurrentLinkedQueue();
   private final ThreadLocal<Cache> threadLocalCache = ThreadLocal.withInitial(this::getDefaultCache);
   private final Cache defaultCache = new DefaultCache();
   private final int size;
   private final boolean direct;
   public static final int LARGE_SIZE = 1048576;
   public static final int MEDIUM_SIZE = 8192;
   public static final int SMALL_SIZE = 64;
   static final int CACHE_LINE_SIZE = Math.max(64, CacheInfo.getSmallestDataCacheLineSize());
   public static final ByteBufferPool LARGE_DIRECT = create(1048576, true);
   public static final ByteBufferPool MEDIUM_DIRECT;
   public static final ByteBufferPool SMALL_DIRECT;
   public static final ByteBufferPool LARGE_HEAP;
   public static final ByteBufferPool MEDIUM_HEAP;
   public static final ByteBufferPool SMALL_HEAP;

   ByteBufferPool(int size, boolean direct) {
      assert Integer.bitCount(size) == 1;

      assert size >= 16;

      assert size <= 1073741824;

      this.size = size;
      this.direct = direct;
   }

   public ByteBuffer allocate() {
      return ((Cache)this.threadLocalCache.get()).allocate();
   }

   public void allocate(ByteBuffer[] array, int offs) {
      this.allocate(array, offs, array.length - offs);
   }

   public void allocate(ByteBuffer[] array, int offs, int len) {
      Assert.checkNotNullParam("array", array);
      Assert.checkArrayBounds((Object[])array, offs, len);

      for(int i = 0; i < len; ++i) {
         array[offs + i] = this.allocate();
      }

   }

   public static void free(ByteBuffer buffer) {
      Assert.checkNotNullParam("buffer", buffer);
      int size = buffer.capacity();
      if (Integer.bitCount(size) == 1 && !buffer.isReadOnly()) {
         if (buffer.isDirect()) {
            if (size == 8192) {
               MEDIUM_DIRECT.doFree(buffer);
            } else if (size == 64) {
               SMALL_DIRECT.doFree(buffer);
            } else if (size == 1048576) {
               LARGE_DIRECT.doFree(buffer);
            }
         } else if (size == 8192) {
            MEDIUM_HEAP.doFree(buffer);
         } else if (size == 64) {
            SMALL_HEAP.doFree(buffer);
         } else if (size == 1048576) {
            LARGE_HEAP.doFree(buffer);
         }
      }

   }

   public static void free(ByteBuffer[] array, int offs, int len) {
      Assert.checkArrayBounds((Object[])array, offs, len);

      for(int i = 0; i < len; ++i) {
         ByteBuffer buffer = array[offs + i];
         if (buffer != null) {
            int size = buffer.capacity();
            if (Integer.bitCount(size) == 1 && !buffer.isReadOnly()) {
               if (buffer.isDirect()) {
                  if (!(buffer instanceof MappedByteBuffer)) {
                     if (size == 8192) {
                        MEDIUM_DIRECT.doFree(buffer);
                     } else if (size == 64) {
                        SMALL_DIRECT.doFree(buffer);
                     } else if (size == 1048576) {
                        LARGE_DIRECT.doFree(buffer);
                     }
                  }
               } else if (size == 8192) {
                  MEDIUM_HEAP.doFree(buffer);
               } else if (size == 64) {
                  SMALL_HEAP.doFree(buffer);
               } else if (size == 1048576) {
                  LARGE_HEAP.doFree(buffer);
               }
            }

            array[offs + i] = null;
         }
      }

   }

   public static void zeroAndFree(ByteBuffer buffer) {
      Buffers.zero(buffer);
      free(buffer);
   }

   public boolean isDirect() {
      return this.direct;
   }

   public int getSize() {
      return this.size;
   }

   public void flushCaches() {
      ((Cache)this.threadLocalCache.get()).flush();
   }

   public static void flushAllCaches() {
      SMALL_HEAP.flushCaches();
      MEDIUM_HEAP.flushCaches();
      LARGE_HEAP.flushCaches();
      SMALL_DIRECT.flushCaches();
      MEDIUM_DIRECT.flushCaches();
      LARGE_DIRECT.flushCaches();
   }

   public <T, U, E extends Exception> void acceptWithCacheEx(int cacheSize, ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
      Assert.checkMinimumParameter("cacheSize", 0, cacheSize);
      Assert.checkNotNullParam("consumer", consumer);
      ThreadLocal<Cache> threadLocalCache = this.threadLocalCache;
      Cache parent = (Cache)threadLocalCache.get();
      if (cacheSize == 0) {
         consumer.accept(param1, param2);
      } else if (cacheSize <= 64) {
         Object cache;
         if (cacheSize == 1) {
            cache = new OneCache(parent);
         } else if (cacheSize == 2) {
            cache = new TwoCache(parent);
         } else {
            cache = new MultiCache(parent, cacheSize);
         }

         threadLocalCache.set(cache);

         try {
            consumer.accept(param1, param2);
         } finally {
            threadLocalCache.set(parent);
            ((Cache)cache).destroy();
         }

      } else {
         Cache cache = new MultiCache(parent, 64);
         threadLocalCache.set(cache);

         try {
            this.acceptWithCacheEx(cacheSize - 64, consumer, param1, param2);
         } finally {
            cache.destroy();
         }

      }
   }

   public <T, E extends Exception> void acceptWithCacheEx(int cacheSize, ExceptionConsumer<T, E> consumer, T param) throws E {
      Assert.checkNotNullParam("consumer", consumer);
      this.acceptWithCacheEx(cacheSize, Functions.exceptionConsumerBiConsumer(), consumer, param);
   }

   public <E extends Exception> void runWithCacheEx(int cacheSize, ExceptionRunnable<E> runnable) throws E {
      Assert.checkNotNullParam("runnable", runnable);
      this.acceptWithCacheEx(cacheSize, Functions.exceptionRunnableConsumer(), runnable);
   }

   public void runWithCache(int cacheSize, Runnable runnable) {
      Assert.checkNotNullParam("runnable", runnable);
      this.acceptWithCacheEx(cacheSize, Runnable::run, runnable);
   }

   public <T, U, R, E extends Exception> R applyWithCacheEx(int cacheSize, ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
      Assert.checkMinimumParameter("cacheSize", 0, cacheSize);
      Assert.checkNotNullParam("function", function);
      ThreadLocal<Cache> threadLocalCache = this.threadLocalCache;
      Cache parent = (Cache)threadLocalCache.get();
      if (cacheSize == 0) {
         return function.apply(param1, param2);
      } else {
         Object var8;
         if (cacheSize <= 64) {
            Object cache;
            if (cacheSize == 1) {
               cache = new OneCache(parent);
            } else if (cacheSize == 2) {
               cache = new TwoCache(parent);
            } else {
               cache = new MultiCache(parent, cacheSize);
            }

            threadLocalCache.set(cache);

            try {
               var8 = function.apply(param1, param2);
            } finally {
               threadLocalCache.set(parent);
               ((Cache)cache).destroy();
            }

            return var8;
         } else {
            Cache cache = new MultiCache(parent, 64);
            threadLocalCache.set(cache);

            try {
               var8 = this.applyWithCacheEx(cacheSize - 64, function, param1, param2);
            } finally {
               cache.destroy();
            }

            return var8;
         }
      }
   }

   public <T, R, E extends Exception> R applyWithCacheEx(int cacheSize, ExceptionFunction<T, R, E> function, T param) throws E {
      return this.applyWithCacheEx(cacheSize, Functions.exceptionFunctionBiFunction(), function, param);
   }

   public <R, E extends Exception> R getWithCacheEx(int cacheSize, ExceptionSupplier<R, E> supplier) throws E {
      return this.applyWithCacheEx(cacheSize, Functions.exceptionSupplierFunction(), supplier);
   }

   Cache getDefaultCache() {
      return this.defaultCache;
   }

   ConcurrentLinkedQueue<ByteBuffer> getMasterQueue() {
      return this.masterQueue;
   }

   private ByteBuffer allocateMaster() {
      ByteBuffer byteBuffer = (ByteBuffer)this.masterQueue.poll();
      if (byteBuffer == null) {
         byteBuffer = this.createBuffer();
      }

      return byteBuffer;
   }

   static ByteBufferPool create(int size, boolean direct) {
      assert Integer.bitCount(size) == 1;

      assert size >= 16;

      assert size <= 1073741824;

      return new ByteBufferPool(size, direct) {
         ByteBuffer createBuffer() {
            return this.isDirect() ? ByteBuffer.allocateDirect(this.getSize()) : ByteBuffer.allocate(this.getSize());
         }
      };
   }

   static ByteBufferPool subPool(final ByteBufferPool parent, int size) {
      assert Integer.bitCount(size) == 1;

      assert Integer.bitCount(parent.getSize()) == 1;

      assert size >= 16;

      assert size < parent.getSize();

      assert parent.getSize() % size == 0;

      return new ByteBufferPool(size, parent.isDirect()) {
         ByteBuffer createBuffer() {
            synchronized(this) {
               ByteBuffer appearing = (ByteBuffer)this.getMasterQueue().poll();
               if (appearing != null) {
                  return appearing;
               } else {
                  ByteBuffer parentBuffer = parent.allocate();
                  int size = this.getSize();

                  ByteBuffer result;
                  for(result = Buffers.slice(parentBuffer, size); parentBuffer.hasRemaining(); super.doFree(Buffers.slice(parentBuffer, size))) {
                     if (size < CACHE_LINE_SIZE) {
                        Buffers.skip(parentBuffer, CACHE_LINE_SIZE - size);
                     }
                  }

                  return result;
               }
            }
         }
      };
   }

   abstract ByteBuffer createBuffer();

   final void freeMaster(ByteBuffer buffer) {
      this.masterQueue.add(buffer);
   }

   final void doFree(ByteBuffer buffer) {
      assert buffer.capacity() == this.size;

      assert buffer.isDirect() == this.direct;

      buffer.clear();
      ((Cache)this.threadLocalCache.get()).free(buffer);
   }

   static {
      MEDIUM_DIRECT = sliceLargeBuffers ? subPool(LARGE_DIRECT, 8192) : create(8192, true);
      SMALL_DIRECT = subPool(MEDIUM_DIRECT, 64);
      LARGE_HEAP = create(1048576, false);
      MEDIUM_HEAP = create(8192, false);
      SMALL_HEAP = create(64, false);
   }

   final class DefaultCache implements Cache {
      public void free(ByteBuffer bb) {
         ByteBufferPool.this.freeMaster(bb);
      }

      public ByteBuffer allocate() {
         return ByteBufferPool.this.allocateMaster();
      }

      public void flushBuffer(ByteBuffer bb) {
         this.free(bb);
      }

      public void destroy() {
      }

      public void flush() {
      }
   }

   static final class MultiCache implements Cache {
      private final Cache parent;
      private final ByteBuffer[] cache;
      private final long mask;
      private long availableBits;

      MultiCache(Cache parent, int size) {
         this.parent = parent;

         assert 0 < size && size <= 64;

         this.cache = new ByteBuffer[size];
         this.mask = this.availableBits = size == 64 ? -1L : (1L << size) - 1L;
      }

      public void free(ByteBuffer bb) {
         long posn = Long.lowestOneBit(~this.availableBits & this.mask);
         if (posn != 0L) {
            int bit = Long.numberOfTrailingZeros(posn);
            this.availableBits |= posn;
            this.cache[bit] = bb;
         } else {
            this.parent.free(bb);
         }

      }

      public void flushBuffer(ByteBuffer bb) {
         this.parent.flushBuffer(bb);
      }

      public ByteBuffer allocate() {
         long posn = Long.lowestOneBit(this.availableBits);
         if (posn != 0L) {
            int bit = Long.numberOfTrailingZeros(posn);
            this.availableBits &= ~posn;

            ByteBuffer var4;
            try {
               var4 = this.cache[bit];
            } finally {
               this.cache[bit] = null;
            }

            return var4;
         } else {
            return this.parent.allocate();
         }
      }

      public void destroy() {
         ByteBuffer[] cache = this.cache;
         Cache parent = this.parent;
         long bits = ~this.availableBits & this.mask;

         try {
            while(bits != 0L) {
               long posn = Long.lowestOneBit(bits);
               int bit = Long.numberOfTrailingZeros(posn);
               parent.free(cache[bit]);
               bits &= ~posn;
               cache[bit] = null;
            }
         } finally {
            this.availableBits = bits;
         }

      }

      public void flush() {
         ByteBuffer[] cache = this.cache;
         Cache parent = this.parent;
         long bits = ~this.availableBits & this.mask;

         try {
            while(bits != 0L) {
               long posn = Long.lowestOneBit(bits);
               int bit = Long.numberOfTrailingZeros(posn);
               this.flushBuffer(cache[bit]);
               bits &= ~posn;
               cache[bit] = null;
            }
         } finally {
            this.availableBits = bits;
         }

         parent.flush();
      }
   }

   static final class TwoCache implements Cache {
      private final Cache parent;
      private ByteBuffer buffer1;
      private ByteBuffer buffer2;

      TwoCache(Cache parent) {
         this.parent = parent;
      }

      public void free(ByteBuffer bb) {
         if (this.buffer1 == null) {
            this.buffer1 = bb;
         } else if (this.buffer2 == null) {
            this.buffer2 = bb;
         } else {
            this.parent.free(bb);
         }

      }

      public void flushBuffer(ByteBuffer bb) {
         this.parent.flushBuffer(bb);
      }

      public ByteBuffer allocate() {
         ByteBuffer var1;
         if (this.buffer1 != null) {
            try {
               var1 = this.buffer1;
            } finally {
               this.buffer1 = null;
            }

            return var1;
         } else if (this.buffer2 != null) {
            try {
               var1 = this.buffer2;
            } finally {
               this.buffer2 = null;
            }

            return var1;
         } else {
            return this.parent.allocate();
         }
      }

      public void destroy() {
         Cache parent = this.parent;
         ByteBuffer buffer1 = this.buffer1;
         if (buffer1 != null) {
            parent.free(buffer1);
            this.buffer1 = null;
         }

         ByteBuffer buffer2 = this.buffer2;
         if (buffer2 != null) {
            parent.free(buffer2);
            this.buffer2 = null;
         }

      }

      public void flush() {
         ByteBuffer buffer1 = this.buffer1;
         if (buffer1 != null) {
            this.flushBuffer(buffer1);
            this.buffer1 = null;
         }

         ByteBuffer buffer2 = this.buffer2;
         if (buffer2 != null) {
            this.flushBuffer(buffer2);
            this.buffer2 = null;
         }

         this.parent.flush();
      }
   }

   static final class OneCache implements Cache {
      private final Cache parent;
      private ByteBuffer buffer;

      OneCache(Cache parent) {
         this.parent = parent;
      }

      public void free(ByteBuffer bb) {
         if (this.buffer == null) {
            this.buffer = bb;
         } else {
            this.parent.free(bb);
         }

      }

      public void flushBuffer(ByteBuffer bb) {
         this.parent.flushBuffer(bb);
      }

      public ByteBuffer allocate() {
         if (this.buffer != null) {
            ByteBuffer var1;
            try {
               var1 = this.buffer;
            } finally {
               this.buffer = null;
            }

            return var1;
         } else {
            return this.parent.allocate();
         }
      }

      public void destroy() {
         ByteBuffer buffer = this.buffer;
         if (buffer != null) {
            this.buffer = null;
            this.parent.free(buffer);
         }

      }

      public void flush() {
         ByteBuffer buffer = this.buffer;
         if (buffer != null) {
            this.buffer = null;
            this.flushBuffer(buffer);
         }

         this.parent.flush();
      }
   }

   interface Cache {
      void free(ByteBuffer var1);

      void flushBuffer(ByteBuffer var1);

      ByteBuffer allocate();

      void destroy();

      void flush();
   }

   public static final class Set {
      private final ByteBufferPool small;
      private final ByteBufferPool normal;
      private final ByteBufferPool large;
      public static final Set DIRECT;
      public static final Set HEAP;

      Set(ByteBufferPool small, ByteBufferPool normal, ByteBufferPool large) {
         this.small = small;
         this.normal = normal;
         this.large = large;
      }

      public ByteBufferPool getSmall() {
         return this.small;
      }

      public ByteBufferPool getNormal() {
         return this.normal;
      }

      public ByteBufferPool getLarge() {
         return this.large;
      }

      static {
         DIRECT = new Set(ByteBufferPool.SMALL_DIRECT, ByteBufferPool.MEDIUM_DIRECT, ByteBufferPool.LARGE_DIRECT);
         HEAP = new Set(ByteBufferPool.SMALL_HEAP, ByteBufferPool.MEDIUM_HEAP, ByteBufferPool.LARGE_HEAP);
      }
   }
}
