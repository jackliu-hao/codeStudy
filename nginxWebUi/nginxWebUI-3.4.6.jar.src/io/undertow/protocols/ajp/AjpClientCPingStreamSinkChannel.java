/*    */ package io.undertow.protocols.ajp;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*    */ import io.undertow.util.ImmediatePooledByteBuffer;
/*    */ import java.nio.ByteBuffer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AjpClientCPingStreamSinkChannel
/*    */   extends AbstractAjpClientStreamSinkChannel
/*    */ {
/* 14 */   private static final byte[] CPING = new byte[] { 18, 52, 0, 1, 10 };
/*    */   
/*    */   protected AjpClientCPingStreamSinkChannel(AjpClientChannel channel) {
/* 17 */     super(channel);
/*    */   }
/*    */ 
/*    */   
/*    */   protected final SendFrameHeader createFrameHeader() {
/* 22 */     return new SendFrameHeader((PooledByteBuffer)new ImmediatePooledByteBuffer(ByteBuffer.wrap(CPING)));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ajp\AjpClientCPingStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */