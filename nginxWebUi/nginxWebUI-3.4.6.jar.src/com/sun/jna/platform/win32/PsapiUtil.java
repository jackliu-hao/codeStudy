/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.ptr.IntByReference;
/*    */ import java.util.Arrays;
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
/*    */ public abstract class PsapiUtil
/*    */ {
/*    */   public static int[] enumProcesses() {
/* 44 */     int size = 0;
/* 45 */     int[] lpidProcess = null;
/* 46 */     IntByReference lpcbNeeded = new IntByReference();
/*    */     do {
/* 48 */       size += 1024;
/* 49 */       lpidProcess = new int[size];
/* 50 */       if (!Psapi.INSTANCE.EnumProcesses(lpidProcess, size * 4, lpcbNeeded)) {
/* 51 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*    */       }
/* 53 */     } while (size == lpcbNeeded.getValue() / 4);
/*    */     
/* 55 */     return Arrays.copyOf(lpidProcess, lpcbNeeded.getValue() / 4);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\PsapiUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */