package javax.mail.event;

import java.util.EventListener;

public interface TransportListener extends EventListener {
   void messageDelivered(TransportEvent var1);

   void messageNotDelivered(TransportEvent var1);

   void messagePartiallyDelivered(TransportEvent var1);
}
