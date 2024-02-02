package io.undertow.servlet.handlers.security;

import io.undertow.security.api.AuthenticatedSessionManager;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import io.undertow.servlet.util.SavedRequest;
import java.security.AccessController;
import javax.servlet.http.HttpSession;

public class CachedAuthenticatedSessionHandler implements HttpHandler {
   public static final String ATTRIBUTE_NAME = CachedAuthenticatedSessionHandler.class.getName() + ".AuthenticatedSession";
   public static final String NO_ID_CHANGE_REQUIRED = CachedAuthenticatedSessionHandler.class.getName() + ".NoIdChangeRequired";
   private final NotificationReceiver NOTIFICATION_RECEIVER = new SecurityNotificationReceiver();
   private final AuthenticatedSessionManager SESSION_MANAGER = new ServletAuthenticatedSessionManager();
   private final HttpHandler next;
   private final ServletContextImpl servletContext;

   public CachedAuthenticatedSessionHandler(HttpHandler next, ServletContextImpl servletContext) {
      this.next = next;
      this.servletContext = servletContext;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      SecurityContext securityContext = exchange.getSecurityContext();
      securityContext.registerNotificationReceiver(this.NOTIFICATION_RECEIVER);
      HttpSession session = this.servletContext.getSession(exchange, false);
      if (session != null) {
         exchange.putAttachment(AuthenticatedSessionManager.ATTACHMENT_KEY, this.SESSION_MANAGER);
         SavedRequest.tryRestoreRequest(exchange, session);
      }

      this.next.handleRequest(exchange);
   }

   protected Session underlyingSession(HttpSessionImpl httpSession) {
      Session session;
      if (System.getSecurityManager() == null) {
         session = httpSession.getSession();
      } else {
         session = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(httpSession));
      }

      return session;
   }

   private boolean isCacheable(SecurityNotification notification) {
      return notification.isProgramatic() || notification.isCachingRequired();
   }

   private class ServletAuthenticatedSessionManager implements AuthenticatedSessionManager {
      private ServletAuthenticatedSessionManager() {
      }

      public AuthenticatedSessionManager.AuthenticatedSession lookupSession(HttpServerExchange exchange) {
         HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(exchange, false);
         if (httpSession != null) {
            Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
            return (AuthenticatedSessionManager.AuthenticatedSession)session.getAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
         } else {
            return null;
         }
      }

      public void clearSession(HttpServerExchange exchange) {
         HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(exchange, false);
         if (httpSession != null) {
            Session session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
            session.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
         }

      }

      // $FF: synthetic method
      ServletAuthenticatedSessionManager(Object x1) {
         this();
      }
   }

   private class SecurityNotificationReceiver implements NotificationReceiver {
      private SecurityNotificationReceiver() {
      }

      public void handleNotification(SecurityNotification notification) {
         SecurityNotification.EventType eventType = notification.getEventType();
         HttpSessionImpl httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(notification.getExchange(), false);
         Session session;
         switch (eventType) {
            case AUTHENTICATED:
               if (CachedAuthenticatedSessionHandler.this.isCacheable(notification)) {
                  if (CachedAuthenticatedSessionHandler.this.servletContext.getDeployment().getDeploymentInfo().isChangeSessionIdOnLogin() && httpSession != null) {
                     session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
                     if (!httpSession.isNew() && !httpSession.isInvalid() && session.getAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED) == null) {
                        ServletRequestContext src = (ServletRequestContext)notification.getExchange().getAttachment(ServletRequestContext.ATTACHMENT_KEY);
                        src.getOriginalRequest().changeSessionId();
                     }

                     if (session.getAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED) == null) {
                        session.setAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED, true);
                     }
                  }

                  if (httpSession == null) {
                     httpSession = CachedAuthenticatedSessionHandler.this.servletContext.getSession(notification.getExchange(), true);
                  }

                  session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
                  session.setAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME, new AuthenticatedSessionManager.AuthenticatedSession(notification.getAccount(), notification.getMechanism()));
               }
               break;
            case LOGGED_OUT:
               if (httpSession != null) {
                  session = CachedAuthenticatedSessionHandler.this.underlyingSession(httpSession);
                  session.removeAttribute(CachedAuthenticatedSessionHandler.ATTRIBUTE_NAME);
                  session.removeAttribute(CachedAuthenticatedSessionHandler.NO_ID_CHANGE_REQUIRED);
               }
         }

      }

      // $FF: synthetic method
      SecurityNotificationReceiver(Object x1) {
         this();
      }
   }
}
