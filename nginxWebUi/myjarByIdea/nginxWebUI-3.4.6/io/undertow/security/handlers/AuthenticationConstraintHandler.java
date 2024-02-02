package io.undertow.security.handlers;

import io.undertow.UndertowLogger;
import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class AuthenticationConstraintHandler implements HttpHandler {
   private final HttpHandler next;

   public AuthenticationConstraintHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (this.isAuthenticationRequired(exchange)) {
         SecurityContext context = exchange.getSecurityContext();
         UndertowLogger.SECURITY_LOGGER.debugf("Setting authentication required for exchange %s", exchange);
         context.setAuthenticationRequired();
      }

      this.next.handleRequest(exchange);
   }

   protected boolean isAuthenticationRequired(HttpServerExchange exchange) {
      return true;
   }
}
