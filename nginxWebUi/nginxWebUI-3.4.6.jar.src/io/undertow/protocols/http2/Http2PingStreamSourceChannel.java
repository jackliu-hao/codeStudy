/*    */ package io.undertow.protocols.http2;
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
/*    */ public class Http2PingStreamSourceChannel
/*    */   extends AbstractHttp2StreamSourceChannel
/*    */ {
/*    */   private final byte[] data;
/*    */   private final boolean ack;
/*    */   
/*    */   Http2PingStreamSourceChannel(Http2Channel framedChannel, byte[] pingData, boolean ack) {
/* 32 */     super(framedChannel);
/* 33 */     this.data = pingData;
/* 34 */     this.ack = ack;
/* 35 */     lastFrame();
/*    */   }
/*    */   
/*    */   public byte[] getData() {
/* 39 */     return this.data;
/*    */   }
/*    */   
/*    */   public boolean isAck() {
/* 43 */     return this.ack;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PingStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */