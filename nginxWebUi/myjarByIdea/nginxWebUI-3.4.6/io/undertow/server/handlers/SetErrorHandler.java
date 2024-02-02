package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SetErrorHandler implements HttpHandler {
   private final int responseCode;
   private final HttpHandler next;

   public SetErrorHandler(HttpHandler next, int responseCode) {
      this.next = next;
      this.responseCode = responseCode;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      exchange.setStatusCode(this.responseCode);
      this.next.handleRequest(exchange);
   }

   public String toString() {
      return "set-error( " + this.responseCode + " )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final Integer responseCode;

      private Wrapper(Integer responseCode) {
         this.responseCode = responseCode;
      }

      public HttpHandler wrap(HttpHandler handler) {
         return new SetErrorHandler(handler, this.responseCode);
      }

      // $FF: synthetic method
      Wrapper(Integer x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "set-error";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         params.put("response-code", Integer.class);
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> req = new HashSet();
         req.add("response-code");
         return req;
      }

      public String defaultParameter() {
         return "response-code";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((Integer)config.get("response-code"));
      }
   }
}
