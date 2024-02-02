package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.HttpString;
import io.undertow.util.Methods;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IdempotentPredicate implements Predicate {
   public static final IdempotentPredicate INSTANCE = new IdempotentPredicate();
   private static final Set<HttpString> METHODS;

   public boolean resolve(HttpServerExchange value) {
      return METHODS.contains(value.getRequestMethod());
   }

   public String toString() {
      return "idempotent()";
   }

   static {
      Set<HttpString> methods = new HashSet();
      methods.add(Methods.GET);
      methods.add(Methods.DELETE);
      methods.add(Methods.PUT);
      methods.add(Methods.HEAD);
      methods.add(Methods.OPTIONS);
      METHODS = Collections.unmodifiableSet(methods);
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "idempotent";
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
         return IdempotentPredicate.INSTANCE;
      }
   }
}
