package javax.mail.event;

public abstract class TransportAdapter implements TransportListener {
   public void messageDelivered(TransportEvent e) {
   }

   public void messageNotDelivered(TransportEvent e) {
   }

   public void messagePartiallyDelivered(TransportEvent e) {
   }
}
