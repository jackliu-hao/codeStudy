/*    */ package io.undertow.websockets.core.protocol.version07;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.websockets.core.StreamSourceFrameChannel;
/*    */ import io.undertow.websockets.core.WebSocketFrameType;
/*    */ import io.undertow.websockets.core.WebSocketInvalidCloseCodeException;
/*    */ import io.undertow.websockets.core.WebSocketMessages;
/*    */ import io.undertow.websockets.core.function.ChannelFunction;
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
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
/*    */ class WebSocket07CloseFrameSourceChannel
/*    */   extends StreamSourceFrameChannel
/*    */ {
/*    */   WebSocket07CloseFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, Masker masker, PooledByteBuffer pooled, long frameLength) {
/* 35 */     super(wsChannel, WebSocketFrameType.CLOSE, rsv, true, pooled, frameLength, masker, new ChannelFunction[] { new CloseFrameValidatorChannelFunction(wsChannel) });
/*    */   }
/*    */ 
/*    */   
/*    */   WebSocket07CloseFrameSourceChannel(WebSocket07Channel wsChannel, int rsv, PooledByteBuffer pooled, long frameLength) {
/* 40 */     super(wsChannel, WebSocketFrameType.CLOSE, rsv, true, pooled, frameLength, null, new ChannelFunction[] { new CloseFrameValidatorChannelFunction(wsChannel) });
/*    */   }
/*    */   
/*    */   public static class CloseFrameValidatorChannelFunction
/*    */     extends UTF8Checker {
/*    */     private final WebSocket07Channel wsChannel;
/*    */     private int statusBytesRead;
/*    */     private int status;
/*    */     
/*    */     CloseFrameValidatorChannelFunction(WebSocket07Channel wsChannel) {
/* 50 */       this.wsChannel = wsChannel;
/*    */     }
/*    */ 
/*    */ 
/*    */     
/*    */     public void afterRead(ByteBuffer buf, int position, int length) throws IOException {
/* 56 */       int i = 0;
/* 57 */       if (this.statusBytesRead < 2) {
/* 58 */         while (this.statusBytesRead < 2 && i < length) {
/* 59 */           this.status <<= 8;
/* 60 */           this.status += buf.get(position + i) & 0xFF;
/* 61 */           this.statusBytesRead++;
/* 62 */           i++;
/*    */         } 
/* 64 */         if (this.statusBytesRead == 2)
/*    */         {
/* 66 */           if ((this.status >= 0 && this.status <= 999) || (this.status >= 1004 && this.status <= 1006) || (this.status >= 1012 && this.status <= 2999) || this.status >= 5000) {
/*    */             
/* 68 */             WebSocketInvalidCloseCodeException webSocketInvalidCloseCodeException = WebSocketMessages.MESSAGES.invalidCloseFrameStatusCode(this.status);
/* 69 */             this.wsChannel.markReadsBroken((Throwable)webSocketInvalidCloseCodeException);
/* 70 */             throw webSocketInvalidCloseCodeException;
/*    */           } 
/*    */         }
/*    */       } 
/* 74 */       super.afterRead(buf, position + i, length - i);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\WebSocket07CloseFrameSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */