/*    */ package com.sun.jna.platform.win32;
/*    */ 
/*    */ import com.sun.jna.Memory;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Pointer;
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
/*    */ public abstract class Cfgmgr32Util
/*    */ {
/*    */   public static class Cfgmgr32Exception
/*    */     extends RuntimeException
/*    */   {
/*    */     private final int errorCode;
/*    */     
/*    */     public Cfgmgr32Exception(int errorCode) {
/* 41 */       this.errorCode = errorCode;
/*    */     }
/*    */     
/*    */     public int getErrorCode() {
/* 45 */       return this.errorCode;
/*    */     }
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
/*    */ 
/*    */   
/*    */   public static String CM_Get_Device_ID(int devInst) throws Cfgmgr32Exception {
/* 61 */     int charToBytes = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
/*    */ 
/*    */     
/* 64 */     IntByReference pulLen = new IntByReference();
/* 65 */     int ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
/* 66 */     if (ret != 0) {
/* 67 */       throw new Cfgmgr32Exception(ret);
/*    */     }
/*    */ 
/*    */     
/* 71 */     Memory buffer = new Memory(((pulLen.getValue() + 1) * charToBytes));
/*    */     
/* 73 */     buffer.clear();
/*    */     
/* 75 */     ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, (Pointer)buffer, pulLen.getValue(), 0);
/*    */ 
/*    */ 
/*    */     
/* 79 */     if (ret == 26) {
/* 80 */       ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID_Size(pulLen, devInst, 0);
/* 81 */       if (ret != 0) {
/* 82 */         throw new Cfgmgr32Exception(ret);
/*    */       }
/* 84 */       buffer = new Memory(((pulLen.getValue() + 1) * charToBytes));
/* 85 */       buffer.clear();
/* 86 */       ret = Cfgmgr32.INSTANCE.CM_Get_Device_ID(devInst, (Pointer)buffer, pulLen.getValue(), 0);
/*    */     } 
/*    */     
/* 89 */     if (ret != 0) {
/* 90 */       throw new Cfgmgr32Exception(ret);
/*    */     }
/*    */     
/* 93 */     if (charToBytes == 1) {
/* 94 */       return buffer.getString(0L);
/*    */     }
/* 96 */     return buffer.getWideString(0L);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Cfgmgr32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */