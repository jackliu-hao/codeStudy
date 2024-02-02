package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.UndertowMessages;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.PasswordCredential;
import io.undertow.server.DefaultResponseListener;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.server.handlers.form.FormParserFactory;
import io.undertow.server.session.Session;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.RedirectBuilder;
import io.undertow.util.Sessions;
import java.io.IOException;
import java.io.UncheckedIOException;

public class FormAuthenticationMechanism implements AuthenticationMechanism {
   public static final String LOCATION_ATTRIBUTE = FormAuthenticationMechanism.class.getName() + ".LOCATION";
   public static final String DEFAULT_POST_LOCATION = "/j_security_check";
   private final String name;
   private final String loginPage;
   private final String errorPage;
   private final String postLocation;
   private final FormParserFactory formParserFactory;
   private final IdentityManager identityManager;

   public FormAuthenticationMechanism(String name, String loginPage, String errorPage) {
      this(FormParserFactory.builder().build(), name, loginPage, errorPage);
   }

   public FormAuthenticationMechanism(String name, String loginPage, String errorPage, String postLocation) {
      this(FormParserFactory.builder().build(), name, loginPage, errorPage, postLocation);
   }

   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage) {
      this(formParserFactory, name, loginPage, errorPage, "/j_security_check");
   }

   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, IdentityManager identityManager) {
      this(formParserFactory, name, loginPage, errorPage, "/j_security_check", identityManager);
   }

   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation) {
      this(formParserFactory, name, loginPage, errorPage, postLocation, (IdentityManager)null);
   }

   public FormAuthenticationMechanism(FormParserFactory formParserFactory, String name, String loginPage, String errorPage, String postLocation, IdentityManager identityManager) {
      this.name = name;
      this.loginPage = loginPage;
      this.errorPage = errorPage;
      this.postLocation = postLocation;
      this.formParserFactory = formParserFactory;
      this.identityManager = identityManager;
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      return exchange.getRequestPath().endsWith(this.postLocation) && exchange.getRequestMethod().equals(Methods.POST) ? this.runFormAuth(exchange, securityContext) : AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome runFormAuth(HttpServerExchange exchange, SecurityContext securityContext) {
      FormDataParser parser = this.formParserFactory.createParser(exchange);
      if (parser == null) {
         UndertowLogger.SECURITY_LOGGER.debug("Could not authenticate as no form parser is present");
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      } else {
         Throwable original = null;
         AuthenticationMechanism.AuthenticationMechanismOutcome retValue = null;

         try {
            FormData data = parser.parseBlocking();
            FormData.FormValue jUsername = data.getFirst("j_username");
            FormData.FormValue jPassword = data.getFirst("j_password");
            if (jUsername == null || jPassword == null) {
               UndertowLogger.SECURITY_LOGGER.debugf("Could not authenticate as username or password was not present in the posted result for %s", exchange);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }

            String userName = jUsername.getValue();
            String password = jPassword.getValue();
            AuthenticationMechanism.AuthenticationMechanismOutcome outcome = null;
            PasswordCredential credential = new PasswordCredential(password.toCharArray());

            try {
               IdentityManager identityManager = this.getIdentityManager(securityContext);
               Account account = identityManager.verify(userName, credential);
               if (account != null) {
                  securityContext.authenticationComplete(account, this.name, true);
                  UndertowLogger.SECURITY_LOGGER.debugf("Authenticated user %s using for auth for %s", account.getPrincipal().getName(), exchange);
                  outcome = AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
               } else {
                  securityContext.authenticationFailed(UndertowMessages.MESSAGES.authenticationFailed(userName), this.name);
               }
            } catch (Throwable var23) {
               original = var23;
            } finally {
               try {
                  if (outcome == AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED) {
                     this.handleRedirectBack(exchange);
                     exchange.endExchange();
                  }

                  retValue = outcome != null ? outcome : AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
               } catch (Throwable var24) {
                  if (original != null) {
                     ((Throwable)original).addSuppressed(var24);
                  } else {
                     original = var24;
                  }
               }

            }
         } catch (IOException var26) {
            original = new UncheckedIOException(var26);
         }

         if (original != null) {
            if (original instanceof RuntimeException) {
               throw (RuntimeException)original;
            }

            if (original instanceof Error) {
               throw (Error)original;
            }
         }

         return retValue;
      }
   }

   protected void handleRedirectBack(HttpServerExchange exchange) {
      Session session = Sessions.getSession(exchange);
      if (session != null) {
         final String location = (String)session.removeAttribute(LOCATION_ATTRIBUTE);
         if (location != null) {
            exchange.addDefaultResponseListener(new DefaultResponseListener() {
               public boolean handleDefaultResponse(HttpServerExchange exchange) {
                  exchange.getResponseHeaders().put(Headers.LOCATION, location);
                  exchange.setStatusCode(302);
                  exchange.endExchange();
                  return true;
               }
            });
         }
      }

   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      if (exchange.getRelativePath().isEmpty()) {
         exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
         return new AuthenticationMechanism.ChallengeResult(true, 302);
      } else {
         Integer code;
         if (exchange.getRequestPath().endsWith(this.postLocation) && exchange.getRequestMethod().equals(Methods.POST)) {
            UndertowLogger.SECURITY_LOGGER.debugf("Serving form auth error page %s for %s", this.loginPage, exchange);
            code = this.servePage(exchange, this.errorPage);
            return new AuthenticationMechanism.ChallengeResult(true, code);
         } else {
            UndertowLogger.SECURITY_LOGGER.debugf("Serving login form %s for %s", this.loginPage, exchange);
            this.storeInitialLocation(exchange);
            code = this.servePage(exchange, this.loginPage);
            return new AuthenticationMechanism.ChallengeResult(true, code);
         }
      }
   }

   protected void storeInitialLocation(HttpServerExchange exchange) {
      Session session = Sessions.getOrCreateSession(exchange);
      session.setAttribute(LOCATION_ATTRIBUTE, RedirectBuilder.redirect(exchange, exchange.getRelativePath()));
   }

   protected Integer servePage(HttpServerExchange exchange, String location) {
      sendRedirect(exchange, location);
      return 307;
   }

   static void sendRedirect(HttpServerExchange exchange, String location) {
      String loc = exchange.getRequestScheme() + "://" + exchange.getHostAndPort() + location;
      exchange.getResponseHeaders().put(Headers.LOCATION, loc);
   }
}
