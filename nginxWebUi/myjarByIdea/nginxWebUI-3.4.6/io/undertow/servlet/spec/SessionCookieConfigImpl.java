package io.undertow.servlet.spec;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.session.SessionConfig;
import io.undertow.servlet.UndertowServletMessages;
import javax.servlet.SessionCookieConfig;

public class SessionCookieConfigImpl implements SessionCookieConfig, SessionConfig {
   private final ServletContextImpl servletContext;
   private final io.undertow.server.session.SessionCookieConfig delegate;
   private SessionConfig fallback;

   public SessionCookieConfigImpl(ServletContextImpl servletContext) {
      this.servletContext = servletContext;
      this.delegate = new io.undertow.server.session.SessionCookieConfig();
   }

   public String rewriteUrl(String originalUrl, String sessionid) {
      return this.fallback != null ? this.fallback.rewriteUrl(originalUrl, sessionid) : originalUrl;
   }

   public void setSessionId(HttpServerExchange exchange, String sessionId) {
      this.delegate.setSessionId(exchange, sessionId);
   }

   public void clearSession(HttpServerExchange exchange, String sessionId) {
      this.delegate.clearSession(exchange, sessionId);
   }

   public String findSessionId(HttpServerExchange exchange) {
      String existing = this.delegate.findSessionId(exchange);
      if (existing != null) {
         return existing;
      } else {
         return this.fallback != null ? this.fallback.findSessionId(exchange) : null;
      }
   }

   public SessionConfig.SessionCookieSource sessionCookieSource(HttpServerExchange exchange) {
      String existing = this.delegate.findSessionId(exchange);
      if (existing != null) {
         return SessionConfig.SessionCookieSource.COOKIE;
      } else if (this.fallback != null) {
         String id = this.fallback.findSessionId(exchange);
         return id != null ? this.fallback.sessionCookieSource(exchange) : SessionConfig.SessionCookieSource.NONE;
      } else {
         return SessionConfig.SessionCookieSource.NONE;
      }
   }

   public String getName() {
      return this.delegate.getCookieName();
   }

   public void setName(String name) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setCookieName(name);
      }
   }

   public String getDomain() {
      return this.delegate.getDomain();
   }

   public void setDomain(String domain) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setDomain(domain);
      }
   }

   public String getPath() {
      return this.delegate.getPath();
   }

   public void setPath(String path) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setPath(path);
      }
   }

   public String getComment() {
      return this.delegate.getComment();
   }

   public void setComment(String comment) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setComment(comment);
      }
   }

   public boolean isHttpOnly() {
      return this.delegate.isHttpOnly();
   }

   public void setHttpOnly(boolean httpOnly) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setHttpOnly(httpOnly);
      }
   }

   public boolean isSecure() {
      return this.delegate.isSecure();
   }

   public void setSecure(boolean secure) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setSecure(secure);
      }
   }

   public int getMaxAge() {
      return this.delegate.getMaxAge();
   }

   public void setMaxAge(int maxAge) {
      if (this.servletContext.isInitialized()) {
         throw UndertowServletMessages.MESSAGES.servletContextAlreadyInitialized();
      } else {
         this.delegate.setMaxAge(maxAge);
      }
   }

   public SessionConfig getFallback() {
      return this.fallback;
   }

   public void setFallback(SessionConfig fallback) {
      this.fallback = fallback;
   }
}
