package org.xnio.channels;

import org.xnio.ChannelListener;

public interface StreamChannel extends SuspendableChannel, StreamSinkChannel, StreamSourceChannel, ByteChannel {
  ChannelListener.Setter<? extends StreamChannel> getReadSetter();
  
  ChannelListener.Setter<? extends StreamChannel> getWriteSetter();
  
  ChannelListener.Setter<? extends StreamChannel> getCloseSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\StreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */