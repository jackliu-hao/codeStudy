package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.UndertowServletLogger;
import io.undertow.servlet.api.SessionPersistenceManager;
import io.undertow.servlet.core.Lifecycle;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.servlet.spec.ServletContextImpl;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionEvent;

public class SessionRestoringHandler implements HttpHandler, Lifecycle {
   private final String deploymentName;
   private final Map<String, SessionPersistenceManager.PersistentSession> data;
   private final SessionManager sessionManager;
   private final ServletContextImpl servletContext;
   private final HttpHandler next;
   private final SessionPersistenceManager sessionPersistenceManager;
   private volatile boolean started = false;

   public SessionRestoringHandler(String deploymentName, SessionManager sessionManager, ServletContextImpl servletContext, HttpHandler next, SessionPersistenceManager sessionPersistenceManager) {
      this.deploymentName = deploymentName;
      this.sessionManager = sessionManager;
      this.servletContext = servletContext;
      this.next = next;
      this.sessionPersistenceManager = sessionPersistenceManager;
      this.data = new ConcurrentHashMap();
   }

   public void start() {
      ClassLoader old = this.getTccl();

      try {
         this.setTccl(this.servletContext.getClassLoader());

         try {
            Map<String, SessionPersistenceManager.PersistentSession> sessionData = this.sessionPersistenceManager.loadSessionAttributes(this.deploymentName, this.servletContext.getClassLoader());
            if (sessionData != null) {
               this.data.putAll(sessionData);
            }
         } catch (Exception var6) {
            UndertowServletLogger.ROOT_LOGGER.failedtoLoadPersistentSessions(var6);
         }

         this.started = true;
      } finally {
         this.setTccl(old);
      }

   }

   public void stop() {
      ClassLoader old = this.getTccl();

      try {
         this.setTccl(this.servletContext.getClassLoader());
         this.started = false;
         Map<String, SessionPersistenceManager.PersistentSession> objectData = new HashMap();
         Iterator var3 = this.sessionManager.getTransientSessions().iterator();

         while(var3.hasNext()) {
            String sessionId = (String)var3.next();
            Session session = this.sessionManager.getSession(sessionId);
            if (session != null) {
               HttpSessionEvent event = new HttpSessionEvent(SecurityActions.forSession(session, this.servletContext, false));
               Map<String, Object> sessionData = new HashMap();
               Iterator var8 = session.getAttributeNames().iterator();

               while(var8.hasNext()) {
                  String attr = (String)var8.next();
                  Object attribute = session.getAttribute(attr);
                  sessionData.put(attr, attribute);
                  if (attribute instanceof HttpSessionActivationListener) {
                     ((HttpSessionActivationListener)attribute).sessionWillPassivate(event);
                  }
               }

               objectData.put(sessionId, new SessionPersistenceManager.PersistentSession(new Date(session.getLastAccessedTime() + (long)(session.getMaxInactiveInterval() * 1000)), sessionData));
            }
         }

         this.sessionPersistenceManager.persistSessions(this.deploymentName, objectData);
         this.data.clear();
      } finally {
         this.setTccl(old);
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String incomingSessionId = this.servletContext.getSessionConfig().findSessionId(exchange);
      if (incomingSessionId != null && this.data.containsKey(incomingSessionId)) {
         SessionPersistenceManager.PersistentSession result = (SessionPersistenceManager.PersistentSession)this.data.remove(incomingSessionId);
         if (result != null) {
            long time = System.currentTimeMillis();
            if (time < result.getExpiration().getTime()) {
               HttpSessionImpl session = this.servletContext.getSession(exchange, true);
               HttpSessionEvent event = new HttpSessionEvent(session);
               Iterator var8 = result.getSessionData().entrySet().iterator();

               while(var8.hasNext()) {
                  Map.Entry<String, Object> entry = (Map.Entry)var8.next();
                  if (entry.getValue() instanceof HttpSessionActivationListener) {
                     ((HttpSessionActivationListener)entry.getValue()).sessionDidActivate(event);
                  }

                  if (((String)entry.getKey()).startsWith("io.undertow")) {
                     session.getSession().setAttribute((String)entry.getKey(), entry.getValue());
                  } else {
                     session.setAttribute((String)entry.getKey(), entry.getValue());
                  }
               }
            }
         }

         this.next.handleRequest(exchange);
      } else {
         this.next.handleRequest(exchange);
      }
   }

   public boolean isStarted() {
      return this.started;
   }

   private ClassLoader getTccl() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(new PrivilegedAction<ClassLoader>() {
         public ClassLoader run() {
            return Thread.currentThread().getContextClassLoader();
         }
      });
   }

   private void setTccl(final ClassLoader classLoader) {
      if (System.getSecurityManager() == null) {
         Thread.currentThread().setContextClassLoader(classLoader);
      } else {
         AccessController.doPrivileged(new PrivilegedAction<Void>() {
            public Void run() {
               Thread.currentThread().setContextClassLoader(classLoader);
               return null;
            }
         });
      }

   }
}
