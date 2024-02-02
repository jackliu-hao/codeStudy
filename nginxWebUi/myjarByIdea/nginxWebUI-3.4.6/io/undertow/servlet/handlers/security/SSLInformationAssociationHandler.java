package io.undertow.servlet.handlers.security;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.util.HexConverter;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import javax.servlet.ServletRequest;

public class SSLInformationAssociationHandler implements HttpHandler {
   private final HttpHandler next;

   public SSLInformationAssociationHandler(HttpHandler next) {
      this.next = next;
   }

   public static int getKeyLength(String cipherSuite) {
      return SSLSessionInfo.calculateKeySize(cipherSuite);
   }

   private X509Certificate[] getCerts(SSLSessionInfo session) {
      try {
         Certificate[] javaCerts = session.getPeerCertificates();
         if (javaCerts == null) {
            return null;
         } else {
            int x509Certs = 0;
            Certificate[] var4 = javaCerts;
            int var5 = javaCerts.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Certificate javaCert = var4[var6];
               if (javaCert instanceof X509Certificate) {
                  ++x509Certs;
               }
            }

            if (x509Certs == 0) {
               return null;
            } else {
               int resultIndex = 0;
               X509Certificate[] results = new X509Certificate[x509Certs];
               Certificate[] var13 = javaCerts;
               int var14 = javaCerts.length;

               for(int var8 = 0; var8 < var14; ++var8) {
                  Certificate certificate = var13[var8];
                  if (certificate instanceof X509Certificate) {
                     results[resultIndex++] = (X509Certificate)certificate;
                  }
               }

               return results;
            }
         }
      } catch (Exception var10) {
         return null;
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequest request = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getServletRequest();
      SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
      if (ssl != null) {
         String cipherSuite = ssl.getCipherSuite();
         byte[] sessionId = ssl.getSessionId();
         request.setAttribute("javax.servlet.request.cipher_suite", cipherSuite);
         request.setAttribute("javax.servlet.request.key_size", ssl.getKeySize());
         request.setAttribute("javax.servlet.request.ssl_session_id", sessionId != null ? HexConverter.convertToHexString(sessionId) : null);
         X509Certificate[] certs = this.getCerts(ssl);
         if (certs != null) {
            request.setAttribute("javax.servlet.request.X509Certificate", certs);
         }
      }

      this.next.handleRequest(exchange);
   }
}
