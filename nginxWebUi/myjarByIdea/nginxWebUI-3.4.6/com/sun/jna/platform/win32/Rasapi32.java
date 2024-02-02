package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Rasapi32 extends StdCallLibrary {
   Rasapi32 INSTANCE = (Rasapi32)Native.load("Rasapi32", Rasapi32.class, W32APIOptions.DEFAULT_OPTIONS);

   int RasDial(WinRas.RASDIALEXTENSIONS.ByReference var1, String var2, WinRas.RASDIALPARAMS.ByReference var3, int var4, WinRas.RasDialFunc2 var5, WinNT.HANDLEByReference var6);

   int RasEnumConnections(WinRas.RASCONN[] var1, IntByReference var2, IntByReference var3);

   int RasGetConnectionStatistics(WinNT.HANDLE var1, Structure.ByReference var2);

   int RasGetConnectStatus(WinNT.HANDLE var1, Structure.ByReference var2);

   int RasGetCredentials(String var1, String var2, WinRas.RASCREDENTIALS.ByReference var3);

   int RasGetEntryProperties(String var1, String var2, WinRas.RASENTRY.ByReference var3, IntByReference var4, Pointer var5, Pointer var6);

   int RasGetProjectionInfo(WinNT.HANDLE var1, int var2, Pointer var3, IntByReference var4);

   int RasHangUp(WinNT.HANDLE var1);

   int RasSetEntryProperties(String var1, String var2, WinRas.RASENTRY.ByReference var3, int var4, byte[] var5, int var6);

   int RasGetEntryDialParams(String var1, WinRas.RASDIALPARAMS.ByReference var2, WinDef.BOOLByReference var3);

   int RasGetErrorString(int var1, char[] var2, int var3);
}
