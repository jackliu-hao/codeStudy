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
/*    */ class Http2WindowUpdateStreamSinkChannel
/*    */   extends Http2NoDataStreamSinkChannel
/*    */ {
/*    */   public static final int HEADER_FIRST_LINE = 1032;
/*    */   private final int streamId;
/*    */   private final int deltaWindowSize;
/*    */   
/*    */   protected Http2WindowUpdateStreamSinkChannel(Http2Channel channel, int streamId, int deltaWindowSize) {
/* 39 */     super(channel);
/* 40 */     this.streamId = streamId;
/* 41 */     this.deltaWindowSize = deltaWindowSize;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeader() {
/* 46 */     ByteBuffer buf = ByteBuffer.allocate(13);
/* 47 */     Http2ProtocolUtils.putInt(buf, 1032);
/* 48 */     buf.put((byte)0);
/* 49 */     Http2ProtocolUtils.putInt(buf, this.streamId);
/* 50 */     Http2ProtocolUtils.putInt(buf, this.deltaWindowSize);
/* 51 */     buf.flip();
/* 52 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(buf));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2WindowUpdateStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */