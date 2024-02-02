package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;

public class ServletDispatchingHandler implements HttpHandler {
   public static final ServletDispatchingHandler INSTANCE = new ServletDispatchingHandler();

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      ServletChain info = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getCurrentServlet();
      info.getHandler().handleRequest(exchange);
   }
}
