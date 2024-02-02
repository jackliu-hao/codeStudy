/*    */ package io.undertow.websockets.core;
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
/*    */ public class WebSocketException
/*    */   extends IOException
/*    */ {
/*    */   private static final long serialVersionUID = -6784834646314672530L;
/*    */   
/*    */   public WebSocketException() {}
/*    */   
/*    */   public WebSocketException(String msg, Throwable cause) {
/* 35 */     super(msg, cause);
/*    */   }
/*    */   
/*    */   public WebSocketException(String msg) {
/* 39 */     super(msg);
/*    */   }
/*    */   
/*    */   public WebSocketException(Throwable cause) {
/* 43 */     super(cause);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketException.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */