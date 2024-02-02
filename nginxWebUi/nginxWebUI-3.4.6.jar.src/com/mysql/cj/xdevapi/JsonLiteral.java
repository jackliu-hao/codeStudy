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
/*    */ public enum JsonLiteral
/*    */   implements JsonValue
/*    */ {
/* 37 */   TRUE("true"), FALSE("false"), NULL("null");
/*    */   
/*    */   public final String value;
/*    */   
/*    */   JsonLiteral(String val) {
/* 42 */     this.value = val;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\JsonLiteral.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */