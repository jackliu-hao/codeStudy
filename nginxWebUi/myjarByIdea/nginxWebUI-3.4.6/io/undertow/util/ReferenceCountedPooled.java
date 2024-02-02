package io.undertow.util;

import io.undertow.UndertowMessages;
import io.undertow.connector.PooledByteBuffer;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

public class ReferenceCountedPooled implements PooledByteBuffer {
   private final PooledByteBuffer underlying;
   private volatile int referenceCount;
   boolean mainFreed;
   private ByteBuffer slice;
   private final FreeNotifier freeNotifier;
   private static final AtomicIntegerFieldUpdater<ReferenceCountedPooled> referenceCountUpdater = AtomicIntegerFieldUpdater.newUpdater(ReferenceCountedPooled.class, "referenceCount");

   public ReferenceCountedPooled(PooledByteBuffer underlying, int referenceCount) {
      this(underlying, referenceCount, (FreeNotifier)null);
   }

   public ReferenceCountedPooled(PooledByteBuffer underlying, int referenceCount, FreeNotifier freeNotifier) {
      this.mainFreed = false;
      this.slice = null;
      this.underlying = underlying;
      this.referenceCount = referenceCount;
      this.freeNotifier = freeNotifier;
   }

   public void close() {
      if (!this.mainFreed) {
         this.mainFreed = true;
         this.freeInternal();
      }
   }

   public boolean isOpen() {
      return !this.mainFreed;
   }

   public boolean isFreed() {
      return this.mainFreed;
   }

   public boolean tryUnfree() {
      int refs;
      do {
         refs = referenceCountUpdater.get(this);
         if (refs <= 0) {
            return false;
         }
      } while(!referenceCountUpdater.compareAndSet(this, refs, refs + 1));

      ByteBuffer resource = this.slice != null ? this.slice : this.underlying.getBuffer();
      resource.position(resource.limit());
      resource.limit(resource.capacity());
      this.slice = resource.slice();
      this.mainFreed = false;
      return true;
   }

   private void freeInternal() {
      if (referenceCountUpdater.decrementAndGet(this) == 0) {
         this.underlying.close();
         if (this.freeNotifier != null) {
            this.freeNotifier.freed();
         }
      }

   }

   public ByteBuffer getBuffer() throws IllegalStateException {
      if (this.mainFreed) {
         throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
      } else {
         return this.slice != null ? this.slice : this.underlying.getBuffer();
      }
   }

   public PooledByteBuffer createView(int viewSize) {
      ByteBuffer newView = this.getBuffer().duplicate();
      newView.limit(newView.position() + viewSize);
      final ByteBuffer newValue = newView.slice();
      ByteBuffer newUnderlying = this.getBuffer().duplicate();
      newUnderlying.position(newUnderlying.position() + viewSize);
      int oldRemaining = newUnderlying.remaining();
      newUnderlying.limit(newUnderlying.capacity());
      newUnderlying = newUnderlying.slice();
      newUnderlying.limit(newUnderlying.position() + oldRemaining);
      this.slice = newUnderlying;
      this.increaseReferenceCount();
      return new PooledByteBuffer() {
         boolean free = false;

         public void close() {
            if (!this.free) {
               this.free = true;
               ReferenceCountedPooled.this.freeInternal();
            }

         }

         public boolean isOpen() {
            return !this.free;
         }

         public ByteBuffer getBuffer() throws IllegalStateException {
            if (this.free) {
               throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
            } else {
               return newValue;
            }
         }

         public String toString() {
            return "ReferenceCountedPooled$view{buffer=" + newValue + "free=" + this.free + "underlying=" + ReferenceCountedPooled.this.underlying + ", referenceCount=" + ReferenceCountedPooled.this.referenceCount + ", mainFreed=" + ReferenceCountedPooled.this.mainFreed + ", slice=" + ReferenceCountedPooled.this.slice + '}';
         }
      };
   }

   public PooledByteBuffer createView() {
      return this.createView(this.getBuffer().remaining());
   }

   public void increaseReferenceCount() {
      int val;
      do {
         val = referenceCountUpdater.get(this);
         if (val == 0) {
            throw UndertowMessages.MESSAGES.objectWasFreed();
         }
      } while(!referenceCountUpdater.compareAndSet(this, val, val + 1));

   }

   public String toString() {
      return "ReferenceCountedPooled{underlying=" + this.underlying + ", referenceCount=" + this.referenceCount + ", mainFreed=" + this.mainFreed + ", slice=" + this.slice + '}';
   }

   public interface FreeNotifier {
      void freed();
   }
}
