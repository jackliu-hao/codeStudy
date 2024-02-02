package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.win32.StdCallLibrary;
import com.sun.jna.win32.W32APIOptions;

public interface User32 extends StdCallLibrary, WinUser, WinNT {
   User32 INSTANCE = (User32)Native.load("user32", User32.class, W32APIOptions.DEFAULT_OPTIONS);
   WinDef.HWND HWND_MESSAGE = new WinDef.HWND(Pointer.createConstant(-3));
   int CS_GLOBALCLASS = 16384;
   int WS_EX_TOPMOST = 8;
   int DEVICE_NOTIFY_WINDOW_HANDLE = 0;
   int DEVICE_NOTIFY_SERVICE_HANDLE = 1;
   int DEVICE_NOTIFY_ALL_INTERFACE_CLASSES = 4;
   int SW_SHOWDEFAULT = 10;

   WinDef.HDC GetDC(WinDef.HWND var1);

   int ReleaseDC(WinDef.HWND var1, WinDef.HDC var2);

   WinDef.HWND FindWindow(String var1, String var2);

   int GetClassName(WinDef.HWND var1, char[] var2, int var3);

   boolean GetGUIThreadInfo(int var1, WinUser.GUITHREADINFO var2);

   boolean GetWindowInfo(WinDef.HWND var1, WinUser.WINDOWINFO var2);

   boolean GetWindowRect(WinDef.HWND var1, WinDef.RECT var2);

   boolean GetClientRect(WinDef.HWND var1, WinDef.RECT var2);

   int GetWindowText(WinDef.HWND var1, char[] var2, int var3);

   int GetWindowTextLength(WinDef.HWND var1);

   int GetWindowModuleFileName(WinDef.HWND var1, char[] var2, int var3);

   int GetWindowThreadProcessId(WinDef.HWND var1, IntByReference var2);

   boolean EnumWindows(WinUser.WNDENUMPROC var1, Pointer var2);

   boolean EnumChildWindows(WinDef.HWND var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean EnumThreadWindows(int var1, WinUser.WNDENUMPROC var2, Pointer var3);

   boolean FlashWindowEx(WinUser.FLASHWINFO var1);

   WinDef.HICON LoadIcon(WinDef.HINSTANCE var1, String var2);

   WinNT.HANDLE LoadImage(WinDef.HINSTANCE var1, String var2, int var3, int var4, int var5, int var6);

   boolean DestroyIcon(WinDef.HICON var1);

   int GetWindowLong(WinDef.HWND var1, int var2);

   int SetWindowLong(WinDef.HWND var1, int var2, int var3);

   BaseTSD.LONG_PTR GetWindowLongPtr(WinDef.HWND var1, int var2);

   Pointer SetWindowLongPtr(WinDef.HWND var1, int var2, Pointer var3);

   boolean SetLayeredWindowAttributes(WinDef.HWND var1, int var2, byte var3, int var4);

   boolean GetLayeredWindowAttributes(WinDef.HWND var1, IntByReference var2, ByteByReference var3, IntByReference var4);

   boolean UpdateLayeredWindow(WinDef.HWND var1, WinDef.HDC var2, WinDef.POINT var3, WinUser.SIZE var4, WinDef.HDC var5, WinDef.POINT var6, int var7, WinUser.BLENDFUNCTION var8, int var9);

   int SetWindowRgn(WinDef.HWND var1, WinDef.HRGN var2, boolean var3);

   boolean GetKeyboardState(byte[] var1);

   short GetAsyncKeyState(int var1);

   WinUser.HHOOK SetWindowsHookEx(int var1, WinUser.HOOKPROC var2, WinDef.HINSTANCE var3, int var4);

   WinDef.LRESULT CallNextHookEx(WinUser.HHOOK var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   boolean UnhookWindowsHookEx(WinUser.HHOOK var1);

   int GetMessage(WinUser.MSG var1, WinDef.HWND var2, int var3, int var4);

   boolean PeekMessage(WinUser.MSG var1, WinDef.HWND var2, int var3, int var4, int var5);

   boolean TranslateMessage(WinUser.MSG var1);

   WinDef.LRESULT DispatchMessage(WinUser.MSG var1);

   void PostMessage(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   int PostThreadMessage(int var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   void PostQuitMessage(int var1);

   int GetSystemMetrics(int var1);

   WinDef.HWND SetParent(WinDef.HWND var1, WinDef.HWND var2);

   boolean IsWindowVisible(WinDef.HWND var1);

   boolean MoveWindow(WinDef.HWND var1, int var2, int var3, int var4, int var5, boolean var6);

   boolean SetWindowPos(WinDef.HWND var1, WinDef.HWND var2, int var3, int var4, int var5, int var6, int var7);

   boolean AttachThreadInput(WinDef.DWORD var1, WinDef.DWORD var2, boolean var3);

   boolean SetForegroundWindow(WinDef.HWND var1);

   WinDef.HWND GetForegroundWindow();

   WinDef.HWND SetFocus(WinDef.HWND var1);

   WinDef.DWORD SendInput(WinDef.DWORD var1, WinUser.INPUT[] var2, int var3);

   WinDef.DWORD WaitForInputIdle(WinNT.HANDLE var1, WinDef.DWORD var2);

   boolean InvalidateRect(WinDef.HWND var1, WinDef.RECT var2, boolean var3);

   boolean RedrawWindow(WinDef.HWND var1, WinDef.RECT var2, WinDef.HRGN var3, WinDef.DWORD var4);

   WinDef.HWND GetWindow(WinDef.HWND var1, WinDef.DWORD var2);

   boolean UpdateWindow(WinDef.HWND var1);

   boolean ShowWindow(WinDef.HWND var1, int var2);

   boolean CloseWindow(WinDef.HWND var1);

   boolean RegisterHotKey(WinDef.HWND var1, int var2, int var3, int var4);

   boolean UnregisterHotKey(Pointer var1, int var2);

   boolean GetLastInputInfo(WinUser.LASTINPUTINFO var1);

   WinDef.ATOM RegisterClassEx(WinUser.WNDCLASSEX var1);

   boolean UnregisterClass(String var1, WinDef.HINSTANCE var2);

   WinDef.HWND CreateWindowEx(int var1, String var2, String var3, int var4, int var5, int var6, int var7, int var8, WinDef.HWND var9, WinDef.HMENU var10, WinDef.HINSTANCE var11, WinDef.LPVOID var12);

   boolean DestroyWindow(WinDef.HWND var1);

   boolean GetClassInfoEx(WinDef.HINSTANCE var1, String var2, WinUser.WNDCLASSEX var3);

   WinDef.LRESULT DefWindowProc(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   WinUser.HDEVNOTIFY RegisterDeviceNotification(WinNT.HANDLE var1, Structure var2, int var3);

   boolean UnregisterDeviceNotification(WinUser.HDEVNOTIFY var1);

   int RegisterWindowMessage(String var1);

   WinUser.HMONITOR MonitorFromPoint(WinDef.POINT.ByValue var1, int var2);

   WinUser.HMONITOR MonitorFromRect(WinDef.RECT var1, int var2);

   WinUser.HMONITOR MonitorFromWindow(WinDef.HWND var1, int var2);

   WinDef.BOOL GetMonitorInfo(WinUser.HMONITOR var1, WinUser.MONITORINFO var2);

   WinDef.BOOL GetMonitorInfo(WinUser.HMONITOR var1, WinUser.MONITORINFOEX var2);

   WinDef.BOOL EnumDisplayMonitors(WinDef.HDC var1, WinDef.RECT var2, WinUser.MONITORENUMPROC var3, WinDef.LPARAM var4);

   WinDef.BOOL GetWindowPlacement(WinDef.HWND var1, WinUser.WINDOWPLACEMENT var2);

   WinDef.BOOL SetWindowPlacement(WinDef.HWND var1, WinUser.WINDOWPLACEMENT var2);

   WinDef.BOOL AdjustWindowRect(WinDef.RECT var1, WinDef.DWORD var2, WinDef.BOOL var3);

   WinDef.BOOL AdjustWindowRectEx(WinDef.RECT var1, WinDef.DWORD var2, WinDef.BOOL var3, WinDef.DWORD var4);

   WinDef.BOOL ExitWindowsEx(WinDef.UINT var1, WinDef.DWORD var2);

   WinDef.BOOL LockWorkStation();

   boolean GetIconInfo(WinDef.HICON var1, WinGDI.ICONINFO var2);

   WinDef.LRESULT SendMessageTimeout(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4, int var5, int var6, WinDef.DWORDByReference var7);

   BaseTSD.ULONG_PTR GetClassLongPtr(WinDef.HWND var1, int var2);

   int GetRawInputDeviceList(WinUser.RAWINPUTDEVICELIST[] var1, IntByReference var2, int var3);

   WinDef.HWND GetDesktopWindow();

   boolean PrintWindow(WinDef.HWND var1, WinDef.HDC var2, int var3);

   boolean IsWindowEnabled(WinDef.HWND var1);

   boolean IsWindow(WinDef.HWND var1);

   WinDef.HWND FindWindowEx(WinDef.HWND var1, WinDef.HWND var2, String var3, String var4);

   WinDef.HWND GetAncestor(WinDef.HWND var1, int var2);

   boolean GetCursorPos(WinDef.POINT var1);

   boolean SetCursorPos(long var1, long var3);

   WinNT.HANDLE SetWinEventHook(int var1, int var2, WinDef.HMODULE var3, WinUser.WinEventProc var4, int var5, int var6, int var7);

   boolean UnhookWinEvent(WinNT.HANDLE var1);

   WinDef.HICON CopyIcon(WinDef.HICON var1);

   int GetClassLong(WinDef.HWND var1, int var2);

   int RegisterClipboardFormat(String var1);

   WinDef.HWND GetActiveWindow();

   WinDef.LRESULT SendMessage(WinDef.HWND var1, int var2, WinDef.WPARAM var3, WinDef.LPARAM var4);

   int GetKeyboardLayoutList(int var1, WinDef.HKL[] var2);

   WinDef.HKL GetKeyboardLayout(int var1);

   boolean GetKeyboardLayoutName(char[] var1);

   short VkKeyScanExA(byte var1, WinDef.HKL var2);

   short VkKeyScanExW(char var1, WinDef.HKL var2);

   int MapVirtualKeyEx(int var1, int var2, WinDef.HKL var3);

   int ToUnicodeEx(int var1, int var2, byte[] var3, char[] var4, int var5, int var6, WinDef.HKL var7);

   int LoadString(WinDef.HINSTANCE var1, int var2, Pointer var3, int var4);
}
