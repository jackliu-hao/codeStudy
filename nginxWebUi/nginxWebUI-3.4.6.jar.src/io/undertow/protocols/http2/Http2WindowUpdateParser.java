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
/*    */ class Http2WindowUpdateParser
/*    */   extends Http2PushBackParser
/*    */ {
/*    */   private int deltaWindowSize;
/*    */   
/*    */   Http2WindowUpdateParser(int frameLength) {
/* 33 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
/* 38 */     if (resource.remaining() < 4) {
/*    */       return;
/*    */     }
/* 41 */     this.deltaWindowSize = Http2ProtocolUtils.readInt(resource);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getDeltaWindowSize() {
/* 46 */     return this.deltaWindowSize;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2WindowUpdateParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */