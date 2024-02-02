package org.xnio.channels;

import org.xnio.ChannelListener;

public interface MultipointMessageChannel extends ReadableMultipointMessageChannel, WritableMultipointMessageChannel, SuspendableChannel {
  ChannelListener.Setter<? extends MultipointMessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends MultipointMessageChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends MultipointMessageChannel> getWriteSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\MultipointMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */