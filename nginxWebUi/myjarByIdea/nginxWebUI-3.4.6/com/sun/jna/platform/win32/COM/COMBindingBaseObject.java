package com.sun.jna.platform.win32.COM;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.OaIdl;
import com.sun.jna.platform.win32.Ole32;
import com.sun.jna.platform.win32.OleAuto;
import com.sun.jna.platform.win32.Variant;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;

public class COMBindingBaseObject extends COMInvoker {
   public static final WinDef.LCID LOCALE_USER_DEFAULT;
   public static final WinDef.LCID LOCALE_SYSTEM_DEFAULT;
   private IUnknown iUnknown;
   private IDispatch iDispatch;
   private PointerByReference pDispatch;
   private PointerByReference pUnknown;

   public COMBindingBaseObject(IDispatch dispatch) {
      this.pDispatch = new PointerByReference();
      this.pUnknown = new PointerByReference();
      this.iDispatch = dispatch;
   }

   public COMBindingBaseObject(Guid.CLSID clsid, boolean useActiveInstance) {
      this((Guid.CLSID)clsid, useActiveInstance, 21);
   }

   public COMBindingBaseObject(Guid.CLSID clsid, boolean useActiveInstance, int dwClsContext) {
      this.pDispatch = new PointerByReference();
      this.pUnknown = new PointerByReference();

      assert COMUtils.comIsInitialized() : "COM not initialized";

      this.init(useActiveInstance, clsid, dwClsContext);
   }

   public COMBindingBaseObject(String progId, boolean useActiveInstance, int dwClsContext) throws COMException {
      this.pDispatch = new PointerByReference();
      this.pUnknown = new PointerByReference();

      assert COMUtils.comIsInitialized() : "COM not initialized";

      Guid.CLSID.ByReference clsid = new Guid.CLSID.ByReference();
      WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);
      COMUtils.checkRC(hr);
      this.init(useActiveInstance, clsid, dwClsContext);
   }

   public COMBindingBaseObject(String progId, boolean useActiveInstance) throws COMException {
      this((String)progId, useActiveInstance, 21);
   }

   private void init(boolean useActiveInstance, Guid.GUID clsid, int dwClsContext) throws COMException {
      WinNT.HRESULT hr;
      if (useActiveInstance) {
         hr = OleAuto.INSTANCE.GetActiveObject(clsid, (WinDef.PVOID)null, this.pUnknown);
         if (COMUtils.SUCCEEDED(hr)) {
            this.iUnknown = new Unknown(this.pUnknown.getValue());
            hr = this.iUnknown.QueryInterface(new Guid.REFIID(IDispatch.IID_IDISPATCH), this.pDispatch);
         } else {
            hr = Ole32.INSTANCE.CoCreateInstance(clsid, (Pointer)null, dwClsContext, IDispatch.IID_IDISPATCH, this.pDispatch);
         }
      } else {
         hr = Ole32.INSTANCE.CoCreateInstance(clsid, (Pointer)null, dwClsContext, IDispatch.IID_IDISPATCH, this.pDispatch);
      }

      COMUtils.checkRC(hr);
      this.iDispatch = new Dispatch(this.pDispatch.getValue());
   }

   public IDispatch getIDispatch() {
      return this.iDispatch;
   }

   public PointerByReference getIDispatchPointer() {
      return this.pDispatch;
   }

   public IUnknown getIUnknown() {
      return this.iUnknown;
   }

   public PointerByReference getIUnknownPointer() {
      return this.pUnknown;
   }

   public void release() {
      if (this.iDispatch != null) {
         this.iDispatch.Release();
      }

   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT[] pArgs) throws COMException {
      WString[] ptName = new WString[]{new WString(name)};
      OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
      WinNT.HRESULT hr = this.iDispatch.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, LOCALE_USER_DEFAULT, pdispID);
      COMUtils.checkRC(hr);
      return this.oleMethod(nType, pvResult, this.iDispatch, pdispID.getValue(), pArgs);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
      int _argsLen = 0;
      Variant.VARIANT[] _args = null;
      OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
      OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
      IntByReference puArgErr = new IntByReference();
      int finalNType;
      if (pArgs != null && pArgs.length > 0) {
         _argsLen = pArgs.length;
         _args = new Variant.VARIANT[_argsLen];
         finalNType = _argsLen;

         for(int i = 0; i < _argsLen; ++i) {
            --finalNType;
            _args[i] = pArgs[finalNType];
         }
      }

      if (nType == 4) {
         dp.setRgdispidNamedArgs(new OaIdl.DISPID[]{OaIdl.DISPID_PROPERTYPUT});
      }

      if (_argsLen > 0) {
         dp.setArgs(_args);
         dp.write();
      }

      if (nType != 1 && nType != 2) {
         finalNType = nType;
      } else {
         finalNType = 3;
      }

      WinNT.HRESULT hr = this.iDispatch.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD((long)finalNType), dp, pvResult, pExcepInfo, puArgErr);
      COMUtils.checkRC(hr, pExcepInfo, puArgErr);
      return hr;
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, name, new Variant.VARIANT[]{pArg});
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, dispId, new Variant.VARIANT[]{pArg});
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name) throws COMException {
      return this.oleMethod(nType, pvResult, name, (Variant.VARIANT[])null);
   }

   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId) throws COMException {
      return this.oleMethod(nType, pvResult, dispId, (Variant.VARIANT[])null);
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT[] pArgs) throws COMException {
      if (pDisp == null) {
         throw new COMException("pDisp (IDispatch) parameter is null!");
      } else {
         WString[] ptName = new WString[]{new WString(name)};
         OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
         WinNT.HRESULT hr = pDisp.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, LOCALE_USER_DEFAULT, pdispID);
         COMUtils.checkRC(hr);
         return this.oleMethod(nType, pvResult, pDisp, pdispID.getValue(), pArgs);
      }
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
      if (pDisp == null) {
         throw new COMException("pDisp (IDispatch) parameter is null!");
      } else {
         int _argsLen = 0;
         Variant.VARIANT[] _args = null;
         OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
         OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
         IntByReference puArgErr = new IntByReference();
         int finalNType;
         if (pArgs != null && pArgs.length > 0) {
            _argsLen = pArgs.length;
            _args = new Variant.VARIANT[_argsLen];
            finalNType = _argsLen;

            for(int i = 0; i < _argsLen; ++i) {
               --finalNType;
               _args[i] = pArgs[finalNType];
            }
         }

         if (nType == 4) {
            dp.setRgdispidNamedArgs(new OaIdl.DISPID[]{OaIdl.DISPID_PROPERTYPUT});
         }

         if (_argsLen > 0) {
            dp.setArgs(_args);
            dp.write();
         }

         if (nType != 1 && nType != 2) {
            finalNType = nType;
         } else {
            finalNType = 3;
         }

         WinNT.HRESULT hr = pDisp.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD((long)finalNType), dp, pvResult, pExcepInfo, puArgErr);
         COMUtils.checkRC(hr, pExcepInfo, puArgErr);
         return hr;
      }
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, name, new Variant.VARIANT[]{pArg});
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, dispId, new Variant.VARIANT[]{pArg});
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, name, (Variant.VARIANT[])null);
   }

   /** @deprecated */
   @Deprecated
   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId) throws COMException {
      return this.oleMethod(nType, pvResult, pDisp, dispId, (Variant.VARIANT[])null);
   }

   /** @deprecated */
   @Deprecated
   protected void checkFailed(WinNT.HRESULT hr) {
      COMUtils.checkRC(hr);
   }

   static {
      LOCALE_USER_DEFAULT = Kernel32.INSTANCE.GetUserDefaultLCID();
      LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE.GetSystemDefaultLCID();
   }
}
