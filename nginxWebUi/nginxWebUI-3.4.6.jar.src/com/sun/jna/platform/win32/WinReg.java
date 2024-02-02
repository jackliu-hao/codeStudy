/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Pointer;
/*    */ import com.sun.jna.ptr.ByReference;
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
/*    */ public interface WinReg
/*    */ {
/*    */   public static class HKEY
/*    */     extends WinNT.HANDLE
/*    */   {
/*    */     public HKEY() {}
/*    */     
/*    */     public HKEY(Pointer p) {
/* 42 */       super(p); } public HKEY(int value) {
/* 43 */       super(new Pointer(value));
/*    */     } }
/*    */   
/*    */   public static class HKEYByReference extends ByReference {
/*    */     public HKEYByReference() {
/* 48 */       this(null);
/*    */     }
/*    */     
/*    */     public HKEYByReference(WinReg.HKEY h) {
/* 52 */       super(Native.POINTER_SIZE);
/* 53 */       setValue(h);
/*    */     }
/*    */     
/*    */     public void setValue(WinReg.HKEY h) {
/* 57 */       getPointer().setPointer(0L, (h != null) ? h.getPointer() : null);
/*    */     }
/*    */     
/*    */     public WinReg.HKEY getValue() {
/* 61 */       Pointer p = getPointer().getPointer(0L);
/* 62 */       if (p == null)
/* 63 */         return null; 
/* 64 */       if (WinBase.INVALID_HANDLE_VALUE.getPointer().equals(p))
/* 65 */         return (WinReg.HKEY)WinBase.INVALID_HANDLE_VALUE; 
/* 66 */       WinReg.HKEY h = new WinReg.HKEY();
/* 67 */       h.setPointer(p);
/* 68 */       return h;
/*    */     }
/*    */   }
/*    */   
/* 72 */   public static final HKEY HKEY_CLASSES_ROOT = new HKEY(-2147483648);
/* 73 */   public static final HKEY HKEY_CURRENT_USER = new HKEY(-2147483647);
/* 74 */   public static final HKEY HKEY_LOCAL_MACHINE = new HKEY(-2147483646);
/* 75 */   public static final HKEY HKEY_USERS = new HKEY(-2147483645);
/* 76 */   public static final HKEY HKEY_PERFORMANCE_DATA = new HKEY(-2147483644);
/* 77 */   public static final HKEY HKEY_PERFORMANCE_TEXT = new HKEY(-2147483568);
/* 78 */   public static final HKEY HKEY_PERFORMANCE_NLSTEXT = new HKEY(-2147483552);
/* 79 */   public static final HKEY HKEY_CURRENT_CONFIG = new HKEY(-2147483643);
/* 80 */   public static final HKEY HKEY_DYN_DATA = new HKEY(-2147483642);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WinReg.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */