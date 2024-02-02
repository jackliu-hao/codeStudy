package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface ReadableMultipointMessageChannel extends SuspendableReadChannel {
   int receiveFrom(SocketAddressBuffer var1, ByteBuffer var2) throws IOException;

   long receiveFrom(SocketAddressBuffer var1, ByteBuffer[] var2) throws IOException;

   long receiveFrom(SocketAddressBuffer var1, ByteBuffer[] var2, int var3, int var4) throws IOException;

   ChannelListener.Setter<? extends ReadableMultipointMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends ReadableMultipointMessageChannel> getCloseSetter();
}
