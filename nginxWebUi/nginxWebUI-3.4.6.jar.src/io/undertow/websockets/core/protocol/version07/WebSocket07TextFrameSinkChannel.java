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
/*    */ 
/*    */ 
/*    */ 
/*    */ class WebSocket07TextFrameSinkChannel
/*    */   extends WebSocket07FrameSinkChannel
/*    */ {
/*    */   WebSocket07TextFrameSinkChannel(WebSocket07Channel wsChannel) {
/* 31 */     super(wsChannel, WebSocketFrameType.TEXT);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isFragmentationSupported() {
/* 36 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean areExtensionsSupported() {
/* 41 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07TextFrameSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */