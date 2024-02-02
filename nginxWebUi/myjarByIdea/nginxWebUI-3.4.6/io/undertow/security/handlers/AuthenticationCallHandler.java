package io.undertow.security.handlers;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class AuthenticationCallHandler implements HttpHandler {
   private final HttpHandler next;

   public AuthenticationCallHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (exchange.isInIoThread()) {
         exchange.dispatch((HttpHandler)this);
      } else {
         SecurityContext context = exchange.getSecurityContext();
         if (context.authenticate()) {
            if (!exchange.isComplete()) {
               this.next.handleRequest(exchange);
            }
         } else {
            exchange.endExchange();
         }

      }
   }
}
