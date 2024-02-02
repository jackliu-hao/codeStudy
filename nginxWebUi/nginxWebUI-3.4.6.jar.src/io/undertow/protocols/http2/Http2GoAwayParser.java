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
/*    */ public class Http2GoAwayParser
/*    */   extends Http2PushBackParser
/*    */ {
/*    */   private int statusCode;
/*    */   private int lastGoodStreamId;
/*    */   
/*    */   public Http2GoAwayParser(int frameLength) {
/* 34 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
/* 39 */     if (resource.remaining() < 8) {
/*    */       return;
/*    */     }
/* 42 */     this.lastGoodStreamId = Http2ProtocolUtils.readInt(resource);
/* 43 */     this.statusCode = Http2ProtocolUtils.readInt(resource);
/*    */   }
/*    */   
/*    */   public int getStatusCode() {
/* 47 */     return this.statusCode;
/*    */   }
/*    */   
/*    */   public int getLastGoodStreamId() {
/* 51 */     return this.lastGoodStreamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2GoAwayParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */