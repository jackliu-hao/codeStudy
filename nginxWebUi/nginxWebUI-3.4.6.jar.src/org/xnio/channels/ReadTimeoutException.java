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
/*    */ public class ReadTimeoutException
/*    */   extends InterruptedIOException
/*    */ {
/*    */   private static final long serialVersionUID = 2058056832934733469L;
/*    */   
/*    */   public ReadTimeoutException() {}
/*    */   
/*    */   public ReadTimeoutException(String msg) {
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
/*    */   public ReadTimeoutException(Throwable cause) {
/* 55 */     initCause(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ReadTimeoutException(String msg, Throwable cause) {
/* 65 */     super(msg);
/* 66 */     initCause(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ReadTimeoutException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */