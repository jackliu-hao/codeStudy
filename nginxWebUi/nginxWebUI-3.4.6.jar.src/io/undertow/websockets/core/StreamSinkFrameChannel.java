/*    */ package io.undertow.websockets.core;
/*    */ 
/*    */ import io.undertow.server.protocol.framed.AbstractFramedStreamSinkChannel;
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
/*    */ public abstract class StreamSinkFrameChannel
/*    */   extends AbstractFramedStreamSinkChannel<WebSocketChannel, StreamSourceFrameChannel, StreamSinkFrameChannel>
/*    */ {
/*    */   private final WebSocketFrameType type;
/*    */   private int rsv;
/*    */   
/*    */   protected StreamSinkFrameChannel(WebSocketChannel channel, WebSocketFrameType type) {
/* 32 */     super(channel);
/* 33 */     this.type = type;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getRsv() {
/* 40 */     return this.rsv;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setRsv(int rsv) {
/* 51 */     if (!areExtensionsSupported() && rsv != 0) {
/* 52 */       throw WebSocketMessages.MESSAGES.extensionsNotSupported();
/*    */     }
/* 54 */     this.rsv = rsv;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isFragmentationSupported() {
/* 61 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean areExtensionsSupported() {
/* 68 */     return false;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public WebSocketFrameType getType() {
/* 75 */     return this.type;
/*    */   }
/*    */   
/*    */   public WebSocketChannel getWebSocketChannel() {
/* 79 */     return (WebSocketChannel)getChannel();
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isLastFrame() {
/* 84 */     return (this.type == WebSocketFrameType.CLOSE);
/*    */   }
/*    */   
/*    */   public boolean isFinalFragment() {
/* 88 */     return isFinalFrameQueued();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\StreamSinkFrameChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */