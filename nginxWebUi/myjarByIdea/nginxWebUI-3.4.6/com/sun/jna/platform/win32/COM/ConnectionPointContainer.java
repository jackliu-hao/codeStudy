package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class ConnectionPointContainer extends Unknown implements IConnectionPointContainer {
   public ConnectionPointContainer(Pointer pointer) {
      super(pointer);
   }

   public WinNT.HRESULT EnumConnectionPoints() {
      int vTableId = true;
      throw new UnsupportedOperationException();
   }

   public WinNT.HRESULT FindConnectionPoint(Guid.REFIID riid, PointerByReference ppCP) {
      int vTableId = true;
      return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), riid, ppCP}, WinNT.HRESULT.class);
   }
}
