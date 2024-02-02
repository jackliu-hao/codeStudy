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
/*    */ class Http2RstStreamSinkChannel
/*    */   extends Http2NoDataStreamSinkChannel
/*    */ {
/*    */   public static final int HEADER_FIRST_LINE = 1027;
/*    */   private final int streamId;
/*    */   private final int errorCode;
/*    */   
/*    */   protected Http2RstStreamSinkChannel(Http2Channel channel, int streamId, int errorCode) {
/* 36 */     super(channel);
/* 37 */     this.errorCode = errorCode;
/* 38 */     this.streamId = streamId;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeader() {
/* 43 */     ByteBuffer buf = ByteBuffer.allocate(13);
/* 44 */     Http2ProtocolUtils.putInt(buf, 1027);
/* 45 */     buf.put((byte)0);
/* 46 */     Http2ProtocolUtils.putInt(buf, this.streamId);
/* 47 */     Http2ProtocolUtils.putInt(buf, this.errorCode);
/* 48 */     buf.flip();
/* 49 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(buf));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2RstStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */