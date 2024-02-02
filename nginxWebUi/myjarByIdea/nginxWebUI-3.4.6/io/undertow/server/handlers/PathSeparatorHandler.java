package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.CanonicalPathUtils;
import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class PathSeparatorHandler implements HttpHandler {
   private final HttpHandler next;

   public PathSeparatorHandler(HttpHandler next) {
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      boolean handlingRequired = File.separatorChar != '/';
      if (handlingRequired) {
         exchange.setRequestPath(CanonicalPathUtils.canonicalize(exchange.getRequestPath().replace(File.separatorChar, '/')));
         exchange.setRelativePath(CanonicalPathUtils.canonicalize(exchange.getRelativePath().replace(File.separatorChar, '/')));
         exchange.setResolvedPath(CanonicalPathUtils.canonicalize(exchange.getResolvedPath().replace(File.separatorChar, '/')));
      }

      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "path-separator()";
   }

   private static class Wrapper implements HandlerWrapper {
      private Wrapper() {
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new PathSeparatorHandler(handler);
      }

      // $FF: synthetic method
      Wrapper(Object x0) {
         this();
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "path-separator";
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
