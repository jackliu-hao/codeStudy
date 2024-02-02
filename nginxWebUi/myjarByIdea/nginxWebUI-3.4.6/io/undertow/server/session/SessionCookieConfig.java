package io.undertow.server.session;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;

public class SessionCookieConfig implements SessionConfig {
   public static final String DEFAULT_SESSION_ID = "JSESSIONID";
   private String cookieName = "JSESSIONID";
   private String path = "/";
   private String domain;
   private boolean discard;
   private boolean secure;
   private boolean httpOnly;
   private int maxAge = -1;
   private String comment;

   public String rewriteUrl(String originalUrl, String sessionId) {
      return originalUrl;
   }

   public void setSessionId(HttpServerExchange exchange, String sessionId) {
      Cookie cookie = (new CookieImpl(this.cookieName, sessionId)).setPath(this.path).setDomain(this.domain).setDiscard(this.discard).setSecure(this.secure).setHttpOnly(this.httpOnly).setComment(this.comment);
      if (this.maxAge > 0) {
         cookie.setMaxAge(this.maxAge);
      }

      exchange.setResponseCookie(cookie);
      UndertowLogger.SESSION_LOGGER.tracef("Setting session cookie session id %s on %s", sessionId, exchange);
   }

   public void clearSession(HttpServerExchange exchange, String sessionId) {
      Cookie cookie = (new CookieImpl(this.cookieName, sessionId)).setPath(this.path).setDomain(this.domain).setDiscard(this.discard).setSecure(this.secure).setHttpOnly(this.httpOnly).setMaxAge(0);
      exchange.setResponseCookie(cookie);
      UndertowLogger.SESSION_LOGGER.tracef("Clearing session cookie session id %s on %s", sessionId, exchange);
   }

   public String findSessionId(HttpServerExchange exchange) {
      Cookie cookie = exchange.getRequestCookie(this.cookieName);
      if (cookie != null) {
         UndertowLogger.SESSION_LOGGER.tracef("Found session cookie session id %s on %s", cookie, exchange);
         return cookie.getValue();
      } else {
         return null;
      }
   }

   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
      return this.findSessionId(exchange) != null ? SessionConfig.SessionCookieSource.COOKIE : SessionConfig.SessionCookieSource.NONE;
   }

   public String getCookieName() {
      return this.cookieName;
   }

   public SessionCookieConfig setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
   }

   public String getPath() {
      return this.path;
   }

   public SessionCookieConfig setPath(String path) {
      this.path = path;
      return this;
   }

   public String getDomain() {
      return this.domain;
   }

   public SessionCookieConfig setDomain(String domain) {
      this.domain = domain;
      return this;
   }

   public boolean isDiscard() {
      return this.discard;
   }

   public SessionCookieConfig setDiscard(boolean discard) {
      this.discard = discard;
      return this;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public SessionCookieConfig setSecure(boolean secure) {
      this.secure = secure;
      return this;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public SessionCookieConfig setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
      return this;
   }

   public int getMaxAge() {
      return this.maxAge;
   }

   public SessionCookieConfig setMaxAge(int maxAge) {
      this.maxAge = maxAge;
      return this;
   }

   public String getComment() {
      return this.comment;
   }

   public SessionCookieConfig setComment(String comment) {
      this.comment = comment;
      return this;
   }
}
