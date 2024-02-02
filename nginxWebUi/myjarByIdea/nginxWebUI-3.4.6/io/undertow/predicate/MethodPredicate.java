package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MethodPredicate implements Predicate {
   private final HttpString[] methods;

   MethodPredicate(String[] methods) {
      HttpString[] values = new HttpString[methods.length];

      for(int i = 0; i < methods.length; ++i) {
         values[i] = HttpString.tryFromString(methods[i]);
      }

      this.methods = values;
   }

   public boolean resolve(HttpServerExchange value) {
      for(int i = 0; i < this.methods.length; ++i) {
         if (value.getRequestMethod().equals(this.methods[i])) {
            return true;
         }
      }

      return false;
   }

   public String toString() {
      return this.methods.length == 1 ? "method( '" + this.methods[0] + "' )" : "method( {" + (String)Arrays.asList(this.methods).stream().map((s) -> {
         return s.toString();
      }).collect(Collectors.joining(", ")) + "} )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "method";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("value", String[].class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         String[] methods = (String[])((String[])config.get("value"));
         return new MethodPredicate(methods);
      }
   }
}
