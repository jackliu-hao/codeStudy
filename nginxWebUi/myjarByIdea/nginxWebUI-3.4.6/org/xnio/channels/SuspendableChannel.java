package org.xnio.channels;

import org.xnio.ChannelListener;

public interface SuspendableChannel extends CloseableChannel, SuspendableReadChannel, SuspendableWriteChannel {
   ChannelListener.Setter<? extends SuspendableChannel> getCloseSetter();

   ChannelListener.Setter<? extends SuspendableChannel> getReadSetter();

   ChannelListener.Setter<? extends SuspendableChannel> getWriteSetter();
}
