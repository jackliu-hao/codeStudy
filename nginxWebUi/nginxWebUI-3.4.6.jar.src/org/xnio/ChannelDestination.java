package org.xnio;

import org.xnio.channels.BoundChannel;

public interface ChannelDestination<T extends java.nio.channels.Channel> {
  IoFuture<? extends T> accept(ChannelListener<? super T> paramChannelListener, ChannelListener<? super BoundChannel> paramChannelListener1);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelDestination.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */