package io.undertow.servlet.core;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionListener;
import io.undertow.servlet.api.Deployment;
import io.undertow.servlet.api.ThreadSetupHandler;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import java.security.AccessController;
import java.util.HashSet;
import java.util.Iterator;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class SessionListenerBridge implements SessionListener {
   public static final String IO_UNDERTOW = "io.undertow";
   private final ApplicationListeners applicationListeners;
   private final ServletContext servletContext;
   private final ThreadSetupHandler.Action<Void, Session> destroyedAction;

   public SessionListenerBridge(Deployment deployment, ApplicationListeners applicationListeners, ServletContext servletContext) {
      this.applicationListeners = applicationListeners;
      this.servletContext = servletContext;
      this.destroyedAction = deployment.createThreadSetupAction(new ThreadSetupHandler.Action<Void, Session>() {
         public Void call(HttpServerExchange exchange, Session session) throws ServletException {
            SessionListenerBridge.this.doDestroy(session);
            return null;
         }
      });
   }

   public void sessionCreated(Session session, HttpServerExchange exchange) {
      HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, true);
      this.applicationListeners.sessionCreated(httpSession);
   }

   public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
      if (reason == SessionListener.SessionDestroyedReason.TIMEOUT) {
         try {
            this.destroyedAction.call(exchange, session);
         } catch (Exception var6) {
            throw new RuntimeException(var6);
         }
      } else {
         this.doDestroy(session);
      }

      ServletRequestContext current = SecurityActions.currentServletRequestContext();
      Session underlying = null;
      if (current != null && current.getSession() != null) {
         if (System.getSecurityManager() == null) {
            underlying = current.getSession().getSession();
         } else {
            underlying = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(current.getSession()));
         }
      }

      if (current != null && underlying == session) {
         current.setSession((HttpSessionImpl)null);
      }

   }

   private void doDestroy(Session session) {
      HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
      this.applicationListeners.sessionDestroyed(httpSession);
      HashSet<String> names = new HashSet(session.getAttributeNames());
      Iterator var4 = names.iterator();

      while(var4.hasNext()) {
         String attribute = (String)var4.next();
         session.removeAttribute(attribute);
      }

   }

   public void attributeAdded(Session session, String name, Object value) {
      if (!name.startsWith("io.undertow")) {
         HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
         this.applicationListeners.httpSessionAttributeAdded(httpSession, name, value);
         if (value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent(httpSession, name, value));
         }

      }
   }

   public void attributeUpdated(Session session, String name, Object value, Object old) {
      if (!name.startsWith("io.undertow")) {
         HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
         if (old != value) {
            if (old instanceof HttpSessionBindingListener) {
               ((HttpSessionBindingListener)old).valueUnbound(new HttpSessionBindingEvent(httpSession, name, old));
            }

            this.applicationListeners.httpSessionAttributeReplaced(httpSession, name, old);
         }

         if (value instanceof HttpSessionBindingListener) {
            ((HttpSessionBindingListener)value).valueBound(new HttpSessionBindingEvent(httpSession, name, value));
         }

      }
   }

   public void attributeRemoved(Session session, String name, Object old) {
      if (!name.startsWith("io.undertow")) {
         HttpSessionImpl httpSession = SecurityActions.forSession(session, this.servletContext, false);
         if (old != null) {
            this.applicationListeners.httpSessionAttributeRemoved(httpSession, name, old);
            if (old instanceof HttpSessionBindingListener) {
               ((HttpSessionBindingListener)old).valueUnbound(new HttpSessionBindingEvent(httpSession, name, old));
            }
         }

      }
   }

   public void sessionIdChanged(Session session, String oldSessionId) {
   }
}
