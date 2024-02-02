package io.undertow.server.session;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class SslSessionConfig implements SessionConfig {
   private final SessionConfig fallbackSessionConfig;
   private final Map<Key, String> sessions;
   private final Map<String, Key> reverse;

   public SslSessionConfig(SessionConfig fallbackSessionConfig, SessionManager sessionManager) {
      this.sessions = new HashMap();
      this.reverse = new HashMap();
      this.fallbackSessionConfig = fallbackSessionConfig;
      sessionManager.registerSessionListener(new SessionListener() {
         public void sessionCreated(Session session, HttpServerExchange exchange) {
         }

         public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
            synchronized(SslSessionConfig.this) {
               Key sid = (Key)SslSessionConfig.this.reverse.remove(session.getId());
               if (sid != null) {
                  SslSessionConfig.this.sessions.remove(sid);
               }

            }
         }

         public void attributeAdded(Session session, String name, Object value) {
         }

         public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
         }

         public void attributeRemoved(Session session, String name, Object oldValue) {
         }

         public void sessionIdChanged(Session session, String oldSessionId) {
            synchronized(SslSessionConfig.this) {
               Key sid = (Key)SslSessionConfig.this.reverse.remove(session.getId());
               if (sid != null) {
                  SslSessionConfig.this.sessions.remove(sid);
               }

            }
         }
      });
   }

   public SslSessionConfig(SessionManager sessionManager) {
      this((SessionConfig)null, sessionManager);
   }

   public void setSessionId(HttpServerExchange exchange, String sessionId) {
      UndertowLogger.SESSION_LOGGER.tracef("Setting SSL session id %s on %s", sessionId, exchange);
      SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
      if (sslSession == null) {
         if (this.fallbackSessionConfig != null) {
            this.fallbackSessionConfig.setSessionId(exchange, sessionId);
         }
      } else {
         Key key = new Key(sslSession.getSessionId());
         synchronized(this) {
            this.sessions.put(key, sessionId);
            this.reverse.put(sessionId, key);
         }
      }

   }

   public void clearSession(HttpServerExchange exchange, String sessionId) {
      UndertowLogger.SESSION_LOGGER.tracef("Clearing SSL session id %s on %s", sessionId, exchange);
      SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
      if (sslSession == null) {
         if (this.fallbackSessionConfig != null) {
            this.fallbackSessionConfig.clearSession(exchange, sessionId);
         }
      } else {
         synchronized(this) {
            Key sid = (Key)this.reverse.remove(sessionId);
            if (sid != null) {
               this.sessions.remove(sid);
            }
         }
      }

   }

   public String findSessionId(HttpServerExchange exchange) {
      SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
      if (sslSession == null) {
         return this.fallbackSessionConfig != null ? this.fallbackSessionConfig.findSessionId(exchange) : null;
      } else {
         synchronized(this) {
            String sessionId = (String)this.sessions.get(new Key(sslSession.getSessionId()));
            if (sessionId != null) {
               UndertowLogger.SESSION_LOGGER.tracef("Found SSL session id %s on %s", sessionId, exchange);
            }

            return sessionId;
         }
      }
   }

   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
      return this.findSessionId(exchange) != null ? SessionConfig.SessionCookieSource.SSL : SessionConfig.SessionCookieSource.NONE;
   }

   public String rewriteUrl(String originalUrl, String sessionId) {
      return originalUrl;
   }

   private static final class Key {
      private final byte[] id;

      private Key(byte[] id) {
         this.id = id;
      }

      public boolean equals(Object o) {
         if (this == o) {
            return true;
         } else if (o != null && this.getClass() == o.getClass()) {
            Key key = (Key)o;
            return Arrays.equals(this.id, key.id);
         } else {
            return false;
         }
      }

      public int hashCode() {
         return this.id != null ? Arrays.hashCode(this.id) : 0;
      }

      // $FF: synthetic method
      Key(byte[] x0, Object x1) {
         this(x0);
      }
   }
}
