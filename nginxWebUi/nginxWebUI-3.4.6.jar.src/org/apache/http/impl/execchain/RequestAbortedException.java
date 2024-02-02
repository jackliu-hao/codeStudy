/*    */ package org.apache.http.impl.execchain;
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
/*    */ public class RequestAbortedException
/*    */   extends InterruptedIOException
/*    */ {
/*    */   private static final long serialVersionUID = 4973849966012490112L;
/*    */   
/*    */   public RequestAbortedException(String message) {
/* 42 */     super(message);
/*    */   }
/*    */   
/*    */   public RequestAbortedException(String message, Throwable cause) {
/* 46 */     super(message);
/* 47 */     if (cause != null)
/* 48 */       initCause(cause); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\execchain\RequestAbortedException.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */