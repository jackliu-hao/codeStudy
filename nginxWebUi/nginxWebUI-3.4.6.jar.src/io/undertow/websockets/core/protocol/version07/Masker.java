/*    */ package io.undertow.websockets.core.protocol.version07;
/*    */ 
/*    */ import io.undertow.server.protocol.framed.FrameHeaderData;
/*    */ import io.undertow.websockets.core.function.ChannelFunction;
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
/*    */ public final class Masker
/*    */   implements ChannelFunction
/*    */ {
/*    */   private byte[] maskingKey;
/*    */   int m;
/*    */   
/*    */   Masker(int maskingKey) {
/* 34 */     this.maskingKey = createsMaskingKey(maskingKey);
/*    */   }
/*    */   
/*    */   public void setMaskingKey(int maskingKey) {
/* 38 */     this.maskingKey = createsMaskingKey(maskingKey);
/* 39 */     this.m = 0;
/*    */   }
/*    */   
/*    */   private static byte[] createsMaskingKey(int maskingKey) {
/* 43 */     byte[] key = new byte[4];
/* 44 */     key[0] = (byte)(maskingKey >> 24 & 0xFF);
/* 45 */     key[1] = (byte)(maskingKey >> 16 & 0xFF);
/* 46 */     key[2] = (byte)(maskingKey >> 8 & 0xFF);
/* 47 */     key[3] = (byte)(maskingKey & 0xFF);
/* 48 */     return key;
/*    */   }
/*    */   
/*    */   private void mask(ByteBuffer buf, int position, int length) {
/* 52 */     int limit = position + length;
/* 53 */     for (int i = position; i < limit; i++) {
/* 54 */       buf.put(i, (byte)(buf.get(i) ^ this.maskingKey[this.m++]));
/* 55 */       this.m %= 4;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void newFrame(FrameHeaderData headerData) {
/* 61 */     WebSocket07Channel.WebSocketFrameHeader header = (WebSocket07Channel.WebSocketFrameHeader)headerData;
/* 62 */     setMaskingKey(header.getMaskingKey());
/*    */   }
/*    */ 
/*    */   
/*    */   public void afterRead(ByteBuffer buf, int position, int length) {
/* 67 */     mask(buf, position, length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void beforeWrite(ByteBuffer buf, int position, int length) {
/* 72 */     mask(buf, position, length);
/*    */   }
/*    */   
/*    */   public void complete() {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\protocol\version07\Masker.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */