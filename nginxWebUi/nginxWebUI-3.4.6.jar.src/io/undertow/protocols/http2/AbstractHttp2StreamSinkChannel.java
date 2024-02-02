/*    */ package io.undertow.protocols.http2;
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
/*    */ public class AbstractHttp2StreamSinkChannel
/*    */   extends AbstractFramedStreamSinkChannel<Http2Channel, AbstractHttp2StreamSourceChannel, AbstractHttp2StreamSinkChannel>
/*    */ {
/*    */   AbstractHttp2StreamSinkChannel(Http2Channel channel) {
/* 29 */     super(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isLastFrame() {
/* 34 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\AbstractHttp2StreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */