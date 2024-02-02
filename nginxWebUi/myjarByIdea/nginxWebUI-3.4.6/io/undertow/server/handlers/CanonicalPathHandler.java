package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.CanonicalPathUtils;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class CanonicalPathHandler implements HttpHandler {
   private volatile HttpHandler next;

   public CanonicalPathHandler() {
      this.next = ResponseCodeHandler.HANDLE_404;
   }

   public CanonicalPathHandler(HttpHandler next) {
      this.next = ResponseCodeHandler.HANDLE_404;
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.setRelativePath(CanonicalPathUtils.canonicalize(exchange.getRelativePath()));
      this.next.handleRequest(exchange);
   }

   public HttpHandler getNext() {
      return this.next;
   }

   public CanonicalPathHandler setNext(HttpHandler next) {
      Handlers.handlerNotNull(next);
      this.next = next;
      return this;
   }

   public String toString() {
      return "canonical-path()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new CanonicalPathHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "canonical-path";
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
