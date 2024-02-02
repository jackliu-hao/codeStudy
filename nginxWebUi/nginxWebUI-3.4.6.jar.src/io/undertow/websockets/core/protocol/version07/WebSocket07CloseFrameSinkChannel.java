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
/*    */ class WebSocket07CloseFrameSinkChannel
/*    */   extends WebSocket07FrameSinkChannel
/*    */ {
/*    */   WebSocket07CloseFrameSinkChannel(WebSocket07Channel wsChannel) {
/* 27 */     super(wsChannel, WebSocketFrameType.CLOSE);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07CloseFrameSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */