package org.xnio.channels;

import java.io.IOException;
import java.nio.ByteBuffer;
import org.xnio.ChannelListener;

public interface ReadableMessageChannel extends SuspendableReadChannel, Configurable {
   int receive(ByteBuffer var1) throws IOException;

   long receive(ByteBuffer[] var1) throws IOException;

   long receive(ByteBuffer[] var1, int var2, int var3) throws IOException;

   ChannelListener.Setter<? extends ReadableMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends ReadableMessageChannel> getCloseSetter();
}
