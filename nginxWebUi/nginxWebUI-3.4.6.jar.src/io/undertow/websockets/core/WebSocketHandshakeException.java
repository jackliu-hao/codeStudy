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
/*    */ public class WebSocketHandshakeException
/*    */   extends WebSocketException
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public WebSocketHandshakeException() {}
/*    */   
/*    */   public WebSocketHandshakeException(String s) {
/* 33 */     super(s);
/*    */   }
/*    */   
/*    */   public WebSocketHandshakeException(String s, Throwable throwable) {
/* 37 */     super(s, throwable);
/*    */   }
/*    */   
/*    */   public WebSocketHandshakeException(Throwable cause) {
/* 41 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketHandshakeException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */