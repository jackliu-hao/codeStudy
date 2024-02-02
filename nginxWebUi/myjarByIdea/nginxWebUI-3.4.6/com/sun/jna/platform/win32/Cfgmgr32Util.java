package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;

public abstract class Cfgmgr32Util {
   public static String CM_Get_Device_ID(int devInst) throws Cfgmgr32Exception {
      int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
      IntByReference pulLen = new IntByReference();
      int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
      if (ret != 0) {
         throw new Cfgmgr32Exception(ret);
      } else {
         Memory buffer = new Memory((long)((pulLen.getValue() + 1) * charToBytes));
         buffer.clear();
         ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
         if (ret == 26) {
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
            if (ret != 0) {
               throw new Cfgmgr32Exception(ret);
            }

            buffer = new Memory((long)((pulLen.getValue() + 1) * charToBytes));
            buffer.clear();
            ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, buffer, pulLen.getValue(), 0);
         }

         if (ret != 0) {
            throw new Cfgmgr32Exception(ret);
         } else {
            return charToBytes == 1 ? buffer.getString(0L) : buffer.getWideString(0L);
         }
      }
   }

   public static class Cfgmgr32Exception extends RuntimeException {
      private final int errorCode;

      public Cfgmgr32Exception(int errorCode) {
         this.errorCode = errorCode;
      }

      public int getErrorCode() {
         return this.errorCode;
      }
   }
}
