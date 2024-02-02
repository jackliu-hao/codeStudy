package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.FileChannel;
import org.xnio.Bits;
import org.xnio.IoUtils;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.ConduitWritableByteChannel;
import org.xnio.conduits.Conduits;
import org.xnio.conduits.StreamSinkConduit;

public final class HeadStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final ConduitListener<? super HeadStreamSinkConduit> finishListener;
   private int state;
   private final boolean shutdownDelegate;
   private static final int FLAG_CLOSE_REQUESTED = 1;
   private static final int FLAG_CLOSE_COMPLETE = 2;
   private static final int FLAG_FINISHED_CALLED = 4;

   public HeadStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super HeadStreamSinkConduit> finishListener) {
      this(next, finishListener, false);
   }

   public HeadStreamSinkConduit(StreamSinkConduit next, ConduitListener<? super HeadStreamSinkConduit> finishListener, boolean shutdownDelegate) {
      super(next);
      this.finishListener = finishListener;
      this.shutdownDelegate = shutdownDelegate;
   }

   public int write(ByteBuffer src) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw new ClosedChannelException();
      } else {
         int remaining = src.remaining();
         src.position(src.position() + remaining);
         return remaining;
      }
   }

   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw new ClosedChannelException();
      } else {
         long total = 0L;

         for(int i = offset; i < offset + length; ++i) {
            ByteBuffer src = srcs[i];
            int remaining = src.remaining();
            total += (long)remaining;
            src.position(src.position() + remaining);
         }

         return total;
      }
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      return Conduits.writeFinalBasic(this, src);
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      return Conduits.writeFinalBasic(this, srcs, offset, length);
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw new ClosedChannelException();
      } else {
         return src.transferTo(position, count, new ConduitWritableByteChannel(this));
      }
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      if (Bits.anyAreSet(this.state, 2)) {
         throw new ClosedChannelException();
      } else {
         return IoUtils.transfer(source, count, throughBuffer, new ConduitWritableByteChannel(this));
      }
   }

   public boolean flush() throws IOException {
      int val = this.state;
      if (Bits.anyAreSet(val, 2)) {
         return true;
      } else {
         boolean flushed = false;

         boolean var3;
         try {
            var3 = flushed = ((StreamSinkConduit)this.next).flush();
         } finally {
            this.exitFlush(val, flushed);
         }

         return var3;
      }
   }

   public void suspendWrites() {
      long val = (long)this.state;
      if (!Bits.anyAreSet(val, 2L)) {
         ((StreamSinkConduit)this.next).suspendWrites();
      }
   }

   public void resumeWrites() {
      long val = (long)this.state;
      if (!Bits.anyAreSet(val, 2L)) {
         ((StreamSinkConduit)this.next).resumeWrites();
      }
   }

   public boolean isWriteResumed() {
      return Bits.allAreClear(this.state, 2) && ((StreamSinkConduit)this.next).isWriteResumed();
   }

   public void wakeupWrites() {
      long val = (long)this.state;
      if (!Bits.anyAreSet(val, 2L)) {
         ((StreamSinkConduit)this.next).wakeupWrites();
      }
   }

   public void terminateWrites() throws IOException {
      int oldVal = this.state;
      if (!Bits.anyAreSet(oldVal, 3)) {
         int newVal = oldVal | 1;
         this.state = newVal;
         if (this.shutdownDelegate) {
            ((StreamSinkConduit)this.next).terminateWrites();
         }

      }
   }

   private void exitFlush(int oldVal, boolean flushed) {
      boolean callFinish = false;
      if (Bits.anyAreSet(oldVal, 1) && flushed) {
         int newVal = oldVal | 2;
         if (!Bits.anyAreSet(oldVal, 4)) {
            newVal |= 4;
            callFinish = true;
         }

         this.state = newVal;
         if (callFinish && this.finishListener != null) {
            this.finishListener.handleEvent(this);
         }
      }

   }
}
