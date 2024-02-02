/*     */ package io.undertow.security.impl;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.security.api.AuthenticationMechanism;
/*     */ import io.undertow.security.api.SecurityContext;
/*     */ import io.undertow.security.idm.Account;
/*     */ import io.undertow.security.idm.Credential;
/*     */ import io.undertow.security.idm.IdentityManager;
/*     */ import io.undertow.security.idm.PasswordCredential;
/*     */ import io.undertow.server.DefaultResponseListener;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.form.FormData;
/*     */ import io.undertow.server.handlers.form.FormDataParser;
/*     */ import io.undertow.server.handlers.form.FormParserFactory;
/*     */ import io.undertow.server.session.Session;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.Methods;
/*     */ import io.undertow.util.RedirectBuilder;
/*     */ import io.undertow.util.Sessions;
/*     */ import java.io.IOException;
/*     */ import java.io.UncheckedIOException;
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
/*     */ public class FormAuthenticationMechanism
/*     */   implements AuthenticationMechanism
/*     */ {
/*  48 */   public static final String LOCATION_ATTRIBUTE = FormAuthenticationMechanism.class.getName() + ".LOCATION";
/*     */   
/*     */   public static final String DEFAULT_POST_LOCATION = "/j_security_check";
/*     */   
/*     */   private final String name;
/*     */   private final String loginPage;
/*     */   private final String errorPage;
/*     */   private final String postLocation;
/*     */   private final FormParserFactory formParserFactory;
/*     */   private final IdentityManager identityManager;
/*     */   
/*     */   public FormAuthenticationMechanism(String name, String loginPage, String errorPage) {
/*  60 */     this(FormParserFactory.builder().build(), name, loginPage, errorPage);
/*     */   }
/*     */   
/*     */   public FormAuthenticationMechanism(String name, String loginPage, String errorPage, String postLocation) {
/*  64 */     this(FormParserFactory.builder().build(), name, loginPage, errorPage, postLocation);
/*     */   }
/*     */   
/*     */   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
/*  68 */     this(formParserFactory, name, loginPage, errorPage, "/j_security_check");
/*     */   }
/*     */   
/*     */   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager) {
/*  72 */     this(formParserFactory, name, loginPage, errorPage, "/j_security_check", identityManager);
/*     */   }
/*     */   
/*     */   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation) {
/*  76 */     this(formParserFactory, name, loginPage, errorPage, postLocation, null);
/*     */   }
/*     */   
/*     */   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation, IdentityManager identityManager) {
/*  80 */     this.name = name;
/*  81 */     this.loginPage = loginPage;
/*  82 */     this.errorPage = errorPage;
/*  83 */     this.postLocation = postLocation;
/*  84 */     this.formParserFactory = formParserFactory;
/*  85 */     this.identityManager = identityManager;
/*     */   }
/*     */ 
/*     */   
/*     */   private IdentityManager getIdentityManager(SecurityContext securityContext) {
/*  90 */     return (this.identityManager != null) ? this.identityManager : securityContext.getIdentityManager();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
/*  96 */     if (exchange.getRequestPath().endsWith(this.postLocation) && exchange.getRequestMethod().equals(Methods.POST)) {
/*  97 */       return runFormAuth(exchange, securityContext);
/*     */     }
/*  99 */     return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
/*     */   }
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.AuthenticationMechanismOutcome runFormAuth(HttpServerExchange exchange, SecurityContext securityContext) {
/* 104 */     FormDataParser parser = this.formParserFactory.createParser(exchange);
/* 105 */     if (parser == null) {
/* 106 */       UndertowLogger.SECURITY_LOGGER.debug("Could not authenticate as no form parser is present");
/*     */       
/* 108 */       return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */     } 
/*     */     
/* 111 */     Throwable original = null;
/* 112 */     AuthenticationMechanism.AuthenticationMechanismOutcome retValue = null;
/*     */     try {
/* 114 */       FormData data = parser.parseBlocking();
/* 115 */       FormData.FormValue jUsername = data.getFirst("j_username");
/* 116 */       FormData.FormValue jPassword = data.getFirst("j_password");
/* 117 */       if (jUsername == null || jPassword == null) {
/* 118 */         UndertowLogger.SECURITY_LOGGER.debugf("Could not authenticate as username or password was not present in the posted result for %s", exchange);
/* 119 */         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/*     */       } 
/* 121 */       String userName = jUsername.getValue();
/* 122 */       String password = jPassword.getValue();
/* 123 */       AuthenticationMechanism.AuthenticationMechanismOutcome outcome = null;
/* 124 */       PasswordCredential credential = new PasswordCredential(password.toCharArray());
/*     */       try {
/* 126 */         IdentityManager identityManager = getIdentityManager(securityContext);
/* 127 */         Account account = identityManager.verify(userName, (Credential)credential);
/* 128 */         if (account != null) {
/* 129 */           securityContext.authenticationComplete(account, this.name, true);
/* 130 */           UndertowLogger.SECURITY_LOGGER.debugf("Authenticated user %s using for auth for %s", account.getPrincipal().getName(), exchange);
/* 131 */           outcome = AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
/*     */         } else {
/* 133 */           securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.name);
/*     */         } 
/* 135 */       } catch (Throwable t) {
/* 136 */         original = t;
/*     */       } finally {
/*     */         try {
/* 139 */           if (outcome == AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED) {
/* 140 */             handleRedirectBack(exchange);
/* 141 */             exchange.endExchange();
/*     */           } 
/* 143 */           retValue = (outcome != null) ? outcome : AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
/* 144 */         } catch (Throwable t) {
/* 145 */           if (original != null) {
/* 146 */             original.addSuppressed(t);
/*     */           } else {
/* 148 */             original = t;
/*     */           } 
/*     */         } 
/*     */       } 
/* 152 */     } catch (IOException e) {
/* 153 */       original = new UncheckedIOException(e);
/*     */     } 
/* 155 */     if (original != null) {
/* 156 */       if (original instanceof RuntimeException) {
/* 157 */         throw (RuntimeException)original;
/*     */       }
/* 159 */       if (original instanceof Error) {
/* 160 */         throw (Error)original;
/*     */       }
/*     */     } 
/* 163 */     return retValue;
/*     */   }
/*     */   
/*     */   protected void handleRedirectBack(HttpServerExchange exchange) {
/* 167 */     Session session = Sessions.getSession(exchange);
/* 168 */     if (session != null) {
/* 169 */       final String location = (String)session.removeAttribute(LOCATION_ATTRIBUTE);
/* 170 */       if (location != null) {
/* 171 */         exchange.addDefaultResponseListener(new DefaultResponseListener()
/*     */             {
/*     */               public boolean handleDefaultResponse(HttpServerExchange exchange) {
/* 174 */                 exchange.getResponseHeaders().put(Headers.LOCATION, location);
/* 175 */                 exchange.setStatusCode(302);
/* 176 */                 exchange.endExchange();
/* 177 */                 return true;
/*     */               }
/*     */             });
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
/* 189 */     if (exchange.getRelativePath().isEmpty()) {
/* 190 */       exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
/* 191 */       return new AuthenticationMechanism.ChallengeResult(true, Integer.valueOf(302));
/*     */     } 
/* 193 */     if (exchange.getRequestPath().endsWith(this.postLocation) && exchange.getRequestMethod().equals(Methods.POST)) {
/* 194 */       UndertowLogger.SECURITY_LOGGER.debugf("Serving form auth error page %s for %s", this.loginPage, exchange);
/*     */       
/* 196 */       Integer integer = servePage(exchange, this.errorPage);
/* 197 */       return new AuthenticationMechanism.ChallengeResult(true, integer);
/*     */     } 
/* 199 */     UndertowLogger.SECURITY_LOGGER.debugf("Serving login form %s for %s", this.loginPage, exchange);
/*     */ 
/*     */     
/* 202 */     storeInitialLocation(exchange);
/*     */ 
/*     */     
/* 205 */     Integer code = servePage(exchange, this.loginPage);
/* 206 */     return new AuthenticationMechanism.ChallengeResult(true, code);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void storeInitialLocation(HttpServerExchange exchange) {
/* 211 */     Session session = Sessions.getOrCreateSession(exchange);
/* 212 */     session.setAttribute(LOCATION_ATTRIBUTE, RedirectBuilder.redirect(exchange, exchange.getRelativePath()));
/*     */   }
/*     */   
/*     */   protected Integer servePage(HttpServerExchange exchange, String location) {
/* 216 */     sendRedirect(exchange, location);
/* 217 */     return Integer.valueOf(307);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static void sendRedirect(HttpServerExchange exchange, String location) {
/* 223 */     String loc = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + location;
/* 224 */     exchange.getResponseHeaders().put(Headers.LOCATION, loc);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\security\impl\FormAuthenticationMechanism.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */