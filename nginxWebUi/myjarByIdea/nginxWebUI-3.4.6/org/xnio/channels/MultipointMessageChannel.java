package org.xnio.channels;

import org.xnio.ChannelListener;

public interface MultipointMessageChannel extends ReadableMultipointMessageChannel, WritableMultipointMessageChannel, SuspendableChannel {
   ChannelListener.Setter<? extends MultipointMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends MultipointMessageChannel> getCloseSetter();

   ChannelListener.Setter<? extends MultipointMessageChannel> getWriteSetter();
}
