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
/*    */ class WebSocket07BinaryFrameSourceChannel
/*    */   extends StreamSourceFrameChannel
/*    */ {
/*    */   WebSocket07BinaryFrameSourceChannel(WebSocketChannel wsChannel, int rsv, boolean finalFragment, Masker masker, PooledByteBuffer pooled, long frameLength) {
/* 31 */     super(wsChannel, WebSocketFrameType.BINARY, rsv, finalFragment, pooled, frameLength, masker, new io.undertow.websockets.core.function.ChannelFunction[0]);
/*    */   }
/*    */   
/*    */   WebSocket07BinaryFrameSourceChannel(WebSocketChannel wsChannel, int rsv, boolean finalFragment, PooledByteBuffer pooled, long frameLength) {
/* 35 */     super(wsChannel, WebSocketFrameType.BINARY, rsv, finalFragment, pooled, frameLength, null, new io.undertow.websockets.core.function.ChannelFunction[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07BinaryFrameSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */