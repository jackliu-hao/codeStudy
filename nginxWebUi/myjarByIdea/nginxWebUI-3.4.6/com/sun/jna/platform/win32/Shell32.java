package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.WString;
import com.sun.jna.ptr.PointerByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface Shell32 extends ShellAPI, StdCallLibrary {
   Shell32 INSTANCE = (Shell32)Native.load("shell32", Shell32.class, W32APIOptions.DEFAULT_OPTIONS);
   int SHERB_NOCONFIRMATION = 1;
   int SHERB_NOPROGRESSUI = 2;
   int SHERB_NOSOUND = 4;
   int SEE_MASK_NOCLOSEPROCESS = 64;
   int SEE_MASK_FLAG_NO_UI = 1024;

   int SHFileOperation(ShellAPI.SHFILEOPSTRUCT var1);

   WinNT.HRESULT SHGetFolderPath(WinDef.HWND var1, int var2, WinNT.HANDLE var3, WinDef.DWORD var4, char[] var5);

   WinNT.HRESULT SHGetKnownFolderPath(Guid.GUID var1, int var2, WinNT.HANDLE var3, PointerByReference var4);

   WinNT.HRESULT SHGetDesktopFolder(PointerByReference var1);

   WinDef.INT_PTR ShellExecute(WinDef.HWND var1, String var2, String var3, String var4, String var5, int var6);

   boolean SHGetSpecialFolderPath(WinDef.HWND var1, char[] var2, int var3, boolean var4);

   WinDef.UINT_PTR SHAppBarMessage(WinDef.DWORD var1, ShellAPI.APPBARDATA var2);

   int SHEmptyRecycleBin(WinNT.HANDLE var1, String var2, int var3);

   boolean ShellExecuteEx(ShellAPI.SHELLEXECUTEINFO var1);

   WinNT.HRESULT SHGetSpecialFolderLocation(WinDef.HWND var1, int var2, PointerByReference var3);

   int ExtractIconEx(String var1, int var2, WinDef.HICON[] var3, WinDef.HICON[] var4, int var5);

   WinNT.HRESULT GetCurrentProcessExplicitAppUserModelID(PointerByReference var1);

   WinNT.HRESULT SetCurrentProcessExplicitAppUserModelID(WString var1);
}
