package io.undertow.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import org.xnio.channels.StreamSinkChannel;
import org.xnio.conduits.AbstractStreamSourceConduit;
import org.xnio.conduits.StreamSourceConduit;

public class BrokenStreamSourceConduit extends AbstractStreamSourceConduit<StreamSourceConduit> {
   private final IOException exception;

   public BrokenStreamSourceConduit(StreamSourceConduit next, IOException exception) {
      super(next);
      this.exception = exception;
   }

   public long transferTo(long position, long count, FileChannel target) throws IOException {
      throw this.exception;
   }

   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
      throw this.exception;
   }

   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
      throw this.exception;
   }

   public int read(ByteBuffer dst) throws IOException {
      throw this.exception;
   }
}
