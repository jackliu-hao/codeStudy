package org.xnio.channels;

import org.xnio.ChannelListener;

public interface BoundMultipointMessageChannel extends MultipointMessageChannel, BoundChannel {
   ChannelListener.Setter<? extends BoundMultipointMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends BoundMultipointMessageChannel> getCloseSetter();

   ChannelListener.Setter<? extends BoundMultipointMessageChannel> getWriteSetter();
}
