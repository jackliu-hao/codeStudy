package io.undertow.security.handlers;

import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionConfig;
import io.undertow.server.session.SessionManager;

public class CachedAuthenticatedSessionHandler implements HttpHandler {
   public static final String ATTRIBUTE_NAME = CachedAuthenticatedSessionHandler.class.getName() + ".AuthenticatedSession";
   public static final String NO_ID_CHANGE_REQUIRED = CachedAuthenticatedSessionHandler.class.getName() + ".NoIdChangeRequired";
   private final NotificationReceiver NOTIFICATION_RECEIVER = new SecurityNotificationReceiver();
   private final AuthenticatedSessionManager SESSION_MANAGER = new ServletAuthenticatedSessionManager();
   private final HttpHandler next;

   public CachedAuthenticatedSessionHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      SecurityContext securityContext = exchange.getSecurityContext();
      securityContext.registerNotificationReceiver(this.NOTIFICATION_RECEIVER);
      SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
      SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
      if (sessionManager != null && sessionConfig != null) {
         Session session = sessionManager.getSession(exchange, sessionConfig);
         if (session != null) {
            exchange.putAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY, this.SESSION_MANAGER);
         }

         this.next.handleRequest(exchange);
      } else {
         this.next.handleRequest(exchange);
      }
   }

   private boolean isCacheable(SecurityNotification notification) {
      return notification.isProgramatic() || notification.isCachingRequired();
   }

   private static class ServletAuthenticatedSessionManager implements AuthenticatedSessionManager {
      private ServletAuthenticatedSessionManager() {
      }

      public AuthenticatedSessionManager.AuthenticatedSession lookupSession(HttpServerExchange exchange) {
         SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
         SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
         if (sessionManager != null && sessionConfig != null) {
            Session httpSession = sessionManager.getSession(exchange, sessionConfig);
            return httpSession != null ? (AuthenticatedSessionManager.AuthenticatedSession)httpSession.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME) : null;
         } else {
            return null;
         }
      }

      public void clearSession(HttpServerExchange exchange) {
         SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
         SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
         if (sessionManager != null && sessionConfig != null) {
            Session httpSession = sessionManager.getSession(exchange, sessionConfig);
            if (httpSession != null) {
               httpSession.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
            }

         }
      }

      // $FF: synthetic method
      ServletAuthenticatedSessionManager(Object x0) {
         this();
      }
   }

   private class SecurityNotificationReceiver implements NotificationReceiver {
      private SecurityNotificationReceiver() {
      }

      public void handleNotification(SecurityNotification notification) {
         SecurityNotification.EventType eventType = notification.getEventType();
         HttpServerExchange exchange = notification.getExchange();
         SessionManager sessionManager = (SessionManager)exchange.getAttachment(SessionManager.ATTACHMENT_KEY);
         SessionConfig sessionConfig = (SessionConfig)exchange.getAttachment(SessionConfig.ATTACHMENT_KEY);
         if (sessionManager != null && sessionConfig != null) {
            Session httpSession = sessionManager.getSession(exchange, sessionConfig);
            switch (eventType) {
               case AUTHENTICATED:
                  if (CachedAuthenticatedSessionHandler.this.isCacheable(notification)) {
                     if (httpSession == null) {
                        httpSession = sessionManager.createSession(exchange, sessionConfig);
                     }

                     httpSession.setAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME, new AuthenticatedSessionManager.AuthenticatedSession(notification.getAccount(), notification.getMechanism()));
                  }
                  break;
               case LOGGED_OUT:
                  if (httpSession != null) {
                     httpSession.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
                     httpSession.removeAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED);
                  }
            }

         }
      }

      // $FF: synthetic method
      SecurityNotificationReceiver(Object x1) {
         this();
      }
   }
}
