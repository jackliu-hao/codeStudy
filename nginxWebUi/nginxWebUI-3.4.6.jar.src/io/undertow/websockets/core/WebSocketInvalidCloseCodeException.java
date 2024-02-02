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
/*    */ 
/*    */ public class WebSocketInvalidCloseCodeException
/*    */   extends WebSocketException
/*    */ {
/*    */   private static final long serialVersionUID = -6784834646314476130L;
/*    */   
/*    */   public WebSocketInvalidCloseCodeException() {}
/*    */   
/*    */   public WebSocketInvalidCloseCodeException(String msg, Throwable cause) {
/* 33 */     super(msg, cause);
/*    */   }
/*    */   
/*    */   public WebSocketInvalidCloseCodeException(String msg) {
/* 37 */     super(msg);
/*    */   }
/*    */   
/*    */   public WebSocketInvalidCloseCodeException(Throwable cause) {
/* 41 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketInvalidCloseCodeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */