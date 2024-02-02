package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;

public class FalsePredicate implements Predicate {
   public static final FalsePredicate INSTANCE = new FalsePredicate();

   public static FalsePredicate instance() {
      return INSTANCE;
   }

   public boolean resolve(HttpServerExchange value) {
      return false;
   }

   public String toString() {
      return "false";
   }
}
