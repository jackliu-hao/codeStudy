/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.OleAuto;
/*     */ import com.sun.jna.platform.win32.Variant;
/*     */ import com.sun.jna.platform.win32.WTypes;
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
/*     */ public class TypeInfo
/*     */   extends Unknown
/*     */   implements ITypeInfo
/*     */ {
/*     */   public static class ByReference
/*     */     extends TypeInfo
/*     */     implements Structure.ByReference {}
/*     */   
/*     */   public TypeInfo() {}
/*     */   
/*     */   public TypeInfo(Pointer pvInstance) {
/*  77 */     super(pvInstance);
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
/*     */   public WinNT.HRESULT GetTypeAttr(PointerByReference ppTypeAttr) {
/*  90 */     return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] {
/*  91 */           getPointer(), ppTypeAttr }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetTypeComp(PointerByReference ppTComp) {
/* 104 */     return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] {
/* 105 */           getPointer(), ppTComp }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetFuncDesc(WinDef.UINT index, PointerByReference ppFuncDesc) {
/* 121 */     return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] {
/* 122 */           getPointer(), index, ppFuncDesc }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetVarDesc(WinDef.UINT index, PointerByReference ppVarDesc) {
/* 139 */     return (WinNT.HRESULT)_invokeNativeObject(6, new Object[] {
/* 140 */           getPointer(), index, ppVarDesc }, WinNT.HRESULT.class);
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
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetNames(OaIdl.MEMBERID memid, WTypes.BSTR[] rgBstrNames, WinDef.UINT cMaxNames, WinDef.UINTByReference pcNames) {
/* 163 */     return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] {
/* 164 */           getPointer(), memid, rgBstrNames, cMaxNames, pcNames }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetRefTypeOfImplType(WinDef.UINT index, OaIdl.HREFTYPEByReference pRefType) {
/* 181 */     return (WinNT.HRESULT)_invokeNativeObject(8, new Object[] {
/* 182 */           getPointer(), index, pRefType }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetImplTypeFlags(WinDef.UINT index, IntByReference pImplTypeFlags) {
/* 199 */     return (WinNT.HRESULT)_invokeNativeObject(9, new Object[] {
/* 200 */           getPointer(), index, pImplTypeFlags }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetIDsOfNames(WTypes.LPOLESTR[] rgszNames, WinDef.UINT cNames, OaIdl.MEMBERID[] pMemId) {
/* 220 */     return (WinNT.HRESULT)_invokeNativeObject(10, new Object[] {
/* 221 */           getPointer(), rgszNames, cNames, pMemId }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT Invoke(WinDef.PVOID pvInstance, OaIdl.MEMBERID memid, WinDef.WORD wFlags, OleAuto.DISPPARAMS.ByReference pDispParams, Variant.VARIANT.ByReference pVarResult, OaIdl.EXCEPINFO.ByReference pExcepInfo, WinDef.UINTByReference puArgErr) {
/* 253 */     return (WinNT.HRESULT)_invokeNativeObject(11, new Object[] {
/* 254 */           getPointer(), pvInstance, memid, wFlags, pDispParams, pVarResult, pExcepInfo, puArgErr }, WinNT.HRESULT.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetDocumentation(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
/* 281 */     return (WinNT.HRESULT)_invokeNativeObject(12, new Object[] {
/* 282 */           getPointer(), memid, pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile }, WinNT.HRESULT.class);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinNT.HRESULT GetDllEntry(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, WTypes.BSTRByReference pBstrDllName, WTypes.BSTRByReference pBstrName, WinDef.WORDByReference pwOrdinal) {
/* 309 */     return (WinNT.HRESULT)_invokeNativeObject(13, new Object[] {
/* 310 */           getPointer(), memid, invKind, pBstrDllName, pBstrName, pwOrdinal }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetRefTypeInfo(OaIdl.HREFTYPE hRefType, PointerByReference ppTInfo) {
/* 327 */     return (WinNT.HRESULT)_invokeNativeObject(14, new Object[] {
/* 328 */           getPointer(), hRefType, ppTInfo }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT AddressOfMember(OaIdl.MEMBERID memid, OaIdl.INVOKEKIND invKind, PointerByReference ppv) {
/* 348 */     return (WinNT.HRESULT)_invokeNativeObject(15, new Object[] {
/* 349 */           getPointer(), memid, invKind, ppv }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT CreateInstance(IUnknown pUnkOuter, Guid.REFIID riid, PointerByReference ppvObj) {
/* 369 */     return (WinNT.HRESULT)_invokeNativeObject(16, new Object[] {
/* 370 */           getPointer(), pUnkOuter, riid, ppvObj }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetMops(OaIdl.MEMBERID memid, WTypes.BSTRByReference pBstrMops) {
/* 387 */     return (WinNT.HRESULT)_invokeNativeObject(17, new Object[] {
/* 388 */           getPointer(), memid, pBstrMops }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetContainingTypeLib(PointerByReference ppTLib, WinDef.UINTByReference pIndex) {
/* 405 */     return (WinNT.HRESULT)_invokeNativeObject(18, new Object[] {
/* 406 */           getPointer(), ppTLib, pIndex }, WinNT.HRESULT.class);
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
/*     */   public void ReleaseTypeAttr(OaIdl.TYPEATTR pTypeAttr) {
/* 418 */     _invokeNativeVoid(19, new Object[] { getPointer(), pTypeAttr });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseFuncDesc(OaIdl.FUNCDESC pFuncDesc) {
/* 429 */     _invokeNativeVoid(20, new Object[] { getPointer(), pFuncDesc });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseVarDesc(OaIdl.VARDESC pVarDesc) {
/* 440 */     _invokeNativeVoid(21, new Object[] { getPointer(), pVarDesc });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\TypeInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */