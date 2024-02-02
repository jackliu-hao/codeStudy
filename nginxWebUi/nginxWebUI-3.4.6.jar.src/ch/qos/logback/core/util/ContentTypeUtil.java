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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ContentTypeUtil
/*    */ {
/*    */   public static boolean isTextual(String contextType) {
/* 25 */     if (contextType == null) {
/* 26 */       return false;
/*    */     }
/* 28 */     return contextType.startsWith("text");
/*    */   }
/*    */   
/*    */   public static String getSubType(String contextType) {
/* 32 */     if (contextType == null) {
/* 33 */       return null;
/*    */     }
/* 35 */     int index = contextType.indexOf('/');
/* 36 */     if (index == -1) {
/* 37 */       return null;
/*    */     }
/* 39 */     int subTypeStartIndex = index + 1;
/* 40 */     if (subTypeStartIndex < contextType.length()) {
/* 41 */       return contextType.substring(subTypeStartIndex);
/*    */     }
/* 43 */     return null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\cor\\util\ContentTypeUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */