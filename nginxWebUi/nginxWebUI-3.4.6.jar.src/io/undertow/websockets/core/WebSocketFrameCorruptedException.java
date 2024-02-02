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
/*    */ public class WebSocketFrameCorruptedException
/*    */   extends WebSocketException
/*    */ {
/*    */   private static final long serialVersionUID = -6784834646314476130L;
/*    */   
/*    */   public WebSocketFrameCorruptedException() {}
/*    */   
/*    */   public WebSocketFrameCorruptedException(String msg, Throwable cause) {
/* 33 */     super(msg, cause);
/*    */   }
/*    */   
/*    */   public WebSocketFrameCorruptedException(String msg) {
/* 37 */     super(msg);
/*    */   }
/*    */   
/*    */   public WebSocketFrameCorruptedException(Throwable cause) {
/* 41 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketFrameCorruptedException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */