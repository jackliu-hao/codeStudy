package io.undertow.util;

import io.undertow.UndertowMessages;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;

public class Sessions {
   public static Session getSession(HttpServerExchange exchange) {
      return getSession(exchange, false);
   }

   public static Session getOrCreateSession(HttpServerExchange exchange) {
      return getSession(exchange, true);
   }

   private static Session getSession(HttpServerExchange exchange, boolean create) {
      SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
      SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
      if (sessionManager == null) {
         throw UndertowMessages.MESSAGES.sessionManagerNotFound();
      } else {
         Session session = sessionManager.getSession(exchange, sessionConfig);
         if (session == null && create) {
            session = sessionManager.createSession(exchange, sessionConfig);
         }

         return session;
      }
   }

   private Sessions() {
   }
}
