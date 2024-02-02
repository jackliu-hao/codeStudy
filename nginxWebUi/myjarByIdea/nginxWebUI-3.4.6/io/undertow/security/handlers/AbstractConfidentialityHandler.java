package io.undertow.security.handlers;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.net.URI;
import java.net.URISyntaxException;

public abstract class AbstractConfidentialityHandler implements HttpHandler {
   private final HttpHandler next;

   protected AbstractConfidentialityHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (!this.isConfidential(exchange) && this.confidentialityRequired(exchange)) {
         try {
            URI redirectUri = this.getRedirectURI(exchange);
            UndertowLogger.SECURITY_LOGGER.debugf("Redirecting request %s to %s to meet confidentiality requirements", exchange, redirectUri);
            exchange.setStatusCode(302);
            exchange.getResponseHeaders().put(Headers.LOCATION, redirectUri.toString());
         } catch (Exception var3) {
            UndertowLogger.REQUEST_LOGGER.exceptionProcessingRequest(var3);
            exchange.setStatusCode(500);
         }

         exchange.endExchange();
      } else {
         this.next.handleRequest(exchange);
      }

   }

   protected boolean isConfidential(HttpServerExchange exchange) {
      return exchange.getRequestScheme().equals("https");
   }

   protected boolean confidentialityRequired(HttpServerExchange exchange) {
      return true;
   }

   protected abstract URI getRedirectURI(HttpServerExchange var1) throws URISyntaxException;
}
