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

@Structure.FieldOrder({"vtbl"})
public class DispatchListener extends Structure {
   public DispatchVTable.ByReference vtbl = this.constructVTable();

   public DispatchListener(IDispatchCallback callback) {
      this.initVTable(callback);
      super.write();
   }

   protected DispatchVTable.ByReference constructVTable() {
      return new DispatchVTable.ByReference();
   }

   protected void initVTable(final IDispatchCallback callback) {
      this.vtbl.QueryInterfaceCallback = new DispatchVTable.QueryInterfaceCallback() {
         public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID refid, PointerByReference ppvObject) {
            return callback.QueryInterface(refid, ppvObject);
         }
      };
      this.vtbl.AddRefCallback = new DispatchVTable.AddRefCallback() {
         public int invoke(Pointer thisPointer) {
            return callback.AddRef();
         }
      };
      this.vtbl.ReleaseCallback = new DispatchVTable.ReleaseCallback() {
         public int invoke(Pointer thisPointer) {
            return callback.Release();
         }
      };
      this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback() {
         public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINTByReference pctinfo) {
            return callback.GetTypeInfoCount(pctinfo);
         }
      };
      this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback() {
         public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
            return callback.GetTypeInfo(iTInfo, lcid, ppTInfo);
         }
      };
      this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback() {
         public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
            return callback.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
         }
      };
      this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback() {
         public WinNT.HRESULT invoke(Pointer thisPointer, OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
            return callback.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
         }
      };
   }
}
