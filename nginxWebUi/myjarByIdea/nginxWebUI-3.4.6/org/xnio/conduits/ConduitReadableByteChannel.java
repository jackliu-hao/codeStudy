package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

public final class ConduitReadableByteChannel implements ReadableByteChannel {
   private StreamSourceConduit conduit;

   public ConduitReadableByteChannel(StreamSourceConduit conduit) {
      this.conduit = conduit;
   }

   public int read(ByteBuffer dst) throws IOException {
      return this.conduit.read(dst);
   }

   public boolean isOpen() {
      return !this.conduit.isReadShutdown();
   }

   public void close() throws IOException {
      this.conduit.terminateReads();
   }
}
