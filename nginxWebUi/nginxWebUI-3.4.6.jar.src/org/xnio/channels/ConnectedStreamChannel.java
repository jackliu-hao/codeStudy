package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedStreamChannel extends StreamChannel, ConnectedChannel {
  ChannelListener.Setter<? extends ConnectedStreamChannel> getReadSetter();
  
  ChannelListener.Setter<? extends ConnectedStreamChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends ConnectedStreamChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ConnectedStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */