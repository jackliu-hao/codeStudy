/*     */ package io.undertow.servlet.spec;
/*     */ 
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.session.SessionConfig;
/*     */ import io.undertow.server.session.SessionCookieConfig;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import javax.servlet.SessionCookieConfig;
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
/*     */ public class SessionCookieConfigImpl
/*     */   implements SessionCookieConfig, SessionConfig
/*     */ {
/*     */   private final ServletContextImpl servletContext;
/*     */   private final SessionCookieConfig delegate;
/*     */   private SessionConfig fallback;
/*     */   
/*     */   public SessionCookieConfigImpl(ServletContextImpl servletContext) {
/*  37 */     this.servletContext = servletContext;
/*  38 */     this.delegate = new SessionCookieConfig();
/*     */   }
/*     */ 
/*     */   
/*     */   public String rewriteUrl(String originalUrl, String sessionid) {
/*  43 */     if (this.fallback != null) {
/*  44 */       return this.fallback.rewriteUrl(originalUrl, sessionid);
/*     */     }
/*  46 */     return originalUrl;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setSessionId(HttpServerExchange exchange, String sessionId) {
/*  51 */     this.delegate.setSessionId(exchange, sessionId);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clearSession(HttpServerExchange exchange, String sessionId) {
/*  56 */     this.delegate.clearSession(exchange, sessionId);
/*     */   }
/*     */ 
/*     */   
/*     */   public String findSessionId(HttpServerExchange exchange) {
/*  61 */     String existing = this.delegate.findSessionId(exchange);
/*  62 */     if (existing != null) {
/*  63 */       return existing;
/*     */     }
/*  65 */     if (this.fallback != null) {
/*  66 */       return this.fallback.findSessionId(exchange);
/*     */     }
/*  68 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/*  73 */     String existing = this.delegate.findSessionId(exchange);
/*  74 */     if (existing != null) {
/*  75 */       return SessionConfig.SessionCookieSource.COOKIE;
/*     */     }
/*  77 */     if (this.fallback != null) {
/*  78 */       String id = this.fallback.findSessionId(exchange);
/*  79 */       return (id != null) ? this.fallback.sessionCookieSource(exchange) : SessionConfig.SessionCookieSource.NONE;
/*     */     } 
/*  81 */     return SessionConfig.SessionCookieSource.NONE;
/*     */   }
/*     */   
/*     */   public String getName() {
/*  85 */     return this.delegate.getCookieName();
/*     */   }
/*     */   
/*     */   public void setName(String name) {
/*  89 */     if (this.servletContext.isInitialized()) {
/*  90 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/*  92 */     this.delegate.setCookieName(name);
/*     */   }
/*     */   
/*     */   public String getDomain() {
/*  96 */     return this.delegate.getDomain();
/*     */   }
/*     */   
/*     */   public void setDomain(String domain) {
/* 100 */     if (this.servletContext.isInitialized()) {
/* 101 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 103 */     this.delegate.setDomain(domain);
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 107 */     return this.delegate.getPath();
/*     */   }
/*     */   
/*     */   public void setPath(String path) {
/* 111 */     if (this.servletContext.isInitialized()) {
/* 112 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 114 */     this.delegate.setPath(path);
/*     */   }
/*     */   
/*     */   public String getComment() {
/* 118 */     return this.delegate.getComment();
/*     */   }
/*     */   
/*     */   public void setComment(String comment) {
/* 122 */     if (this.servletContext.isInitialized()) {
/* 123 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 125 */     this.delegate.setComment(comment);
/*     */   }
/*     */   
/*     */   public boolean isHttpOnly() {
/* 129 */     return this.delegate.isHttpOnly();
/*     */   }
/*     */   
/*     */   public void setHttpOnly(boolean httpOnly) {
/* 133 */     if (this.servletContext.isInitialized()) {
/* 134 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 136 */     this.delegate.setHttpOnly(httpOnly);
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/* 140 */     return this.delegate.isSecure();
/*     */   }
/*     */   
/*     */   public void setSecure(boolean secure) {
/* 144 */     if (this.servletContext.isInitialized()) {
/* 145 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 147 */     this.delegate.setSecure(secure);
/*     */   }
/*     */   
/*     */   public int getMaxAge() {
/* 151 */     return this.delegate.getMaxAge();
/*     */   }
/*     */   
/*     */   public void setMaxAge(int maxAge) {
/* 155 */     if (this.servletContext.isInitialized()) {
/* 156 */       throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
/*     */     }
/* 158 */     this.delegate.setMaxAge(maxAge);
/*     */   }
/*     */   
/*     */   public SessionConfig getFallback() {
/* 162 */     return this.fallback;
/*     */   }
/*     */   
/*     */   public void setFallback(SessionConfig fallback) {
/* 166 */     this.fallback = fallback;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\SessionCookieConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */