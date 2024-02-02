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

public class DisallowedMethodsHandler implements HttpHandler {
   private final Set<HttpString> disallowedMethods;
   private final HttpHandler next;

   public DisallowedMethodsHandler(HttpHandler next, Set<HttpString> disallowedMethods) {
      this.disallowedMethods = new HashSet(disallowedMethods);
      this.next = next;
   }

   public DisallowedMethodsHandler(HttpHandler next, HttpString... disallowedMethods) {
      this.disallowedMethods = new HashSet(Arrays.asList(disallowedMethods));
      this.next = next;
   }

   public void handleRequest(HttpServerExchange exchange) throws Exception {
      if (this.disallowedMethods.contains(exchange.getRequestMethod())) {
         exchange.setStatusCode(405);
         exchange.endExchange();
      } else {
         this.next.handleRequest(exchange);
      }

   }

   public String toString() {
      return this.disallowedMethods.size() == 1 ? "disallowed-methods( " + this.disallowedMethods.toArray()[0] + " )" : "disallowed-methods( {" + (String)this.disallowedMethods.stream().map((s) -> {
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

         return new DisallowedMethodsHandler(handler, strings);
      }

      // $FF: synthetic method
      Wrapper(String[] x0, Object x1) {
         this(x0);
      }
   }

   public static class Builder implements HandlerBuilder {
      public String name() {
         return "disallowed-methods";
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
