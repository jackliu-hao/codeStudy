/*    */ package com.cym.sqlhelper.utils;
/*    */ 
/*    */ public class Condition {
/*    */   public Condition(String column, String operation, Object value) {
/*  5 */     this.column = column;
/*  6 */     this.operation = operation;
/*  7 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   String column;
/*    */   String operation;
/*    */   Object value;
/*    */   
/*    */   public String getColumn() {
/* 16 */     return this.column;
/*    */   }
/*    */   
/*    */   public void setColumn(String column) {
/* 20 */     this.column = column;
/*    */   }
/*    */   
/*    */   public String getOperation() {
/* 24 */     return this.operation;
/*    */   }
/*    */   
/*    */   public void setOperation(String operation) {
/* 28 */     this.operation = operation;
/*    */   }
/*    */   
/*    */   public Object getValue() {
/* 32 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(Object value) {
/* 36 */     this.value = value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelpe\\utils\Condition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */