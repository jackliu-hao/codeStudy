/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.ptr.IntByReference;
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
/*    */ public abstract class NtDllUtil
/*    */ {
/*    */   public static String getKeyName(WinReg.HKEY hkey) {
/* 43 */     IntByReference resultLength = new IntByReference();
/* 44 */     int rc = NtDll.INSTANCE.ZwQueryKey(hkey, 0, null, 0, resultLength);
/*    */     
/* 46 */     if (rc != -1073741789 || resultLength.getValue() <= 0) {
/* 47 */       throw new Win32Exception(rc);
/*    */     }
/*    */     
/* 50 */     Wdm.KEY_BASIC_INFORMATION keyInformation = new Wdm.KEY_BASIC_INFORMATION(resultLength.getValue());
/* 51 */     rc = NtDll.INSTANCE.ZwQueryKey(hkey, 0, keyInformation, resultLength
/* 52 */         .getValue(), resultLength);
/* 53 */     if (rc != 0) {
/* 54 */       throw new Win32Exception(rc);
/*    */     }
/* 56 */     return keyInformation.getName();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\NtDllUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */