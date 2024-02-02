package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public interface ConnectedChannel extends BoundChannel {
   SocketAddress getPeerAddress();

   <A extends SocketAddress> A getPeerAddress(Class<A> var1);

   ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter();
}
