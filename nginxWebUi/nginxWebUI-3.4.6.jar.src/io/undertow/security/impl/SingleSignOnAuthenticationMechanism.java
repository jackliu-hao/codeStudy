/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.NotificationReceiver;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.api.SecurityNotification;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.server.ConduitWrapper;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.Cookie;
/*     */ import io.undertow.server.handlers.CookieImpl;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.server.session.SessionListener;
/*     */ import io.undertow.server.session.SessionManager;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.Sessions;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Set;
/*     */ import java.util.WeakHashMap;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.conduits.Conduit;
/*     */ import org.xnio.conduits.StreamSinkConduit;
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
/*     */ public class SingleSignOnAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  55 */   private static final Logger log = Logger.getLogger(SingleSignOnAuthenticationMechanism.class);
/*     */   
/*  57 */   private static final String SSO_SESSION_ATTRIBUTE = SingleSignOnAuthenticationMechanism.class.getName() + ".SSOID";
/*     */ 
/*     */   
/*  60 */   private final Set<SessionManager> seenSessionManagers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));
/*     */   
/*  62 */   private String cookieName = "JSESSIONIDSSO";
/*     */   private boolean httpOnly;
/*     */   private boolean secure;
/*     */   private String domain;
/*     */   private String path;
/*  67 */   private final SessionInvalidationListener listener = new SessionInvalidationListener();
/*  68 */   private final ResponseListener responseListener = new ResponseListener();
/*     */   private final SingleSignOnManager singleSignOnManager;
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism(SingleSignOnManager storage) {
/*  73 */     this(storage, null);
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism(SingleSignOnManager storage, IdentityManager identityManager) {
/*  77 */     this.singleSignOnManager = storage;
/*  78 */     this.identityManager = identityManager;
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/*  83 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/*  88 */     Cookie cookie = null;
/*  89 */     for (Cookie c : exchange.requestCookies()) {
/*  90 */       if (this.cookieName.equals(c.getName())) {
/*  91 */         cookie = c;
/*     */       }
/*     */     } 
/*  94 */     if (cookie != null) {
/*  95 */       String ssoId = cookie.getValue();
/*  96 */       log.tracef("Found SSO cookie %s", ssoId);
/*  97 */       try (SingleSignOn sso = this.singleSignOnManager.findSingleSignOn(ssoId)) {
/*  98 */         if (sso != null) {
/*  99 */           if (log.isTraceEnabled()) {
/* 100 */             log.tracef("SSO session with ID: %s found.", ssoId);
/*     */           }
/* 102 */           Account verified = getIdentityManager(securityContext).verify(sso.getAccount());
/* 103 */           if (verified == null) {
/* 104 */             if (log.isTraceEnabled()) {
/* 105 */               log.tracef("Account not found. Returning 'not attempted' here.", new Object[0]);
/*     */             }
/*     */             
/* 108 */             return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */           } 
/* 110 */           Session session = getSession(exchange);
/* 111 */           registerSessionIfRequired(sso, session);
/* 112 */           securityContext.authenticationComplete(verified, sso.getMechanismName(), false);
/* 113 */           securityContext.registerNotificationReceiver(new NotificationReceiver()
/*     */               {
/*     */                 public void handleNotification(SecurityNotification notification) {
/* 116 */                   if (notification.getEventType() == SecurityNotification.EventType.LOGGED_OUT) {
/* 117 */                     SingleSignOnAuthenticationMechanism.this.singleSignOnManager.removeSingleSignOn(sso);
/*     */                   }
/*     */                 }
/*     */               });
/* 121 */           log.tracef("Authenticated account %s using SSO", verified.getPrincipal().getName());
/* 122 */           return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */         } 
/*     */       } 
/* 125 */       clearSsoCookie(exchange);
/*     */     } 
/* 127 */     exchange.addResponseWrapper(this.responseListener);
/* 128 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */   
/*     */   private void registerSessionIfRequired(SingleSignOn sso, Session session) {
/* 132 */     if (!sso.contains(session)) {
/* 133 */       if (log.isTraceEnabled()) {
/* 134 */         log.tracef("Session %s added to SSO %s", session.getId(), sso.getId());
/*     */       }
/* 136 */       sso.add(session);
/*     */     } 
/* 138 */     if (session.getAttribute(SSO_SESSION_ATTRIBUTE) == null) {
/* 139 */       if (log.isTraceEnabled()) {
/* 140 */         log.tracef("SSO_SESSION_ATTRIBUTE not found. Creating it with SSO ID %s as value.", sso.getId());
/*     */       }
/* 142 */       session.setAttribute(SSO_SESSION_ATTRIBUTE, sso.getId());
/*     */     } 
/* 144 */     SessionManager manager = session.getSessionManager();
/* 145 */     if (this.seenSessionManagers.add(manager)) {
/* 146 */       manager.registerSessionListener(this.listener);
/*     */     }
/*     */   }
/*     */   
/*     */   private void clearSsoCookie(HttpServerExchange exchange) {
/* 151 */     exchange.setResponseCookie((Cookie)(new CookieImpl(this.cookieName)).setMaxAge(Integer.valueOf(0)).setHttpOnly(this.httpOnly).setSecure(this.secure).setDomain(this.domain).setPath(this.path));
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 156 */     return AuthenticationMechanism.ChallengeResult.NOT_SENT;
/*     */   }
/*     */   
/*     */   protected Session getSession(HttpServerExchange exchange) {
/* 160 */     return Sessions.getOrCreateSession(exchange);
/*     */   }
/*     */   
/*     */   final class ResponseListener
/*     */     implements ConduitWrapper<StreamSinkConduit>
/*     */   {
/*     */     public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
/* 167 */       SecurityContext sc = exchange.getSecurityContext();
/* 168 */       Account account = sc.getAuthenticatedAccount();
/* 169 */       if (account != null) {
/* 170 */         try (SingleSignOn sso = SingleSignOnAuthenticationMechanism.this.singleSignOnManager.createSingleSignOn(account, sc.getMechanismName())) {
/* 171 */           Session session = SingleSignOnAuthenticationMechanism.this.getSession(exchange);
/* 172 */           SingleSignOnAuthenticationMechanism.this.registerSessionIfRequired(sso, session);
/* 173 */           exchange.setResponseCookie((Cookie)(new CookieImpl(SingleSignOnAuthenticationMechanism.this.cookieName, sso.getId())).setHttpOnly(SingleSignOnAuthenticationMechanism.this.httpOnly).setSecure(SingleSignOnAuthenticationMechanism.this.secure).setDomain(SingleSignOnAuthenticationMechanism.this.domain).setPath(SingleSignOnAuthenticationMechanism.this.path));
/*     */         } 
/*     */       }
/* 176 */       return (StreamSinkConduit)factory.create();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final class SessionInvalidationListener
/*     */     implements SessionListener
/*     */   {
/*     */     public void sessionCreated(Session session, HttpServerExchange exchange) {}
/*     */ 
/*     */     
/*     */     public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
/* 189 */       String ssoId = (String)session.getAttribute(SingleSignOnAuthenticationMechanism.SSO_SESSION_ATTRIBUTE);
/* 190 */       if (ssoId != null) {
/* 191 */         if (SingleSignOnAuthenticationMechanism.log.isTraceEnabled()) {
/* 192 */           SingleSignOnAuthenticationMechanism.log.tracef("Removing SSO ID %s from destroyed session %s.", ssoId, session.getId());
/*     */         }
/* 194 */         List<Session> sessionsToRemove = new LinkedList<>();
/* 195 */         try (SingleSignOn sso = SingleSignOnAuthenticationMechanism.this.singleSignOnManager.findSingleSignOn(ssoId)) {
/* 196 */           if (sso != null) {
/* 197 */             sso.remove(session);
/* 198 */             if (reason == SessionListener.SessionDestroyedReason.INVALIDATED) {
/* 199 */               for (Session associatedSession : sso) {
/* 200 */                 sso.remove(associatedSession);
/* 201 */                 sessionsToRemove.add(associatedSession);
/*     */               } 
/*     */             }
/*     */             
/* 205 */             if (!sso.iterator().hasNext()) {
/* 206 */               SingleSignOnAuthenticationMechanism.this.singleSignOnManager.removeSingleSignOn(sso);
/*     */             }
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 212 */         for (Session sessionToRemove : sessionsToRemove) {
/* 213 */           sessionToRemove.invalidate(null);
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void attributeAdded(Session session, String name, Object value) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void attributeUpdated(Session session, String name, Object newValue, Object oldValue) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void attributeRemoved(Session session, String name, Object oldValue) {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void sessionIdChanged(Session session, String oldSessionId) {}
/*     */   }
/*     */ 
/*     */   
/*     */   public String getCookieName() {
/* 237 */     return this.cookieName;
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism setCookieName(String cookieName) {
/* 241 */     this.cookieName = cookieName;
/* 242 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isHttpOnly() {
/* 246 */     return this.httpOnly;
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism setHttpOnly(boolean httpOnly) {
/* 250 */     this.httpOnly = httpOnly;
/* 251 */     return this;
/*     */   }
/*     */   
/*     */   public boolean isSecure() {
/* 255 */     return this.secure;
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism setSecure(boolean secure) {
/* 259 */     this.secure = secure;
/* 260 */     return this;
/*     */   }
/*     */   
/*     */   public String getDomain() {
/* 264 */     return this.domain;
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism setDomain(String domain) {
/* 268 */     this.domain = domain;
/* 269 */     return this;
/*     */   }
/*     */   
/*     */   public String getPath() {
/* 273 */     return this.path;
/*     */   }
/*     */   
/*     */   public SingleSignOnAuthenticationMechanism setPath(String path) {
/* 277 */     this.path = path;
/* 278 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SingleSignOnAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */