package org.noear.solon.core.message;

import java.io.IOException;

public abstract class ListenerEmpty implements Listener {
  public void onMessage(Session session, Message message) throws IOException {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\message\ListenerEmpty.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */