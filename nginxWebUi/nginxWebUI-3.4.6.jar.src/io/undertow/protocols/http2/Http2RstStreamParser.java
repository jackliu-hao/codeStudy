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
/*    */ class Http2RstStreamParser
/*    */   extends Http2PushBackParser
/*    */ {
/*    */   private int errorCode;
/*    */   
/*    */   Http2RstStreamParser(int frameLength) {
/* 33 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
/* 38 */     if (resource.remaining() < 4) {
/*    */       return;
/*    */     }
/* 41 */     this.errorCode = Http2ProtocolUtils.readInt(resource);
/*    */   }
/*    */ 
/*    */   
/*    */   public int getErrorCode() {
/* 46 */     return this.errorCode;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2RstStreamParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */