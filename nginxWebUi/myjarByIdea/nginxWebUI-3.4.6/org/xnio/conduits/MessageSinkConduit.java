package org.xnio.conduits;

import java.io.IOException;
import java.nio.ByteBuffer;

public interface MessageSinkConduit extends SinkConduit {
   boolean send(ByteBuffer var1) throws IOException;

   boolean send(ByteBuffer[] var1, int var2, int var3) throws IOException;

   boolean sendFinal(ByteBuffer var1) throws IOException;

   boolean sendFinal(ByteBuffer[] var1, int var2, int var3) throws IOException;
}
