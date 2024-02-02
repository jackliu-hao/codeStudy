package com.github.jaiimageio.impl.common;

import java.security.AccessController;
import java.security.PrivilegedAction;

public class PackageUtil {
   private static boolean isCodecLibAvailable = false;
   private static String version = "1.0";
   private static String vendor = "Sun Microsystems, Inc.";
   private static String specTitle = "Java Advanced Imaging Image I/O Tools";

   public static final boolean isCodecLibAvailable() {
      Boolean result = (Boolean)AccessController.doPrivileged(new PrivilegedAction() {
         public Object run() {
            String property = null;

            try {
               property = System.getProperty("com.github.jaiimageio.disableCodecLib");
            } catch (SecurityException var3) {
            }

            return property != null && property.equalsIgnoreCase("true") ? Boolean.TRUE : Boolean.FALSE;
         }
      });
      boolean isCodecLibDisabled = result;
      return isCodecLibAvailable && !isCodecLibDisabled;
   }

   public static final String getVersion() {
      return version;
   }

   public static final String getVendor() {
      return vendor;
   }

   public static final String getSpecificationTitle() {
      return specTitle;
   }

   static {
      isCodecLibAvailable = false;

      try {
         Class thisClass = Class.forName("com.github.jaiimageio.impl.common.PackageUtil");
         Package thisPackage = thisClass.getPackage();
         if (thisPackage.getImplementationVersion() != null && thisPackage.getImplementationVendor() != null) {
            version = thisPackage.getImplementationVersion();
            vendor = thisPackage.getImplementationVendor();
            specTitle = thisPackage.getSpecificationTitle();
         }
      } catch (ClassNotFoundException var2) {
      }

   }
}
