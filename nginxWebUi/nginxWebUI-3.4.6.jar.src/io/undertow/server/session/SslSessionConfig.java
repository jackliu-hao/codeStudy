/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.SSLSessionInfo;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SslSessionConfig
/*     */   implements SessionConfig
/*     */ {
/*     */   private final SessionConfig fallbackSessionConfig;
/*  38 */   private final Map<Key, String> sessions = new HashMap<>();
/*  39 */   private final Map<String, Key> reverse = new HashMap<>();
/*     */   
/*     */   public SslSessionConfig(SessionConfig fallbackSessionConfig, SessionManager sessionManager) {
/*  42 */     this.fallbackSessionConfig = fallbackSessionConfig;
/*  43 */     sessionManager.registerSessionListener(new SessionListener()
/*     */         {
/*     */           public void sessionCreated(Session session, HttpServerExchange exchange) {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
/*  50 */             synchronized (SslSessionConfig.this) {
/*  51 */               SslSessionConfig.Key sid = (SslSessionConfig.Key)SslSessionConfig.this.reverse.remove(session.getId());
/*  52 */               if (sid != null) {
/*  53 */                 SslSessionConfig.this.sessions.remove(sid);
/*     */               }
/*     */             } 
/*     */           }
/*     */ 
/*     */ 
/*     */           
/*     */           public void attributeAdded(Session session, String name, Object value) {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {}
/*     */ 
/*     */ 
/*     */           
/*     */           public void attributeRemoved(Session session, String name, Object oldValue) {}
/*     */ 
/*     */           
/*     */           public void sessionIdChanged(Session session, String oldSessionId) {
/*  72 */             synchronized (SslSessionConfig.this) {
/*  73 */               SslSessionConfig.Key sid = (SslSessionConfig.Key)SslSessionConfig.this.reverse.remove(session.getId());
/*  74 */               if (sid != null) {
/*  75 */                 SslSessionConfig.this.sessions.remove(sid);
/*     */               }
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public SslSessionConfig(SessionManager sessionManager) {
/*  83 */     this(null, sessionManager);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionId(HttpServerExchange exchange, String sessionId) {
/*  88 */     UndertowLogger.SESSION_LOGGER.tracef("Setting SSL session id %s on %s", sessionId, exchange);
/*  89 */     SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
/*  90 */     if (sslSession == null) {
/*  91 */       if (this.fallbackSessionConfig != null) {
/*  92 */         this.fallbackSessionConfig.setSessionId(exchange, sessionId);
/*     */       }
/*     */     } else {
/*  95 */       Key key = new Key(sslSession.getSessionId());
/*  96 */       synchronized (this) {
/*  97 */         this.sessions.put(key, sessionId);
/*  98 */         this.reverse.put(sessionId, key);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearSession(HttpServerExchange exchange, String sessionId) {
/* 105 */     UndertowLogger.SESSION_LOGGER.tracef("Clearing SSL session id %s on %s", sessionId, exchange);
/* 106 */     SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
/* 107 */     if (sslSession == null) {
/* 108 */       if (this.fallbackSessionConfig != null) {
/* 109 */         this.fallbackSessionConfig.clearSession(exchange, sessionId);
/*     */       }
/*     */     } else {
/* 112 */       synchronized (this) {
/* 113 */         Key sid = this.reverse.remove(sessionId);
/* 114 */         if (sid != null) {
/* 115 */           this.sessions.remove(sid);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String findSessionId(HttpServerExchange exchange) {
/* 123 */     SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
/* 124 */     if (sslSession == null) {
/* 125 */       if (this.fallbackSessionConfig != null) {
/* 126 */         return this.fallbackSessionConfig.findSessionId(exchange);
/*     */       }
/*     */     } else {
/* 129 */       synchronized (this) {
/* 130 */         String sessionId = this.sessions.get(new Key(sslSession.getSessionId()));
/* 131 */         if (sessionId != null) {
/* 132 */           UndertowLogger.SESSION_LOGGER.tracef("Found SSL session id %s on %s", sessionId, exchange);
/*     */         }
/* 134 */         return sessionId;
/*     */       } 
/*     */     } 
/* 137 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/* 142 */     return (findSessionId(exchange) != null) ? SessionConfig.SessionCookieSource.SSL : SessionConfig.SessionCookieSource.NONE;
/*     */   }
/*     */ 
/*     */   
/*     */   public String rewriteUrl(String originalUrl, String sessionId) {
/* 147 */     return originalUrl;
/*     */   }
/*     */   
/*     */   private static final class Key {
/*     */     private final byte[] id;
/*     */     
/*     */     private Key(byte[] id) {
/* 154 */       this.id = id;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 159 */       if (this == o) return true; 
/* 160 */       if (o == null || getClass() != o.getClass()) return false;
/*     */       
/* 162 */       Key key = (Key)o;
/*     */       
/* 164 */       if (!Arrays.equals(this.id, key.id)) return false;
/*     */       
/* 166 */       return true;
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 171 */       return (this.id != null) ? Arrays.hashCode(this.id) : 0;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SslSessionConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */