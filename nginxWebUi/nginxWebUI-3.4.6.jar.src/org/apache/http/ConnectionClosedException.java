/*    */ package org.apache.http;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ConnectionClosedException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = 617550366255636674L;
/*    */   
/*    */   public ConnectionClosedException() {
/* 47 */     super("Connection is closed");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ConnectionClosedException(String message) {
/* 56 */     super(HttpException.clean(message));
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
/*    */   public ConnectionClosedException(String format, Object... args) {
/* 68 */     super(HttpException.clean(String.format(format, args)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ConnectionClosedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */