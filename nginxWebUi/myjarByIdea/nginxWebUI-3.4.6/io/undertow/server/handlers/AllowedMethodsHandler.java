package io.undertow.server.handlers;

import io.undertow.server.HandlerWrapper;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.builder.HandlerBuilder;
import io.undertow.util.HttpString;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class AllowedMethodsHandler implements HttpHandler {
   private final Set<HttpString> allowedMethods;
   private final HttpHandler next;

   public AllowedMethodsHandler(HttpHandler next, Set<HttpString> allowedMethods) {
      this.allowedMethods = new HashSet(allowedMethods);
      this.next = next;
   }

   public AllowedMethodsHandler(HttpHandler next, HttpString... allowedMethods) {
      this.allowedMethods = new HashSet(Arrays.asList(allowedMethods));
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (this.allowedMethods.contains(exchange.getRequestMethod())) {
         this.next.handleRequest(exchange);
      } else {
         exchange.setStatusCode(405);
         exchange.endExchange();
      }

   }

   public Set<HttpString> getAllowedMethods() {
      return Collections.unmodifiableSet(this.allowedMethods);
   }

   public String toString() {
      return this.allowedMethods.size() == 1 ? "allowed-methods( " + this.allowedMethods.toArray()[0] + " )" : "allowed-methods( {" + (String)this.allowedMethods.stream().map((s) -> {
         return s.toString();
      }).collect(Collectors.joining(", ")) + "} )";
   }

   private static class Wrapper implements HandlerWrapper {
      private final String[] methods;

      private Wrapper(String[] methods) {
         this.methods = methods;
      }

      public HttpHandler wrap(HttpHandler handler) {
         HttpString[] strings = new HttpString[this.methods.length];

         for(int i = 0; i < this.methods.length; ++i) {
            strings[i] = new HttpString(this.methods[i]);
         }

         return new AllowedMethodsHandler(handler, strings);
      }

      // $FF: synthetic method
      Wrapper(String[] x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "allowed-methods";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("methods", String[].class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("methods");
      }

      public String defaultParameter() {
         return "methods";
      }

      public HandlerWrapper build(Map<String, Object> config) {
         return new Wrapper((String[])((String[])config.get("methods")));
      }
   }
}
