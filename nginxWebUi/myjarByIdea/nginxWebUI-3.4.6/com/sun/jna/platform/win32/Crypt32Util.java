package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;

public abstract class Crypt32Util {
   public static byte[] cryptProtectData(byte[] data) {
      return cryptProtectData(data, 0);
   }

   public static byte[] cryptProtectData(byte[] data, int flags) {
      return cryptProtectData(data, (byte[])null, flags, "", (WinCrypt.CRYPTPROTECT_PROMPTSTRUCT)null);
   }

   public static byte[] cryptProtectData(byte[] data, byte[] entropy, int flags, String description, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
      WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
      WinCrypt.DATA_BLOB pDataProtected = new WinCrypt.DATA_BLOB();
      WinCrypt.DATA_BLOB pEntropy = entropy == null ? null : new WinCrypt.DATA_BLOB(entropy);

      byte[] var8;
      try {
         if (!Crypt32.INSTANCE.CryptProtectData(pDataIn, description, pEntropy, (Pointer)null, prompt, flags, pDataProtected)) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

         var8 = pDataProtected.getData();
      } finally {
         if (pDataProtected.pbData != null) {
            Kernel32Util.freeLocalMemory(pDataProtected.pbData);
         }

      }

      return var8;
   }

   public static byte[] cryptUnprotectData(byte[] data) {
      return cryptUnprotectData(data, 0);
   }

   public static byte[] cryptUnprotectData(byte[] data, int flags) {
      return cryptUnprotectData(data, (byte[])null, flags, (WinCrypt.CRYPTPROTECT_PROMPTSTRUCT)null);
   }

   public static byte[] cryptUnprotectData(byte[] data, byte[] entropy, int flags, WinCrypt.CRYPTPROTECT_PROMPTSTRUCT prompt) {
      WinCrypt.DATA_BLOB pDataIn = new WinCrypt.DATA_BLOB(data);
      WinCrypt.DATA_BLOB pDataUnprotected = new WinCrypt.DATA_BLOB();
      WinCrypt.DATA_BLOB pEntropy = entropy == null ? null : new WinCrypt.DATA_BLOB(entropy);
      PointerByReference pDescription = new PointerByReference();
      Win32Exception err = null;
      byte[] unProtectedData = null;

      try {
         if (!Crypt32.INSTANCE.CryptUnprotectData(pDataIn, pDescription, pEntropy, (Pointer)null, prompt, flags, pDataUnprotected)) {
            err = new Win32Exception(Kernel32.INSTANCE.GetLastError());
         } else {
            unProtectedData = pDataUnprotected.getData();
         }
      } finally {
         if (pDataUnprotected.pbData != null) {
            try {
               Kernel32Util.freeLocalMemory(pDataUnprotected.pbData);
            } catch (Win32Exception var19) {
               if (err == null) {
                  err = var19;
               } else {
                  err.addSuppressedReflected(var19);
               }
            }
         }

         if (pDescription.getValue() != null) {
            try {
               Kernel32Util.freeLocalMemory(pDescription.getValue());
            } catch (Win32Exception var18) {
               if (err == null) {
                  err = var18;
               } else {
                  err.addSuppressedReflected(var18);
               }
            }
         }

      }

      if (err != null) {
         throw err;
      } else {
         return unProtectedData;
      }
   }

   public static String CertNameToStr(int dwCertEncodingType, int dwStrType, WinCrypt.DATA_BLOB pName) {
      int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
      int requiredSize = Crypt32.INSTANCE.CertNameToStr(dwCertEncodingType, pName, dwStrType, Pointer.NULL, 0);
      Memory mem = new Memory((long)(requiredSize * charToBytes));
      int resultBytes = Crypt32.INSTANCE.CertNameToStr(dwCertEncodingType, pName, dwStrType, mem, requiredSize);

      assert resultBytes == requiredSize;

      return Boolean.getBoolean("w32.ascii") ? mem.getString(0L) : mem.getWideString(0L);
   }
}
