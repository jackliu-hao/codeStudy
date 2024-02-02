package io.undertow.server.handlers;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.HeaderMap;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import io.undertow.util.PathMatcher;

public class ConfiguredPushHandler implements HttpHandler {
   private final PathMatcher<String[]> pathMatcher = new PathMatcher();
   private final HttpHandler next;
   private final HeaderMap requestHeaders = new HeaderMap();

   public ConfiguredPushHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (exchange.getConnection().isPushSupported()) {
         PathMatcher.PathMatch<String[]> result = this.pathMatcher.match(exchange.getRelativePath());
         if (result != null) {
            String[] value = (String[])result.getValue();

            for(int i = 0; i < value.length; ++i) {
               exchange.getConnection().pushResource(value[i], Methods.GET, this.requestHeaders);
            }
         }
      }

      this.next.handleRequest(exchange);
   }

   public ConfiguredPushHandler addRequestHeader(HttpString name, String value) {
      this.requestHeaders.put(name, value);
      return this;
   }

   public ConfiguredPushHandler addRoute(String url, String... resourcesToPush) {
      if (url.endsWith("/*")) {
         String partial = url.substring(0, url.length() - 1);
         this.pathMatcher.addPrefixPath(partial, resourcesToPush);
      } else {
         this.pathMatcher.addExactPath(url, resourcesToPush);
      }

      return this;
   }
}
