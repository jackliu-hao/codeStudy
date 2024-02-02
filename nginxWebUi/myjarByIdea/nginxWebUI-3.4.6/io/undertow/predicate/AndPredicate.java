package io.undertow.predicate;

import io.undertow.server.HttpServerExchange;

class AndPredicate implements Predicate {
   private final Predicate[] predicates;

   AndPredicate(Predicate... predicates) {
      this.predicates = predicates;
   }

   public boolean resolve(HttpServerExchange value) {
      Predicate[] var2 = this.predicates;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Predicate predicate = var2[var4];
         if (!predicate.resolve(value)) {
            return false;
         }
      }

      return true;
   }

   public String toString() {
      StringBuilder result = new StringBuilder();
      Predicate[] var2 = this.predicates;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Predicate predicate = var2[var4];
         if (result.length() > 0) {
            result.append(" and ");
         }

         result.append(predicate.toString());
      }

      return result.toString();
   }
}
