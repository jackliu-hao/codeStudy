package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class TypeComp extends Unknown {
   public TypeComp() {
   }

   public TypeComp(Pointer pvInstance) {
      super(pvInstance);
   }

   public WinNT.HRESULT Bind(WString szName, WinDef.ULONG lHashVal, WinDef.WORD wFlags, PointerByReference ppTInfo, OaIdl.DESCKIND.ByReference pDescKind, OaIdl.BINDPTR.ByReference pBindPtr) {
      return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[]{this.getPointer(), szName, lHashVal, wFlags, ppTInfo, pDescKind, pBindPtr}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT BindType(WString szName, WinDef.ULONG lHashVal, PointerByReference ppTInfo, PointerByReference ppTComp) {
      return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), szName, lHashVal, ppTInfo, ppTComp}, WinNT.HRESULT.class);
   }

   public static class ByReference extends TypeComp implements Structure.ByReference {
   }
}
