/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.UndertowMessages;
/*    */ import java.io.IOException;
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
/*    */ class Http2PingParser
/*    */   extends Http2PushBackParser
/*    */ {
/* 35 */   final byte[] data = new byte[8];
/*    */   
/*    */   Http2PingParser(int frameLength) {
/* 38 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser parser) throws IOException {
/* 43 */     if (parser.length != 8) {
/* 44 */       throw new IOException(UndertowMessages.MESSAGES.httpPingDataMustBeLength8());
/*    */     }
/* 46 */     if (parser.streamId != 0) {
/* 47 */       throw new IOException(UndertowMessages.MESSAGES.streamIdMustBeZeroForFrameType(6));
/*    */     }
/* 49 */     if (resource.remaining() < 8) {
/*    */       return;
/*    */     }
/* 52 */     resource.get(this.data);
/*    */   }
/*    */   
/*    */   byte[] getData() {
/* 56 */     return this.data;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PingParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */