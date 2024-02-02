package javax.mail.event;

import java.util.EventListener;

public interface ConnectionListener extends EventListener {
  void opened(ConnectionEvent paramConnectionEvent);
  
  void disconnected(ConnectionEvent paramConnectionEvent);
  
  void closed(ConnectionEvent paramConnectionEvent);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\ConnectionListener.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */