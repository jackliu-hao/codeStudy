/*    */ package com.cym.sqlhelper.bean;
/*    */ 
/*    */ 
/*    */ public class Order
/*    */ {
/*    */   Sort.Direction direction;
/*    */   String column;
/*    */   
/*    */   public Sort.Direction getDirection() {
/* 10 */     return this.direction;
/*    */   }
/*    */   
/*    */   public void setDirection(Sort.Direction direction) {
/* 14 */     this.direction = direction;
/*    */   }
/*    */   
/*    */   public String getColumn() {
/* 18 */     return this.column;
/*    */   }
/*    */   
/*    */   public void setColumn(String column) {
/* 22 */     this.column = column;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\cym\sqlhelper\bean\Order.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */