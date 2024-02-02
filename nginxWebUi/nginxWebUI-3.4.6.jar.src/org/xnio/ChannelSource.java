package org.xnio;

public interface ChannelSource<T extends java.nio.channels.Channel> {
  IoFuture<T> open(ChannelListener<? super T> paramChannelListener);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */