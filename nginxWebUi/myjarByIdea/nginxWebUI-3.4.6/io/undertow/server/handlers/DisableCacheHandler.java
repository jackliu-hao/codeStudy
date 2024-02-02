package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class DisableCacheHandler implements HttpHandler {
   private final HttpHandler next;

   public DisableCacheHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.getResponseHeaders().add(Headers.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
      exchange.getResponseHeaders().add(Headers.PRAGMA, "no-cache");
      exchange.getResponseHeaders().add(Headers.EXPIRES, "0");
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "disable-cache()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new DisableCacheHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "disable-cache";
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
