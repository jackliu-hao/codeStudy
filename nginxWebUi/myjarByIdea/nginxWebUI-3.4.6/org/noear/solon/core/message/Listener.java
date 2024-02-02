package org.noear.solon.core.message;

import java.io.IOException;

@FunctionalInterface
public interface Listener {
   default void onOpen(Session session) {
   }

   void onMessage(Session session, Message message) throws IOException;

   default void onClose(Session session) {
   }

   default void onError(Session session, Throwable error) {
   }
}
