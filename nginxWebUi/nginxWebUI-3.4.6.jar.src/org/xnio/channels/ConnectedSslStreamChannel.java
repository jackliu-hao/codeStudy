package org.xnio.channels;

import org.xnio.ChannelListener;

public interface ConnectedSslStreamChannel extends ConnectedStreamChannel, SslChannel {
  ChannelListener.Setter<? extends ConnectedSslStreamChannel> getReadSetter();
  
  ChannelListener.Setter<? extends ConnectedSslStreamChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends ConnectedSslStreamChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ConnectedSslStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */