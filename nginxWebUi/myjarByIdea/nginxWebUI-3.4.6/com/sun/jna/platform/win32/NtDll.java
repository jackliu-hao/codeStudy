package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface NtDll extends StdCallLibrary {
   NtDll INSTANCE = (NtDll)Native.load("NtDll", NtDll.class, W32APIOptions.DEFAULT_OPTIONS);

   int ZwQueryKey(WinNT.HANDLE var1, int var2, Structure var3, int var4, IntByReference var5);

   int NtSetSecurityObject(WinNT.HANDLE var1, int var2, Pointer var3);

   int NtQuerySecurityObject(WinNT.HANDLE var1, int var2, Pointer var3, int var4, IntByReference var5);

   int RtlNtStatusToDosError(int var1);
}
