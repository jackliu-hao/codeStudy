package io.undertow.servlet.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class MarkSecureHandler implements HttpHandler {
   public static final HandlerWrapper WRAPPER = new Wrapper();
   private final HttpHandler next;

   public MarkSecureHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.putAttachment(HttpServerExchange.SECURE_REQUEST, Boolean.TRUE);
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "mark-secure()";
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "mark-secure";
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
         return MarkSecureHandler.WRAPPER;
      }
   }

   public static class Wrapper implements HandlerWrapper {
      public HttpHandler wrap(HttpHandler handler) {
         return new MarkSecureHandler(handler);
      }
   }
}
