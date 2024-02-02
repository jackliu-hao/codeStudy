/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.OaIdl;
/*     */ import com.sun.jna.platform.win32.WTypes;
/*     */ import com.sun.jna.platform.win32.WinDef;
/*     */ import com.sun.jna.platform.win32.WinNT;
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
/*     */ public class TypeLib
/*     */   extends Unknown
/*     */   implements ITypeLib
/*     */ {
/*     */   public static class ByReference
/*     */     extends TypeLib
/*     */     implements Structure.ByReference {}
/*     */   
/*     */   public TypeLib() {}
/*     */   
/*     */   public TypeLib(Pointer pvInstance) {
/*  67 */     super(pvInstance);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WinDef.UINT GetTypeInfoCount() {
/*  76 */     return (WinDef.UINT)_invokeNativeObject(3, new Object[] {
/*  77 */           getPointer() }, WinDef.UINT.class);
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
/*     */   public WinNT.HRESULT GetTypeInfo(WinDef.UINT index, PointerByReference pTInfo) {
/*  93 */     return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] {
/*  94 */           getPointer(), index, pTInfo }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetTypeInfoType(WinDef.UINT index, OaIdl.TYPEKIND.ByReference pTKind) {
/* 111 */     return (WinNT.HRESULT)_invokeNativeObject(5, new Object[] {
/* 112 */           getPointer(), index, pTKind }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetTypeInfoOfGuid(Guid.GUID guid, PointerByReference pTinfo) {
/* 129 */     return (WinNT.HRESULT)
/* 130 */       _invokeNativeObject(6, new Object[] { getPointer(), guid, pTinfo }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetLibAttr(PointerByReference ppTLibAttr) {
/* 144 */     return (WinNT.HRESULT)_invokeNativeObject(7, new Object[] {
/* 145 */           getPointer(), ppTLibAttr }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetTypeComp(PointerByReference pTComp) {
/* 158 */     return (WinNT.HRESULT)_invokeNativeObject(8, new Object[] {
/* 159 */           getPointer(), pTComp }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT GetDocumentation(int index, WTypes.BSTRByReference pBstrName, WTypes.BSTRByReference pBstrDocString, WinDef.DWORDByReference pdwHelpContext, WTypes.BSTRByReference pBstrHelpFile) {
/* 184 */     return (WinNT.HRESULT)_invokeNativeObject(9, new Object[] {
/* 185 */           getPointer(), Integer.valueOf(index), pBstrName, pBstrDocString, pdwHelpContext, pBstrHelpFile }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT IsName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, WinDef.BOOLByReference pfName) {
/* 206 */     return (WinNT.HRESULT)
/* 207 */       _invokeNativeObject(10, new Object[] { getPointer(), szNameBuf, lHashVal, pfName }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT FindName(WTypes.LPOLESTR szNameBuf, WinDef.ULONG lHashVal, Pointer[] ppTInfo, OaIdl.MEMBERID[] rgMemId, WinDef.USHORTByReference pcFound) {
/* 233 */     return (WinNT.HRESULT)_invokeNativeObject(11, new Object[] {
/* 234 */           getPointer(), szNameBuf, lHashVal, ppTInfo, rgMemId, pcFound }, WinNT.HRESULT.class);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void ReleaseTLibAttr(OaIdl.TLIBATTR pTLibAttr) {
/* 245 */     _invokeNativeObject(12, new Object[] { getPointer(), pTLibAttr
/* 246 */           .getPointer() }, WinNT.HRESULT.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\TypeLib.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */