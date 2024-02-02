package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class BlockingHandler implements HttpHandler {
   private volatile HttpHandler handler;

   public BlockingHandler(HttpHandler handler) {
      this.handler = handler;
   }

   public BlockingHandler() {
      this((HttpHandler)null);
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.startBlocking();
      if (exchange.isInIoThread()) {
         exchange.dispatch(this.handler);
      } else {
         this.handler.handleRequest(exchange);
      }

   }

   public HttpHandler getHandler() {
      return this.handler;
   }

   public BlockingHandler setRootHandler(HttpHandler rootHandler) {
      this.handler = rootHandler;
      return this;
   }

   public String toString() {
      return "blocking()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new BlockingHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "blocking";
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
