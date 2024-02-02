package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public class BytesReceivedStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final ByteActivityCallback callback;

   public BytesReceivedStreamSourceConduit(StreamSourceConduit next, ByteActivityCallback callback) {
      super(next);
      this.callback = callback;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      long l = super.transferTo(position, count, target);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      long l = super.transferTo(count, throughBuffer, target);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }

   public int read(ByteBuffer dst) throws IOException {
      int i = super.read(dst);
      if (i > 0) {
         this.callback.activity((long)i);
      }

      return i;
   }

   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
      long l = super.read(dsts, offs, len);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }
}
