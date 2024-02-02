/*    */ package io.undertow.websockets.core.protocol.version07;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import io.undertow.websockets.core.WebSocketChannel;
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
/*    */ class WebSocket07PingFrameSourceChannel
/*    */   extends StreamSourceFrameChannel
/*    */ {
/*    */   WebSocket07PingFrameSourceChannel(WebSocketChannel wsChannel, int rsv, Masker masker, PooledByteBuffer pooled, long frameLength) {
/* 31 */     super(wsChannel, WebSocketFrameType.PING, rsv, true, pooled, frameLength, masker, new io.undertow.websockets.core.function.ChannelFunction[0]);
/*    */   }
/*    */ 
/*    */   
/*    */   WebSocket07PingFrameSourceChannel(WebSocketChannel wsChannel, int rsv, PooledByteBuffer pooled, long frameLength) {
/* 36 */     super(wsChannel, WebSocketFrameType.PING, rsv, true, pooled, frameLength, null, new io.undertow.websockets.core.function.ChannelFunction[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07PingFrameSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */