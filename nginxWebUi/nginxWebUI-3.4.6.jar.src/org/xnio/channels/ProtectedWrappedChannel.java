package org.xnio.channels;

public interface ProtectedWrappedChannel<C extends java.nio.channels.Channel> {
  C getChannel(Object paramObject);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ProtectedWrappedChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */