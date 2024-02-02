package io.undertow.server.handlers.cache;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import org.xnio.BufferAllocator;

public final class LimitedBufferSlicePool {
   private static final AtomicIntegerFieldUpdater regionUpdater = AtomicIntegerFieldUpdater.newUpdater(LimitedBufferSlicePool.class, "regionsUsed");
   private final Queue<Slice> sliceQueue;
   private final BufferAllocator<ByteBuffer> allocator;
   private final int bufferSize;
   private final int buffersPerRegion;
   private final int maxRegions;
   private volatile int regionsUsed;

   public LimitedBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize, int maxRegions) {
      this.sliceQueue = new ConcurrentLinkedQueue();
      if (bufferSize <= 0) {
         throw new IllegalArgumentException("Buffer size must be greater than zero");
      } else if (maxRegionSize < bufferSize) {
         throw new IllegalArgumentException("Maximum region size must be greater than or equal to the buffer size");
      } else {
         this.buffersPerRegion = maxRegionSize / bufferSize;
         this.bufferSize = bufferSize;
         this.allocator = allocator;
         this.maxRegions = maxRegions;
      }
   }

   public LimitedBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize) {
      this(allocator, bufferSize, maxRegionSize, 0);
   }

   public LimitedBufferSlicePool(int bufferSize, int maxRegionSize) {
      this(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, bufferSize, maxRegionSize);
   }

   public PooledByteBuffer allocate() {
      Queue<Slice> sliceQueue = this.sliceQueue;
      Slice slice = (Slice)sliceQueue.poll();
      if (slice != null || this.maxRegions > 0 && regionUpdater.getAndIncrement(this) >= this.maxRegions) {
         return slice == null ? null : new PooledByteBuffer(slice, slice.slice(), sliceQueue);
      } else {
         int bufferSize = this.bufferSize;
         int buffersPerRegion = this.buffersPerRegion;
         ByteBuffer region = (ByteBuffer)this.allocator.allocate(buffersPerRegion * bufferSize);
         int idx = bufferSize;

         for(int i = 1; i < buffersPerRegion; ++i) {
            sliceQueue.add(new Slice(region, idx, bufferSize));
            idx += bufferSize;
         }

         Slice newSlice = new Slice(region, 0, bufferSize);
         return new PooledByteBuffer(newSlice, newSlice.slice(), sliceQueue);
      }
   }

   public boolean canAllocate(int slices) {
      if (this.regionsUsed < this.maxRegions) {
         return true;
      } else if (this.sliceQueue.isEmpty()) {
         return false;
      } else {
         Iterator iterator = this.sliceQueue.iterator();

         for(int i = 0; i < slices; ++i) {
            if (!iterator.hasNext()) {
               return false;
            }

            try {
               iterator.next();
            } catch (NoSuchElementException var5) {
               return false;
            }
         }

         return true;
      }
   }

   private static final class Slice {
      private final ByteBuffer parent;
      private final int start;
      private final int size;

      private Slice(ByteBuffer parent, int start, int size) {
         this.parent = parent;
         this.start = start;
         this.size = size;
      }

      ByteBuffer slice() {
         return ((ByteBuffer)this.parent.duplicate().position(this.start).limit(this.start + this.size)).slice();
      }

      // $FF: synthetic method
      Slice(ByteBuffer x0, int x1, int x2, Object x3) {
         this(x0, x1, x2);
      }
   }

   public static final class PooledByteBuffer {
      private final Slice region;
      private final Queue<Slice> slices;
      volatile ByteBuffer buffer;
      private static final AtomicReferenceFieldUpdater<PooledByteBuffer, ByteBuffer> bufferUpdater = AtomicReferenceFieldUpdater.newUpdater(PooledByteBuffer.class, ByteBuffer.class, "buffer");

      private PooledByteBuffer(Slice region, ByteBuffer buffer, Queue<Slice> slices) {
         this.region = region;
         this.buffer = buffer;
         this.slices = slices;
      }

      public void free() {
         if (bufferUpdater.getAndSet(this, (Object)null) != null) {
            this.slices.add(this.region);
         }

      }

      public ByteBuffer getBuffer() {
         ByteBuffer buffer = this.buffer;
         if (buffer == null) {
            throw new IllegalStateException();
         } else {
            return buffer;
         }
      }

      public String toString() {
         return "Pooled buffer " + this.buffer;
      }

      // $FF: synthetic method
      PooledByteBuffer(Slice x0, ByteBuffer x1, Queue x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
