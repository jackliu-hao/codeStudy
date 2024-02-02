/*    */ package com.mysql.cj.callback;
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
/*    */ public class UsernameCallback
/*    */   implements MysqlCallback
/*    */ {
/*    */   private String username;
/*    */   
/*    */   public UsernameCallback(String username) {
/* 39 */     this.username = username;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getUsername() {
/* 49 */     return this.username;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\callback\UsernameCallback.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */