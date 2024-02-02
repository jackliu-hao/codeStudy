package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

/** @deprecated */
@Deprecated
public class MaxContentSizePredicate implements Predicate {
   private final long maxSize;

   MaxContentSizePredicate(long maxSize) {
      this.maxSize = maxSize;
   }

   public boolean resolve(HttpServerExchange value) {
      String length = value.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
      if (length == null) {
         return false;
      } else {
         return Long.parseLong(length) > this.maxSize;
      }
   }

   public String toString() {
      return "max-content-size( " + this.maxSize + " )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "max-content-size";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("value", Long.class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("value");
      }

      public String defaultParameter() {
         return "value";
      }

      public Predicate build(Map<String, Object> config) {
         Long max = (Long)config.get("value");
         return new MaxContentSizePredicate(max);
      }
   }
}
