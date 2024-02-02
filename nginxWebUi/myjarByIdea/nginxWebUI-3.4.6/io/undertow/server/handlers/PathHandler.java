package io.undertow.server.handlers;

import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.cache.LRUCache;
import io.undertow.util.PathMatcher;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class PathHandler implements HttpHandler {
   private final PathMatcher<HttpHandler> pathMatcher;
   private final LRUCache<String, PathMatcher.PathMatch<HttpHandler>> cache;

   public PathHandler(HttpHandler defaultHandler) {
      this(0);
      this.pathMatcher.addPrefixPath("/", defaultHandler);
   }

   public PathHandler(HttpHandler defaultHandler, int cacheSize) {
      this(cacheSize);
      this.pathMatcher.addPrefixPath("/", defaultHandler);
   }

   public PathHandler() {
      this(0);
   }

   public PathHandler(int cacheSize) {
      this.pathMatcher = new PathMatcher();
      if (cacheSize > 0) {
         this.cache = new LRUCache(cacheSize, -1, true);
      } else {
         this.cache = null;
      }

   }

   public String toString() {
      Set<Map.Entry<String, HttpHandler>> paths = this.pathMatcher.getPaths().entrySet();
      return paths.size() == 1 ? "path( " + paths.toArray()[0] + " )" : "path( {" + (String)paths.stream().map((s) -> {
         return ((HttpHandler)s.getValue()).toString();
      }).collect(Collectors.joining(", ")) + "} )";
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      PathMatcher.PathMatch<HttpHandler> match = null;
      boolean hit = false;
      if (this.cache != null) {
         match = (PathMatcher.PathMatch)this.cache.get(exchange.getRelativePath());
         hit = true;
      }

      if (match == null) {
         match = this.pathMatcher.match(exchange.getRelativePath());
      }

      if (match.getValue() == null) {
         ResponseCodeHandler.HANDLE_404.handleRequest(exchange);
      } else {
         if (hit) {
            this.cache.add(exchange.getRelativePath(), match);
         }

         exchange.setRelativePath(match.getRemaining());
         if (exchange.getResolvedPath().isEmpty()) {
            exchange.setResolvedPath(match.getMatched());
         } else {
            exchange.setResolvedPath(exchange.getResolvedPath() + match.getMatched());
         }

         ((HttpHandler)match.getValue()).handleRequest(exchange);
      }
   }

   /** @deprecated */
   @Deprecated
   public synchronized PathHandler addPath(String path, HttpHandler handler) {
      return this.addPrefixPath(path, handler);
   }

   public synchronized PathHandler addPrefixPath(String path, HttpHandler handler) {
      Handlers.handlerNotNull(handler);
      this.pathMatcher.addPrefixPath(path, handler);
      return this;
   }

   public synchronized PathHandler addExactPath(String path, HttpHandler handler) {
      Handlers.handlerNotNull(handler);
      this.pathMatcher.addExactPath(path, handler);
      return this;
   }

   /** @deprecated */
   @Deprecated
   public synchronized PathHandler removePath(String path) {
      return this.removePrefixPath(path);
   }

   public synchronized PathHandler removePrefixPath(String path) {
      this.pathMatcher.removePrefixPath(path);
      return this;
   }

   public synchronized PathHandler removeExactPath(String path) {
      this.pathMatcher.removeExactPath(path);
      return this;
   }

   public synchronized PathHandler clearPaths() {
      this.pathMatcher.clearPaths();
      return this;
   }
}
