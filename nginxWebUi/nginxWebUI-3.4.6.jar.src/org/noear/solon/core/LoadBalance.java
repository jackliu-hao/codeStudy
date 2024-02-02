/*    */ package org.noear.solon.core;
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
/*    */ @FunctionalInterface
/*    */ public interface LoadBalance
/*    */ {
/*    */   static LoadBalance get(String service) {
/* 38 */     return get("", service);
/*    */   }
/*    */   
/*    */   static LoadBalance get(String group, String service) {
/* 42 */     return Bridge.upstreamFactory().create(group, service);
/*    */   }
/*    */   
/*    */   String getServer();
/*    */   
/*    */   public static interface Factory {
/*    */     LoadBalance create(String param1String1, String param1String2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\LoadBalance.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */