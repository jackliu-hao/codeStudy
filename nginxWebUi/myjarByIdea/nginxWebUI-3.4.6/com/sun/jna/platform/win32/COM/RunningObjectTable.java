package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.Structure;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.PointerByReference;

public class RunningObjectTable extends Unknown implements IRunningObjectTable {
   public RunningObjectTable() {
   }

   public RunningObjectTable(Pointer pointer) {
      super(pointer);
   }

   public WinNT.HRESULT Register(WinDef.DWORD grfFlags, Pointer punkObject, Pointer pmkObjectName, WinDef.DWORDByReference pdwRegister) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(3, new Object[]{this.getPointer(), grfFlags, punkObject, pmkObjectName, pdwRegister}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT Revoke(WinDef.DWORD dwRegister) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(4, new Object[]{this.getPointer(), dwRegister}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT IsRunning(Pointer pmkObjectName) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(5, new Object[]{this.getPointer(), pmkObjectName}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT GetObject(Pointer pmkObjectName, PointerByReference ppunkObject) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(6, new Object[]{this.getPointer(), pmkObjectName, ppunkObject}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT NoteChangeTime(WinDef.DWORD dwRegister, WinBase.FILETIME pfiletime) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(7, new Object[]{this.getPointer(), dwRegister, pfiletime}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT GetTimeOfLastChange(Pointer pmkObjectName, WinBase.FILETIME.ByReference pfiletime) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(8, new Object[]{this.getPointer(), pmkObjectName, pfiletime}, WinNT.HRESULT.class);
      return hr;
   }

   public WinNT.HRESULT EnumRunning(PointerByReference ppenumMoniker) {
      int vTableId = true;
      WinNT.HRESULT hr = (WinNT.HRESULT)this._invokeNativeObject(9, new Object[]{this.getPointer(), ppenumMoniker}, WinNT.HRESULT.class);
      return hr;
   }

   public static class ByReference extends RunningObjectTable implements Structure.ByReference {
   }
}
