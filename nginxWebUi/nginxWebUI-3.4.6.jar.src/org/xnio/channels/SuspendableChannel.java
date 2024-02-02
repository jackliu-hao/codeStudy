package org.xnio.channels;

import org.xnio.ChannelListener;

public interface SuspendableChannel extends CloseableChannel, SuspendableReadChannel, SuspendableWriteChannel {
  ChannelListener.Setter<? extends SuspendableChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends SuspendableChannel> getReadSetter();
  
  ChannelListener.Setter<? extends SuspendableChannel> getWriteSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SuspendableChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */