package org.jboss.logging;

public final class NDC {
   private NDC() {
   }

   public static void clear() {
      LoggerProviders.PROVIDER.clearNdc();
   }

   public static String get() {
      return LoggerProviders.PROVIDER.getNdc();
   }

   public static int getDepth() {
      return LoggerProviders.PROVIDER.getNdcDepth();
   }

   public static String pop() {
      return LoggerProviders.PROVIDER.popNdc();
   }

   public static String peek() {
      return LoggerProviders.PROVIDER.peekNdc();
   }

   public static void push(String message) {
      LoggerProviders.PROVIDER.pushNdc(message);
   }

   public static void setMaxDepth(int maxDepth) {
      LoggerProviders.PROVIDER.setNdcMaxDepth(maxDepth);
   }
}
