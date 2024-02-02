package org.noear.solon.socketd;

import java.net.URI;
import org.noear.solon.core.message.Session;

public interface SessionFactory {
  String[] schemes();
  
  Class<?> driveType();
  
  Session createSession(Connector paramConnector);
  
  Session createSession(URI paramURI, boolean paramBoolean);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\socketd\SessionFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */