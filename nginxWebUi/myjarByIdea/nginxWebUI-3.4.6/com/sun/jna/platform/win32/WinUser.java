package com.sun.jna.platform.win32;

import com.sun.jna.Callback;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.Union;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APITypeMapper;

public interface WinUser extends WinDef {
   WinDef.HWND HWND_BROADCAST = new WinDef.HWND(Pointer.createConstant(65535));
   WinDef.HWND HWND_MESSAGE = new WinDef.HWND(Pointer.createConstant(-3));
   int FLASHW_STOP = 0;
   int FLASHW_CAPTION = 1;
   int FLASHW_TRAY = 2;
   int FLASHW_ALL = 3;
   int FLASHW_TIMER = 4;
   int FLASHW_TIMERNOFG = 12;
   int IMAGE_BITMAP = 0;
   int IMAGE_ICON = 1;
   int IMAGE_CURSOR = 2;
   int IMAGE_ENHMETAFILE = 3;
   int LR_DEFAULTCOLOR = 0;
   int LR_MONOCHROME = 1;
   int LR_COLOR = 2;
   int LR_COPYRETURNORG = 4;
   int LR_COPYDELETEORG = 8;
   int LR_LOADFROMFILE = 16;
   int LR_LOADTRANSPARENT = 32;
   int LR_DEFAULTSIZE = 64;
   int LR_VGACOLOR = 128;
   int LR_LOADMAP3DCOLORS = 4096;
   int LR_CREATEDIBSECTION = 8192;
   int LR_COPYFROMRESOURCE = 16384;
   int LR_SHARED = 32768;
   int GWL_EXSTYLE = -20;
   int GWL_STYLE = -16;
   int GWL_WNDPROC = -4;
   int GWL_HINSTANCE = -6;
   int GWL_ID = -12;
   int GWL_USERDATA = -21;
   int GWL_HWNDPARENT = -8;
   int DWL_DLGPROC = Native.POINTER_SIZE;
   int DWL_MSGRESULT = 0;
   int DWL_USER = 2 * Native.POINTER_SIZE;
   int WS_BORDER = 8388608;
   int WS_CAPTION = 12582912;
   int WS_CHILD = 1073741824;
   int WS_CHILDWINDOW = 1073741824;
   int WS_CLIPCHILDREN = 33554432;
   int WS_CLIPSIBLINGS = 67108864;
   int WS_DISABLED = 134217728;
   int WS_DLGFRAME = 4194304;
   int WS_GROUP = 131072;
   int WS_HSCROLL = 1048576;
   int WS_ICONIC = 536870912;
   int WS_MAXIMIZE = 16777216;
   int WS_MAXIMIZEBOX = 65536;
   int WS_MINIMIZE = 536870912;
   int WS_MINIMIZEBOX = 131072;
   int WS_OVERLAPPED = 0;
   int WS_POPUP = Integer.MIN_VALUE;
   int WS_SYSMENU = 524288;
   int WS_THICKFRAME = 262144;
   int WS_POPUPWINDOW = -2138570752;
   int WS_OVERLAPPEDWINDOW = 13565952;
   int WS_SIZEBOX = 262144;
   int WS_TABSTOP = 65536;
   int WS_TILED = 0;
   int WS_TILEDWINDOW = 13565952;
   int WS_VISIBLE = 268435456;
   int WS_VSCROLL = 2097152;
   int WS_EX_COMPOSITED = 536870912;
   int WS_EX_LAYERED = 524288;
   int WS_EX_TRANSPARENT = 32;
   int LWA_COLORKEY = 1;
   int LWA_ALPHA = 2;
   int ULW_COLORKEY = 1;
   int ULW_ALPHA = 2;
   int ULW_OPAQUE = 4;
   int AC_SRC_OVER = 0;
   int AC_SRC_ALPHA = 1;
   int AC_SRC_NO_PREMULT_ALPHA = 1;
   int AC_SRC_NO_ALPHA = 2;
   int VK_SHIFT = 16;
   int VK_LSHIFT = 160;
   int VK_RSHIFT = 161;
   int VK_CONTROL = 17;
   int VK_LCONTROL = 162;
   int VK_RCONTROL = 163;
   int VK_MENU = 18;
   int VK_LMENU = 164;
   int VK_RMENU = 165;
   int MOD_ALT = 1;
   int MOD_CONTROL = 2;
   int MOD_NOREPEAT = 16384;
   int MOD_SHIFT = 4;
   int MOD_WIN = 8;
   int WH_KEYBOARD = 2;
   int WH_CALLWNDPROC = 4;
   int WH_MOUSE = 7;
   int WH_KEYBOARD_LL = 13;
   int WH_MOUSE_LL = 14;
   int WM_PAINT = 15;
   int WM_CLOSE = 16;
   int WM_QUIT = 18;
   int WM_SHOWWINDOW = 24;
   int WM_DRAWITEM = 43;
   int WM_KEYDOWN = 256;
   int WM_CHAR = 258;
   int WM_SYSCOMMAND = 274;
   int WM_MDIMAXIMIZE = 549;
   int WM_HOTKEY = 786;
   int WM_USER = 1024;
   int WM_COPYDATA = 74;
   int WM_KEYUP = 257;
   int WM_SYSKEYDOWN = 260;
   int WM_SYSKEYUP = 261;
   int WM_SESSION_CHANGE = 689;
   int WM_CREATE = 1;
   int WM_SIZE = 5;
   int WM_DESTROY = 2;
   int WM_DEVICECHANGE = 537;
   int WM_GETICON = 127;
   int ICON_BIG = 1;
   int ICON_SMALL = 0;
   int ICON_SMALL2 = 2;
   int SM_CXSCREEN = 0;
   int SM_CYSCREEN = 1;
   int SM_CXVSCROLL = 2;
   int SM_CYHSCROLL = 3;
   int SM_CYCAPTION = 4;
   int SM_CXBORDER = 5;
   int SM_CYBORDER = 6;
   int SM_CXDLGFRAME = 7;
   int SM_CYDLGFRAME = 8;
   int SM_CYVTHUMB = 9;
   int SM_CXHTHUMB = 10;
   int SM_CXICON = 11;
   int SM_CYICON = 12;
   int SM_CXCURSOR = 13;
   int SM_CYCURSOR = 14;
   int SM_CYMENU = 15;
   int SM_CXFULLSCREEN = 16;
   int SM_CYFULLSCREEN = 17;
   int SM_CYKANJIWINDOW = 18;
   int SM_MOUSEPRESENT = 19;
   int SM_CYVSCROLL = 20;
   int SM_CXHSCROLL = 21;
   int SM_DEBUG = 22;
   int SM_SWAPBUTTON = 23;
   int SM_RESERVED1 = 24;
   int SM_RESERVED2 = 25;
   int SM_RESERVED3 = 26;
   int SM_RESERVED4 = 27;
   int SM_CXMIN = 28;
   int SM_CYMIN = 29;
   int SM_CXSIZE = 30;
   int SM_CYSIZE = 31;
   int SM_CXFRAME = 32;
   int SM_CYFRAME = 33;
   int SM_CXMINTRACK = 34;
   int SM_CYMINTRACK = 35;
   int SM_CXDOUBLECLK = 36;
   int SM_CYDOUBLECLK = 37;
   int SM_CXICONSPACING = 38;
   int SM_CYICONSPACING = 39;
   int SM_MENUDROPALIGNMENT = 40;
   int SM_PENWINDOWS = 41;
   int SM_DBCSENABLED = 42;
   int SM_CMOUSEBUTTONS = 43;
   int SM_CXFIXEDFRAME = 7;
   int SM_CYFIXEDFRAME = 8;
   int SM_CXSIZEFRAME = 32;
   int SM_CYSIZEFRAME = 33;
   int SM_SECURE = 44;
   int SM_CXEDGE = 45;
   int SM_CYEDGE = 46;
   int SM_CXMINSPACING = 47;
   int SM_CYMINSPACING = 48;
   int SM_CXSMICON = 49;
   int SM_CYSMICON = 50;
   int SM_CYSMCAPTION = 51;
   int SM_CXSMSIZE = 52;
   int SM_CYSMSIZE = 53;
   int SM_CXMENUSIZE = 54;
   int SM_CYMENUSIZE = 55;
   int SM_ARRANGE = 56;
   int SM_CXMINIMIZED = 57;
   int SM_CYMINIMIZED = 58;
   int SM_CXMAXTRACK = 59;
   int SM_CYMAXTRACK = 60;
   int SM_CXMAXIMIZED = 61;
   int SM_CYMAXIMIZED = 62;
   int SM_NETWORK = 63;
   int SM_CLEANBOOT = 67;
   int SM_CXDRAG = 68;
   int SM_CYDRAG = 69;
   int SM_SHOWSOUNDS = 70;
   int SM_CXMENUCHECK = 71;
   int SM_CYMENUCHECK = 72;
   int SM_SLOWMACHINE = 73;
   int SM_MIDEASTENABLED = 74;
   int SM_MOUSEWHEELPRESENT = 75;
   int SM_XVIRTUALSCREEN = 76;
   int SM_YVIRTUALSCREEN = 77;
   int SM_CXVIRTUALSCREEN = 78;
   int SM_CYVIRTUALSCREEN = 79;
   int SM_CMONITORS = 80;
   int SM_SAMEDISPLAYFORMAT = 81;
   int SM_IMMENABLED = 82;
   int SM_CXFOCUSBORDER = 83;
   int SM_CYFOCUSBORDER = 84;
   int SM_TABLETPC = 86;
   int SM_MEDIACENTER = 87;
   int SM_STARTER = 88;
   int SM_SERVERR2 = 89;
   int SM_MOUSEHORIZONTALWHEELPRESENT = 91;
   int SM_CXPADDEDBORDER = 92;
   int SM_REMOTESESSION = 4096;
   int SM_SHUTTINGDOWN = 8192;
   int SM_REMOTECONTROL = 8193;
   int SM_CARETBLINKINGENABLED = 8194;
   int SW_HIDE = 0;
   int SW_SHOWNORMAL = 1;
   int SW_NORMAL = 1;
   int SW_SHOWMINIMIZED = 2;
   int SW_SHOWMAXIMIZED = 3;
   int SW_MAXIMIZE = 3;
   int SW_SHOWNOACTIVATE = 4;
   int SW_SHOW = 5;
   int SW_MINIMIZE = 6;
   int SW_SHOWMINNOACTIVE = 7;
   int SW_SHOWNA = 8;
   int SW_RESTORE = 9;
   int SW_SHOWDEFAULT = 10;
   int SW_FORCEMINIMIZE = 11;
   int SW_MAX = 11;
   int RDW_INVALIDATE = 1;
   int RDW_INTERNALPAINT = 2;
   int RDW_ERASE = 4;
   int RDW_VALIDATE = 8;
   int RDW_NOINTERNALPAINT = 16;
   int RDW_NOERASE = 32;
   int RDW_NOCHILDREN = 64;
   int RDW_ALLCHILDREN = 128;
   int RDW_UPDATENOW = 256;
   int RDW_ERASENOW = 512;
   int RDW_FRAME = 1024;
   int RDW_NOFRAME = 2048;
   int GW_HWNDFIRST = 0;
   int GW_HWNDLAST = 1;
   int GW_HWNDNEXT = 2;
   int GW_HWNDPREV = 3;
   int GW_OWNER = 4;
   int GW_CHILD = 5;
   int GW_ENABLEDPOPUP = 6;
   int SWP_ASYNCWINDOWPOS = 16384;
   int SWP_DEFERERASE = 8192;
   int SWP_DRAWFRAME = 32;
   int SWP_FRAMECHANGED = 32;
   int SWP_HIDEWINDOW = 128;
   int SWP_NOACTIVATE = 16;
   int SWP_NOCOPYBITS = 256;
   int SWP_NOMOVE = 2;
   int SWP_NOOWNERZORDER = 512;
   int SWP_NOREDRAW = 8;
   int SWP_NOREPOSITION = 512;
   int SWP_NOSENDCHANGING = 1024;
   int SWP_NOSIZE = 1;
   int SWP_NOZORDER = 4;
   int SWP_SHOWWINDOW = 64;
   int SC_MINIMIZE = 61472;
   int SC_MAXIMIZE = 61488;
   int BS_PUSHBUTTON = 0;
   int BS_DEFPUSHBUTTON = 1;
   int BS_CHECKBOX = 2;
   int BS_AUTOCHECKBOX = 3;
   int BS_RADIOBUTTON = 4;
   int BS_3STATE = 5;
   int BS_AUTO3STATE = 6;
   int BS_GROUPBOX = 7;
   int BS_USERBUTTON = 8;
   int BS_AUTORADIOBUTTON = 9;
   int BS_PUSHBOX = 10;
   int BS_OWNERDRAW = 11;
   int BS_TYPEMASK = 15;
   int BS_LEFTTEXT = 32;
   int MONITOR_DEFAULTTONULL = 0;
   int MONITOR_DEFAULTTOPRIMARY = 1;
   int MONITOR_DEFAULTTONEAREST = 2;
   int MONITORINFOF_PRIMARY = 1;
   int CCHDEVICENAME = 32;
   int EWX_HYBRID_SHUTDOWN = 4194304;
   int EWX_LOGOFF = 0;
   int EWX_POWEROFF = 8;
   int EWX_REBOOT = 2;
   int EWX_RESTARTAPPS = 64;
   int EWX_SHUTDOWN = 1;
   int EWX_FORCE = 4;
   int EWX_FORCEIFHUNG = 16;
   int GA_PARENT = 1;
   int GA_ROOT = 2;
   int GA_ROOTOWNER = 3;
   int GCW_ATOM = -32;
   int GCL_HICON = -14;
   int GCL_HICONSM = -34;
   int GCL_CBCLSEXTRA = -20;
   int GCL_CBWNDEXTRA = -18;
   int GCLP_HBRBACKGROUND = -10;
   int GCLP_HCURSOR = -12;
   int GCLP_HICON = -14;
   int GCLP_HICONSM = -34;
   int GCLP_HMODULE = -16;
   int GCLP_MENUNAME = -8;
   int GCL_STYLE = -26;
   int GCLP_WNDPROC = -24;
   int SMTO_ABORTIFHUNG = 2;
   int SMTO_BLOCK = 1;
   int SMTO_NORMAL = 0;
   int SMTO_NOTIMEOUTIFNOTHUNG = 8;
   int SMTO_ERRORONEXIT = 32;
   int IDC_APPSTARTING = 32650;
   int IDC_ARROW = 32512;
   int IDC_CROSS = 32515;
   int IDC_HAND = 32649;
   int IDC_HELP = 32651;
   int IDC_IBEAM = 32513;
   int IDC_NO = 32648;
   int IDC_SIZEALL = 32646;
   int IDC_SIZENESW = 32643;
   int IDC_SIZENS = 32645;
   int IDC_SIZENWSE = 32642;
   int IDC_SIZEWE = 32644;
   int IDC_UPARROW = 32516;
   int IDC_WAIT = 32514;
   int IDI_APPLICATION = 32512;
   int IDI_ASTERISK = 32516;
   int IDI_EXCLAMATION = 32515;
   int IDI_HAND = 32513;
   int IDI_QUESTION = 32514;
   int IDI_WINLOGO = 32517;
   int RIM_TYPEMOUSE = 0;
   int RIM_TYPEKEYBOARD = 1;
   int RIM_TYPEHID = 2;
   int CF_BITMAT = 2;
   int CF_DIB = 8;
   int CF_DIBV5 = 17;
   int CF_DIF = 5;
   int CF_DSPBITMAP = 130;
   int CF_DSPENHMETAFILE = 142;
   int CF_DSPMETAFILEPICT = 131;
   int CF_DSPTEXT = 129;
   int CF_ENHMETAFILE = 14;
   int CF_GDIOBJFIRST = 768;
   int CF_GDIOBJLAST = 1023;
   int CF_HDROP = 15;
   int CF_LOCALE = 16;
   int CF_METAFILEPICT = 3;
   int CF_OEMTEXT = 7;
   int CF_OWNERDISPLAY = 128;
   int CF_PALETTE = 9;
   int CF_PENDATA = 10;
   int CF_PRIVATEFIRST = 512;
   int CF_PRIVATELAST = 767;
   int CF_RIFF = 11;
   int CF_SYLK = 4;
   int CF_TEXT = 1;
   int CF_TIFF = 6;
   int CF_UNICODETEXT = 13;
   int CF_WAVE = 12;
   int MAPVK_VK_TO_VSC = 0;
   int MAPVK_VSC_TO_VK = 1;
   int MAPVK_VK_TO_CHAR = 2;
   int MAPVK_VSC_TO_VK_EX = 3;
   int MAPVK_VK_TO_VSC_EX = 4;
   int KL_NAMELENGTH = 9;
   int MODIFIER_SHIFT_MASK = 1;
   int MODIFIER_CTRL_MASK = 2;
   int MODIFIER_ALT_MASK = 4;
   int MODIFIER_HANKAKU_MASK = 8;
   int MODIFIER_RESERVED1_MASK = 16;
   int MODIFIER_RESERVED2_MASK = 32;

   @Structure.FieldOrder({"hDevice", "dwType"})
   public static class RAWINPUTDEVICELIST extends Structure {
      public WinNT.HANDLE hDevice;
      public int dwType;

      public RAWINPUTDEVICELIST() {
      }

      public RAWINPUTDEVICELIST(Pointer p) {
         super(p);
      }

      public int sizeof() {
         return this.calculateSize(false);
      }

      public String toString() {
         return "hDevice=" + this.hDevice + ", dwType=" + this.dwType;
      }
   }

   public interface MONITORENUMPROC extends StdCallLibrary.StdCallCallback {
      int apply(HMONITOR var1, WinDef.HDC var2, WinDef.RECT var3, WinDef.LPARAM var4);
   }

   @Structure.FieldOrder({"cbSize", "rcMonitor", "rcWork", "dwFlags", "szDevice"})
   public static class MONITORINFOEX extends Structure {
      public int cbSize = this.size();
      public WinDef.RECT rcMonitor;
      public WinDef.RECT rcWork;
      public int dwFlags;
      public char[] szDevice = new char[32];
   }

   @Structure.FieldOrder({"cbSize", "rcMonitor", "rcWork", "dwFlags"})
   public static class MONITORINFO extends Structure {
      public int cbSize = this.size();
      public WinDef.RECT rcMonitor;
      public WinDef.RECT rcWork;
      public int dwFlags;
   }

   public static class HMONITOR extends WinNT.HANDLE {
      public HMONITOR() {
      }

      public HMONITOR(Pointer p) {
         super(p);
      }
   }

   public interface WindowProc extends StdCallLibrary.StdCallCallback {
      WinDef.LRESULT callback(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);
   }

   @Structure.FieldOrder({"cbSize", "style", "lpfnWndProc", "cbClsExtra", "cbWndExtra", "hInstance", "hIcon", "hCursor", "hbrBackground", "lpszMenuName", "lpszClassName", "hIconSm"})
   public static class WNDCLASSEX extends Structure {
      public int cbSize = this.size();
      public int style;
      public Callback lpfnWndProc;
      public int cbClsExtra;
      public int cbWndExtra;
      public WinDef.HINSTANCE hInstance;
      public WinDef.HICON hIcon;
      public WinDef.HCURSOR hCursor;
      public WinDef.HBRUSH hbrBackground;
      public String lpszMenuName;
      public String lpszClassName;
      public WinDef.HICON hIconSm;

      public WNDCLASSEX() {
         super(W32APITypeMapper.DEFAULT);
      }

      public WNDCLASSEX(Pointer memory) {
         super(memory, 0, W32APITypeMapper.DEFAULT);
         this.read();
      }

      public static class ByReference extends WNDCLASSEX implements Structure.ByReference {
      }
   }

   @Structure.FieldOrder({"cbSize", "dwTime"})
   public static class LASTINPUTINFO extends Structure {
      public int cbSize = this.size();
      public int dwTime;
   }

   @Structure.FieldOrder({"dx", "dy", "mouseData", "dwFlags", "time", "dwExtraInfo"})
   public static class MOUSEINPUT extends Structure {
      public WinDef.LONG dx;
      public WinDef.LONG dy;
      public WinDef.DWORD mouseData;
      public WinDef.DWORD dwFlags;
      public WinDef.DWORD time;
      public BaseTSD.ULONG_PTR dwExtraInfo;

      public MOUSEINPUT() {
      }

      public MOUSEINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends MOUSEINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"wVk", "wScan", "dwFlags", "time", "dwExtraInfo"})
   public static class KEYBDINPUT extends Structure {
      public static final int KEYEVENTF_EXTENDEDKEY = 1;
      public static final int KEYEVENTF_KEYUP = 2;
      public static final int KEYEVENTF_UNICODE = 4;
      public static final int KEYEVENTF_SCANCODE = 8;
      public WinDef.WORD wVk;
      public WinDef.WORD wScan;
      public WinDef.DWORD dwFlags;
      public WinDef.DWORD time;
      public BaseTSD.ULONG_PTR dwExtraInfo;

      public KEYBDINPUT() {
      }

      public KEYBDINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends KEYBDINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"type", "input"})
   public static class INPUT extends Structure {
      public static final int INPUT_MOUSE = 0;
      public static final int INPUT_KEYBOARD = 1;
      public static final int INPUT_HARDWARE = 2;
      public WinDef.DWORD type;
      public INPUT_UNION input = new INPUT_UNION();

      public INPUT() {
      }

      public INPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class INPUT_UNION extends Union {
         public MOUSEINPUT mi;
         public KEYBDINPUT ki;
         public HARDWAREINPUT hi;

         public INPUT_UNION() {
         }

         public INPUT_UNION(Pointer memory) {
            super(memory);
            this.read();
         }
      }

      public static class ByReference extends INPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"uMsg", "wParamL", "wParamH"})
   public static class HARDWAREINPUT extends Structure {
      public WinDef.DWORD uMsg;
      public WinDef.WORD wParamL;
      public WinDef.WORD wParamH;

      public HARDWAREINPUT() {
      }

      public HARDWAREINPUT(Pointer memory) {
         super(memory);
         this.read();
      }

      public static class ByReference extends HARDWAREINPUT implements Structure.ByReference {
         public ByReference() {
         }

         public ByReference(Pointer memory) {
            super(memory);
         }
      }
   }

   @Structure.FieldOrder({"vkCode", "scanCode", "flags", "time", "dwExtraInfo"})
   public static class KBDLLHOOKSTRUCT extends Structure {
      public int vkCode;
      public int scanCode;
      public int flags;
      public int time;
      public BaseTSD.ULONG_PTR dwExtraInfo;
   }

   @Structure.FieldOrder({"lParam", "wParam", "message", "hwnd"})
   public static class CWPSTRUCT extends Structure {
      public WinDef.LPARAM lParam;
      public WinDef.WPARAM wParam;
      public int message;
      public WinDef.HWND hwnd;

      public CWPSTRUCT() {
      }

      public CWPSTRUCT(Pointer p) {
         super(p);
         this.read();
      }
   }

   public interface HOOKPROC extends StdCallLibrary.StdCallCallback {
   }

   public static class HHOOK extends WinNT.HANDLE {
   }

   @Structure.FieldOrder({"BlendOp", "BlendFlags", "SourceConstantAlpha", "AlphaFormat"})
   public static class BLENDFUNCTION extends Structure {
      public byte BlendOp = 0;
      public byte BlendFlags = 0;
      public byte SourceConstantAlpha;
      public byte AlphaFormat;
   }

   @Structure.FieldOrder({"cx", "cy"})
   public static class SIZE extends Structure {
      public int cx;
      public int cy;

      public SIZE() {
      }

      public SIZE(int w, int h) {
         this.cx = w;
         this.cy = h;
      }
   }

   public interface WinEventProc extends Callback {
      void callback(WinNT.HANDLE var1, WinDef.DWORD var2, WinDef.HWND var3, WinDef.LONG var4, WinDef.LONG var5, WinDef.DWORD var6, WinDef.DWORD var7);
   }

   public interface LowLevelKeyboardProc extends HOOKPROC {
      WinDef.LRESULT callback(int var1, WinDef.WPARAM var2, KBDLLHOOKSTRUCT var3);
   }

   public interface WNDENUMPROC extends StdCallLibrary.StdCallCallback {
      boolean callback(WinDef.HWND var1, Pointer var2);
   }

   @Structure.FieldOrder({"cbSize", "hWnd", "dwFlags", "uCount", "dwTimeout"})
   public static class FLASHWINFO extends Structure {
      public int cbSize = this.size();
      public WinNT.HANDLE hWnd;
      public int dwFlags;
      public int uCount;
      public int dwTimeout;
   }

   @Structure.FieldOrder({"dwData", "cbData", "lpData"})
   public static class COPYDATASTRUCT extends Structure {
      public BaseTSD.ULONG_PTR dwData;
      public int cbData;
      public Pointer lpData;

      public COPYDATASTRUCT() {
      }

      public COPYDATASTRUCT(Pointer p) {
         super(p);
         this.read();
      }
   }

   @Structure.FieldOrder({"hWnd", "message", "wParam", "lParam", "time", "pt"})
   public static class MSG extends Structure {
      public WinDef.HWND hWnd;
      public int message;
      public WinDef.WPARAM wParam;
      public WinDef.LPARAM lParam;
      public int time;
      public WinDef.POINT pt;
   }

   @Structure.FieldOrder({"length", "flags", "showCmd", "ptMinPosition", "ptMaxPosition", "rcNormalPosition"})
   public static class WINDOWPLACEMENT extends Structure {
      public static final int WPF_SETMINPOSITION = 1;
      public static final int WPF_RESTORETOMAXIMIZED = 2;
      public static final int WPF_ASYNCWINDOWPLACEMENT = 4;
      public int length = this.size();
      public int flags;
      public int showCmd;
      public WinDef.POINT ptMinPosition;
      public WinDef.POINT ptMaxPosition;
      public WinDef.RECT rcNormalPosition;
   }

   @Structure.FieldOrder({"cbSize", "rcWindow", "rcClient", "dwStyle", "dwExStyle", "dwWindowStatus", "cxWindowBorders", "cyWindowBorders", "atomWindowType", "wCreatorVersion"})
   public static class WINDOWINFO extends Structure {
      public int cbSize = this.size();
      public WinDef.RECT rcWindow;
      public WinDef.RECT rcClient;
      public int dwStyle;
      public int dwExStyle;
      public int dwWindowStatus;
      public int cxWindowBorders;
      public int cyWindowBorders;
      public short atomWindowType;
      public short wCreatorVersion;
   }

   @Structure.FieldOrder({"cbSize", "flags", "hwndActive", "hwndFocus", "hwndCapture", "hwndMenuOwner", "hwndMoveSize", "hwndCaret", "rcCaret"})
   public static class GUITHREADINFO extends Structure {
      public int cbSize = this.size();
      public int flags;
      public WinDef.HWND hwndActive;
      public WinDef.HWND hwndFocus;
      public WinDef.HWND hwndCapture;
      public WinDef.HWND hwndMenuOwner;
      public WinDef.HWND hwndMoveSize;
      public WinDef.HWND hwndCaret;
      public WinDef.RECT rcCaret;
   }

   public static class HDEVNOTIFY extends WinDef.PVOID {
      public HDEVNOTIFY() {
      }

      public HDEVNOTIFY(Pointer p) {
         super(p);
      }
   }
}
