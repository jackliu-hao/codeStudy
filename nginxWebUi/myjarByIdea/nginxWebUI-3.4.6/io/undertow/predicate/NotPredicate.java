package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;

public class NotPredicate implements Predicate {
   private final Predicate predicate;

   NotPredicate(Predicate predicate) {
      this.predicate = predicate;
   }

   public boolean resolve(HttpServerExchange value) {
      return !this.predicate.resolve(value);
   }

   public String toString() {
      return " not " + this.predicate.toString();
   }
}
