package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedMessageChannel extends MessageChannel, ConnectedChannel {
   ChannelListener.Setter<? extends ConnectedMessageChannel> getReadSetter();

   ChannelListener.Setter<? extends ConnectedMessageChannel> getCloseSetter();

   ChannelListener.Setter<? extends ConnectedMessageChannel> getWriteSetter();
}
