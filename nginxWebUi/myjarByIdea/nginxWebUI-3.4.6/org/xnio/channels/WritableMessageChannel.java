package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface WritableMessageChannel extends SuspendableWriteChannel, Configurable {
   boolean send(ByteBuffer var1) throws IOException;

   boolean send(ByteBuffer[] var1) throws IOException;

   boolean send(ByteBuffer[] var1, int var2, int var3) throws IOException;

   boolean sendFinal(ByteBuffer var1) throws IOException;

   boolean sendFinal(ByteBuffer[] var1) throws IOException;

   boolean sendFinal(ByteBuffer[] var1, int var2, int var3) throws IOException;

   ChannelListener.Setter<? extends WritableMessageChannel> getWriteSetter();

   ChannelListener.Setter<? extends WritableMessageChannel> getCloseSetter();
}
