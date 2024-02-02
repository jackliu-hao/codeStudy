/*    */ package org.apache.http.conn;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConnectionPoolTimeoutException
/*    */   extends ConnectTimeoutException
/*    */ {
/*    */   private static final long serialVersionUID = -7898874842020245128L;
/*    */   
/*    */   public ConnectionPoolTimeoutException() {}
/*    */   
/*    */   public ConnectionPoolTimeoutException(String message) {
/* 54 */     super(message);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ConnectionPoolTimeoutException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */