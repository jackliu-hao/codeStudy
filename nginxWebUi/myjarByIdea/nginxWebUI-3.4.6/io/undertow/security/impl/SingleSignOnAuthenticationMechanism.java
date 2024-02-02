package io.undertow.security.impl;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.NotificationReceiver;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.api.SecurityNotification;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.ConduitWrapper;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.server.session.Session;
import io.undertow.server.session.SessionListener;
import io.undertow.server.session.SessionManager;
import io.undertow.util.ConduitFactory;
import io.undertow.util.Sessions;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;
import org.jboss.logging.Logger;
import org.xnio.conduits.StreamSinkConduit;

public class SingleSignOnAuthenticationMechanism implements AuthenticationMechanism {
   private static final Logger log = Logger.getLogger(SingleSignOnAuthenticationMechanism.class);
   private static final String SSO_SESSION_ATTRIBUTE = SingleSignOnAuthenticationMechanism.class.getName() + ".SSOID";
   private final Set<SessionManager> seenSessionManagers;
   private String cookieName;
   private boolean httpOnly;
   private boolean secure;
   private String domain;
   private String path;
   private final SessionInvalidationListener listener;
   private final ResponseListener responseListener;
   private final SingleSignOnManager singleSignOnManager;
   private final IdentityManager identityManager;

   public SingleSignOnAuthenticationMechanism(SingleSignOnManager storage) {
      this(storage, (IdentityManager)null);
   }

