package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;

public final class ConduitWritableByteChannel implements WritableByteChannel {
   private StreamSinkConduit conduit;

   public ConduitWritableByteChannel(StreamSinkConduit conduit) {
      this.conduit = conduit;
   }

   public int write(ByteBuffer src) throws IOException {
      return this.conduit.write(src);
   }

   public boolean isOpen() {
      return !this.conduit.isWriteShutdown();
   }

   public void close() throws IOException {
      this.conduit.truncateWrites();
   }
}
