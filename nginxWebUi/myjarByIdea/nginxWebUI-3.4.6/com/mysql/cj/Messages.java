package com.mysql.cj;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
   private static final String BUNDLE_NAME = "com.mysql.cj.LocalizedErrorMessages";
   private static final ResourceBundle RESOURCE_BUNDLE;
   private static final Object[] emptyObjectArray = new Object[0];

   public static String getString(String key) {
      return getString(key, emptyObjectArray);
   }

   public static String getString(String key, Object[] args) {
      if (RESOURCE_BUNDLE == null) {
         throw new RuntimeException("Localized messages from resource bundle 'com.mysql.cj.LocalizedErrorMessages' not loaded during initialization of driver.");
      } else {
         try {
            if (key == null) {
               throw new IllegalArgumentException("Message key can not be null");
            } else {
               String message = RESOURCE_BUNDLE.getString(key);
               if (message == null) {
                  message = "Missing error message for key '" + key + "'";
               }

               return MessageFormat.format(message, args);
            }
         } catch (MissingResourceException var3) {
            return '!' + key + '!';
         }
      }
   }

   private Messages() {
   }

   static {
      ResourceBundle temp = null;

      try {
         temp = ResourceBundle.getBundle("com.mysql.cj.LocalizedErrorMessages", Locale.getDefault(), Messages.class.getClassLoader());
      } catch (Throwable var9) {
         try {
            temp = ResourceBundle.getBundle("com.mysql.cj.LocalizedErrorMessages");
         } catch (Throwable var8) {
            RuntimeException rt = new RuntimeException("Can't load resource bundle due to underlying exception " + var9.toString());
            rt.initCause(var8);
            throw rt;
         }
      } finally {
         RESOURCE_BUNDLE = temp;
      }

   }
}
