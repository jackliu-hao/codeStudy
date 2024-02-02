package io.undertow.predicate;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpServerExchange;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthenticationRequiredPredicate implements Predicate {
   public static final AuthenticationRequiredPredicate INSTANCE = new AuthenticationRequiredPredicate();

   public boolean resolve(HttpServerExchange value) {
      SecurityContext sc = value.getSecurityContext();
      return sc == null ? false : sc.isAuthenticationRequired();
   }

   public String toString() {
      return "auth-required()";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "auth-required";
      }

      public Map<String, Class<?>> parameters() {
         Map<String, Class<?>> params = new HashMap();
         return params;
      }

      public Set<String> requiredParameters() {
         Set<String> params = new HashSet();
         return params;
      }

      public String defaultParameter() {
         return null;
      }

      public Predicate build(Map<String, Object> config) {
         return AuthenticationRequiredPredicate.INSTANCE;
      }
   }
}
