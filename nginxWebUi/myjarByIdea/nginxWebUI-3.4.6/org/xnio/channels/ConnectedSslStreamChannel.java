package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedSslStreamChannel extends ConnectedStreamChannel, SslChannel {
   ChannelListener.Setter<? extends ConnectedSslStreamChannel> getReadSetter();

   ChannelListener.Setter<? extends ConnectedSslStreamChannel> getWriteSetter();

   ChannelListener.Setter<? extends ConnectedSslStreamChannel> getCloseSetter();
}
