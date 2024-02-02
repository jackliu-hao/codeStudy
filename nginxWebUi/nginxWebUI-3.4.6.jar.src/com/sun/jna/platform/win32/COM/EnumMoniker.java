/*    */ package com.sun.jna.platform.win32.COM;
/*    */ 
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.platform.win32.WinDef;
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
/*    */ public class EnumMoniker
/*    */   extends Unknown
/*    */   implements IEnumMoniker
/*    */ {
/*    */   public EnumMoniker(Pointer pointer) {
/* 36 */     super(pointer);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Next(WinDef.ULONG celt, PointerByReference rgelt, WinDef.ULONGByReference pceltFetched) {
/* 45 */     int vTableId = 3;
/*    */     
/* 47 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(3, new Object[] { getPointer(), celt, rgelt, pceltFetched }, WinNT.HRESULT.class);
/*    */ 
/*    */     
/* 50 */     return hr;
/*    */   }
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Skip(WinDef.ULONG celt) {
/* 55 */     int vTableId = 4;
/*    */     
/* 57 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(4, new Object[] { getPointer(), celt }, WinNT.HRESULT.class);
/*    */ 
/*    */     
/* 60 */     return hr;
/*    */   }
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Reset() {
/* 65 */     int vTableId = 5;
/*    */     
/* 67 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(5, new Object[] { getPointer() }, WinNT.HRESULT.class);
/*    */ 
/*    */     
/* 70 */     return hr;
/*    */   }
/*    */ 
/*    */   
/*    */   public WinNT.HRESULT Clone(PointerByReference ppenum) {
/* 75 */     int vTableId = 6;
/*    */     
/* 77 */     WinNT.HRESULT hr = (WinNT.HRESULT)_invokeNativeObject(6, new Object[] {
/* 78 */           getPointer(), ppenum }, WinNT.HRESULT.class);
/*    */     
/* 80 */     return hr;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\COM\EnumMoniker.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */