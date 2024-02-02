package org.jboss.threads;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.concurrent.AbstractExecutorService;

abstract class EnhancedQueueExecutorBase0 extends AbstractExecutorService {
   int p00;
   int p01;
   int p02;
   int p03;
   int p04;
   int p05;
   int p06;
   int p07;
   int p08;
   int p09;
   int p0A;
   int p0B;
   int p0C;
   int p0D;
   int p0E;
   int p0F;

   static int readIntPropertyPrefixed(String name, int defVal) {
      try {
         return Integer.parseInt(readPropertyPrefixed(name, Integer.toString(defVal)));
      } catch (NumberFormatException var3) {
         return defVal;
      }
   }

   static boolean readBooleanPropertyPrefixed(String name, boolean defVal) {
      return Boolean.parseBoolean(readPropertyPrefixed(name, Boolean.toString(defVal)));
   }

   static String readPropertyPrefixed(String name, String defVal) {
      return readProperty("jboss.threads.eqe." + name, defVal);
   }

   static String readProperty(final String name, final String defVal) {
      SecurityManager sm = System.getSecurityManager();
      return sm != null ? (String)AccessController.doPrivileged(new PrivilegedAction<String>() {
         public String run() {
            return EnhancedQueueExecutorBase0.readPropertyRaw(name, defVal);
         }
      }) : readPropertyRaw(name, defVal);
   }

   static String readPropertyRaw(String name, String defVal) {
      return System.getProperty(name, defVal);
   }
}
