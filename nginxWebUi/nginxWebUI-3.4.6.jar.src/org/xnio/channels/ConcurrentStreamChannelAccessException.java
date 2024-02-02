/*    */ package org.xnio.channels;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConcurrentStreamChannelAccessException
/*    */   extends IllegalStateException
/*    */ {
/*    */   public ConcurrentStreamChannelAccessException() {}
/*    */   
/*    */   public ConcurrentStreamChannelAccessException(String msg) {
/* 45 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConcurrentStreamChannelAccessException(Throwable cause) {
/* 57 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConcurrentStreamChannelAccessException(String msg, Throwable cause) {
/* 67 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\ConcurrentStreamChannelAccessException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */