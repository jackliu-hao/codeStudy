package com.sun.jna.platform.win32;

import com.sun.jna.Platform;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.TypeMapper;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

public interface ShellAPI extends StdCallLibrary {
   int STRUCTURE_ALIGNMENT = Platform.is64Bit() ? 0 : 1;
   TypeMapper TYPE_MAPPER = Boolean.getBoolean("w32.ascii") ? W32APITypeMapper.ASCII : W32APITypeMapper.UNICODE;
   int FO_MOVE = 1;
   int FO_COPY = 2;
   int FO_DELETE = 3;
   int FO_RENAME = 4;
   int FOF_MULTIDESTFILES = 1;
   int FOF_CONFIRMMOUSE = 2;
   int FOF_SILENT = 4;
   int FOF_RENAMEONCOLLISION = 8;
   int FOF_NOCONFIRMATION = 16;
   int FOF_WANTMAPPINGHANDLE = 32;
   int FOF_ALLOWUNDO = 64;
   int FOF_FILESONLY = 128;
   int FOF_SIMPLEPROGRESS = 256;
   int FOF_NOCONFIRMMKDIR = 512;
   int FOF_NOERRORUI = 1024;
   int FOF_NOCOPYSECURITYATTRIBS = 2048;
   int FOF_NORECURSION = 4096;
   int FOF_NO_CONNECTED_ELEMENTS = 8192;
   int FOF_WANTNUKEWARNING = 16384;
   int FOF_NORECURSEREPARSE = 32768;
   int FOF_NO_UI = 1556;
   int PO_DELETE = 19;
   int PO_RENAME = 20;
   int PO_PORTCHANGE = 32;
   int PO_REN_PORT = 52;
   int ABM_NEW = 0;
   int ABM_REMOVE = 1;
   int ABM_QUERYPOS = 2;
   int ABM_SETPOS = 3;
   int ABM_GETSTATE = 4;
   int ABM_GETTASKBARPOS = 5;
   int ABM_ACTIVATE = 6;
   int ABM_GETAUTOHIDEBAR = 7;
   int ABM_SETAUTOHIDEBAR = 8;
   int ABM_WINDOWPOSCHANGED = 9;
   int ABM_SETSTATE = 10;
   int ABE_LEFT = 0;
   int ABE_TOP = 1;
   int ABE_RIGHT = 2;
   int ABE_BOTTOM = 3;

   @Structure.FieldOrder({"cbSize", "fMask", "hwnd", "lpVerb", "lpFile", "lpParameters", "lpDirectory", "nShow", "hInstApp", "lpIDList", "lpClass", "hKeyClass", "dwHotKey", "hMonitor", "hProcess"})
   public static class SHELLEXECUTEINFO extends Structure {
      public int cbSize = this.size();
      public int fMask;
      public WinDef.HWND hwnd;
      public String lpVerb;
      public String lpFile;
      public String lpParameters;
      public String lpDirectory;
      public int nShow;
      public WinDef.HINSTANCE hInstApp;
      public Pointer lpIDList;
      public String lpClass;
      public WinReg.HKEY hKeyClass;
      public int dwHotKey;
      public WinNT.HANDLE hMonitor;
      public WinNT.HANDLE hProcess;
   }

   @Structure.FieldOrder({"cbSize", "hWnd", "uCallbackMessage", "uEdge", "rc", "lParam"})
   public static class APPBARDATA extends Structure {
      public WinDef.DWORD cbSize;
      public WinDef.HWND hWnd;
      public WinDef.UINT uCallbackMessage;
      public WinDef.UINT uEdge;
      public WinDef.RECT rc;
      public WinDef.LPARAM lParam;

      public APPBARDATA() {
      }

      public APPBARDATA(Pointer p) {
         super(p);
      }

      public static class ByReference extends APPBARDATA implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"hwnd", "wFunc", "pFrom", "pTo", "fFlags", "fAnyOperationsAborted", "pNameMappings", "lpszProgressTitle"})
   public static class SHFILEOPSTRUCT extends Structure {
      public WinNT.HANDLE hwnd;
      public int wFunc;
      public String pFrom;
      public String pTo;
      public short fFlags;
      public boolean fAnyOperationsAborted;
      public Pointer pNameMappings;
      public String lpszProgressTitle;

      public String encodePaths(String[] paths) {
         String encoded = "";

         for(int i = 0; i < paths.length; ++i) {
            encoded = encoded + paths[i];
            encoded = encoded + "\u0000";
         }

         return encoded + "\u0000";
      }
   }
}
