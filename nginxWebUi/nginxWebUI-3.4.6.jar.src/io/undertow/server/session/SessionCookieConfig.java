/*     */ package io.undertow.server.session;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.CookieImpl;
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
/*     */ 
/*     */ public class SessionCookieConfig
/*     */   implements SessionConfig
/*     */ {
/*     */   public static final String DEFAULT_SESSION_ID = "JSESSIONID";
/*  37 */   private String cookieName = "JSESSIONID";
/*  38 */   private String path = "/";
/*     */   private String domain;
/*     */   private boolean discard;
/*     */   private boolean secure;
/*     */   private boolean httpOnly;
/*  43 */   private int maxAge = -1;
/*     */   
/*     */   private String comment;
/*     */ 
/*     */   
/*     */   public String rewriteUrl(String originalUrl, String sessionId) {
/*  49 */     return originalUrl;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setSessionId(HttpServerExchange exchange, String sessionId) {
/*  60 */     Cookie cookie = (new CookieImpl(this.cookieName, sessionId)).setPath(this.path).setDomain(this.domain).setDiscard(this.discard).setSecure(this.secure).setHttpOnly(this.httpOnly).setComment(this.comment);
/*  61 */     if (this.maxAge > 0) {
/*  62 */       cookie.setMaxAge(Integer.valueOf(this.maxAge));
/*     */     }
/*  64 */     exchange.setResponseCookie(cookie);
/*  65 */     UndertowLogger.SESSION_LOGGER.tracef("Setting session cookie session id %s on %s", sessionId, exchange);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clearSession(HttpServerExchange exchange, String sessionId) {
/*  76 */     CookieImpl cookieImpl = (new CookieImpl(this.cookieName, sessionId)).setPath(this.path).setDomain(this.domain).setDiscard(this.discard).setSecure(this.secure).setHttpOnly(this.httpOnly).setMaxAge(Integer.valueOf(0));
/*  77 */     exchange.setResponseCookie((Cookie)cookieImpl);
/*  78 */     UndertowLogger.SESSION_LOGGER.tracef("Clearing session cookie session id %s on %s", sessionId, exchange);
/*     */   }
/*     */ 
/*     */   
/*     */   public String findSessionId(HttpServerExchange exchange) {
/*  83 */     Cookie cookie = exchange.getRequestCookie(this.cookieName);
/*  84 */     if (cookie != null) {
/*  85 */       UndertowLogger.SESSION_LOGGER.tracef("Found session cookie session id %s on %s", cookie, exchange);
/*  86 */       return cookie.getValue();
/*     */     } 
/*  88 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
/*  93 */     return (findSessionId(exchange) != null) ? SessionConfig.SessionCookieSource.COOKIE : SessionConfig.SessionCookieSource.NONE;
/*     */   }
/*     */   
/*     */   public String getCookieName() {
/*  97 */     return this.cookieName;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setCookieName(String cookieName) {
/* 101 */     this.cookieName = cookieName;
/* 102 */     return this;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 106 */     return this.path;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setPath(String path) {
/* 110 */     this.path = path;
/* 111 */     return this;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/* 115 */     return this.domain;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setDomain(String domain) {
/* 119 */     this.domain = domain;
/* 120 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isDiscard() {
/* 124 */     return this.discard;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setDiscard(boolean discard) {
/* 128 */     this.discard = discard;
/* 129 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/* 133 */     return this.secure;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setSecure(boolean secure) {
/* 137 */     this.secure = secure;
/* 138 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isHttpOnly() {
/* 142 */     return this.httpOnly;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setHttpOnly(boolean httpOnly) {
/* 146 */     this.httpOnly = httpOnly;
/* 147 */     return this;
/*     */   }
/*     */   
/*     */   public int getMaxAge() {
/* 151 */     return this.maxAge;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setMaxAge(int maxAge) {
/* 155 */     this.maxAge = maxAge;
/* 156 */     return this;
/*     */   }
/*     */   
/*     */   public String getComment() {
/* 160 */     return this.comment;
/*     */   }
/*     */   
/*     */   public SessionCookieConfig setComment(String comment) {
/* 164 */     this.comment = comment;
/* 165 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\session\SessionCookieConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */