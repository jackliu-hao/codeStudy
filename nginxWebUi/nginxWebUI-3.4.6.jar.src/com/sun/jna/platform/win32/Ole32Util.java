/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Native;
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
/*    */ public abstract class Ole32Util
/*    */ {
/*    */   public static Guid.GUID getGUIDFromString(String guidString) {
/* 44 */     Guid.GUID lpiid = new Guid.GUID();
/* 45 */     WinNT.HRESULT hr = Ole32.INSTANCE.IIDFromString(guidString, lpiid);
/* 46 */     if (!hr.equals(W32Errors.S_OK)) {
/* 47 */       throw new RuntimeException(hr.toString());
/*    */     }
/* 49 */     return lpiid;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String getStringFromGUID(Guid.GUID guid) {
/* 60 */     Guid.GUID pguid = new Guid.GUID(guid.getPointer());
/* 61 */     int max = 39;
/* 62 */     char[] lpsz = new char[max];
/* 63 */     int len = Ole32.INSTANCE.StringFromGUID2(pguid, lpsz, max);
/* 64 */     if (len == 0) {
/* 65 */       throw new RuntimeException("StringFromGUID2");
/*    */     }
/* 67 */     lpsz[len - 1] = Character.MIN_VALUE;
/* 68 */     return Native.toString(lpsz);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Guid.GUID generateGUID() {
/* 77 */     Guid.GUID pguid = new Guid.GUID();
/* 78 */     WinNT.HRESULT hr = Ole32.INSTANCE.CoCreateGuid(pguid);
/* 79 */     if (!hr.equals(W32Errors.S_OK)) {
/* 80 */       throw new RuntimeException(hr.toString());
/*    */     }
/* 82 */     return pguid;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Ole32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */