package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class SecurePredicate implements Predicate {
   public static final SecurePredicate INSTANCE = new SecurePredicate();

   public boolean resolve(HttpServerExchange value) {
      return value.getRequestScheme().equals("https");
   }

   public String toString() {
      return "secure()";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "secure";
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

      public Predicate build(Map<String, Object> config) {
         return SecurePredicate.INSTANCE;
      }
   }
}
