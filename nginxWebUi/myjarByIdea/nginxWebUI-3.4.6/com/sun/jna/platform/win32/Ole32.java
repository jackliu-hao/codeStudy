package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.COM.Unknown;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Ole32 extends StdCallLibrary {
   Ole32 INSTANCE = (Ole32)Native.load("Ole32", Ole32.class, W32APIOptions.DEFAULT_OPTIONS);
   int COINIT_APARTMENTTHREADED = 2;
   int COINIT_MULTITHREADED = 0;
   int COINIT_DISABLE_OLE1DDE = 4;
   int COINIT_SPEED_OVER_MEMORY = 8;
   int RPC_C_AUTHN_LEVEL_DEFAULT = 0;
   int RPC_C_AUTHN_WINNT = 10;
   int RPC_C_IMP_LEVEL_IMPERSONATE = 3;
   int RPC_C_AUTHZ_NONE = 0;
   int RPC_C_AUTHN_LEVEL_CALL = 3;
   int EOAC_NONE = 0;

   WinNT.HRESULT CoCreateGuid(Guid.GUID var1);

   int StringFromGUID2(Guid.GUID var1, char[] var2, int var3);

   WinNT.HRESULT IIDFromString(String var1, Guid.GUID var2);

   WinNT.HRESULT CoInitialize(WinDef.LPVOID var1);

   WinNT.HRESULT CoInitializeEx(Pointer var1, int var2);

   WinNT.HRESULT CoInitializeSecurity(WinNT.SECURITY_DESCRIPTOR var1, int var2, Pointer var3, Pointer var4, int var5, int var6, Pointer var7, int var8, Pointer var9);

   WinNT.HRESULT CoSetProxyBlanket(Unknown var1, int var2, int var3, WTypes.LPOLESTR var4, int var5, int var6, Pointer var7, int var8);

   void CoUninitialize();

   WinNT.HRESULT CoCreateInstance(Guid.GUID var1, Pointer var2, int var3, Guid.GUID var4, PointerByReference var5);

   WinNT.HRESULT CLSIDFromProgID(String var1, Guid.CLSID.ByReference var2);

   WinNT.HRESULT CLSIDFromString(String var1, Guid.CLSID.ByReference var2);

   Pointer CoTaskMemAlloc(long var1);

   Pointer CoTaskMemRealloc(Pointer var1, long var2);

   void CoTaskMemFree(Pointer var1);

   WinNT.HRESULT CoGetMalloc(WinDef.DWORD var1, PointerByReference var2);

   WinNT.HRESULT GetRunningObjectTable(WinDef.DWORD var1, PointerByReference var2);

   WinNT.HRESULT CreateBindCtx(WinDef.DWORD var1, PointerByReference var2);

   boolean CoIsHandlerConnected(Pointer var1);

   WinNT.HRESULT OleInitialize(Pointer var1);

   void OleUninitialize();

   WinNT.HRESULT OleFlushClipboard();

   WinNT.HRESULT OleRun(Pointer var1);
}
