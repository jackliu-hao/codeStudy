package io.undertow.servlet.handlers.security;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.impl.FormAuthenticationMechanism;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpSessionImpl;
import io.undertow.servlet.util.SavedRequest;
import io.undertow.util.Headers;
import io.undertow.util.RedirectBuilder;
import java.io.IOException;
import java.security.AccessController;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class ServletFormAuthenticationMechanism extends FormAuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   private static final String SESSION_KEY = "io.undertow.servlet.form.auth.redirect.location";
   public static final String SAVE_ORIGINAL_REQUEST = "save-original-request";
   private final boolean saveOriginalRequest;
   private final Set<SessionManager> seenSessionManagers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
   private final String defaultPage;
   private final boolean overrideInitial;
   private static final SessionListener LISTENER = new SessionListener() {
      public void sessionCreated(Session session, HttpServerExchange exchange) {
      }

      public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
      }

      public void attributeAdded(Session session, String name, Object value) {
      }

      public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {
      }

      public void attributeRemoved(Session session, String name, Object oldValue) {
      }

      public void sessionIdChanged(Session session, String oldSessionId) {
         String oldLocation = (String)session.getAttribute("io.undertow.servlet.form.auth.redirect.location");
         if (oldLocation != null) {
            String oldPart = ";jsessionid=" + oldSessionId;
            if (oldLocation.contains(oldPart)) {
               session.setAttribute("io.undertow.servlet.form.auth.redirect.location", oldLocation.replace(oldPart, ";jsessionid=" + session.getId()));
            }
         }

      }
   };

   /** @deprecated */
   @Deprecated
   public ServletFormAuthenticationMechanism(String name, String loginPage, String errorPage) {
      super(name, loginPage, errorPage);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   /** @deprecated */
   @Deprecated
   public ServletFormAuthenticationMechanism(String name, String loginPage, String errorPage, String postLocation) {
      super(name, loginPage, errorPage, postLocation);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation) {
      super(formParserFactory, name, loginPage, errorPage, postLocation);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
      super(formParserFactory, name, loginPage, errorPage);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager) {
      super(formParserFactory, name, loginPage, errorPage, identityManager);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager, boolean saveOriginalRequest) {
      super(formParserFactory, name, loginPage, errorPage, identityManager);
      this.saveOriginalRequest = true;
      this.defaultPage = null;
      this.overrideInitial = false;
   }

   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String defaultPage, boolean overrideInitial, IdentityManager identityManager, boolean saveOriginalRequest) {
      super(formParserFactory, name, loginPage, errorPage, identityManager);
      this.saveOriginalRequest = saveOriginalRequest;
      this.defaultPage = defaultPage;
      this.overrideInitial = overrideInitial;
   }

   protected Integer servePage(HttpServerExchange exchange, String location) {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      ServletRequest req = servletRequestContext.getServletRequest();
      ServletResponse resp = servletRequestContext.getServletResponse();
      RequestDispatcher disp = req.getRequestDispatcher(location);
      exchange.getResponseHeaders().add(Headers.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
      exchange.getResponseHeaders().add(Headers.PRAGMA, "no-cache");
      exchange.getResponseHeaders().add(Headers.EXPIRES, "0");
      FormResponseWrapper respWrapper = exchange.getStatusCode() != 200 && resp instanceof HttpServletResponse ? new FormResponseWrapper((HttpServletResponse)resp) : null;

      try {
         disp.forward(req, (ServletResponse)(respWrapper != null ? respWrapper : resp));
      } catch (ServletException var9) {
         throw new RuntimeException(var9);
      } catch (IOException var10) {
         throw new RuntimeException(var10);
      }

      return respWrapper != null ? respWrapper.getStatus() : null;
   }

   protected void storeInitialLocation(HttpServerExchange exchange) {
      this.storeInitialLocation(exchange, (byte[])null, 0);
   }

   protected void storeInitialLocation(HttpServerExchange exchange, byte[] bytes, int contentLength) {
      if (this.saveOriginalRequest) {
         ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
         HttpSessionImpl httpSession = servletRequestContext.getCurrentServletContext().getSession(exchange, true);
         Session session;
         if (System.getSecurityManager() == null) {
            session = httpSession.getSession();
         } else {
            session = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(httpSession));
         }

         SessionManager manager = session.getSessionManager();
         if (this.seenSessionManagers.add(manager)) {
            manager.registerSessionListener(LISTENER);
         }

         session.setAttribute("io.undertow.servlet.form.auth.redirect.location", RedirectBuilder.redirect(exchange, exchange.getRelativePath()));
         if (bytes == null) {
            SavedRequest.trySaveRequest(exchange);
         } else {
            SavedRequest.trySaveRequest(exchange, bytes, contentLength);
         }

      }
   }

   protected void handleRedirectBack(HttpServerExchange exchange) {
      ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      HttpServletResponse resp = (HttpServletResponse)servletRequestContext.getServletResponse();
      HttpSessionImpl httpSession = servletRequestContext.getCurrentServletContext().getSession(exchange, false);
      if (httpSession != null) {
         Session session;
         if (System.getSecurityManager() == null) {
            session = httpSession.getSession();
         } else {
            session = (Session)AccessController.doPrivileged(new HttpSessionImpl.UnwrapSessionAction(httpSession));
         }

         String path = (String)session.getAttribute("io.undertow.servlet.form.auth.redirect.location");
         if ((path == null || this.overrideInitial) && this.defaultPage != null) {
            path = this.defaultPage;
         }

         if (path != null) {
            try {
               resp.sendRedirect(path);
            } catch (IOException var8) {
               throw new RuntimeException(var8);
            }
         }
      }

   }

   public static class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         String loginPage = (String)properties.get("login_page");
         String errorPage = (String)properties.get("error_page");
         String defaultPage = (String)properties.get("default_page");
         boolean overrideInitial = properties.containsKey("override_initial") ? Boolean.parseBoolean((String)properties.get("override_initial")) : false;
         boolean saveOriginal = true;
         if (properties.containsKey("save-original-request")) {
            saveOriginal = Boolean.parseBoolean((String)properties.get("save-original-request"));
         }

         return new ServletFormAuthenticationMechanism(formParserFactory, mechanismName, loginPage, errorPage, defaultPage, overrideInitial, identityManager, saveOriginal);
      }
   }

   private static class FormResponseWrapper extends HttpServletResponseWrapper {
      private int status;

      private FormResponseWrapper(HttpServletResponse wrapped) {
         super(wrapped);
         this.status = 200;
      }

      public void setStatus(int sc, String sm) {
         this.status = sc;
      }

      public void setStatus(int sc) {
         this.status = sc;
      }

      public int getStatus() {
         return this.status;
      }

      // $FF: synthetic method
      FormResponseWrapper(HttpServletResponse x0, Object x1) {
         this(x0);
      }
   }
}
