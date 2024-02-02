/*     */ package io.undertow.servlet.handlers.security;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismFactory;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.impl.FormAuthenticationMechanism;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionListener;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import io.undertow.servlet.spec.HttpSessionImpl;
/*     */ import io.undertow.servlet.util.SavedRequest;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.RedirectBuilder;
/*     */ import java.io.IOException;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import javax.servlet.RequestDispatcher;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.http.HttpServletResponse;
/*     */ import javax.servlet.http.HttpServletResponseWrapper;
/*     */ import javax.servlet.http.HttpSession;
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
/*     */ public class ServletFormAuthenticationMechanism
/*     */   extends FormAuthenticationMechanism
/*     */ {
/*  60 */   public static final AuthenticationMechanismFactory FACTORY = new Factory();
/*     */   
/*     */   private static final String SESSION_KEY = "io.undertow.servlet.form.auth.redirect.location";
/*     */   
/*     */   public static final String SAVE_ORIGINAL_REQUEST = "save-original-request";
/*     */   
/*     */   private final boolean saveOriginalRequest;
/*     */   
/*  68 */   private final Set<SessionManager> seenSessionManagers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
/*     */   
/*     */   private final String defaultPage;
/*     */   
/*     */   private final boolean overrideInitial;
/*     */   
/*  74 */   private static final SessionListener LISTENER = new SessionListener()
/*     */     {
/*     */       public void sessionCreated(Session session, HttpServerExchange exchange) {}
/*     */ 
/*     */       
/*     */       public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {}
/*     */ 
/*     */       
/*     */       public void attributeAdded(Session session, String name, Object value) {}
/*     */ 
/*     */       
/*     */       public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {}
/*     */ 
/*     */       
/*     */       public void attributeRemoved(Session session, String name, Object oldValue) {}
/*     */ 
/*     */       
/*     */       public void sessionIdChanged(Session session, String oldSessionId) {
/*  92 */         String oldLocation = (String)session.getAttribute("io.undertow.servlet.form.auth.redirect.location");
/*  93 */         if (oldLocation != null) {
/*     */ 
/*     */           
/*  96 */           String oldPart = ";jsessionid=" + oldSessionId;
/*  97 */           if (oldLocation.contains(oldPart)) {
/*  98 */             session.setAttribute("io.undertow.servlet.form.auth.redirect.location", oldLocation.replace(oldPart, ";jsessionid=" + session.getId()));
/*     */           }
/*     */         } 
/*     */       }
/*     */     };
/*     */   
/*     */   @Deprecated
/*     */   public ServletFormAuthenticationMechanism(String name, String loginPage, String errorPage) {
/* 106 */     super(name, loginPage, errorPage);
/* 107 */     this.saveOriginalRequest = true;
/* 108 */     this.defaultPage = null;
/* 109 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public ServletFormAuthenticationMechanism(String name, String loginPage, String errorPage, String postLocation) {
/* 114 */     super(name, loginPage, errorPage, postLocation);
/* 115 */     this.saveOriginalRequest = true;
/* 116 */     this.defaultPage = null;
/* 117 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation) {
/* 121 */     super(formParserFactory, name, loginPage, errorPage, postLocation);
/* 122 */     this.saveOriginalRequest = true;
/* 123 */     this.defaultPage = null;
/* 124 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
/* 128 */     super(formParserFactory, name, loginPage, errorPage);
/* 129 */     this.saveOriginalRequest = true;
/* 130 */     this.defaultPage = null;
/* 131 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager) {
/* 135 */     super(formParserFactory, name, loginPage, errorPage, identityManager);
/* 136 */     this.saveOriginalRequest = true;
/* 137 */     this.defaultPage = null;
/* 138 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager, boolean saveOriginalRequest) {
/* 142 */     super(formParserFactory, name, loginPage, errorPage, identityManager);
/* 143 */     this.saveOriginalRequest = true;
/* 144 */     this.defaultPage = null;
/* 145 */     this.overrideInitial = false;
/*     */   }
/*     */   
/*     */   public ServletFormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String defaultPage, boolean overrideInitial, IdentityManager identityManager, boolean saveOriginalRequest) {
/* 149 */     super(formParserFactory, name, loginPage, errorPage, identityManager);
/* 150 */     this.saveOriginalRequest = saveOriginalRequest;
/* 151 */     this.defaultPage = defaultPage;
/* 152 */     this.overrideInitial = overrideInitial;
/*     */   }
/*     */ 
/*     */   
/*     */   protected Integer servePage(HttpServerExchange exchange, String location) {
/* 157 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 158 */     ServletRequest req = servletRequestContext.getServletRequest();
/* 159 */     ServletResponse resp = servletRequestContext.getServletResponse();
/* 160 */     RequestDispatcher disp = req.getRequestDispatcher(location);
/*     */     
/* 162 */     exchange.getResponseHeaders().add(Headers.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
/* 163 */     exchange.getResponseHeaders().add(Headers.PRAGMA, "no-cache");
/* 164 */     exchange.getResponseHeaders().add(Headers.EXPIRES, "0");
/*     */     
/* 166 */     FormResponseWrapper respWrapper = (exchange.getStatusCode() != 200 && resp instanceof HttpServletResponse) ? new FormResponseWrapper((HttpServletResponse)resp) : null;
/*     */ 
/*     */     
/*     */     try {
/* 170 */       disp.forward(req, (respWrapper != null) ? (ServletResponse)respWrapper : resp);
/* 171 */     } catch (ServletException e) {
/* 172 */       throw new RuntimeException(e);
/* 173 */     } catch (IOException e) {
/* 174 */       throw new RuntimeException(e);
/*     */     } 
/*     */     
/* 177 */     return (respWrapper != null) ? Integer.valueOf(respWrapper.getStatus()) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void storeInitialLocation(HttpServerExchange exchange) {
/* 182 */     storeInitialLocation(exchange, null, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected void storeInitialLocation(HttpServerExchange exchange, byte[] bytes, int contentLength) {
/*     */     Session session;
/* 194 */     if (!this.saveOriginalRequest) {
/*     */       return;
/*     */     }
/* 197 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 198 */     HttpSessionImpl httpSession = servletRequestContext.getCurrentServletContext().getSession(exchange, true);
/*     */     
/* 200 */     if (System.getSecurityManager() == null) {
/* 201 */       session = httpSession.getSession();
/*     */     } else {
/* 203 */       session = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)httpSession));
/*     */     } 
/* 205 */     SessionManager manager = session.getSessionManager();
/* 206 */     if (this.seenSessionManagers.add(manager)) {
/* 207 */       manager.registerSessionListener(LISTENER);
/*     */     }
/* 209 */     session.setAttribute("io.undertow.servlet.form.auth.redirect.location", RedirectBuilder.redirect(exchange, exchange.getRelativePath()));
/* 210 */     if (bytes == null) {
/* 211 */       SavedRequest.trySaveRequest(exchange);
/*     */     } else {
/* 213 */       SavedRequest.trySaveRequest(exchange, bytes, contentLength);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void handleRedirectBack(HttpServerExchange exchange) {
/* 219 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 220 */     HttpServletResponse resp = (HttpServletResponse)servletRequestContext.getServletResponse();
/* 221 */     HttpSessionImpl httpSession = servletRequestContext.getCurrentServletContext().getSession(exchange, false);
/* 222 */     if (httpSession != null) {
/*     */       Session session;
/* 224 */       if (System.getSecurityManager() == null) {
/* 225 */         session = httpSession.getSession();
/*     */       } else {
/* 227 */         session = AccessController.<Session>doPrivileged((PrivilegedAction<Session>)new HttpSessionImpl.UnwrapSessionAction((HttpSession)httpSession));
/*     */       } 
/* 229 */       String path = (String)session.getAttribute("io.undertow.servlet.form.auth.redirect.location");
/* 230 */       if ((path == null || this.overrideInitial) && this.defaultPage != null) {
/* 231 */         path = this.defaultPage;
/*     */       }
/* 233 */       if (path != null) {
/*     */         try {
/* 235 */           resp.sendRedirect(path);
/* 236 */         } catch (IOException e) {
/* 237 */           throw new RuntimeException(e);
/*     */         } 
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class FormResponseWrapper
/*     */     extends HttpServletResponseWrapper
/*     */   {
/* 246 */     private int status = 200;
/*     */     
/*     */     private FormResponseWrapper(HttpServletResponse wrapped) {
/* 249 */       super(wrapped);
/*     */     }
/*     */ 
/*     */     
/*     */     public void setStatus(int sc, String sm) {
/* 254 */       this.status = sc;
/*     */     }
/*     */ 
/*     */     
/*     */     public void setStatus(int sc) {
/* 259 */       this.status = sc;
/*     */     }
/*     */ 
/*     */     
/*     */     public int getStatus() {
/* 264 */       return this.status;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Factory
/*     */     implements AuthenticationMechanismFactory
/*     */   {
/*     */     @Deprecated
/*     */     public Factory(IdentityManager identityManager) {}
/*     */     
/*     */     public Factory() {}
/*     */     
/*     */     public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
/* 278 */       String loginPage = properties.get("login_page");
/* 279 */       String errorPage = properties.get("error_page");
/* 280 */       String defaultPage = properties.get("default_page");
/*     */       
/* 282 */       boolean overrideInitial = properties.containsKey("override_initial") ? Boolean.parseBoolean(properties.get("override_initial")) : false;
/* 283 */       boolean saveOriginal = true;
/* 284 */       if (properties.containsKey("save-original-request")) {
/* 285 */         saveOriginal = Boolean.parseBoolean(properties.get("save-original-request"));
/*     */       }
/* 287 */       return (AuthenticationMechanism)new ServletFormAuthenticationMechanism(formParserFactory, mechanismName, loginPage, errorPage, defaultPage, overrideInitial, identityManager, saveOriginal);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletFormAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */