package org.xnio.channels;

public interface WrappedChannel<C extends java.nio.channels.Channel> {
  C getChannel();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\WrappedChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */