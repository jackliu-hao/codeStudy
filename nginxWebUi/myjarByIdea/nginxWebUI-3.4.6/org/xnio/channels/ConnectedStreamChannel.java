package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedStreamChannel extends StreamChannel, ConnectedChannel {
   ChannelListener.Setter<? extends ConnectedStreamChannel> getReadSetter();

   ChannelListener.Setter<? extends ConnectedStreamChannel> getWriteSetter();

   ChannelListener.Setter<? extends ConnectedStreamChannel> getCloseSetter();
}
