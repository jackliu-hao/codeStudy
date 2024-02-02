/*    */ package io.undertow.websockets.core;
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
/*    */ public class InvalidOpCodeException
/*    */   extends WebSocketException
/*    */ {
/*    */   public InvalidOpCodeException() {}
/*    */   
/*    */   public InvalidOpCodeException(String msg, Throwable cause) {
/* 30 */     super(msg, cause);
/*    */   }
/*    */   
/*    */   public InvalidOpCodeException(String msg) {
/* 34 */     super(msg);
/*    */   }
/*    */   
/*    */   public InvalidOpCodeException(Throwable cause) {
/* 38 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\InvalidOpCodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */