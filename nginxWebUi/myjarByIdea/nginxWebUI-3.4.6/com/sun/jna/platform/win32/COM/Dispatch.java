package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class Dispatch extends Unknown implements IDispatch {
   public Dispatch() {
   }

   public Dispatch(Pointer pvInstance) {
      super(pvInstance);
   }

   public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
      return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[]{this.getPointer(), pctinfo}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
      return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), iTInfo, lcid, ppTInfo}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
      return (WinNT.HRESULT)this._invokeNativeObject(5, new Object[]{this.getPointer(), riid, rgszNames, cNames, lcid, rgDispId}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
      return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[]{this.getPointer(), dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr}, WinNT.HRESULT.class);
   }

   public static class ByReference extends Dispatch implements Structure.ByReference {
   }
}
