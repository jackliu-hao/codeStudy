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
/*    */ public class DataReadException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 1684265521187171525L;
/*    */   
/*    */   public DataReadException(Exception cause) {
/* 39 */     super(cause);
/* 40 */     setSQLState("S1009");
/*    */   }
/*    */   
/*    */   public DataReadException(String msg) {
/* 44 */     super(msg);
/* 45 */     setSQLState("S1009");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\DataReadException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */