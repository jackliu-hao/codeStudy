/*    */ package com.mysql.cj.xdevapi;
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
/*    */ public interface Client
/*    */ {
/*    */   Session getSession();
/*    */   
/*    */   void close();
/*    */   
/*    */   public enum ClientProperty
/*    */   {
/* 56 */     POOLING_ENABLED("pooling.enabled"), POOLING_MAX_SIZE("pooling.maxSize"), POOLING_MAX_IDLE_TIME("pooling.maxIdleTime"),
/* 57 */     POOLING_QUEUE_TIMEOUT("pooling.queueTimeout");
/*    */     
/* 59 */     private String keyName = "";
/*    */     
/*    */     ClientProperty(String keyName) {
/* 62 */       this.keyName = keyName;
/*    */     }
/*    */     
/*    */     public String getKeyName() {
/* 66 */       return this.keyName;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Client.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */