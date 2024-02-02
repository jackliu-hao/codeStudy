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
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum WebSocketFrameType
/*    */ {
/* 30 */   BINARY,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 35 */   TEXT,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 40 */   PING,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 45 */   PONG,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 50 */   CLOSE,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 55 */   CONTINUATION,
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 60 */   UNKOWN;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\WebSocketFrameType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */