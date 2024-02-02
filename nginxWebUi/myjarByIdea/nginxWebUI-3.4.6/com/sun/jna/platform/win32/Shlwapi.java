package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Shlwapi extends StdCallLibrary {
   Shlwapi INSTANCE = (Shlwapi)Native.load("Shlwapi", Shlwapi.class, W32APIOptions.DEFAULT_OPTIONS);

   WinNT.HRESULT StrRetToStr(ShTypes.STRRET var1, Pointer var2, PointerByReference var3);

   boolean PathIsUNC(String var1);
}
