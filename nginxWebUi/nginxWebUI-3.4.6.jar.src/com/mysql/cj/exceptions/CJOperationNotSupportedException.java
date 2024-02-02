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
/*    */ public class CJOperationNotSupportedException
/*    */   extends CJException
/*    */ {
/*    */   private static final long serialVersionUID = 2619184100062994443L;
/*    */   
/*    */   public CJOperationNotSupportedException() {}
/*    */   
/*    */   public CJOperationNotSupportedException(String message) {
/* 41 */     super(message);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\exceptions\CJOperationNotSupportedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */