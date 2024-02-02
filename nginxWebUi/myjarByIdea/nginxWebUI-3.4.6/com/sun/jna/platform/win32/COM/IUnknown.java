package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public interface IUnknown {
   Guid.IID IID_IUNKNOWN = new Guid.IID("{00000000-0000-0000-C000-000000000046}");

   WinNT.HRESULT QueryInterface(Guid.REFIID var1, PointerByReference var2);

   int AddRef();

   int Release();
}
