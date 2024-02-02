package io.undertow.server.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.BasicSSLSessionInfo;
import io.undertow.server.ExchangeCompletionListener;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.SSLSessionInfo;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HeaderMap;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.security.cert.CertificateException;

public class SSLHeaderHandler implements HttpHandler {
   public static final String HTTPS = "https";
   private static final String NULL_VALUE = "(null)";
   private static final ExchangeCompletionListener CLEAR_SSL_LISTENER = new ExchangeCompletionListener() {
      public void exchangeEvent(HttpServerExchange exchange, ExchangeCompletionListener.NextListener nextListener) {
         exchange.getConnection().setSslSessionInfo((SSLSessionInfo)null);
         nextListener.proceed();
      }
   };
   private final HttpHandler next;

   public SSLHeaderHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      HeaderMap requestHeaders = exchange.getRequestHeaders();
      String sessionId = requestHeaders.getFirst(Headers.SSL_SESSION_ID);
      String cipher = requestHeaders.getFirst(Headers.SSL_CIPHER);
      String clientCert = requestHeaders.getFirst(Headers.SSL_CLIENT_CERT);
      String keySizeStr = requestHeaders.getFirst(Headers.SSL_CIPHER_USEKEYSIZE);
      Integer keySize = null;
      if (keySizeStr != null) {
         try {
            keySize = Integer.parseUnsignedInt(keySizeStr);
         } catch (NumberFormatException var10) {
            UndertowLogger.REQUEST_LOGGER.debugf("Invalid SSL_CIPHER_USEKEYSIZE header %s", keySizeStr);
         }
      }

      if (clientCert != null || sessionId != null || cipher != null) {
         if (clientCert != null) {
            if (!clientCert.isEmpty() && !clientCert.equals("(null)")) {
               if (clientCert.length() > 54) {
                  StringBuilder sb = new StringBuilder(clientCert.length() + 1);
                  sb.append("-----BEGIN CERTIFICATE-----");
                  sb.append('\n');
                  sb.append(clientCert.replace(' ', '\n').substring(28, clientCert.length() - 26));
                  sb.append('\n');
                  sb.append("-----END CERTIFICATE-----");
                  clientCert = sb.toString();
               }
            } else {
               clientCert = null;
            }
         }

         try {
            SSLSessionInfo info = new BasicSSLSessionInfo(sessionId, cipher, clientCert, keySize);
            exchange.setRequestScheme("https");
            exchange.getConnection().setSslSessionInfo(info);
            exchange.addExchangeCompleteListener(CLEAR_SSL_LISTENER);
         } catch (CertificateException | java.security.cert.CertificateException var9) {
            UndertowLogger.REQUEST_LOGGER.debugf(var9, "Could not create certificate from header %s", clientCert);
         }
      }

      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "ssl-headers()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new SSLHeaderHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "ssl-headers";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.emptyMap();
      }

      public Set<String> requiredParameters() {
         return Collections.emptySet();
      }

      public String defaultParameter() {
         return null;
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper();
      }
   }
}
