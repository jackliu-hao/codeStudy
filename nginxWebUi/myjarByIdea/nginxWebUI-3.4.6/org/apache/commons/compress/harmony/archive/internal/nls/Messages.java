package org.apache.commons.compress.harmony.archive.internal.nls;

import java.security.AccessController;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Messages {
   private static ResourceBundle bundle = null;

   public static String getString(String msg) {
      if (bundle == null) {
         return msg;
      } else {
         try {
            return bundle.getString(msg);
         } catch (MissingResourceException var2) {
            return "Missing message: " + msg;
         }
      }
   }

   public static String getString(String msg, Object arg) {
      return getString(msg, new Object[]{arg});
   }

   public static String getString(String msg, int arg) {
      return getString(msg, new Object[]{Integer.toString(arg)});
   }

   public static String getString(String msg, char arg) {
      return getString(msg, new Object[]{String.valueOf(arg)});
   }

   public static String getString(String msg, Object arg1, Object arg2) {
      return getString(msg, new Object[]{arg1, arg2});
   }

   public static String getString(String msg, Object[] args) {
      String format = msg;
      if (bundle != null) {
         try {
            format = bundle.getString(msg);
         } catch (MissingResourceException var4) {
         }
      }

      return format(format, args);
   }

   public static String format(String format, Object[] args) {
      StringBuilder answer = new StringBuilder(format.length() + args.length * 20);
      String[] argStrings = new String[args.length];

      int lastI;
      for(lastI = 0; lastI < args.length; ++lastI) {
         if (args[lastI] == null) {
            argStrings[lastI] = "<null>";
         } else {
            argStrings[lastI] = args[lastI].toString();
         }
      }

      lastI = 0;

      for(int i = format.indexOf(123, 0); i >= 0; i = format.indexOf(123, lastI)) {
         if (i != 0 && format.charAt(i - 1) == '\\') {
            if (i != 1) {
               answer.append(format.substring(lastI, i - 1));
            }

            answer.append('{');
            lastI = i + 1;
         } else if (i > format.length() - 3) {
            answer.append(format.substring(lastI));
            lastI = format.length();
         } else {
            int argnum = (byte)Character.digit(format.charAt(i + 1), 10);
            if (argnum >= 0 && format.charAt(i + 2) == '}') {
               answer.append(format.substring(lastI, i));
               if (argnum >= argStrings.length) {
                  answer.append("<missing argument>");
               } else {
                  answer.append(argStrings[argnum]);
               }

               lastI = i + 3;
            } else {
               answer.append(format.substring(lastI, i + 1));
               lastI = i + 1;
            }
         }
      }

      if (lastI < format.length()) {
         answer.append(format.substring(lastI));
      }

      return answer.toString();
   }

   public static ResourceBundle setLocale(Locale locale, String resource) {
      try {
         ClassLoader loader = null;
         return (ResourceBundle)AccessController.doPrivileged(() -> {
            return ResourceBundle.getBundle(resource, locale, loader != null ? loader : ClassLoader.getSystemClassLoader());
         });
      } catch (MissingResourceException var3) {
         return null;
      }
   }

   static {
      try {
         bundle = setLocale(Locale.getDefault(), "org.apache.commons.compress.harmony.archive.internal.nls.messages");
      } catch (Throwable var1) {
         var1.printStackTrace();
      }

   }
}
