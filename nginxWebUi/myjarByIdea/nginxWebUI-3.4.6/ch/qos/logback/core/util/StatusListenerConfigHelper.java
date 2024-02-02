package ch.qos.logback.core.util;

import ch.qos.logback.core.Context;
import ch.qos.logback.core.spi.ContextAware;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.OnConsoleStatusListener;
import ch.qos.logback.core.status.StatusListener;

public class StatusListenerConfigHelper {
   public static void installIfAsked(Context context) {
      String slClass = OptionHelper.getSystemProperty("logback.statusListenerClass");
      if (!OptionHelper.isEmpty(slClass)) {
         addStatusListener(context, slClass);
      }

   }

   private static void addStatusListener(Context context, String listenerClassName) {
      StatusListener listener = null;
      if ("SYSOUT".equalsIgnoreCase(listenerClassName)) {
         listener = new OnConsoleStatusListener();
      } else {
         listener = createListenerPerClassName(context, listenerClassName);
      }

      initAndAddListener(context, (StatusListener)listener);
   }

   private static void initAndAddListener(Context context, StatusListener listener) {
      if (listener != null) {
         if (listener instanceof ContextAware) {
            ((ContextAware)listener).setContext(context);
         }

         boolean effectivelyAdded = context.getStatusManager().add(listener);
         if (effectivelyAdded && listener instanceof LifeCycle) {
            ((LifeCycle)listener).start();
         }
      }

   }

   private static StatusListener createListenerPerClassName(Context context, String listenerClass) {
      try {
         return (StatusListener)OptionHelper.instantiateByClassName(listenerClass, StatusListener.class, context);
      } catch (Exception var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public static void addOnConsoleListenerInstance(Context context, OnConsoleStatusListener onConsoleStatusListener) {
      onConsoleStatusListener.setContext(context);
      boolean effectivelyAdded = context.getStatusManager().add((StatusListener)onConsoleStatusListener);
      if (effectivelyAdded) {
         onConsoleStatusListener.start();
      }

   }
}
