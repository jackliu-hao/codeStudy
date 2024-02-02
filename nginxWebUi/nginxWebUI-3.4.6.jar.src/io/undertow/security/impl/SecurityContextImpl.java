/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.AuthenticationMechanismContext;
/*     */ import io.undertow.security.api.AuthenticationMode;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.idm.PasswordCredential;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.Collections;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
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
/*     */ public class SecurityContextImpl
/*     */   extends AbstractSecurityContext
/*     */   implements AuthenticationMechanismContext
/*     */ {
/*  48 */   private static final RuntimePermission PERMISSION = new RuntimePermission("MODIFY_UNDERTOW_SECURITY_CONTEXT");
/*     */   
/*  50 */   private AuthenticationState authenticationState = AuthenticationState.NOT_ATTEMPTED;
/*     */   
/*     */   private final AuthenticationMode authenticationMode;
/*  53 */   private String programaticMechName = "Programatic";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   private Node<AuthenticationMechanism> authMechanisms = null;
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   public SecurityContextImpl(HttpServerExchange exchange, IdentityManager identityManager) {
/*  63 */     this(exchange, AuthenticationMode.PRO_ACTIVE, identityManager);
/*     */   }
/*     */   
/*     */   public SecurityContextImpl(HttpServerExchange exchange, AuthenticationMode authenticationMode, IdentityManager identityManager) {
/*  67 */     super(exchange);
/*  68 */     this.authenticationMode = authenticationMode;
/*  69 */     this.identityManager = identityManager;
/*  70 */     if (System.getSecurityManager() != null) {
/*  71 */       System.getSecurityManager().checkPermission(PERMISSION);
/*     */     }
/*     */   }
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
/*     */   public boolean authenticate() {
/*  86 */     UndertowLogger.SECURITY_LOGGER.debugf("Attempting to authenticate %s, authentication required: %s", this.exchange.getRequestPath(), Boolean.valueOf(isAuthenticationRequired()));
/*  87 */     if (this.authenticationState == AuthenticationState.ATTEMPTED || (this.authenticationState == AuthenticationState.CHALLENGE_SENT && !this.exchange.isResponseStarted()))
/*     */     {
/*     */       
/*  90 */       this.authenticationState = AuthenticationState.NOT_ATTEMPTED;
/*     */     }
/*  92 */     return !authTransition();
/*     */   }
/*     */   
/*     */   private boolean authTransition() {
/*  96 */     if (authTransitionRequired()) {
/*  97 */       switch (this.authenticationState) {
/*     */         case AUTHENTICATED:
/*  99 */           this.authenticationState = attemptAuthentication();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 107 */           return authTransition();case NOT_AUTHENTICATED: this.authenticationState = sendChallenges(); return authTransition();
/*     */       }  throw new IllegalStateException("It should not be possible to reach this.");
/*     */     } 
/* 110 */     UndertowLogger.SECURITY_LOGGER.debugf("Authentication result was %s for %s", this.authenticationState, this.exchange.getRequestPath());
/*     */     
/* 112 */     switch (this.authenticationState) {
/*     */       case AUTHENTICATED:
/*     */       case NOT_AUTHENTICATED:
/*     */       case NOT_ATTEMPTED:
/* 116 */         return false;
/*     */     } 
/*     */     
/* 119 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private AuthenticationState attemptAuthentication() {
/* 125 */     return (new AuthAttempter(this.authMechanisms, this.exchange)).transition();
/*     */   }
/*     */   
/*     */   private AuthenticationState sendChallenges() {
/* 129 */     UndertowLogger.SECURITY_LOGGER.debugf("Sending authentication challenge for %s", this.exchange);
/* 130 */     return (new ChallengeSender(this.authMechanisms, this.exchange)).transition();
/*     */   }
/*     */   
/*     */   private boolean authTransitionRequired() {
/* 134 */     switch (this.authenticationState) {
/*     */ 
/*     */       
/*     */       case AUTHENTICATED:
/* 138 */         return (isAuthenticationRequired() || this.authenticationMode == AuthenticationMode.PRO_ACTIVE);
/*     */ 
/*     */       
/*     */       case NOT_AUTHENTICATED:
/* 142 */         return isAuthenticationRequired();
/*     */     } 
/*     */ 
/*     */     
/* 146 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProgramaticMechName(String programaticMechName) {
/* 156 */     this.programaticMechName = programaticMechName;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAuthenticationMechanism(AuthenticationMechanism handler) {
/* 162 */     if (this.authMechanisms == null) {
/* 163 */       this.authMechanisms = new Node<>(handler);
/*     */     } else {
/* 165 */       Node<AuthenticationMechanism> cur = this.authMechanisms;
/* 166 */       while (cur.next != null) {
/* 167 */         cur = cur.next;
/*     */       }
/* 169 */       cur.next = new Node<>(handler);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public List<AuthenticationMechanism> getAuthenticationMechanisms() {
/* 176 */     List<AuthenticationMechanism> ret = new LinkedList<>();
/* 177 */     Node<AuthenticationMechanism> cur = this.authMechanisms;
/* 178 */     while (cur != null) {
/* 179 */       ret.add((AuthenticationMechanism)cur.item);
/* 180 */       cur = cur.next;
/*     */     } 
/* 182 */     return Collections.unmodifiableList(ret);
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public IdentityManager getIdentityManager() {
/* 188 */     return this.identityManager;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean login(final String username, final String password) {
/*     */     Account account;
/* 194 */     UndertowLogger.SECURITY_LOGGER.debugf("Attempting programatic login for user %s for request %s", username, this.exchange);
/*     */ 
/*     */     
/* 197 */     if (System.getSecurityManager() == null) {
/* 198 */       account = this.identityManager.verify(username, (Credential)new PasswordCredential(password.toCharArray()));
/*     */     } else {
/* 200 */       account = AccessController.<Account>doPrivileged(new PrivilegedAction<Account>()
/*     */           {
/*     */             public Account run() {
/* 203 */               return SecurityContextImpl.this.identityManager.verify(username, (Credential)new PasswordCredential(password.toCharArray()));
/*     */             }
/*     */           });
/*     */     } 
/*     */     
/* 208 */     if (account == null) {
/* 209 */       return false;
/*     */     }
/*     */     
/* 212 */     authenticationComplete(account, this.programaticMechName, true);
/* 213 */     this.authenticationState = AuthenticationState.AUTHENTICATED;
/*     */     
/* 215 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public void logout() {
/* 220 */     Account authenticatedAccount = getAuthenticatedAccount();
/* 221 */     if (authenticatedAccount != null) {
/* 222 */       UndertowLogger.SECURITY_LOGGER.debugf("Logging out user %s for %s", authenticatedAccount.getPrincipal().getName(), this.exchange);
/*     */     } else {
/* 224 */       UndertowLogger.SECURITY_LOGGER.debugf("Logout called with no authenticated user in exchange %s", this.exchange);
/*     */     } 
/* 226 */     super.logout();
/* 227 */     this.authenticationState = AuthenticationState.NOT_ATTEMPTED;
/*     */   }
/*     */ 
/*     */   
/*     */   private class AuthAttempter
/*     */   {
/*     */     private SecurityContextImpl.Node<AuthenticationMechanism> currentMethod;
/*     */     private final HttpServerExchange exchange;
/*     */     
/*     */     private AuthAttempter(SecurityContextImpl.Node<AuthenticationMechanism> currentMethod, HttpServerExchange exchange) {
/* 237 */       this.exchange = exchange;
/* 238 */       this.currentMethod = currentMethod;
/*     */     }
/*     */     
/*     */     private SecurityContextImpl.AuthenticationState transition() {
/* 242 */       if (this.currentMethod != null) {
/* 243 */         AuthenticationMechanism mechanism = (AuthenticationMechanism)this.currentMethod.item;
/* 244 */         this.currentMethod = this.currentMethod.next;
/* 245 */         AuthenticationMechanism.AuthenticationMechanismOutcome outcome = mechanism.authenticate(this.exchange, SecurityContextImpl.this);
/* 246 */         if (UndertowLogger.SECURITY_LOGGER.isDebugEnabled()) {
/* 247 */           UndertowLogger.SECURITY_LOGGER.debugf("Authentication outcome was %s with method %s for %s", outcome, mechanism, this.exchange.getRequestURI());
/* 248 */           if (UndertowLogger.SECURITY_LOGGER.isTraceEnabled()) {
/* 249 */             UndertowLogger.SECURITY_LOGGER.tracef("Contents of exchange after authentication attempt is %s", this.exchange);
/*     */           }
/*     */         } 
/*     */         
/* 253 */         if (outcome == null) {
/* 254 */           throw UndertowMessages.MESSAGES.authMechanismOutcomeNull();
/*     */         }
/*     */         
/* 257 */         switch (outcome) {
/*     */           
/*     */           case AUTHENTICATED:
/* 260 */             return SecurityContextImpl.AuthenticationState.AUTHENTICATED;
/*     */ 
/*     */           
/*     */           case NOT_AUTHENTICATED:
/* 264 */             SecurityContextImpl.this.setAuthenticationRequired();
/* 265 */             return SecurityContextImpl.AuthenticationState.ATTEMPTED;
/*     */           
/*     */           case NOT_ATTEMPTED:
/* 268 */             return transition();
/*     */         } 
/* 270 */         throw new IllegalStateException();
/*     */       } 
/*     */ 
/*     */ 
/*     */       
/* 275 */       return SecurityContextImpl.AuthenticationState.ATTEMPTED;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ChallengeSender
/*     */   {
/*     */     private SecurityContextImpl.Node<AuthenticationMechanism> currentMethod;
/*     */ 
/*     */     
/*     */     private final HttpServerExchange exchange;
/*     */ 
/*     */     
/* 289 */     private Integer chosenStatusCode = null;
/*     */     private boolean challengeSent = false;
/*     */     
/*     */     private ChallengeSender(SecurityContextImpl.Node<AuthenticationMechanism> currentMethod, HttpServerExchange exchange) {
/* 293 */       this.exchange = exchange;
/* 294 */       this.currentMethod = currentMethod;
/*     */     }
/*     */     
/*     */     private SecurityContextImpl.AuthenticationState transition() {
/* 298 */       if (this.currentMethod != null) {
/* 299 */         AuthenticationMechanism mechanism = (AuthenticationMechanism)this.currentMethod.item;
/* 300 */         this.currentMethod = this.currentMethod.next;
/* 301 */         AuthenticationMechanism.ChallengeResult result = mechanism.sendChallenge(this.exchange, SecurityContextImpl.this);
/* 302 */         if (result == null) {
/* 303 */           throw UndertowMessages.MESSAGES.sendChallengeReturnedNull(mechanism);
/*     */         }
/* 305 */         if (result.isChallengeSent()) {
/* 306 */           this.challengeSent = true;
/* 307 */           Integer desiredCode = result.getDesiredResponseCode();
/* 308 */           if (desiredCode != null && (this.chosenStatusCode == null || this.chosenStatusCode.equals(Integer.valueOf(200)))) {
/* 309 */             this.chosenStatusCode = desiredCode;
/* 310 */             if (!this.chosenStatusCode.equals(Integer.valueOf(200)) && 
/* 311 */               !this.exchange.isResponseStarted()) {
/* 312 */               this.exchange.setStatusCode(this.chosenStatusCode.intValue());
/*     */             }
/*     */           } 
/*     */         } 
/*     */ 
/*     */ 
/*     */         
/* 319 */         return transition();
/*     */       } 
/*     */       
/* 322 */       if (!this.exchange.isResponseStarted())
/*     */       {
/* 324 */         if (this.chosenStatusCode == null) {
/* 325 */           if (!this.challengeSent)
/*     */           {
/* 327 */             this.exchange.setStatusCode(403);
/*     */           }
/* 329 */         } else if (this.chosenStatusCode.equals(Integer.valueOf(200))) {
/* 330 */           this.exchange.setStatusCode(this.chosenStatusCode.intValue());
/*     */         } 
/*     */       }
/*     */       
/* 334 */       return SecurityContextImpl.AuthenticationState.CHALLENGE_SENT;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   enum AuthenticationState
/*     */   {
/* 344 */     NOT_ATTEMPTED,
/*     */     
/* 346 */     ATTEMPTED,
/*     */     
/* 348 */     AUTHENTICATED,
/*     */     
/* 350 */     CHALLENGE_SENT;
/*     */   }
/*     */ 
/*     */   
/*     */   private static final class Node<T>
/*     */   {
/*     */     final T item;
/*     */     
/*     */     Node<T> next;
/*     */ 
/*     */     
/*     */     private Node(T item) {
/* 362 */       this.item = item;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\SecurityContextImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */