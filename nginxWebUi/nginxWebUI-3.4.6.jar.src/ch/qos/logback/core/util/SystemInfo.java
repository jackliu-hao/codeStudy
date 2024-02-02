/*    */ package ch.qos.logback.core.util;
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
/*    */ public class SystemInfo
/*    */ {
/*    */   public static String getJavaVendor() {
/* 19 */     return OptionHelper.getSystemProperty("java.vendor", null);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\SystemInfo.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */