package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Mpr extends StdCallLibrary {
   Mpr INSTANCE = (Mpr)Native.load("Mpr", Mpr.class, W32APIOptions.DEFAULT_OPTIONS);

   int WNetOpenEnum(int var1, int var2, int var3, Winnetwk.NETRESOURCE.ByReference var4, WinNT.HANDLEByReference var5);

   int WNetEnumResource(WinNT.HANDLE var1, IntByReference var2, Pointer var3, IntByReference var4);

   int WNetCloseEnum(WinNT.HANDLE var1);

   int WNetGetUniversalName(String var1, int var2, Pointer var3, IntByReference var4);

   int WNetUseConnection(WinDef.HWND var1, Winnetwk.NETRESOURCE var2, String var3, String var4, int var5, Pointer var6, IntByReference var7, IntByReference var8);

   int WNetAddConnection3(WinDef.HWND var1, Winnetwk.NETRESOURCE var2, String var3, String var4, int var5);

   int WNetCancelConnection2(String var1, int var2, boolean var3);
}
