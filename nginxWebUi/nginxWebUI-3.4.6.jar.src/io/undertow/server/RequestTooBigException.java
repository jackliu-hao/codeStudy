/*    */ package io.undertow.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class RequestTooBigException
/*    */   extends IOException
/*    */ {
/*    */   public RequestTooBigException() {}
/*    */   
/*    */   public RequestTooBigException(String message) {
/* 15 */     super(message);
/*    */   }
/*    */   
/*    */   public RequestTooBigException(String message, Throwable cause) {
/* 19 */     super(message, cause);
/*    */   }
/*    */   
/*    */   public RequestTooBigException(Throwable cause) {
/* 23 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\RequestTooBigException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */