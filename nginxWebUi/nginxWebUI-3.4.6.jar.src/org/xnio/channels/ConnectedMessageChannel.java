package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedMessageChannel extends MessageChannel, ConnectedChannel {
  ChannelListener.Setter<? extends ConnectedMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends ConnectedMessageChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends ConnectedMessageChannel> getWriteSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ConnectedMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */