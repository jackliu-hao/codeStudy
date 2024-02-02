package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSourceChannel;
import org.xnio.conduits.AbstractStreamSinkConduit;
import org.xnio.conduits.StreamSinkConduit;

public class BytesSentStreamSinkConduit extends AbstractStreamSinkConduit<StreamSinkConduit> {
   private final ByteActivityCallback callback;

   public BytesSentStreamSinkConduit(StreamSinkConduit next, ByteActivityCallback callback) {
      super(next);
      this.callback = callback;
   }

   public long transferFrom(FileChannel src, long position, long count) throws IOException {
      long l = ((StreamSinkConduit)this.next).transferFrom(src, position, count);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }

   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
      long l = ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }

   public int write(ByteBuffer src) throws IOException {
      int i = ((StreamSinkConduit)this.next).write(src);
      if (i > 0) {
         this.callback.activity((long)i);
      }

      return i;
   }

   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
      long l = ((StreamSinkConduit)this.next).write(srcs, offs, len);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }

   public int writeFinal(ByteBuffer src) throws IOException {
      int i = ((StreamSinkConduit)this.next).writeFinal(src);
      if (i > 0) {
         this.callback.activity((long)i);
      }

      return i;
   }

   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
      long l = ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
      if (l > 0L) {
         this.callback.activity(l);
      }

      return l;
   }
}
