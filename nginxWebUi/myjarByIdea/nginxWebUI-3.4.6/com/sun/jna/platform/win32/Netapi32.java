package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;
import com.sun.jna.win32.W32APITypeMapper;

public interface Netapi32 extends StdCallLibrary {
   Netapi32 INSTANCE = (Netapi32)Native.load("Netapi32", Netapi32.class, W32APIOptions.DEFAULT_OPTIONS);
   int MAX_PREFERRED_LENGTH = -1;

   int NetSessionEnum(WString var1, WString var2, WString var3, int var4, PointerByReference var5, int var6, IntByReference var7, IntByReference var8, IntByReference var9);

   int NetGetJoinInformation(String var1, PointerByReference var2, IntByReference var3);

   int NetApiBufferFree(Pointer var1);

   int NetLocalGroupEnum(String var1, int var2, PointerByReference var3, int var4, IntByReference var5, IntByReference var6, IntByReference var7);

   int NetGetDCName(String var1, String var2, PointerByReference var3);

   int NetGroupEnum(String var1, int var2, PointerByReference var3, int var4, IntByReference var5, IntByReference var6, IntByReference var7);

   int NetUserEnum(String var1, int var2, int var3, PointerByReference var4, int var5, IntByReference var6, IntByReference var7, IntByReference var8);

   int NetUserGetGroups(String var1, String var2, int var3, PointerByReference var4, int var5, IntByReference var6, IntByReference var7);

   int NetUserGetLocalGroups(String var1, String var2, int var3, int var4, PointerByReference var5, int var6, IntByReference var7, IntByReference var8);

   int NetUserAdd(String var1, int var2, Structure var3, IntByReference var4);

   int NetUserDel(String var1, String var2);

   int NetUserChangePassword(String var1, String var2, String var3, String var4);

   int DsGetDcName(String var1, String var2, Guid.GUID var3, String var4, int var5, DsGetDC.PDOMAIN_CONTROLLER_INFO var6);

   int DsGetForestTrustInformation(String var1, String var2, int var3, NTSecApi.PLSA_FOREST_TRUST_INFORMATION var4);

   int DsEnumerateDomainTrusts(String var1, int var2, PointerByReference var3, IntByReference var4);

   int NetUserGetInfo(String var1, String var2, int var3, PointerByReference var4);

   int NetShareAdd(String var1, int var2, Pointer var3, IntByReference var4);

   int NetShareDel(String var1, String var2, int var3);

   @Structure.FieldOrder({"sesi10_cname", "sesi10_username", "sesi10_time", "sesi10_idle_time"})
   public static class SESSION_INFO_10 extends Structure {
      public String sesi10_cname;
      public String sesi10_username;
      public int sesi10_time;
      public int sesi10_idle_time;

      public SESSION_INFO_10() {
         super(W32APITypeMapper.UNICODE);
      }

      public SESSION_INFO_10(Pointer p) {
         super(p, 0, W32APITypeMapper.UNICODE);
         this.read();
      }
   }
}
