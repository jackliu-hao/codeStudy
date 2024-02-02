/*    */ package org.noear.solon.boot.io;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class LimitedInputException
/*    */   extends IOException
/*    */ {
/*    */   public LimitedInputException() {}
/*    */   
/*    */   public LimitedInputException(String message) {
/* 16 */     super(message);
/*    */   }
/*    */   
/*    */   public LimitedInputException(String message, Throwable cause) {
/* 20 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public LimitedInputException(Throwable cause) {
/* 24 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\boot\io\LimitedInputException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */