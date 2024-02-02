package org.noear.solon.socketd;

import java.io.IOException;
import java.net.URI;
import org.noear.solon.core.message.Session;

public interface Connector<T> {
  URI uri();
  
  boolean autoReconnect();
  
  Class<T> driveType();
  
  T open(Session paramSession) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\Connector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */