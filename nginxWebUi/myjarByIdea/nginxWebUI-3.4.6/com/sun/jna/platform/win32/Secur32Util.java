package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;

public abstract class Secur32Util {
   public static String getUserNameEx(int format) {
      char[] buffer = new char[128];
      IntByReference len = new IntByReference(buffer.length);
      boolean result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
      if (!result) {
         int rc = Kernel32.INSTANCE.GetLastError();
         switch (rc) {
            case 234:
               buffer = new char[len.getValue() + 1];
               result = Secur32.INSTANCE.GetUserNameEx(format, buffer, len);
               break;
            default:
               throw new Win32Exception(Native.getLastError());
         }
      }

      if (!result) {
         throw new Win32Exception(Native.getLastError());
      } else {
         return Native.toString(buffer);
      }
   }

   public static SecurityPackage[] getSecurityPackages() {
      IntByReference pcPackages = new IntByReference();
      Sspi.PSecPkgInfo pPackageInfo = new Sspi.PSecPkgInfo();
      int rc = Secur32.INSTANCE.EnumerateSecurityPackages(pcPackages, pPackageInfo);
      if (0 != rc) {
         throw new Win32Exception(rc);
      } else {
         Sspi.SecPkgInfo[] packagesInfo = pPackageInfo.toArray(pcPackages.getValue());
         ArrayList<SecurityPackage> packages = new ArrayList(pcPackages.getValue());
         Sspi.SecPkgInfo.ByReference[] var5 = packagesInfo;
         int var6 = packagesInfo.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Sspi.SecPkgInfo packageInfo = var5[var7];
            SecurityPackage securityPackage = new SecurityPackage();
            securityPackage.name = packageInfo.Name.toString();
            securityPackage.comment = packageInfo.Comment.toString();
            packages.add(securityPackage);
         }

         rc = Secur32.INSTANCE.FreeContextBuffer(pPackageInfo.pPkgInfo.getPointer());
         if (0 != rc) {
            throw new Win32Exception(rc);
         } else {
            return (SecurityPackage[])packages.toArray(new SecurityPackage[0]);
         }
      }
   }

   public static class SecurityPackage {
      public String name;
      public String comment;
   }
}
