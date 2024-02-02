package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface IEnumMoniker extends IUnknown {
   Guid.IID IID = new Guid.IID("{00000102-0000-0000-C000-000000000046}");

   WinNT.HRESULT Clone(PointerByReference var1);

   WinNT.HRESULT Next(WinDef.ULONG var1, PointerByReference var2, WinDef.ULONGByReference var3);

   WinNT.HRESULT Reset();

   WinNT.HRESULT Skip(WinDef.ULONG var1);
}
