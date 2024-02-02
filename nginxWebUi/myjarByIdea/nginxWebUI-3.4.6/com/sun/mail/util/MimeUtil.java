package com.sun.mail.util;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import javax.mail.internet.MimePart;

public class MimeUtil {
   private static final Method cleanContentType;

   private MimeUtil() {
   }

   public static String cleanContentType(MimePart mp, String contentType) {
      if (cleanContentType != null) {
         try {
            return (String)cleanContentType.invoke((Object)null, mp, contentType);
         } catch (Exception var3) {
            return contentType;
         }
      } else {
         return contentType;
      }
   }

   private static ClassLoader getContextClassLoader() {
      return (ClassLoader)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            ClassLoader cl = null;

            try {
               cl = Thread.currentThread().getContextClassLoader();
            } catch (SecurityException var3) {
            }

            return cl;
         }
      });
   }

   static {
      Method meth = null;

      try {
         String cth = System.getProperty("mail.mime.contenttypehandler");
         if (cth != null) {
            ClassLoader cl = getContextClassLoader();
            Class clsHandler = null;
            if (cl != null) {
               try {
                  clsHandler = Class.forName(cth, false, cl);
               } catch (ClassNotFoundException var12) {
               }
            }

            if (clsHandler == null) {
               clsHandler = Class.forName(cth);
            }

            meth = clsHandler.getMethod("cleanContentType", MimePart.class, String.class);
         }
      } catch (ClassNotFoundException var13) {
      } catch (NoSuchMethodException var14) {
      } catch (RuntimeException var15) {
      } finally {
         cleanContentType = meth;
      }

   }
}
