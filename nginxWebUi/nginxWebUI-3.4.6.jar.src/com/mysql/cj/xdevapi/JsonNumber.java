/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import java.math.BigDecimal;
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
/*    */ public class JsonNumber
/*    */   implements JsonValue
/*    */ {
/* 39 */   private String val = "null";
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Integer getInteger() {
/* 47 */     return Integer.valueOf((new BigDecimal(this.val)).intValue());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public BigDecimal getBigDecimal() {
/* 56 */     return new BigDecimal(this.val);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public JsonNumber setValue(String value) {
/* 68 */     this.val = (new BigDecimal(value)).toString();
/* 69 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 74 */     return this.val;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\JsonNumber.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */