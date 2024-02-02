package io.undertow.server.session;

import io.undertow.server.HttpServerExchange;

public interface SessionListener {
   default void sessionCreated(Session session, HttpServerExchange exchange) {
   }

   default void sessionDestroyed(Session session, HttpServerExchange exchange, SessionDestroyedReason reason) {
   }

   default void attributeAdded(Session session, String name, Object value) {
   }

   default void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
   }

   default void attributeRemoved(Session session, String name, Object oldValue) {
   }

   default void sessionIdChanged(Session session, String oldSessionId) {
   }

   public static enum SessionDestroyedReason {
      INVALIDATED,
      TIMEOUT,
      UNDEPLOY;
   }
}
