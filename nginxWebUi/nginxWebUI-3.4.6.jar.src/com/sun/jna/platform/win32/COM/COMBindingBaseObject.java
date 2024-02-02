/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.Ole32;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class COMBindingBaseObject
/*     */   extends COMInvoker
/*     */ {
/*  57 */   public static final WinDef.LCID LOCALE_USER_DEFAULT = Kernel32.INSTANCE
/*  58 */     .GetUserDefaultLCID();
/*     */ 
/*     */   
/*  61 */   public static final WinDef.LCID LOCALE_SYSTEM_DEFAULT = Kernel32.INSTANCE
/*  62 */     .GetSystemDefaultLCID();
/*     */ 
/*     */   
/*     */   private IUnknown iUnknown;
/*     */ 
/*     */   
/*     */   private IDispatch iDispatch;
/*     */ 
/*     */   
/*  71 */   private PointerByReference pDispatch = new PointerByReference();
/*     */ 
/*     */   
/*  74 */   private PointerByReference pUnknown = new PointerByReference();
/*     */ 
/*     */   
/*     */   public COMBindingBaseObject(IDispatch dispatch) {
/*  78 */     this.iDispatch = dispatch;
/*     */   }
/*     */   
/*     */   public COMBindingBaseObject(Guid.CLSID clsid, boolean useActiveInstance) {
/*  82 */     this(clsid, useActiveInstance, 21);
/*     */   }
/*     */ 
/*     */   
/*     */   public COMBindingBaseObject(Guid.CLSID clsid, boolean useActiveInstance, int dwClsContext) {
/*  87 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*  89 */     init(useActiveInstance, (Guid.GUID)clsid, dwClsContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public COMBindingBaseObject(String progId, boolean useActiveInstance, int dwClsContext) throws COMException {
/*  94 */     assert COMUtils.comIsInitialized() : "COM not initialized";
/*     */     
/*  96 */     Guid.CLSID.ByReference clsid = new Guid.CLSID.ByReference();
/*  97 */     WinNT.HRESULT hr = Ole32.INSTANCE.CLSIDFromProgID(progId, clsid);
/*     */     
/*  99 */     COMUtils.checkRC(hr);
/*     */     
/* 101 */     init(useActiveInstance, (Guid.GUID)clsid, dwClsContext);
/*     */   }
/*     */ 
/*     */   
/*     */   public COMBindingBaseObject(String progId, boolean useActiveInstance) throws COMException {
/* 106 */     this(progId, useActiveInstance, 21);
/*     */   }
/*     */   
/*     */   private void init(boolean useActiveInstance, Guid.GUID clsid, int dwClsContext) throws COMException {
/*     */     WinNT.HRESULT hr;
/* 111 */     if (useActiveInstance) {
/* 112 */       hr = OleAuto.INSTANCE.GetActiveObject(clsid, null, this.pUnknown);
/*     */       
/* 114 */       if (COMUtils.SUCCEEDED(hr)) {
/* 115 */         this.iUnknown = new Unknown(this.pUnknown.getValue());
/* 116 */         hr = this.iUnknown.QueryInterface(new Guid.REFIID(IDispatch.IID_IDISPATCH), this.pDispatch);
/*     */       } else {
/*     */         
/* 119 */         hr = Ole32.INSTANCE.CoCreateInstance(clsid, null, dwClsContext, (Guid.GUID)IDispatch.IID_IDISPATCH, this.pDispatch);
/*     */       } 
/*     */     } else {
/*     */       
/* 123 */       hr = Ole32.INSTANCE.CoCreateInstance(clsid, null, dwClsContext, (Guid.GUID)IDispatch.IID_IDISPATCH, this.pDispatch);
/*     */     } 
/*     */ 
/*     */     
/* 127 */     COMUtils.checkRC(hr);
/*     */     
/* 129 */     this.iDispatch = new Dispatch(this.pDispatch.getValue());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IDispatch getIDispatch() {
/* 138 */     return this.iDispatch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PointerByReference getIDispatchPointer() {
/* 147 */     return this.pDispatch;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public IUnknown getIUnknown() {
/* 156 */     return this.iUnknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PointerByReference getIUnknownPointer() {
/* 165 */     return this.pUnknown;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void release() {
/* 172 */     if (this.iDispatch != null) {
/* 173 */       this.iDispatch.Release();
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT[] pArgs) throws COMException {
/* 181 */     WString[] ptName = { new WString(name) };
/* 182 */     OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
/*     */ 
/*     */     
/* 185 */     WinNT.HRESULT hr = this.iDispatch.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, LOCALE_USER_DEFAULT, pdispID);
/*     */ 
/*     */     
/* 188 */     COMUtils.checkRC(hr);
/*     */     
/* 190 */     return 
/* 191 */       oleMethod(nType, pvResult, this.iDispatch, pdispID.getValue(), pArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
/* 199 */     int finalNType, _argsLen = 0;
/* 200 */     Variant.VARIANT[] _args = null;
/* 201 */     OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
/* 202 */     OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
/* 203 */     IntByReference puArgErr = new IntByReference();
/*     */ 
/*     */     
/* 206 */     if (pArgs != null && pArgs.length > 0) {
/* 207 */       _argsLen = pArgs.length;
/* 208 */       _args = new Variant.VARIANT[_argsLen];
/*     */       
/* 210 */       int revCount = _argsLen;
/* 211 */       for (int i = 0; i < _argsLen; i++) {
/* 212 */         _args[i] = pArgs[--revCount];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 217 */     if (nType == 4) {
/* 218 */       dp.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT });
/*     */     }
/*     */ 
/*     */     
/* 222 */     if (_argsLen > 0) {
/* 223 */       dp.setArgs(_args);
/*     */ 
/*     */       
/* 226 */       dp.write();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 254 */     if (nType == 1 || nType == 2) {
/* 255 */       finalNType = 3;
/*     */     } else {
/* 257 */       finalNType = nType;
/*     */     } 
/*     */ 
/*     */     
/* 261 */     WinNT.HRESULT hr = this.iDispatch.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD(finalNType), dp, pvResult, pExcepInfo, puArgErr);
/*     */ 
/*     */     
/* 264 */     COMUtils.checkRC(hr, (OaIdl.EXCEPINFO)pExcepInfo, puArgErr);
/* 265 */     return hr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name, Variant.VARIANT pArg) throws COMException {
/* 286 */     return oleMethod(nType, pvResult, name, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
/* 292 */     return oleMethod(nType, pvResult, dispId, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, String name) throws COMException {
/* 311 */     return oleMethod(nType, pvResult, name, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, OaIdl.DISPID dispId) throws COMException {
/* 317 */     return oleMethod(nType, pvResult, dispId, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT[] pArgs) throws COMException {
/* 327 */     if (pDisp == null) {
/* 328 */       throw new COMException("pDisp (IDispatch) parameter is null!");
/*     */     }
/*     */     
/* 331 */     WString[] ptName = { new WString(name) };
/* 332 */     OaIdl.DISPIDByReference pdispID = new OaIdl.DISPIDByReference();
/*     */ 
/*     */     
/* 335 */     WinNT.HRESULT hr = pDisp.GetIDsOfNames(new Guid.REFIID(Guid.IID_NULL), ptName, 1, LOCALE_USER_DEFAULT, pdispID);
/*     */ 
/*     */     
/* 338 */     COMUtils.checkRC(hr);
/*     */     
/* 340 */     return 
/* 341 */       oleMethod(nType, pvResult, pDisp, pdispID.getValue(), pArgs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT[] pArgs) throws COMException {
/*     */     int finalNType;
/* 352 */     if (pDisp == null) {
/* 353 */       throw new COMException("pDisp (IDispatch) parameter is null!");
/*     */     }
/*     */     
/* 356 */     int _argsLen = 0;
/* 357 */     Variant.VARIANT[] _args = null;
/* 358 */     OleAuto.DISPPARAMS.ByReference dp = new OleAuto.DISPPARAMS.ByReference();
/* 359 */     OaIdl.EXCEPINFO.ByReference pExcepInfo = new OaIdl.EXCEPINFO.ByReference();
/* 360 */     IntByReference puArgErr = new IntByReference();
/*     */ 
/*     */     
/* 363 */     if (pArgs != null && pArgs.length > 0) {
/* 364 */       _argsLen = pArgs.length;
/* 365 */       _args = new Variant.VARIANT[_argsLen];
/*     */       
/* 367 */       int revCount = _argsLen;
/* 368 */       for (int i = 0; i < _argsLen; i++) {
/* 369 */         _args[i] = pArgs[--revCount];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 374 */     if (nType == 4) {
/* 375 */       dp.setRgdispidNamedArgs(new OaIdl.DISPID[] { OaIdl.DISPID_PROPERTYPUT });
/*     */     }
/*     */ 
/*     */     
/* 379 */     if (_argsLen > 0) {
/* 380 */       dp.setArgs(_args);
/*     */ 
/*     */       
/* 383 */       dp.write();
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 411 */     if (nType == 1 || nType == 2) {
/* 412 */       finalNType = 3;
/*     */     } else {
/* 414 */       finalNType = nType;
/*     */     } 
/*     */ 
/*     */     
/* 418 */     WinNT.HRESULT hr = pDisp.Invoke(dispId, new Guid.REFIID(Guid.IID_NULL), LOCALE_SYSTEM_DEFAULT, new WinDef.WORD(finalNType), dp, pvResult, pExcepInfo, puArgErr);
/*     */ 
/*     */     
/* 421 */     COMUtils.checkRC(hr, (OaIdl.EXCEPINFO)pExcepInfo, puArgErr);
/* 422 */     return hr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name, Variant.VARIANT pArg) throws COMException {
/* 432 */     return oleMethod(nType, pvResult, pDisp, name, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId, Variant.VARIANT pArg) throws COMException {
/* 443 */     return oleMethod(nType, pvResult, pDisp, dispId, new Variant.VARIANT[] { pArg });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, String name) throws COMException {
/* 454 */     return oleMethod(nType, pvResult, pDisp, name, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected WinNT.HRESULT oleMethod(int nType, Variant.VARIANT.ByReference pvResult, IDispatch pDisp, OaIdl.DISPID dispId) throws COMException {
/* 464 */     return oleMethod(nType, pvResult, pDisp, dispId, (Variant.VARIANT[])null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   protected void checkFailed(WinNT.HRESULT hr) {
/* 476 */     COMUtils.checkRC(hr);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMBindingBaseObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */