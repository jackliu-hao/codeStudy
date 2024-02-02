package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.WTypes;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class TypeLib extends Unknown implements ITypeLib {
   public TypeLib() {
   }

   public TypeLib(Pointer pvInstance) {
      super(pvInstance);
   }

   public WinDef.UINT GetTypeInfoCount() {
      return (WinDef.UINT)this._invokeNativeObject(3, new Object[]{this.getPointer()}, WinDef.UINT.class);
   }

   public WinNT.HRESULT GetTypeInfo(WinDef.UINT index, PointerByReference pTInfo) {
      return (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), index, pTInfo}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetTypeInfoType(WinDef.UINT index, OaIdl.TYPEKIND.ByReference pTKind) {
      return (WinNT.HRESULT)this._invokeNativeObject(5, new Object[]{this.getPointer(), index, pTKind}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetTypeInfoOfGuid(Guid.GUID guid, PointerByReference pTinfo) {
      return (WinNT.HRESULT)this._invokeNativeObject(6, new Object[]{this.getPointer(), guid, pTinfo}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetLibAttr(PointerByReference ppTLibAttr) {
      return (WinNT.HRESULT)this._invokeNativeObject(7, new Object[]{this.getPointer(), ppTLibAttr}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetTypeComp(PointerByReference pTComp) {
      return (WinNT.HRESULT)this._invokeNativeObject(8, new Object[]{this.getPointer(), pTComp}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT GetDocumentation(int index, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
      return (WinNT.HRESULT)this._invokeNativeObject(9, new Object[]{this.getPointer(), index, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT IsName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, WinDef.BOOLByReference pfName) {
      return (WinNT.HRESULT)this._invokeNativeObject(10, new Object[]{this.getPointer(), szNameBuf, lHashVal, pfName}, WinNT.HRESULT.class);
   }

   public WinNT.HRESULT FindName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, Pointer[] ppTInfo, OaIdl.MEMBERID[] rgMemId, WinDef.USHORTByReference pcFound) {
      return (WinNT.HRESULT)this._invokeNativeObject(11, new Object[]{this.getPointer(), szNameBuf, lHashVal, ppTInfo, rgMemId, pcFound}, WinNT.HRESULT.class);
   }

   public void ReleaseTLibAttr(OaIdl.TLIBATTR pTLibAttr) {
      this._invokeNativeObject(12, new Object[]{this.getPointer(), pTLibAttr.getPointer()}, WinNT.HRESULT.class);
   }

   public static class ByReference extends TypeLib implements Structure.ByReference {
   }
}
