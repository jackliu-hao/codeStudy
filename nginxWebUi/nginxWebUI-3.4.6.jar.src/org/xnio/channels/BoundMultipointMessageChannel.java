package org.xnio.channels;

import org.xnio.ChannelListener;

public interface BoundMultipointMessageChannel extends MultipointMessageChannel, BoundChannel {
  ChannelListener.Setter<? extends BoundMultipointMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends BoundMultipointMessageChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends BoundMultipointMessageChannel> getWriteSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\BoundMultipointMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */