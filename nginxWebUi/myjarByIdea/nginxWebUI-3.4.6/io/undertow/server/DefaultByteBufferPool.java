package io.undertow.server;

import io.undertow.UndertowMessages;
import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class DefaultByteBufferPool implements ByteBufferPool {
   private final ThreadLocal<ThreadLocalData> threadLocalCache;
   private final List<WeakReference<ThreadLocalData>> threadLocalDataList;
   private final ConcurrentLinkedQueue<ByteBuffer> queue;
   private final boolean direct;
   private final int bufferSize;
   private final int maximumPoolSize;
   private final int threadLocalCacheSize;
   private final int leakDectionPercent;
   private int count;
   private volatile int currentQueueLength;
   private static final AtomicIntegerFieldUpdater<DefaultByteBufferPool> currentQueueLengthUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultByteBufferPool.class, "currentQueueLength");
   private volatile int reclaimedThreadLocals;
   private static final AtomicIntegerFieldUpdater<DefaultByteBufferPool> reclaimedThreadLocalsUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultByteBufferPool.class, "reclaimedThreadLocals");
   private volatile boolean closed;
   private final DefaultByteBufferPool arrayBackedPool;

   public DefaultByteBufferPool(boolean direct, int bufferSize) {
      this(direct, bufferSize, -1, 12, 0);
   }

   public DefaultByteBufferPool(boolean direct, int bufferSize, int maximumPoolSize, int threadLocalCacheSize, int leakDecetionPercent) {
      this.threadLocalCache = new ThreadLocal();
      this.threadLocalDataList = new ArrayList();
      this.queue = new ConcurrentLinkedQueue();
      this.currentQueueLength = 0;
      this.reclaimedThreadLocals = 0;
      this.direct = direct;
      this.bufferSize = bufferSize;
      this.maximumPoolSize = maximumPoolSize;
      this.threadLocalCacheSize = threadLocalCacheSize;
      this.leakDectionPercent = leakDecetionPercent;
      if (direct) {
         this.arrayBackedPool = new DefaultByteBufferPool(false, bufferSize, maximumPoolSize, 0, leakDecetionPercent);
      } else {
         this.arrayBackedPool = this;
      }

   }

   public DefaultByteBufferPool(boolean direct, int bufferSize, int maximumPoolSize, int threadLocalCacheSize) {
      this(direct, bufferSize, maximumPoolSize, threadLocalCacheSize, 0);
   }

   public int getBufferSize() {
      return this.bufferSize;
   }

   public boolean isDirect() {
      return this.direct;
   }

   public PooledByteBuffer allocate() {
      if (this.closed) {
         throw UndertowMessages.MESSAGES.poolIsClosed();
      } else {
         ByteBuffer buffer = null;
         ThreadLocalData local = null;
         if (this.threadLocalCacheSize > 0) {
            local = (ThreadLocalData)this.threadLocalCache.get();
            if (local != null) {
               buffer = (ByteBuffer)local.buffers.poll();
            } else {
               local = new ThreadLocalData();
               synchronized(this.threadLocalDataList) {
                  if (this.closed) {
                     throw UndertowMessages.MESSAGES.poolIsClosed();
                  }

                  this.cleanupThreadLocalData();
                  this.threadLocalDataList.add(new WeakReference(local));
                  this.threadLocalCache.set(local);
               }
            }
         }

         if (buffer == null) {
            buffer = (ByteBuffer)this.queue.poll();
            if (buffer != null) {
               currentQueueLengthUpdater.decrementAndGet(this);
            }
         }

         if (buffer == null) {
            if (this.direct) {
               buffer = ByteBuffer.allocateDirect(this.bufferSize);
            } else {
               buffer = ByteBuffer.allocate(this.bufferSize);
            }
         }

         if (local != null && local.allocationDepth < this.threadLocalCacheSize) {
            ++local.allocationDepth;
         }

         buffer.clear();
         return new DefaultPooledBuffer(this, buffer, this.leakDectionPercent == 0 ? false : ++this.count % 100 < this.leakDectionPercent);
      }
   }

   public ByteBufferPool getArrayBackedPool() {
      return this.arrayBackedPool;
   }

   private void cleanupThreadLocalData() {
      int size = this.threadLocalDataList.size();
      if (this.reclaimedThreadLocals > size / 4) {
         int j = 0;

         int i;
         for(i = 0; i < size; ++i) {
            WeakReference<ThreadLocalData> ref = (WeakReference)this.threadLocalDataList.get(i);
            if (ref.get() != null) {
               this.threadLocalDataList.set(j++, ref);
            }
         }

         for(i = size - 1; i >= j; --i) {
            this.threadLocalDataList.remove(i);
         }

         reclaimedThreadLocalsUpdater.addAndGet(this, -1 * (size - j));
      }

   }

   private void freeInternal(ByteBuffer buffer) {
      if (this.closed) {
         DirectByteBufferDeallocator.free(buffer);
      } else {
         ThreadLocalData local = (ThreadLocalData)this.threadLocalCache.get();
         if (local != null && local.allocationDepth > 0) {
            --local.allocationDepth;
            if (local.buffers.size() < this.threadLocalCacheSize) {
               local.buffers.add(buffer);
               return;
            }
         }

         this.queueIfUnderMax(buffer);
      }
   }

   private void queueIfUnderMax(ByteBuffer buffer) {
      int size;
      do {
         size = this.currentQueueLength;
         if (size > this.maximumPoolSize) {
            DirectByteBufferDeallocator.free(buffer);
            return;
         }
      } while(!currentQueueLengthUpdater.compareAndSet(this, size, size + 1));

      this.queue.add(buffer);
   }

   public void close() {
      if (!this.closed) {
         this.closed = true;
         this.queue.clear();
         synchronized(this.threadLocalDataList) {
            WeakReference ref;
            for(Iterator var2 = this.threadLocalDataList.iterator(); var2.hasNext(); ref.clear()) {
               ref = (WeakReference)var2.next();
               ThreadLocalData local = (ThreadLocalData)ref.get();
               if (local != null) {
                  local.buffers.clear();
               }
            }

            this.threadLocalDataList.clear();
         }
      }
   }

   protected void finalize() throws Throwable {
      super.finalize();
      this.close();
   }

   private static class LeakDetector {
      volatile boolean closed;
      private final Throwable allocationPoint;

      private LeakDetector() {
         this.closed = false;
         this.allocationPoint = new Throwable("Buffer leak detected");
      }

      protected void finalize() throws Throwable {
         super.finalize();
         if (!this.closed) {
            this.allocationPoint.printStackTrace();
         }

      }

      // $FF: synthetic method
      LeakDetector(Object x0) {
         this();
      }
   }

   private class ThreadLocalData {
      ArrayDeque<ByteBuffer> buffers;
      int allocationDepth;

      private ThreadLocalData() {
         this.buffers = new ArrayDeque(DefaultByteBufferPool.this.threadLocalCacheSize);
         this.allocationDepth = 0;
      }

      protected void finalize() throws Throwable {
         super.finalize();
         DefaultByteBufferPool.reclaimedThreadLocalsUpdater.incrementAndGet(DefaultByteBufferPool.this);
         ByteBuffer buffer;
         if (this.buffers != null) {
            while((buffer = (ByteBuffer)this.buffers.poll()) != null) {
               DefaultByteBufferPool.this.queueIfUnderMax(buffer);
            }
         }

      }

      // $FF: synthetic method
      ThreadLocalData(Object x1) {
         this();
      }
   }

   private static class DefaultPooledBuffer implements PooledByteBuffer {
      private final DefaultByteBufferPool pool;
      private final LeakDetector leakDetector;
      private ByteBuffer buffer;
      private volatile int referenceCount = 1;
      private static final AtomicIntegerFieldUpdater<DefaultPooledBuffer> referenceCountUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultPooledBuffer.class, "referenceCount");

      DefaultPooledBuffer(DefaultByteBufferPool pool, ByteBuffer buffer, boolean detectLeaks) {
         this.pool = pool;
         this.buffer = buffer;
         this.leakDetector = detectLeaks ? new LeakDetector() : null;
      }

      public ByteBuffer getBuffer() {
         if (this.referenceCount == 0) {
            throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
         } else {
            return this.buffer;
         }
      }

      public void close() {
         if (referenceCountUpdater.compareAndSet(this, 1, 0)) {
            if (this.leakDetector != null) {
               this.leakDetector.closed = true;
            }

            this.pool.freeInternal(this.buffer);
            this.buffer = null;
         }

      }

      public boolean isOpen() {
         return this.referenceCount > 0;
      }

      public String toString() {
         return "DefaultPooledBuffer{buffer=" + this.buffer + ", referenceCount=" + this.referenceCount + '}';
      }
   }
}
