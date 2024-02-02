package org.xnio;

import java.io.IOException;
import java.util.EventListener;

public interface ChannelExceptionHandler<T extends java.nio.channels.Channel> extends EventListener {
  void handleException(T paramT, IOException paramIOException);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ChannelExceptionHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */