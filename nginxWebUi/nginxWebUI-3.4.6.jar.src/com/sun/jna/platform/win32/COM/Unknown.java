/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.Structure;
/*    */ import com.sun.jna.platform.win32.Guid;
/*    */ import com.sun.jna.platform.win32.WinNT;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Unknown
/*    */   extends COMInvoker
/*    */   implements IUnknown
/*    */ {
/*    */   public static class ByReference
/*    */     extends Unknown
/*    */     implements Structure.ByReference {}
/*    */   
/*    */   public Unknown() {}
/*    */   
/*    */   public Unknown(Pointer pvInstance) {
/* 57 */     setPointer(pvInstance);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT QueryInterface(Guid.REFIID riid, PointerByReference ppvObject) {
/* 70 */     return (WinNT.HRESULT)_invokeNativeObject(0, new Object[] {
/* 71 */           getPointer(), riid, ppvObject }, WinNT.HRESULT.class);
/*    */   }
/*    */ 
/*    */   
/*    */   public int AddRef() {
/* 76 */     return _invokeNativeInt(1, new Object[] { getPointer() });
/*    */   }
/*    */   
/*    */   public int Release() {
/* 80 */     return _invokeNativeInt(2, new Object[] { getPointer() });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\Unknown.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */