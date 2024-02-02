/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.io.IOException;
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
/*    */ 
/*    */ 
/*    */ public class FixedLengthUnderflowException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 7294784996964683484L;
/*    */   
/*    */   public FixedLengthUnderflowException() {}
/*    */   
/*    */   public FixedLengthUnderflowException(String msg) {
/* 46 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FixedLengthUnderflowException(Throwable cause) {
/* 57 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FixedLengthUnderflowException(String msg, Throwable cause) {
/* 67 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\FixedLengthUnderflowException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */