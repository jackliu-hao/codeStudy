package io.undertow.servlet.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.Methods;
import io.undertow.util.RedirectBuilder;

public class RedirectDirHandler implements HttpHandler {
   private static final String HTTP2_UPGRADE_PREFIX = "h2";
   private final HttpHandler next;
   private final ServletPathMatches paths;

   public RedirectDirHandler(HttpHandler next, ServletPathMatches paths) {
      this.next = next;
      this.paths = paths;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      String path = exchange.getRelativePath();
      ServletPathMatch info = this.paths.getServletHandlerByPath(path);
      String upgradeString = exchange.getRequestHeaders().getFirst(Headers.UPGRADE);
      boolean isUpgradeRequest = upgradeString != null && !upgradeString.startsWith("h2");
      if (info.getType() == ServletPathMatch.Type.REDIRECT && !isUpgradeRequest) {
         if (!exchange.getRequestMethod().equals(Methods.GET) && !exchange.getRequestMethod().equals(Methods.HEAD)) {
            exchange.setStatusCode(307);
         } else {
            exchange.setStatusCode(302);
         }

         exchange.getResponseHeaders().put(Headers.LOCATION, RedirectBuilder.redirect(exchange, exchange.getRelativePath() + "/", true));
      } else {
         this.next.handleRequest(exchange);
      }
   }
}
