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
/*    */ @ThreadSafe
/*    */ public final class Lshw
/*    */ {
/*    */   public static String queryModel() {
/* 45 */     String modelMarker = "product:";
/* 46 */     for (String checkLine : ExecutingCommand.runNative("lshw -C system")) {
/* 47 */       if (checkLine.contains(modelMarker)) {
/* 48 */         return checkLine.split(modelMarker)[1].trim();
/*    */       }
/*    */     } 
/* 51 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String querySerialNumber() {
/* 60 */     String serialMarker = "serial:";
/* 61 */     for (String checkLine : ExecutingCommand.runNative("lshw -C system")) {
/* 62 */       if (checkLine.contains(serialMarker)) {
/* 63 */         return checkLine.split(serialMarker)[1].trim();
/*    */       }
/*    */     } 
/* 66 */     return null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static long queryCpuCapacity() {
/* 75 */     String capacityMarker = "capacity:";
/* 76 */     for (String checkLine : ExecutingCommand.runNative("lshw -class processor")) {
/* 77 */       if (checkLine.contains(capacityMarker)) {
/* 78 */         return ParseUtil.parseHertz(checkLine.split(capacityMarker)[1].trim());
/*    */       }
/*    */     } 
/* 81 */     return -1L;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Lshw.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */