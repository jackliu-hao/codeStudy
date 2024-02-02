package org.xnio.channels;

import org.xnio.ChannelListener;

public interface MessageChannel extends ReadableMessageChannel, WritableMessageChannel, SuspendableChannel {
  ChannelListener.Setter<? extends MessageChannel> getReadSetter();
  
  ChannelListener.Setter<? extends MessageChannel> getCloseSetter();
  
  ChannelListener.Setter<? extends MessageChannel> getWriteSetter();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\MessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */