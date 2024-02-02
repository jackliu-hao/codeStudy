package javax.mail.event;

import java.util.EventListener;

public interface MessageChangedListener extends EventListener {
   void messageChanged(MessageChangedEvent var1);
}
