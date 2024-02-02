package com.sun.jna.platform.win32.COM;

import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;

public interface IConnectionPoint extends IUnknown {
   Guid.IID IID_IConnectionPoint = new Guid.IID("B196B286-BAB4-101A-B69C-00AA00341D07");

   WinNT.HRESULT GetConnectionInterface(Guid.IID var1);

   WinNT.HRESULT Advise(IUnknownCallback var1, WinDef.DWORDByReference var2);

   WinNT.HRESULT Unadvise(WinDef.DWORD var1);
}
