/*    */ package oshi.driver.linux;
/*    */ 
/*    */ import oshi.annotation.concurrent.ThreadSafe;
/*    */ import oshi.util.FileUtil;
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
/*    */ public final class Devicetree
/*    */ {
/*    */   public static String queryModel() {
/* 44 */     String modelStr = FileUtil.getStringFromFile("/sys/firmware/devicetree/base/model");
/* 45 */     if (!modelStr.isEmpty()) {
/* 46 */       return modelStr.replace("Machine: ", "");
/*    */     }
/* 48 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\Devicetree.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */