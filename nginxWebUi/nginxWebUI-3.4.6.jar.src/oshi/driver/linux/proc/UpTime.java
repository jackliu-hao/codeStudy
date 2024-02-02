/*    */ package oshi.driver.linux.proc;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.FileUtil;
/*    */ import oshi.util.platform.linux.ProcPath;
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
/*    */ public final class UpTime
/*    */ {
/*    */   public static double getSystemUptimeSeconds() {
/* 45 */     String uptime = FileUtil.getStringFromFile(ProcPath.UPTIME);
/* 46 */     int spaceIndex = uptime.indexOf(' ');
/*    */     try {
/* 48 */       if (spaceIndex < 0)
/*    */       {
/* 50 */         return 0.0D;
/*    */       }
/* 52 */       return Double.parseDouble(uptime.substring(0, spaceIndex));
/*    */     }
/* 54 */     catch (NumberFormatException nfe) {
/* 55 */       return 0.0D;
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\UpTime.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */