package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface IRunningObjectTable extends IUnknown {
   Guid.IID IID = new Guid.IID("{00000010-0000-0000-C000-000000000046}");

   WinNT.HRESULT EnumRunning(PointerByReference var1);

   WinNT.HRESULT GetObject(Pointer var1, PointerByReference var2);

   WinNT.HRESULT GetTimeOfLastChange(Pointer var1, WinBase.FILETIME.ByReference var2);

   WinNT.HRESULT IsRunning(Pointer var1);

   WinNT.HRESULT NoteChangeTime(WinDef.DWORD var1, WinBase.FILETIME var2);

   WinNT.HRESULT Register(WinDef.DWORD var1, Pointer var2, Pointer var3, WinDef.DWORDByReference var4);

   WinNT.HRESULT Revoke(WinDef.DWORD var1);
}
