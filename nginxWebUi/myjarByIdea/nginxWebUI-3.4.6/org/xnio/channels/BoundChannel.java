package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public interface BoundChannel extends CloseableChannel {
   SocketAddress getLocalAddress();

   <A extends SocketAddress> A getLocalAddress(Class<A> var1);

   ChannelListener.Setter<? extends BoundChannel> getCloseSetter();
}
