/*    */ package com.mysql.cj.exceptions;
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
/*    */ public class NumberOutOfRange
/*    */   extends DataReadException
/*    */ {
/*    */   private static final long serialVersionUID = -61091413023651438L;
/*    */   
/*    */   public NumberOutOfRange(String msg) {
/* 39 */     super(msg);
/* 40 */     setSQLState("22003");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\NumberOutOfRange.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */