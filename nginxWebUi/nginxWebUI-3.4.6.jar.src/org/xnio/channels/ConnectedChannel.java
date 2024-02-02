package org.xnio.channels;

import java.net.SocketAddress;
import org.xnio.ChannelListener;

public interface ConnectedChannel extends BoundChannel {
  SocketAddress getPeerAddress();
  
  <A extends SocketAddress> A getPeerAddress(Class<A> paramClass);
  
  ChannelListener.Setter<? extends ConnectedChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ConnectedChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */