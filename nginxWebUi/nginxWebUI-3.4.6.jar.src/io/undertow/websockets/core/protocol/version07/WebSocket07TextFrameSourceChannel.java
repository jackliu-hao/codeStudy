/*    */ package io.undertow.websockets.core.protocol.version07;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import io.undertow.websockets.core.WebSocketFrameType;
/*    */ import io.undertow.websockets.core.function.ChannelFunction;
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
/*    */ class WebSocket07TextFrameSourceChannel
/*    */   extends StreamSourceFrameChannel
/*    */ {
/*    */   WebSocket07TextFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, boolean finalFragment, Masker masker, UTF8Checker checker, PooledByteBuffer pooled, long frameLength) {
/* 30 */     super(wsChannel, WebSocketFrameType.TEXT, rsv, finalFragment, pooled, frameLength, masker, new ChannelFunction[] { checker });
/*    */   }
/*    */   
/*    */   WebSocket07TextFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, boolean finalFragment, UTF8Checker checker, PooledByteBuffer pooled, long frameLength) {
/* 34 */     super(wsChannel, WebSocketFrameType.TEXT, rsv, finalFragment, pooled, frameLength, null, new ChannelFunction[] { checker });
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07TextFrameSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */