/*    */ package com.github.odiszapc.nginxparser;
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
/*    */ public class NgxToken
/*    */ {
/*    */   private String token;
/*    */   
/*    */   public NgxToken(String token) {
/* 23 */     this.token = token;
/*    */   }
/*    */   
/*    */   public String getToken() {
/* 27 */     return this.token;
/*    */   }
/*    */   
/*    */   public void setToken(String token) {
/* 31 */     this.token = token;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 36 */     return this.token;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\github\odiszapc\nginxparser\NgxToken.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */