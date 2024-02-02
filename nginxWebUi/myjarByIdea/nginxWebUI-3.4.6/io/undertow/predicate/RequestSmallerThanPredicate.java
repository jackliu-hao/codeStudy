package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class RequestSmallerThanPredicate implements Predicate {
   private final long size;

   RequestSmallerThanPredicate(long size) {
      this.size = size;
   }

   public boolean resolve(HttpServerExchange exchange) {
      String length = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
      if (length == null) {
         return false;
      } else {
         return Long.parseLong(length) < this.size;
      }
   }

   public String toString() {
      return "request-smaller-than( '" + this.size + "' )";
   }

   public static class Builder implements PredicateBuilder {
      public String name() {
         return "request-smaller-than";
      }

      public Map<String, Class<?>> parameters() {
         return Collections.singletonMap("size", Long.class);
      }

      public Set<String> requiredParameters() {
         return Collections.singleton("size");
      }

      public String defaultParameter() {
         return "size";
      }

      public Predicate build(Map<String, Object> config) {
         Long size = (Long)config.get("size");
         return new RequestSmallerThanPredicate(size);
      }
   }
}
