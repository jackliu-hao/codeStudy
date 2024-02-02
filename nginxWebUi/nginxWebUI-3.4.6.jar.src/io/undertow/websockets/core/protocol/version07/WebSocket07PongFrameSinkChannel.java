/*    */ package io.undertow.websockets.core.protocol.version07;
/*    */ 
/*    */ import io.undertow.websockets.core.WebSocketFrameType;
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
/*    */ class WebSocket07PongFrameSinkChannel
/*    */   extends WebSocket07FrameSinkChannel
/*    */ {
/*    */   WebSocket07PongFrameSinkChannel(WebSocket07Channel wsChannel) {
/* 27 */     super(wsChannel, WebSocketFrameType.PONG);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07PongFrameSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */