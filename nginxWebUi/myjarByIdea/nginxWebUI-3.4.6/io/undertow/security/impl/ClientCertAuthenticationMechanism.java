package io.undertow.security.impl;

import io.undertow.security.api.AuthenticationMechanism;
import io.undertow.security.api.AuthenticationMechanismFactory;
import io.undertow.security.api.SecurityContext;
import io.undertow.security.idm.Account;
import io.undertow.security.idm.Credential;
import io.undertow.security.idm.IdentityManager;
import io.undertow.security.idm.X509CertificateCredential;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.RenegotiationRequiredException;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.handlers.form.FormParserFactory;
import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Map;
import javax.net.ssl.SSLPeerUnverifiedException;
import org.xnio.SslClientAuthMode;

public class ClientCertAuthenticationMechanism implements AuthenticationMechanism {
   public static final AuthenticationMechanismFactory FACTORY = new Factory();
   public static final String FORCE_RENEGOTIATION = "force_renegotiation";
   private final String name;
   private final IdentityManager identityManager;
   private final boolean forceRenegotiation;

   public ClientCertAuthenticationMechanism() {
      this(true);
   }

   public ClientCertAuthenticationMechanism(boolean forceRenegotiation) {
      this("CLIENT_CERT", forceRenegotiation);
   }

   public ClientCertAuthenticationMechanism(String mechanismName) {
      this(mechanismName, true);
   }

   public ClientCertAuthenticationMechanism(String mechanismName, boolean forceRenegotiation) {
      this(mechanismName, forceRenegotiation, (IdentityManager)null);
   }

   public ClientCertAuthenticationMechanism(String mechanismName, boolean forceRenegotiation, IdentityManager identityManager) {
      this.name = mechanismName;
      this.forceRenegotiation = forceRenegotiation;
      this.identityManager = identityManager;
   }

   private IdentityManager getIdentityManager(SecurityContext securityContext) {
      return this.identityManager != null ? this.identityManager : securityContext.getIdentityManager();
   }

   public AuthenticationMechanism.AuthenticationMechanismOutcome authenticate(HttpServerExchange exchange, SecurityContext securityContext) {
      SSLSessionInfo sslSession = exchange.getConnection().getSslSessionInfo();
      if (sslSession != null) {
         try {
            Certificate[] clientCerts = this.getPeerCertificates(exchange, sslSession, securityContext);
            if (clientCerts[0] instanceof X509Certificate) {
               Credential credential = new X509CertificateCredential((X509Certificate)clientCerts[0]);
               IdentityManager idm = this.getIdentityManager(securityContext);
               Account account = idm.verify((Credential)credential);
               if (account != null) {
                  securityContext.authenticationComplete(account, this.name, false);
                  return AuthenticationMechanism.AuthenticationMechanismOutcome.AUTHENTICATED;
               }
            }
         } catch (SSLPeerUnverifiedException var8) {
         }
      }

      return AuthenticationMechanism.AuthenticationMechanismOutcome.NOT_ATTEMPTED;
   }

   private Certificate[] getPeerCertificates(HttpServerExchange exchange, SSLSessionInfo sslSession, SecurityContext securityContext) throws SSLPeerUnverifiedException {
      try {
         return sslSession.getPeerCertificates();
      } catch (RenegotiationRequiredException var8) {
         if (this.forceRenegotiation && securityContext.isAuthenticationRequired()) {
            try {
               sslSession.renegotiate(exchange, SslClientAuthMode.REQUESTED);
               return sslSession.getPeerCertificates();
            } catch (IOException var6) {
            } catch (RenegotiationRequiredException var7) {
            }
         }

         throw new SSLPeerUnverifiedException("");
      }
   }

   public AuthenticationMechanism.ChallengeResult sendChallenge(HttpServerExchange exchange, SecurityContext securityContext) {
      return AuthenticationMechanism.ChallengeResult.NOT_SENT;
   }

   public static final class Factory implements AuthenticationMechanismFactory {
      /** @deprecated */
      @Deprecated
      public Factory(IdentityManager identityManager) {
      }

      public Factory() {
      }

      public AuthenticationMechanism create(String mechanismName, IdentityManager identityManager, FormParserFactory formParserFactory, Map<String, String> properties) {
         String forceRenegotiation = (String)properties.get("force_renegotiation");
         return new ClientCertAuthenticationMechanism(mechanismName, forceRenegotiation == null ? true : "true".equals(forceRenegotiation), identityManager);
      }
   }
}
