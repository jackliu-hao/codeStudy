package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.PointerType;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Cryptui extends StdCallLibrary {
   Cryptui INSTANCE = (Cryptui)Native.load("Cryptui", Cryptui.class, W32APIOptions.UNICODE_OPTIONS);

   WinCrypt.CERT_CONTEXT.ByReference CryptUIDlgSelectCertificateFromStore(WinCrypt.HCERTSTORE var1, WinDef.HWND var2, String var3, String var4, int var5, int var6, PointerType var7);
}
