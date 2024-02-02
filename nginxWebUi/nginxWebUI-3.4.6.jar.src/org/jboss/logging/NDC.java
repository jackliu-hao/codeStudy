/*    */ package org.jboss.logging;
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
/*    */ public final class NDC
/*    */ {
/*    */   public static void clear() {
/* 30 */     LoggerProviders.PROVIDER.clearNdc();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String get() {
/* 39 */     return LoggerProviders.PROVIDER.getNdc();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int getDepth() {
/* 48 */     return LoggerProviders.PROVIDER.getNdcDepth();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String pop() {
/* 57 */     return LoggerProviders.PROVIDER.popNdc();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String peek() {
/* 66 */     return LoggerProviders.PROVIDER.peekNdc();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void push(String message) {
/* 75 */     LoggerProviders.PROVIDER.pushNdc(message);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setMaxDepth(int maxDepth) {
/* 84 */     LoggerProviders.PROVIDER.setNdcMaxDepth(maxDepth);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\logging\NDC.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */