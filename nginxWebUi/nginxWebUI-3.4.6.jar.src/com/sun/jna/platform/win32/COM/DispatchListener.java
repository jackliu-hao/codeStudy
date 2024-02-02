/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.Structure.FieldOrder;
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
/*     */ @FieldOrder({"vtbl"})
/*     */ public class DispatchListener
/*     */   extends Structure
/*     */ {
/*     */   public DispatchVTable.ByReference vtbl;
/*     */   
/*     */   public DispatchListener(IDispatchCallback callback) {
/*  49 */     this.vtbl = constructVTable();
/*  50 */     initVTable(callback);
/*  51 */     write();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected DispatchVTable.ByReference constructVTable() {
/*  57 */     return new DispatchVTable.ByReference();
/*     */   }
/*     */   
/*     */   protected void initVTable(final IDispatchCallback callback) {
/*  61 */     this.vtbl.QueryInterfaceCallback = new DispatchVTable.QueryInterfaceCallback()
/*     */       {
/*     */         public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID refid, PointerByReference ppvObject) {
/*  64 */           return callback.QueryInterface(refid, ppvObject);
/*     */         }
/*     */       };
/*  67 */     this.vtbl.AddRefCallback = new DispatchVTable.AddRefCallback()
/*     */       {
/*     */         public int invoke(Pointer thisPointer) {
/*  70 */           return callback.AddRef();
/*     */         }
/*     */       };
/*  73 */     this.vtbl.ReleaseCallback = new DispatchVTable.ReleaseCallback()
/*     */       {
/*     */         public int invoke(Pointer thisPointer) {
/*  76 */           return callback.Release();
/*     */         }
/*     */       };
/*  79 */     this.vtbl.GetTypeInfoCountCallback = new DispatchVTable.GetTypeInfoCountCallback()
/*     */       {
/*     */         public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINTByReference pctinfo) {
/*  82 */           return callback.GetTypeInfoCount(pctinfo);
/*     */         }
/*     */       };
/*  85 */     this.vtbl.GetTypeInfoCallback = new DispatchVTable.GetTypeInfoCallback()
/*     */       {
/*     */         public WinNT.HRESULT invoke(Pointer thisPointer, WinDef.UINT iTInfo, WinDef.LCID lcid, PointerByReference ppTInfo) {
/*  88 */           return callback.GetTypeInfo(iTInfo, lcid, ppTInfo);
/*     */         }
/*     */       };
/*  91 */     this.vtbl.GetIDsOfNamesCallback = new DispatchVTable.GetIDsOfNamesCallback()
/*     */       {
/*     */         public WinNT.HRESULT invoke(Pointer thisPointer, Guid.REFIID riid, WString[] rgszNames, int cNames, WinDef.LCID lcid, OaIdl.DISPIDByReference rgDispId)
/*     */         {
/*  95 */           return callback.GetIDsOfNames(riid, rgszNames, cNames, lcid, rgDispId);
/*     */         }
/*     */       };
/*  98 */     this.vtbl.InvokeCallback = new DispatchVTable.InvokeCallback()
/*     */       {
/*     */ 
/*     */         
/*     */         public WinNT.HRESULT invoke(Pointer thisPointer, OaIdl.DISPID dispIdMember, Guid.REFIID riid, WinDef.LCID lcid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, IntByReference puArgErr)
/*     */         {
/* 104 */           return callback.Invoke(dispIdMember, riid, lcid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr);
/*     */         }
/*     */       };
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\DispatchListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */