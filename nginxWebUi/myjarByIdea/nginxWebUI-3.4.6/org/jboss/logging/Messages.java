package org.jboss.logging;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

public final class Messages {
   private Messages() {
   }

   public static <T> T getBundle(Class<T> type) {
      return getBundle(type, LoggingLocale.getLocale());
   }

   public static <T> T getBundle(final Class<T> type, final Locale locale) {
      return System.getSecurityManager() == null ? doGetBundle(type, locale) : AccessController.doPrivileged(new PrivilegedAction<T>() {
         public T run() {
            return Messages.doGetBundle(type, locale);
         }
      });
   }

   private static <T> T doGetBundle(Class<T> type, Locale locale) {
      String language = locale.getLanguage();
      String country = locale.getCountry();
      String variant = locale.getVariant();
      Class<? extends T> bundleClass = null;
      if (variant != null && variant.length() > 0) {
         try {
            bundleClass = Class.forName(join(type.getName(), "$bundle", language, country, variant), true, type.getClassLoader()).asSubclass(type);
         } catch (ClassNotFoundException var13) {
         }
      }

      if (bundleClass == null && country != null && country.length() > 0) {
         try {
            bundleClass = Class.forName(join(type.getName(), "$bundle", language, country, (String)null), true, type.getClassLoader()).asSubclass(type);
         } catch (ClassNotFoundException var12) {
         }
      }

      if (bundleClass == null && language != null && language.length() > 0) {
         try {
            bundleClass = Class.forName(join(type.getName(), "$bundle", language, (String)null, (String)null), true, type.getClassLoader()).asSubclass(type);
         } catch (ClassNotFoundException var11) {
         }
      }

      if (bundleClass == null) {
         try {
            bundleClass = Class.forName(join(type.getName(), "$bundle", (String)null, (String)null, (String)null), true, type.getClassLoader()).asSubclass(type);
         } catch (ClassNotFoundException var10) {
            throw new IllegalArgumentException("Invalid bundle " + type + " (implementation not found)");
         }
      }

      Field field;
      try {
         field = bundleClass.getField("INSTANCE");
      } catch (NoSuchFieldException var9) {
         throw new IllegalArgumentException("Bundle implementation " + bundleClass + " has no instance field");
      }

      try {
         return type.cast(field.get((Object)null));
      } catch (IllegalAccessException var8) {
         throw new IllegalArgumentException("Bundle implementation " + bundleClass + " could not be instantiated", var8);
      }
   }

   private static String join(String interfaceName, String a, String b, String c, String d) {
      StringBuilder build = new StringBuilder();
      build.append(interfaceName).append('_').append(a);
      if (b != null && b.length() > 0) {
         build.append('_');
         build.append(b);
      }

      if (c != null && c.length() > 0) {
         build.append('_');
         build.append(c);
      }

      if (d != null && d.length() > 0) {
         build.append('_');
         build.append(d);
      }

      return build.toString();
   }
}
