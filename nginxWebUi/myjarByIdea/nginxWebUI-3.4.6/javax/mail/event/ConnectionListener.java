package javax.mail.event;

import java.util.EventListener;

public interface ConnectionListener extends EventListener {
   void opened(ConnectionEvent var1);

   void disconnected(ConnectionEvent var1);

   void closed(ConnectionEvent var1);
}
