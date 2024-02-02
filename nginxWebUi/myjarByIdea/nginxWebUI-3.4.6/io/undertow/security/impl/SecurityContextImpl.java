package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismContext;
import io.undertow.security.api.AuthenticationMode;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.HttpServerExchange;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class SecurityContextImpl extends AbstractSecurityContext implements AuthenticationMechanismContext {
   private static final RuntimePermission PERMISSION = new RuntimePermission("MODIFY_UNDERTOW_SECURITY_CONTEXT");
   private AuthenticationState authenticationState;
   private final AuthenticationMode authenticationMode;
   private String programaticMechName;
   private Node<AuthenticationMechanism> authMechanisms;
   private final IdentityManager identityManager;

   public SecurityContextImpl(HttpServerExchange exchange, IdentityManager identityManager) {
      this(exchange, AuthenticationMode.PRO_ACTIVE, identityManager);
   }

   public SecurityContextImpl(HttpServerExchange exchange, AuthenticationMode authenticationMode, IdentityManager identityManager) {
      super(exchange);
      this.authenticationState = SecurityContextImpl.AuthenticationState.NOT_ATTEMPTED;
      this.programaticMechName = "Programatic";
      this.authMechanisms = null;
      this.authenticationMode = authenticationMode;
      this.identityManager = identityManager;
      if (System.getSecurityManager() != null) {
         System.getSecurityManager().checkPermission(PERMISSION);
      }

   }

   public boolean authenticate() {
      UndertowLogger.SECURITY_LOGGER.debugf("Attempting to authenticate %s, authentication required: %s", this.exchange.getRequestPath(), this.isAuthenticationRequired());
      if (this.authenticationState == SecurityContextImpl.AuthenticationState.ATTEMPTED || this.authenticationState == SecurityContextImpl.AuthenticationState.CHALLENGE_SENT && !this.exchange.isResponseStarted()) {
         this.authenticationState = SecurityContextImpl.AuthenticationState.NOT_ATTEMPTED;
      }

      return !this.authTransition();
   }

   private boolean authTransition() {
      if (this.authTransitionRequired()) {
         switch (this.authenticationState) {
            case NOT_ATTEMPTED:
               this.authenticationState = this.attemptAuthentication();
               break;
            case ATTEMPTED:
               this.authenticationState = this.sendChallenges();
               break;
            default:
               throw new IllegalStateException("It should not be possible to reach this.");
         }

         return this.authTransition();
      } else {
         UndertowLogger.SECURITY_LOGGER.debugf("Authentication result was %s for %s", this.authenticationState, this.exchange.getRequestPath());
         switch (this.authenticationState) {
            case NOT_ATTEMPTED:
            case ATTEMPTED:
            case AUTHENTICATED:
               return false;
            default:
               return true;
         }
      }
   }

   private AuthenticationState attemptAuthentication() {
      return (new AuthAttempter(this.authMechanisms, this.exchange)).transition();
   }

   private AuthenticationState sendChallenges() {
      UndertowLogger.SECURITY_LOGGER.debugf("Sending authentication challenge for %s", this.exchange);
      return (new ChallengeSender(this.authMechanisms, this.exchange)).transition();
   }

   private boolean authTransitionRequired() {
      switch (this.authenticationState) {
         case NOT_ATTEMPTED:
            return this.isAuthenticationRequired() || this.authenticationMode == AuthenticationMode.PRO_ACTIVE;
         case ATTEMPTED:
            return this.isAuthenticationRequired();
         default:
            return false;
      }
   }

   public void setProgramaticMechName(String programaticMechName) {
      this.programaticMechName = programaticMechName;
   }

   public void addAuthenticationMechanism(AuthenticationMechanism handler) {
      if (this.authMechanisms == null) {
         this.authMechanisms = new Node(handler);
      } else {
         Node cur;
         for(cur = this.authMechanisms; cur.next != null; cur = cur.next) {
         }

         cur.next = new Node(handler);
      }

   }

   /** @deprecated */
   @Deprecated
   public List<AuthenticationMechanism> getAuthenticationMechanisms() {
      List<AuthenticationMechanism> ret = new LinkedList();

      for(Node<AuthenticationMechanism> cur = this.authMechanisms; cur != null; cur = cur.next) {
         ret.add(cur.item);
      }

      return Collections.unmodifiableList(ret);
   }

   /** @deprecated */
   @Deprecated
   public IdentityManager getIdentityManager() {
      return this.identityManager;
   }

   public boolean login(final String username, final String password) {
      UndertowLogger.SECURITY_LOGGER.debugf("Attempting programatic login for user %s for request %s", username, this.exchange);
      Account account;
      if (System.getSecurityManager() == null) {
         account = this.identityManager.verify(username, new PasswordCredential(password.toCharArray()));
      } else {
         account = (Account)AccessController.doPrivileged(new PrivilegedAction<Account>() {
            public Account run() {
               return SecurityContextImpl.this.identityManager.verify(username, new PasswordCredential(password.toCharArray()));
            }
         });
      }

      if (account == null) {
         return false;
      } else {
         this.authenticationComplete(account, this.programaticMechName, true);
         this.authenticationState = SecurityContextImpl.AuthenticationState.AUTHENTICATED;
         return true;
      }
   }

   public void logout() {
      Account authenticatedAccount = this.getAuthenticatedAccount();
      if (authenticatedAccount != null) {
         UndertowLogger.SECURITY_LOGGER.debugf("Logging out user %s for %s", authenticatedAccount.getPrincipal().getName(), this.exchange);
      } else {
         UndertowLogger.SECURITY_LOGGER.debugf("Logout called with no authenticated user in exchange %s", this.exchange);
      }

      super.logout();
      this.authenticationState = SecurityContextImpl.AuthenticationState.NOT_ATTEMPTED;
   }

   private static final class Node<T> {
      final T item;
      Node<T> next;

      private Node(T item) {
         this.item = item;
      }

      // $FF: synthetic method
      Node(Object x0, Object x1) {
         this(x0);
      }
   }

   static enum AuthenticationState {
      NOT_ATTEMPTED,
      ATTEMPTED,
      AUTHENTICATED,
      CHALLENGE_SENT;
   }

   private class ChallengeSender {
      private Node<AuthenticationMechanism> currentMethod;
      private final HttpServerExchange exchange;
      private Integer chosenStatusCode;
      private boolean challengeSent;

      private ChallengeSender(Node<AuthenticationMechanism> currentMethod, HttpServerExchange exchange) {
         this.chosenStatusCode = null;
         this.challengeSent = false;
         this.exchange = exchange;
         this.currentMethod = currentMethod;
      }

      private AuthenticationState transition() {
         if (this.currentMethod != null) {
            AuthenticationMechanism mechanism = (AuthenticationMechanism)this.currentMethod.item;
            this.currentMethod = this.currentMethod.next;
            AuthenticationMechanism.ChallengeResult result = mechanism.sendChallenge(this.exchange, SecurityContextImpl.this);
            if (result == null) {
               throw UndertowMessages.MESSAGES.sendChallengeReturnedNull(mechanism);
            } else {
               if (result.isChallengeSent()) {
                  this.challengeSent = true;
                  Integer desiredCode = result.getDesiredResponseCode();
                  if (desiredCode != null && (this.chosenStatusCode == null || this.chosenStatusCode.equals(200))) {
                     this.chosenStatusCode = desiredCode;
                     if (!this.chosenStatusCode.equals(200) && !this.exchange.isResponseStarted()) {
                        this.exchange.setStatusCode(this.chosenStatusCode);
                     }
                  }
               }

               return this.transition();
            }
         } else {
            if (!this.exchange.isResponseStarted()) {
               if (this.chosenStatusCode == null) {
                  if (!this.challengeSent) {
                     this.exchange.setStatusCode(403);
                  }
               } else if (this.chosenStatusCode.equals(200)) {
                  this.exchange.setStatusCode(this.chosenStatusCode);
               }
            }

            return SecurityContextImpl.AuthenticationState.CHALLENGE_SENT;
         }
      }

      // $FF: synthetic method
      ChallengeSender(Node x1, HttpServerExchange x2, Object x3) {
         this(x1, x2);
      }
   }

   private class AuthAttempter {
      private Node<AuthenticationMechanism> currentMethod;
      private final HttpServerExchange exchange;

      private AuthAttempter(Node<AuthenticationMechanism> currentMethod, HttpServerExchange exchange) {
         this.exchange = exchange;
         this.currentMethod = currentMethod;
      }

      private AuthenticationState transition() {
         if (this.currentMethod != null) {
            AuthenticationMechanism mechanism = (AuthenticationMechanism)this.currentMethod.item;
            this.currentMethod = this.currentMethod.next;
            AuthenticationMechanism.AuthenticationMechanismOutcome outcome = mechanism.authenticate(this.exchange, SecurityContextImpl.this);
            if (UndertowLogger.SECURITY_LOGGER.isDebugEnabled()) {
               UndertowLogger.SECURITY_LOGGER.debugf("Authentication outcome was %s with method %s for %s", outcome, mechanism, this.exchange.getRequestURI());
               if (UndertowLogger.SECURITY_LOGGER.isTraceEnabled()) {
                  UndertowLogger.SECURITY_LOGGER.tracef("Contents of exchange after authentication attempt is %s", this.exchange);
               }
            }

            if (outcome == null) {
               throw UndertowMessages.MESSAGES.authMechanismOutcomeNull();
            } else {
               switch (outcome) {
                  case AUTHENTICATED:
                     return SecurityContextImpl.AuthenticationState.AUTHENTICATED;
                  case NOT_AUTHENTICATED:
                     SecurityContextImpl.this.setAuthenticationRequired();
                     return SecurityContextImpl.AuthenticationState.ATTEMPTED;
                  case NOT_ATTEMPTED:
                     return this.transition();
                  default:
                     throw new IllegalStateException();
               }
            }
         } else {
            return SecurityContextImpl.AuthenticationState.ATTEMPTED;
         }
      }

      // $FF: synthetic method
      AuthAttempter(Node x1, HttpServerExchange x2, Object x3) {
         this(x1, x2);
      }
   }
}
