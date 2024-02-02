/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.server.protocol.framed.SendFrameHeader;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.util.List;
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
/*    */ public class Http2SettingsStreamSinkChannel
/*    */   extends Http2StreamSinkChannel
/*    */ {
/*    */   private final List<Http2Setting> settings;
/*    */   
/*    */   Http2SettingsStreamSinkChannel(Http2Channel channel, List<Http2Setting> settings) {
/* 37 */     super(channel, 0);
/* 38 */     this.settings = settings;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   Http2SettingsStreamSinkChannel(Http2Channel channel) {
/* 47 */     super(channel, 0);
/* 48 */     this.settings = null;
/*    */   }
/*    */ 
/*    */   
/*    */   protected SendFrameHeader createFrameHeaderImpl() {
/* 53 */     PooledByteBuffer pooled = ((Http2Channel)getChannel()).getBufferPool().allocate();
/* 54 */     ByteBuffer currentBuffer = pooled.getBuffer();
/* 55 */     if (this.settings != null) {
/* 56 */       int size = this.settings.size() * 6;
/* 57 */       currentBuffer.put((byte)(size >> 16 & 0xFF));
/* 58 */       currentBuffer.put((byte)(size >> 8 & 0xFF));
/* 59 */       currentBuffer.put((byte)(size & 0xFF));
/* 60 */       currentBuffer.put((byte)4);
/* 61 */       currentBuffer.put((byte)0);
/* 62 */       Http2ProtocolUtils.putInt(currentBuffer, getStreamId());
/* 63 */       for (Http2Setting setting : this.settings) {
/* 64 */         currentBuffer.put((byte)(setting.getId() >> 8 & 0xFF));
/* 65 */         currentBuffer.put((byte)(setting.getId() & 0xFF));
/*    */         
/* 67 */         currentBuffer.put((byte)(int)(setting.getValue() >> 24L & 0xFFL));
/* 68 */         currentBuffer.put((byte)(int)(setting.getValue() >> 16L & 0xFFL));
/* 69 */         currentBuffer.put((byte)(int)(setting.getValue() >> 8L & 0xFFL));
/* 70 */         currentBuffer.put((byte)(int)(setting.getValue() & 0xFFL));
/*    */       } 
/*    */     } else {
/*    */       
/* 74 */       currentBuffer.put((byte)0);
/* 75 */       currentBuffer.put((byte)0);
/* 76 */       currentBuffer.put((byte)0);
/* 77 */       currentBuffer.put((byte)4);
/* 78 */       currentBuffer.put((byte)1);
/* 79 */       Http2ProtocolUtils.putInt(currentBuffer, getStreamId());
/*    */     } 
/* 81 */     currentBuffer.flip();
/* 82 */     return new SendFrameHeader(pooled);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2SettingsStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */