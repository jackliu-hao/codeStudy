package javax.mail.event;

public abstract class TransportAdapter implements TransportListener {
  public void messageDelivered(TransportEvent e) {}
  
  public void messageNotDelivered(TransportEvent e) {}
  
  public void messagePartiallyDelivered(TransportEvent e) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\TransportAdapter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */