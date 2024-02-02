package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class TypeInfo extends Unknown implements ITypeInfo {
   public TypeInfo() {
   }

   public TypeInfo(Pointer pvInstance) {
      super(pvInstance);
   }

   public WinNT.HRESULT GetTypeAttr(PointerByReference ppTypeAttr) {
      return (WinNT.HRESULT)this._invokeNativeObject(3, new Object[]{this.getPointer(), ppTypeAttr}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetTypeComp(PointerByReference ppTComp) {
      return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), ppTComp}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetFuncDesc(WinDef.UINT index, PointerByReference ppFuncDesc) {
      return (WinNT.HRESULT)this._invokeNativeObject(5, new Object[]{this.getPointer(), index, ppFuncDesc}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetVarDesc(WinDef.UINT index, PointerByReference ppVarDesc) {
      return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[]{this.getPointer(), index, ppVarDesc}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetNames(OaIdl.MEMBERID memid, WTypes.BSTR[] rgBstrNames, WinDef.UINT cMaxNames, WinDef.UINTByReference pcNames) {
      return (WinNT.HRESULT)this._invokeNativeObject(7, new Object[]{this.getPointer(), memid, rgBstrNames, cMaxNames, pcNames}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetRefTypeOfImplType(WinDef.UINT index, OaIdl.HREFTYPEByReference pRefType) {
      return (WinNT.HRESULT)this._invokeNativeObject(8, new Object[]{this.getPointer(), index, pRefType}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetImplTypeFlags(WinDef.UINT index, IntByReference pImplTypeFlags) {
      return (WinNT.HRESULT)this._invokeNativeObject(9, new Object[]{this.getPointer(), index, pImplTypeFlags}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetIDsOfNames(WTypes.LPOLESTR[] rgszNames, WinDef.UINT cNames, OaIdl.MEMBERID[] pMemId) {
      return (WinNT.HRESULT)this._invokeNativeObject(10, new Object[]{this.getPointer(), rgszNames, cNames, pMemId}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT Invoke(WinDef.PVOID pvInstance, OaIdl.MEMBERID memid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, WinDef.UINTByReference puArgErr) {
      return (WinNT.HRESULT)this._invokeNativeObject(11, new Object[]{this.getPointer(), pvInstance, memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetDocumentation(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
      return (WinNT.HRESULT)this._invokeNativeObject(12, new Object[]{this.getPointer(), memid, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetDllEntry(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, WTypes.BSTRByReference pBstrDllName, WTypes.BSTRByReference pBstrName, WinDef.WORDByReference pwOrdinal) {
      return (WinNT.HRESULT)this._invokeNativeObject(13, new Object[]{this.getPointer(), memid, invKind, pBstrDllName, pBstrName, pwOrdinal}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetRefTypeInfo(OaIdl.HREFTYPE hRefType, PointerByReference ppTInfo) {
      return (WinNT.HRESULT)this._invokeNativeObject(14, new Object[]{this.getPointer(), hRefType, ppTInfo}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT AddressOfMember(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, PointerByReference ppv) {
      return (WinNT.HRESULT)this._invokeNativeObject(15, new Object[]{this.getPointer(), memid, invKind, ppv}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT CreateInstance(IUnknown pUnkOuter, Guid.REFIID riid, PointerByReference ppvObj) {
      return (WinNT.HRESULT)this._invokeNativeObject(16, new Object[]{this.getPointer(), pUnkOuter, riid, ppvObj}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetMops(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrMops) {
      return (WinNT.HRESULT)this._invokeNativeObject(17, new Object[]{this.getPointer(), memid, pBstrMops}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetContainingTypeLib(PointerByReference ppTLib, WinDef.UINTByReference pIndex) {
      return (WinNT.HRESULT)this._invokeNativeObject(18, new Object[]{this.getPointer(), ppTLib, pIndex}, WinNT.HRESULT.class);
   }

   public void ReleaseTypeAttr(OaIdl.TYPEATTR pTypeAttr) {
      this._invokeNativeVoid(19, new Object[]{this.getPointer(), pTypeAttr});
   }

   public void ReleaseFuncDesc(OaIdl.FUNCDESC pFuncDesc) {
      this._invokeNativeVoid(20, new Object[]{this.getPointer(), pFuncDesc});
   }

   public void ReleaseVarDesc(OaIdl.VARDESC pVarDesc) {
      this._invokeNativeVoid(21, new Object[]{this.getPointer(), pVarDesc});
   }

   public static class ByReference extends TypeInfo implements Structure.ByReference {
   }
}
