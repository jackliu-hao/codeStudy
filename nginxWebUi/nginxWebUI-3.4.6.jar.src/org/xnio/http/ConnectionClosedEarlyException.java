/*    */ package org.xnio.http;
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
/*    */ public class ConnectionClosedEarlyException
/*    */   extends UpgradeFailedException
/*    */ {
/*    */   private static final long serialVersionUID = -2954011903833115915L;
/*    */   
/*    */   public ConnectionClosedEarlyException() {}
/*    */   
/*    */   public ConnectionClosedEarlyException(String msg) {
/* 40 */     super(msg);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionClosedEarlyException(Throwable cause) {
/* 51 */     super(cause);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionClosedEarlyException(String msg, Throwable cause) {
/* 61 */     super(msg, cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\http\ConnectionClosedEarlyException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */