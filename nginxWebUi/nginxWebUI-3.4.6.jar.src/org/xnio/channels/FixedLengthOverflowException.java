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
/*    */ public class FixedLengthOverflowException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 475540863890698430L;
/*    */   
/*    */   public FixedLengthOverflowException() {}
/*    */   
/*    */   public FixedLengthOverflowException(String msg) {
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
/*    */   public FixedLengthOverflowException(Throwable cause) {
/* 57 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public FixedLengthOverflowException(String msg, Throwable cause) {
/* 67 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\FixedLengthOverflowException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */