package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public final class RequestLimitingHandler implements HttpHandler {
   private final HttpHandler nextHandler;
   private final RequestLimit requestLimit;

   public RequestLimitingHandler(int maximumConcurrentRequests, HttpHandler nextHandler) {
      this(maximumConcurrentRequests, -1, nextHandler);
   }

   public RequestLimitingHandler(int maximumConcurrentRequests, int queueSize, HttpHandler nextHandler) {
      if (nextHandler == null) {
         throw new IllegalArgumentException("nextHandler is null");
      } else if (maximumConcurrentRequests < 1) {
         throw new IllegalArgumentException("Maximum concurrent requests must be at least 1");
      } else {
         this.requestLimit = new RequestLimit(maximumConcurrentRequests, queueSize);
         this.nextHandler = nextHandler;
      }
   }

   public RequestLimitingHandler(RequestLimit requestLimit, HttpHandler nextHandler) {
      if (nextHandler == null) {
         throw new IllegalArgumentException("nextHandler is null");
      } else {
         this.requestLimit = requestLimit;
         this.nextHandler = nextHandler;
      }
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      this.requestLimit.handleRequest(exchange, this.nextHandler);
   }

   public RequestLimit getRequestLimit() {
      return this.requestLimit;
   }

   public String toString() {
      return "request-limit( " + this.requestLimit.getMaximumConcurrentRequests() + " )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final int requests;

      private Wrapper(int requests) {
         this.requests = requests;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new RequestLimitingHandler(this.requests, handler);
      }

      // $FF: synthetic method
      Wrapper(int x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "request-limit";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("requests", Integer.TYPE);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("requests");
      }

      public String defaultParameter() {
         return "requests";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((Integer)config.get("requests"));
      }
   }
}
