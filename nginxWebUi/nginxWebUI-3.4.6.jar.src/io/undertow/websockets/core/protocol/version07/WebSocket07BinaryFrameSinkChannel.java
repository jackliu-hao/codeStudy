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
/*    */ 
/*    */ class WebSocket07BinaryFrameSinkChannel
/*    */   extends WebSocket07FrameSinkChannel
/*    */ {
/*    */   WebSocket07BinaryFrameSinkChannel(WebSocket07Channel wsChannel) {
/* 28 */     super(wsChannel, WebSocketFrameType.BINARY);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFragmentationSupported() {
/* 33 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean areExtensionsSupported() {
/* 38 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07BinaryFrameSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */