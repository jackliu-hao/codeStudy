package org.apache.commons.compress.utils;

public class OsgiUtils {
   private static final boolean inOsgiEnvironment;

   private static boolean isBundleReference(Class<?> clazz) {
      for(Class<?> c = clazz; c != null; c = c.getSuperclass()) {
         if (c.getName().equals("org.osgi.framework.BundleReference")) {
            return true;
         }

         Class[] var2 = c.getInterfaces();
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Class<?> ifc = var2[var4];
            if (isBundleReference(ifc)) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean isRunningInOsgiEnvironment() {
      return inOsgiEnvironment;
   }

   static {
      Class<?> classloaderClass = OsgiUtils.class.getClassLoader().getClass();
      inOsgiEnvironment = isBundleReference(classloaderClass);
   }
}
