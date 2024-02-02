/*    */ package org.xnio.channels;
/*    */ 
/*    */ import java.io.InterruptedIOException;
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
/*    */ public class WriteTimeoutException
/*    */   extends InterruptedIOException
/*    */ {
/*    */   private static final long serialVersionUID = 2058056832934733469L;
/*    */   
/*    */   public WriteTimeoutException() {}
/*    */   
/*    */   public WriteTimeoutException(String msg) {
/* 44 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WriteTimeoutException(Throwable cause) {
/* 55 */     initCause(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WriteTimeoutException(String msg, Throwable cause) {
/* 65 */     super(msg);
/* 66 */     initCause(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\WriteTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */