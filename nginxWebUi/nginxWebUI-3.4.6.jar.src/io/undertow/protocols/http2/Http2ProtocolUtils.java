/*    */ package io.undertow.protocols.http2;
/*    */ 
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
/*    */ class Http2ProtocolUtils
/*    */ {
/*    */   public static void putInt(ByteBuffer buffer, int value) {
/* 29 */     buffer.put((byte)(value >> 24));
/* 30 */     buffer.put((byte)(value >> 16));
/* 31 */     buffer.put((byte)(value >> 8));
/* 32 */     buffer.put((byte)value);
/*    */   }
/*    */   
/*    */   public static void putInt(ByteBuffer buffer, int value, int position) {
/* 36 */     buffer.put(position, (byte)(value >> 24));
/* 37 */     buffer.put(position + 1, (byte)(value >> 16));
/* 38 */     buffer.put(position + 2, (byte)(value >> 8));
/* 39 */     buffer.put(position + 3, (byte)value);
/*    */   }
/*    */   
/*    */   public static int readInt(ByteBuffer buffer) {
/* 43 */     int id = (buffer.get() & 0xFF) << 24;
/* 44 */     id += (buffer.get() & 0xFF) << 16;
/* 45 */     id += (buffer.get() & 0xFF) << 8;
/* 46 */     id += buffer.get() & 0xFF;
/* 47 */     return id;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2ProtocolUtils.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */