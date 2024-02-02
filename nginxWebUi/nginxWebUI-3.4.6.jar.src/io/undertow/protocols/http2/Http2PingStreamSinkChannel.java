/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
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
/*    */ class Http2PingStreamSinkChannel
/*    */   extends Http2NoDataStreamSinkChannel
/*    */ {
/*    */   public static final int HEADER = 2054;
/*    */   private final byte[] data;
/*    */   private final boolean ack;
/*    */   
/*    */   protected Http2PingStreamSinkChannel(Http2Channel channel, byte[] data, boolean ack) {
/* 39 */     super(channel);
/* 40 */     if (data.length != 8) {
/* 41 */       throw new IllegalArgumentException(UndertowMessages.MESSAGES.httpPingDataMustBeLength8());
/*    */     }
/* 43 */     this.data = data;
/* 44 */     this.ack = ack;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeader() {
/* 49 */     ByteBuffer buf = ByteBuffer.allocate(17);
/* 50 */     Http2ProtocolUtils.putInt(buf, 2054);
/* 51 */     buf.put((byte)(this.ack ? 1 : 0));
/* 52 */     Http2ProtocolUtils.putInt(buf, 0);
/* 53 */     for (int i = 0; i < 8; i++) {
/* 54 */       buf.put(this.data[i]);
/*    */     }
/* 56 */     buf.flip();
/* 57 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(buf));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PingStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */