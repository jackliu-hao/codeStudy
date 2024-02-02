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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class Http2GoAwayStreamSinkChannel
/*    */   extends Http2NoDataStreamSinkChannel
/*    */ {
/*    */   public static final int HEADER_FIRST_LINE = 2055;
/*    */   private final int status;
/*    */   private final int lastGoodStreamId;
/*    */   
/*    */   protected Http2GoAwayStreamSinkChannel(Http2Channel channel, int status, int lastGoodStreamId) {
/* 41 */     super(channel);
/* 42 */     this.status = status;
/* 43 */     this.lastGoodStreamId = lastGoodStreamId;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeader() {
/* 48 */     ByteBuffer buf = ByteBuffer.allocate(17);
/*    */     
/* 50 */     Http2ProtocolUtils.putInt(buf, 2055);
/* 51 */     buf.put((byte)0);
/* 52 */     Http2ProtocolUtils.putInt(buf, 0);
/* 53 */     Http2ProtocolUtils.putInt(buf, this.lastGoodStreamId);
/* 54 */     Http2ProtocolUtils.putInt(buf, this.status);
/* 55 */     buf.flip();
/* 56 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(buf));
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean isLastFrame() {
/* 61 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2GoAwayStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */