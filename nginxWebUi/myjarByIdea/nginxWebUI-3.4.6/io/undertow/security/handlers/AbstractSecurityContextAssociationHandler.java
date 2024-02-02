package io.undertow.security.handlers;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public abstract class AbstractSecurityContextAssociationHandler implements HttpHandler {
   private final HttpHandler next;

   protected AbstractSecurityContextAssociationHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      SecurityActions.setSecurityContext(exchange, this.createSecurityContext(exchange));
      this.next.handleRequest(exchange);
   }

   public abstract SecurityContext createSecurityContext(HttpServerExchange var1);
}
