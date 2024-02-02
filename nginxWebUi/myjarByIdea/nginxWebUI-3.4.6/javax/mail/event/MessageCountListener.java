package javax.mail.event;

import java.util.EventListener;

public interface MessageCountListener extends EventListener {
   void messagesAdded(MessageCountEvent var1);

   void messagesRemoved(MessageCountEvent var1);
}
