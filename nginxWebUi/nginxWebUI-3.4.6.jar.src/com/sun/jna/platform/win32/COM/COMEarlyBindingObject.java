/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
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
/*     */ public class COMEarlyBindingObject
/*     */   extends COMBindingBaseObject
/*     */   implements IDispatch
/*     */ {
/*     */   public COMEarlyBindingObject(Guid.CLSID clsid, boolean useActiveInstance, int dwClsContext) {
/*  54 */     super(clsid, useActiveInstance, dwClsContext);
/*     */   }
/*     */   
/*     */   protected String getStringProperty(OaIdl.DISPID dispId) {
/*  58 */     Variant.VARIANT.ByReference result = new Variant.VARIANT.ByReference();
/*  59 */     oleMethod(2, result, dispId);
/*     */     
/*  61 */     return result.getValue().toString();
/*     */   }
/*     */   
/*     */   protected void setProperty(OaIdl.DISPID dispId, boolean value) {
/*  65 */     oleMethod(4, (Variant.VARIANT.ByReference)null, dispId, new Variant.VARIANT(value));
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT QueryInterface(Guid.REFIID riid, PointerByReference ppvObject) {
/*  70 */     return getIDispatch().QueryInterface(riid, ppvObject);
/*     */   }
/*     */ 
/*     */   
/*     */   public int AddRef() {
/*  75 */     return getIDispatch().AddRef();
/*     */   }
/*     */ 
/*     */   
/*     */   public int Release() {
/*  80 */     return getIDispatch().Release();
/*     */   }
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetTypeInfoCount(WinDef.UINTByReference pctinfo) {
/*  85 */     return getIDispatch().GetTypeInfoCount(pctinfo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetTypeInfo(WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
/*  91 */     return getIDispatch().GetTypeInfo(iTInfo, lcid, ppTInfo);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetIDsOfNames(Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId) {
/*  97 */     return getIDispatch().GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT Invoke(OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr) {
/* 106 */     return getIDispatch().Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\COMEarlyBindingObject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */