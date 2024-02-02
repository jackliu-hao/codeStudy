package org.noear.solon.core.message;

import java.io.IOException;

@FunctionalInterface
public interface Listener {
  default void onOpen(Session session) {}
  
  void onMessage(Session paramSession, Message paramMessage) throws IOException;
  
  default void onClose(Session session) {}
  
  default void onError(Session session, Throwable error) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\Listener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */