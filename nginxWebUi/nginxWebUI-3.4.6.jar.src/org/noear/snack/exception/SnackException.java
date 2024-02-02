/*    */ package org.noear.snack.exception;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SnackException
/*    */   extends RuntimeException
/*    */ {
/*    */   public SnackException(String message) {
/* 11 */     super(message);
/*    */   }
/*    */   
/*    */   public SnackException(Throwable cause) {
/* 15 */     super(cause);
/*    */   }
/*    */   
/*    */   public SnackException(String message, Throwable cause) {
/* 19 */     super(message, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\exception\SnackException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */