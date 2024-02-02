/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.server.protocol.framed.AbstractFramedChannel;
/*    */ import io.undertow.server.protocol.framed.AbstractFramedStreamSourceChannel;
/*    */ import io.undertow.server.protocol.framed.FrameHeaderData;
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
/*    */ public class AbstractHttp2StreamSourceChannel
/*    */   extends AbstractFramedStreamSourceChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>
/*    */ {
/*    */   AbstractHttp2StreamSourceChannel(Http2Channel framedChannel) {
/* 34 */     super(framedChannel);
/*    */   }
/*    */   
/*    */   AbstractHttp2StreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining) {
/* 38 */     super(framedChannel, data, frameDataRemaining);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void handleHeaderData(FrameHeaderData headerData) {}
/*    */ 
/*    */ 
/*    */   
/*    */   protected Http2Channel getFramedChannel() {
/* 48 */     return (Http2Channel)super.getFramedChannel();
/*    */   }
/*    */   
/*    */   public Http2Channel getHttp2Channel() {
/* 52 */     return getFramedChannel();
/*    */   }
/*    */ 
/*    */   
/*    */   protected void lastFrame() {
/* 57 */     super.lastFrame();
/*    */   }
/*    */   
/*    */   void rstStream() {
/* 61 */     rstStream(8);
/*    */   }
/*    */ 
/*    */   
/*    */   void rstStream(int error) {}
/*    */   
/*    */   protected void markStreamBroken() {
/* 68 */     super.markStreamBroken();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\AbstractHttp2StreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */