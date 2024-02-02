package javax.mail.event;

public abstract class MessageCountAdapter implements MessageCountListener {
  public void messagesAdded(MessageCountEvent e) {}
  
  public void messagesRemoved(MessageCountEvent e) {}
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\mail\event\MessageCountAdapter.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */