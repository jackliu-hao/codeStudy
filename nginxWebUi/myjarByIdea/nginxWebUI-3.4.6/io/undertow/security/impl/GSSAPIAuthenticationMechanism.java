package io.undertow.security.impl;

import io.undertow.UndertowLogger;
import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.GSSAPIServerSubjectFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.GSSContextCredential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.ServerConnection;
import io.undertow.server.handlers.proxy.ExclusivityChecker;
import io.undertow.util.AttachmentKey;
import io.undertow.util.FlexBase64;
import io.undertow.util.Headers;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.Iterator;
import java.util.List;
import javax.security.auth.Subject;
import javax.security.auth.kerberos.KerberosPrincipal;
import org.ietf.jgss.GSSContext;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSManager;
import org.ietf.jgss.GSSName;
import org.ietf.jgss.Oid;

public class GSSAPIAuthenticationMechanism implements AuthenticationMechanism {
   public static final ExclusivityChecker EXCLUSIVITY_CHECKER = new ExclusivityChecker() {
      public boolean isExclusivityRequired(HttpServerExchange exchange) {
         List<String> authHeaders = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
         if (authHeaders != null) {
            Iterator var3 = authHeaders.iterator();

            while(var3.hasNext()) {
               String current = (String)var3.next();
               if (current.startsWith(GSSAPIAuthenticationMechanism.NEGOTIATE_PREFIX)) {
                  return true;
               }
            }
         }

         return false;
      }
   };
   private static final String NEGOTIATION_PLAIN;
   private static final String NEGOTIATE_PREFIX;
   private static final Oid[] DEFAULT_MECHANISMS;
   private static final String name = "SPNEGO";
   private final IdentityManager identityManager;
   private final GSSAPIServerSubjectFactory subjectFactory;
   private final Oid[] mechanisms;

   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory, IdentityManager identityManager, Oid... supportedMechanisms) {
      this.subjectFactory = subjectFactory;
      this.identityManager = identityManager;
      this.mechanisms = supportedMechanisms;
   }

   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory, Oid... supportedMechanisms) {
      this(subjectFactory, (IdentityManager)null, supportedMechanisms);
   }

   public GSSAPIAuthenticationMechanism(GSSAPIServerSubjectFactory subjectFactory) {
      this(subjectFactory, DEFAULT_MECHANISMS);
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      ServerConnection connection = exchange.getConnection();
      NegotiationContext negContext = (NegotiationContext)connection.getAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY);
      if (negContext != null) {
         UndertowLogger.SECURITY_LOGGER.debugf("Existing negotiation context found for %s", exchange);
         exchange.putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, negContext);
         if (negContext.isEstablished()) {
            IdentityManager identityManager = this.getIdentityManager(securityContext);
            Account account = identityManager.verify((Credential)(new GSSContextCredential(negContext.getGssContext())));
            if (account != null) {
               securityContext.authenticationComplete(account, "SPNEGO", false);
               UndertowLogger.SECURITY_LOGGER.debugf("Authenticated as user %s with existing GSSAPI negotiation context for %s", account.getPrincipal().getName(), exchange);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
            }

            UndertowLogger.SECURITY_LOGGER.debugf("Failed to authenticate with existing GSSAPI negotiation context for %s", exchange);
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         }
      }

      List<String> authHeaders = exchange.getRequestHeaders().get(Headers.AUTHORIZATION);
      if (authHeaders != null) {
         Iterator var6 = authHeaders.iterator();

         while(var6.hasNext()) {
            String current = (String)var6.next();
            if (current.startsWith(NEGOTIATE_PREFIX)) {
               String base64Challenge = current.substring(NEGOTIATE_PREFIX.length());

               try {
                  ByteBuffer challenge = FlexBase64.decode(base64Challenge);
                  return this.runGSSAPI(exchange, challenge, securityContext);
               } catch (IOException var10) {
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
               }
            }
         }
      }

      return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      NegotiationContext negContext = (NegotiationContext)exchange.getAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY);
      String header = NEGOTIATION_PLAIN;
      if (negContext != null) {
         byte[] responseChallenge = negContext.useResponseToken();
         exchange.putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, (Object)null);
         if (responseChallenge != null) {
            header = NEGOTIATE_PREFIX + FlexBase64.encodeString(responseChallenge, false);
         }
      } else {
         Subject server = null;

         try {
            server = this.subjectFactory.getSubjectForHost(this.getHostName(exchange));
         } catch (GeneralSecurityException var7) {
         }

         if (server == null) {
            return AuthenticationMechanism.ChallengeResult.NOT_SENT;
         }
      }

      exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, header);
      UndertowLogger.SECURITY_LOGGER.debugf("Sending GSSAPI challenge for %s", exchange);
      return new AuthenticationMechanism.ChallengeResult(true, 401);
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome runGSSAPI(HttpServerExchange exchange, ByteBuffer challenge, SecurityContext securityContext) {
      try {
         Subject server = this.subjectFactory.getSubjectForHost(this.getHostName(exchange));
         return (AuthenticationMechanism.AuthenticationMechanismOutcome)Subject.doAs(server, new AcceptSecurityContext(exchange, challenge, securityContext));
      } catch (GeneralSecurityException var5) {
         UndertowLogger.SECURITY_LOGGER.failedToObtainSubject(exchange, var5);
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      } catch (PrivilegedActionException var6) {
         UndertowLogger.SECURITY_LOGGER.failedToNegotiateAtGSSAPI(exchange, var6.getCause());
         return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
      }
   }

   private String getHostName(HttpServerExchange exchange) {
      String hostName = exchange.getRequestHeaders().getFirst(Headers.HOST);
      if (hostName == null) {
         return null;
      } else {
         if (hostName.startsWith("[") && hostName.contains("]")) {
            hostName = hostName.substring(0, hostName.indexOf(93) + 1);
         } else if (hostName.contains(":")) {
            hostName = hostName.substring(0, hostName.indexOf(":"));
         }

         return hostName;
      }
   }

   static {
      NEGOTIATION_PLAIN = Headers.NEGOTIATE.toString();
      NEGOTIATE_PREFIX = Headers.NEGOTIATE + " ";

      try {
         Oid spnego = new Oid("1.3.6.1.5.5.2");
         Oid kerberos = new Oid("1.2.840.113554.1.2.2");
         DEFAULT_MECHANISMS = new Oid[]{spnego, kerberos};
      } catch (GSSException var2) {
         throw new RuntimeException(var2);
      }
   }

   private static class NegotiationContext {
      static final AttachmentKey<NegotiationContext> ATTACHMENT_KEY = AttachmentKey.create(NegotiationContext.class);
      private GSSContext gssContext;
      private byte[] responseToken;
      private Principal principal;

      private NegotiationContext() {
      }

      GSSContext getGssContext() {
         return this.gssContext;
      }

      void setGssContext(GSSContext gssContext) {
         this.gssContext = gssContext;
      }

      byte[] useResponseToken() {
         byte[] var1;
         try {
            var1 = this.responseToken;
         } finally {
            this.responseToken = null;
         }

         return var1;
      }

      void setResponseToken(byte[] responseToken) {
         this.responseToken = responseToken;
      }

      boolean isEstablished() {
         return this.gssContext != null ? this.gssContext.isEstablished() : false;
      }

      Principal getPrincipal() {
         if (!this.isEstablished()) {
            throw new IllegalStateException("No established GSSContext to use for the Principal.");
         } else {
            if (this.principal == null) {
               try {
                  this.principal = new KerberosPrincipal(this.gssContext.getSrcName().toString());
               } catch (GSSException var2) {
                  throw new IllegalStateException("Unable to create Principal", var2);
               }
            }

            return this.principal;
         }
      }

      // $FF: synthetic method
      NegotiationContext(Object x0) {
         this();
      }
   }

   private class AcceptSecurityContext implements PrivilegedExceptionAction<AuthenticationMechanism.AuthenticationMechanismOutcome> {
      private final HttpServerExchange exchange;
      private final ByteBuffer challenge;
      private final SecurityContext securityContext;

      private AcceptSecurityContext(HttpServerExchange exchange, ByteBuffer challenge, SecurityContext securityContext) {
         this.exchange = exchange;
         this.challenge = challenge;
         this.securityContext = securityContext;
      }

      public AuthenticationMechanism.AuthenticationMechanismOutcome run() throws GSSException {
         NegotiationContext negContext = (NegotiationContext)this.exchange.getAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY);
         if (negContext == null) {
            negContext = new NegotiationContext();
            this.exchange.putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, negContext);
            this.exchange.getConnection().putAttachment(GSSAPIAuthenticationMechanism.NegotiationContext.ATTACHMENT_KEY, negContext);
         }

         GSSContext gssContext = negContext.getGssContext();
         if (gssContext == null) {
            GSSManager manager = GSSManager.getInstance();
            GSSCredential credential = manager.createCredential((GSSName)null, Integer.MAX_VALUE, GSSAPIAuthenticationMechanism.this.mechanisms, 2);
            gssContext = manager.createContext(credential);
            negContext.setGssContext(gssContext);
         }

         byte[] respToken = gssContext.acceptSecContext(this.challenge.array(), this.challenge.arrayOffset(), this.challenge.limit());
         negContext.setResponseToken(respToken);
         if (negContext.isEstablished()) {
            if (respToken != null) {
               this.exchange.getResponseHeaders().add(Headers.WWW_AUTHENTICATE, GSSAPIAuthenticationMechanism.NEGOTIATE_PREFIX + FlexBase64.encodeString(respToken, false));
            }

            IdentityManager identityManager = this.securityContext.getIdentityManager();
            Account account = identityManager.verify((Credential)(new GSSContextCredential(negContext.getGssContext())));
            if (account != null) {
               this.securityContext.authenticationComplete(account, "SPNEGO", false);
               return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
            } else {
               return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
            }
         } else {
            return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_AUTHENTICATED;
         }
      }

      // $FF: synthetic method
      AcceptSecurityContext(HttpServerExchange x1, ByteBuffer x2, SecurityContext x3, Object x4) {
         this(x1, x2, x3);
      }
   }
}
