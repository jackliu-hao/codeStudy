package org.xnio.channels;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface WritableMultipointMessageChannel extends SuspendableWriteChannel {
   boolean sendTo(SocketAddress var1, ByteBuffer var2) throws IOException;

   boolean sendTo(SocketAddress var1, ByteBuffer[] var2) throws IOException;

   boolean sendTo(SocketAddress var1, ByteBuffer[] var2, int var3, int var4) throws IOException;

   ChannelListener.Setter<? extends WritableMultipointMessageChannel> getWriteSetter();

   ChannelListener.Setter<? extends WritableMultipointMessageChannel> getCloseSetter();
}
