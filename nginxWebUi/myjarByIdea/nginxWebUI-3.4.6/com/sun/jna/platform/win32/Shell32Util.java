package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.PointerByReference;

public abstract class Shell32Util {
   public static String getFolderPath(WinDef.HWND hwnd, int nFolder, WinDef.DWORD dwFlags) {
      char[] pszPath = new char[260];
      WinNT.HRESULT hr = Shell32.INSTANCE.SHGetFolderPath(hwnd, nFolder, (WinNT.HANDLE)null, dwFlags, pszPath);
      if (!hr.equals(W32Errors.S_OK)) {
         throw new Win32Exception(hr);
      } else {
         return Native.toString(pszPath);
      }
   }

   public static String getFolderPath(int nFolder) {
      return getFolderPath((WinDef.HWND)null, nFolder, ShlObj.SHGFP_TYPE_CURRENT);
   }

   public static String getKnownFolderPath(Guid.GUID guid) throws Win32Exception {
      int flags = ShlObj.KNOWN_FOLDER_FLAG.NONE.getFlag();
      PointerByReference outPath = new PointerByReference();
      WinNT.HANDLE token = null;
      WinNT.HRESULT hr = Shell32.INSTANCE.SHGetKnownFolderPath(guid, flags, (WinNT.HANDLE)token, outPath);
      if (!W32Errors.SUCCEEDED(hr.intValue())) {
         throw new Win32Exception(hr);
      } else {
         String result = outPath.getValue().getWideString(0L);
         Ole32.INSTANCE.CoTaskMemFree(outPath.getValue());
         return result;
      }
   }

   public static final String getSpecialFolderPath(int csidl, boolean create) {
      char[] pszPath = new char[260];
      if (!Shell32.INSTANCE.SHGetSpecialFolderPath((WinDef.HWND)null, pszPath, csidl, create)) {
         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
      } else {
         return Native.toString(pszPath);
      }
   }
}
