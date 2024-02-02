package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;

public class TruePredicate implements Predicate {
   public static final TruePredicate INSTANCE = new TruePredicate();

   public static TruePredicate instance() {
      return INSTANCE;
   }

   public boolean resolve(HttpServerExchange value) {
      return true;
   }

   public String toString() {
      return "true";
   }
}
