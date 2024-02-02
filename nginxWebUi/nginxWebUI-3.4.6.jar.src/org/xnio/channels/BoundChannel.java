package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public interface BoundChannel extends CloseableChannel {
  SocketAddress getLocalAddress();
  
  <A extends SocketAddress> A getLocalAddress(Class<A> paramClass);
  
  ChannelListener.Setter<? extends BoundChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\BoundChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */