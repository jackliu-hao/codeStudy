/*    */ package oshi.driver.linux;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.ExecutingCommand;
/*    */ import oshi.util.ParseUtil;
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
/*    */ @ThreadSafe
/*    */ public final class Lshal
/*    */ {
/*    */   public static String querySerialNumber() {
/* 46 */     String marker = "system.hardware.serial =";
/* 47 */     for (String checkLine : ExecutingCommand.runNative("lshal")) {
/* 48 */       if (checkLine.contains(marker)) {
/* 49 */         return ParseUtil.getSingleQuoteStringValue(checkLine);
/*    */       }
/*    */     } 
/* 52 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Lshal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */