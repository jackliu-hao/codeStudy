package org.wildfly.common;

public final class Branch {
   private Branch() {
   }

   public static boolean veryLikely(boolean expr) {
      return expr;
   }

   public static boolean veryUnlikely(boolean expr) {
      return expr;
   }

   public static boolean likely(boolean expr) {
      return expr;
   }

   public static boolean unlikely(boolean expr) {
      return expr;
   }

   public static boolean probability(float prob, boolean expr) {
      assert 0.0F <= prob && prob <= 1.0F;

      return expr;
   }
}
