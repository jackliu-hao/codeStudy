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
/*    */ 
/*    */ class Http2DiscardParser
/*    */   extends Http2PushBackParser
/*    */ {
/*    */   int remaining;
/*    */   
/*    */   Http2DiscardParser(int frameLength) {
/* 33 */     super(frameLength);
/* 34 */     this.remaining = frameLength;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
/* 39 */     int toUse = Math.min(resource.remaining(), this.remaining);
/* 40 */     this.remaining -= toUse;
/* 41 */     resource.position(resource.position() + toUse);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2DiscardParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */