package javax.mail.event;

public abstract class ConnectionAdapter implements ConnectionListener {
  public void opened(ConnectionEvent e) {}
  
  public void disconnected(ConnectionEvent e) {}
  
  public void closed(ConnectionEvent e) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\ConnectionAdapter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */