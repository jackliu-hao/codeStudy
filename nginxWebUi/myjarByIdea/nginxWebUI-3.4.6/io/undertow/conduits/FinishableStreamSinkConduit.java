package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.Buffers;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

public final class FinishableStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final ConduitListener<? super FinishableStreamSinkConduit> finishListener;
   private int shutdownState = 0;

   public FinishableStreamSinkConduit(StreamSinkConduit delegate, ConduitListener<? super FinishableStreamSinkConduit> finishListener) {
      super(delegate);
      this.finishListener = finishListener;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      int res = ((StreamSinkConduit)this.next).writeFinal(src);
      if (!src.hasRemaining() && this.shutdownState == 0) {
         this.shutdownState = 1;
      }

      return res;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      long res = ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
      if (!Buffers.hasRemaining(srcs, offset, length) && this.shutdownState == 0) {
         this.shutdownState = 1;
      }

      return res;
   }

   public void terminateWrites() throws IOException {
      super.terminateWrites();
      if (this.shutdownState == 0) {
         this.shutdownState = 1;
      }

   }

   public void truncateWrites() throws IOException {
      ((StreamSinkConduit)this.next).truncateWrites();
      if (this.shutdownState != 2) {
         this.shutdownState = 2;
         this.finishListener.handleEvent(this);
      }

   }

   public boolean flush() throws IOException {
      boolean val = ((StreamSinkConduit)this.next).flush();
      if (val && this.shutdownState == 1) {
         this.shutdownState = 2;
         this.finishListener.handleEvent(this);
      }

      return val;
   }
}
