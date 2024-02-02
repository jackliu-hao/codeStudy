package io.undertow.servlet.handlers.security;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.handlers.ServletRequestContext;

public class ServletAuthenticationCallHandler implements HttpHandler {
   private final HttpHandler next;

   public ServletAuthenticationCallHandler(HttpHandler next) {
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
         } else if (exchange.getStatusCode() >= 400 && !exchange.isComplete()) {
            ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
            src.getOriginalResponse().sendError(exchange.getStatusCode());
         } else {
            exchange.endExchange();
         }

      }
   }
}