   public SingleSignOnAuthenticationMechanism(SingleSignOnManager storage, IdentityManager identityManager) {
      this.seenSessionManagers = Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap()));
      this.cookieName = "JSESSIONIDSSO";
      this.listener = new SessionInvalidationListener();
      this.responseListener = new ResponseListener();
      this.singleSignOnManager = storage;
      this.identityManager = identityManager;
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      Cookie cookie = null;
      Iterator var4 = exchange.requestCookies().iterator();

      while(var4.hasNext()) {
         Cookie c = (Cookie)var4.next();
         if (this.cookieName.equals(c.getName())) {
            cookie = c;
         }
      }

      if (cookie != null) {
         String ssoId = cookie.getValue();
         log.tracef("Found SSO cookie %s", (Object)ssoId);
         final SingleSignOn sso = this.singleSignOnManager.findSingleSignOn(ssoId);
         Throwable var6 = null;

         try {
            if (sso != null) {
               if (log.isTraceEnabled()) {
                  log.tracef("SSO session with ID: %s found.", (Object)ssoId);
               }

               Account verified = this.getIdentityManager(securityContext).verify(sso.getAccount());
               if (verified == null) {
                  if (log.isTraceEnabled()) {
                     log.tracef("Account not found. Returning 'not attempted' here.");
                  }

                  AuthenticationMechanism.AuthenticationMechanismOutcome var24 = AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
                  return var24;
               }

               Session session = this.getSession(exchange);
               this.registerSessionIfRequired(sso, session);
               securityContext.authenticationComplete(verified, sso.getMechanismName(), false);
               securityContext.registerNotificationReceiver(new NotificationReceiver() {
                  public void handleNotification(SecurityNotification notification) {
                     if (notification.getEventType() == SecurityNotification.EventType.LOGGED_OUT) {
                        SingleSignOnAuthenticationMechanism.this.singleSignOnManager.removeSingleSignOn(sso);
                     }

                  }
               });
               log.tracef("Authenticated account %s using SSO", (Object)verified.getPrincipal().getName());
               AuthenticationMechanism.AuthenticationMechanismOutcome var9 = AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
               return var9;
            }
         } catch (Throwable var20) {
            var6 = var20;
            throw var20;
         } finally {
            if (sso != null) {
               if (var6 != null) {
                  try {
                     sso.close();
                  } catch (Throwable var19) {
                     var6.addSuppressed(var19);
                  }
               } else {
                  sso.close();
               }
            }

         }

         this.clearSsoCookie(exchange);
      }

      exchange.addResponseWrapper(this.responseListener);
      return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   private void registerSessionIfRequired(SingleSignOn sso, Session session) {
      if (!sso.contains(session)) {
         if (log.isTraceEnabled()) {
            log.tracef((String)"Session %s added to SSO %s", (Object)session.getId(), (Object)sso.getId());
         }

         sso.add(session);
      }

      if (session.getAttribute(SSO_SESSION_ATTRIBUTE) == null) {
         if (log.isTraceEnabled()) {
            log.tracef("SSO_SESSION_ATTRIBUTE not found. Creating it with SSO ID %s as value.", (Object)sso.getId());
         }

         session.setAttribute(SSO_SESSION_ATTRIBUTE, sso.getId());
      }

      SessionManager manager = session.getSessionManager();
      if (this.seenSessionManagers.add(manager)) {
         manager.registerSessionListener(this.listener);
      }

   }

   private void clearSsoCookie(HttpServerExchange exchange) {
      exchange.setResponseCookie((new CookieImpl(this.cookieName)).setMaxAge(0).setHttpOnly(this.httpOnly).setSecure(this.secure).setDomain(this.domain).setPath(this.path));
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      return AuthenticationMechanism.ChallengeResult.NOT_SENT;
   }

   protected Session getSession(HttpServerExchange exchange) {
      return Sessions.getOrCreateSession(exchange);
   }

   public String getCookieName() {
      return this.cookieName;
   }

   public SingleSignOnAuthenticationMechanism setCookieName(String cookieName) {
      this.cookieName = cookieName;
      return this;
   }

   public boolean isHttpOnly() {
      return this.httpOnly;
   }

   public SingleSignOnAuthenticationMechanism setHttpOnly(boolean httpOnly) {
      this.httpOnly = httpOnly;
      return this;
   }

   public boolean isSecure() {
      return this.secure;
   }

   public SingleSignOnAuthenticationMechanism setSecure(boolean secure) {
      this.secure = secure;
      return this;
   }

   public String getDomain() {
      return this.domain;
   }

   public SingleSignOnAuthenticationMechanism setDomain(String domain) {
      this.domain = domain;
      return this;
   }

   public String getPath() {
      return this.path;
   }

   public SingleSignOnAuthenticationMechanism setPath(String path) {
      this.path = path;
      return this;
   }

   final class SessionInvalidationListener implements SessionListener {
      public void sessionCreated(Session session, HttpServerExchange exchange) {
      }

      public void sessionDestroyed(Session session, HttpServerExchange exchange, SessionListener.SessionDestroyedReason reason) {
         String ssoId = (String)session.getAttribute(SingleSignOnAuthenticationMechanism.SSO_SESSION_ATTRIBUTE);
         if (ssoId != null) {
            if (SingleSignOnAuthenticationMechanism.log.isTraceEnabled()) {
               SingleSignOnAuthenticationMechanism.log.tracef((String)"Removing SSO ID %s from destroyed session %s.", (Object)ssoId, (Object)session.getId());
            }

            List<Session> sessionsToRemove = new LinkedList();
            SingleSignOn sso = SingleSignOnAuthenticationMechanism.this.singleSignOnManager.findSingleSignOn(ssoId);
            Throwable var7 = null;

            try {
               if (sso != null) {
                  sso.remove(session);
                  if (reason == SessionListener.SessionDestroyedReason.INVALIDATED) {
                     Iterator var8 = sso.iterator();

                     while(var8.hasNext()) {
                        Session associatedSession = (Session)var8.next();
                        sso.remove(associatedSession);
                        sessionsToRemove.add(associatedSession);
                     }
                  }

                  if (!sso.iterator().hasNext()) {
                     SingleSignOnAuthenticationMechanism.this.singleSignOnManager.removeSingleSignOn(sso);
                  }
               }
            } catch (Throwable var17) {
               var7 = var17;
               throw var17;
            } finally {
               if (sso != null) {
                  if (var7 != null) {
                     try {
                        sso.close();
                     } catch (Throwable var16) {
                        var7.addSuppressed(var16);
                     }
                  } else {
                     sso.close();
                  }
               }

            }

            Iterator var19 = sessionsToRemove.iterator();

            while(var19.hasNext()) {
               Session sessionToRemove = (Session)var19.next();
               sessionToRemove.invalidate((HttpServerExchange)null);
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
      }
   }

   final class ResponseListener implements ConduitWrapper<StreamSinkConduit> {
      public StreamSinkConduit wrap(ConduitFactory<StreamSinkConduit> factory, HttpServerExchange exchange) {
         SecurityContext sc = exchange.getSecurityContext();
         Account account = sc.getAuthenticatedAccount();
         if (account != null) {
            SingleSignOn sso = SingleSignOnAuthenticationMechanism.this.singleSignOnManager.createSingleSignOn(account, sc.getMechanismName());
            Throwable var6 = null;

            try {
               Session session = SingleSignOnAuthenticationMechanism.this.getSession(exchange);
               SingleSignOnAuthenticationMechanism.this.registerSessionIfRequired(sso, session);
               exchange.setResponseCookie((new CookieImpl(SingleSignOnAuthenticationMechanism.this.cookieName, sso.getId())).setHttpOnly(SingleSignOnAuthenticationMechanism.this.httpOnly).setSecure(SingleSignOnAuthenticationMechanism.this.secure).setDomain(SingleSignOnAuthenticationMechanism.this.domain).setPath(SingleSignOnAuthenticationMechanism.this.path));
            } catch (Throwable var15) {
               var6 = var15;
               throw var15;
            } finally {
               if (sso != null) {
                  if (var6 != null) {
                     try {
                        sso.close();
                     } catch (Throwable var14) {
                        var6.addSuppressed(var14);
                     }
                  } else {
                     sso.close();
                  }
               }

            }
         }

         return (StreamSinkConduit)factory.create();
      }
   }
}
