package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.servlet.http.HttpServletResponse;

public class SendErrorPageHandler implements HttpHandler {
   private final HttpHandler next;

   public SendErrorPageHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
      if (src != null && exchange.getStatusCode() > 399 && !exchange.isResponseStarted()) {
         ((HttpServletResponse)src.getServletResponse()).sendError(exchange.getStatusCode());
      } else {
         this.next.handleRequest(exchange);
      }

   }
}
