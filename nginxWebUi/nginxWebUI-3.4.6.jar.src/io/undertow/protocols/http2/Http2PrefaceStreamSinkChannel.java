/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*    */ import io.undertow.util.ImmediatePooledByteBuffer;
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
/*    */ 
/*    */ 
/*    */ class Http2PrefaceStreamSinkChannel
/*    */   extends Http2StreamSinkChannel
/*    */ {
/*    */   Http2PrefaceStreamSinkChannel(Http2Channel channel) {
/* 33 */     super(channel, 0);
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeaderImpl() {
/* 38 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(Http2Channel.PREFACE_BYTES)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PrefaceStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */