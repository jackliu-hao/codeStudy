/*     */ package com.sun.jna.platform.win32.COM;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.WString;
/*     */ import com.sun.jna.platform.win32.OaIdl;
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
/*     */ public class TypeComp
/*     */   extends Unknown
/*     */ {
/*     */   public static class ByReference
/*     */     extends TypeComp
/*     */     implements Structure.ByReference {}
/*     */   
/*     */   public TypeComp() {}
/*     */   
/*     */   public TypeComp(Pointer pvInstance) {
/*  61 */     super(pvInstance);
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
/*     */   public WinNT.HRESULT Bind(WString szName, WinDef.ULONG lHashVal, WinDef.WORD wFlags, PointerByReference ppTInfo, OaIdl.DESCKIND.ByReference pDescKind, OaIdl.BINDPTR.ByReference pBindPtr) {
/*  90 */     return (WinNT.HRESULT)_invokeNativeObject(3, new Object[] {
/*  91 */           getPointer(), szName, lHashVal, wFlags, ppTInfo, pDescKind, pBindPtr }, WinNT.HRESULT.class);
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
/*     */   public WinNT.HRESULT BindType(WString szName, WinDef.ULONG lHashVal, PointerByReference ppTInfo, PointerByReference ppTComp) {
/* 115 */     return (WinNT.HRESULT)_invokeNativeObject(4, new Object[] {
/* 116 */           getPointer(), szName, lHashVal, ppTInfo, ppTComp }, WinNT.HRESULT.class);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\TypeComp.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */