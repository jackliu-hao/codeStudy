/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
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
/*    */ public class Http2GoAwayStreamSourceChannel
/*    */   extends AbstractHttp2StreamSourceChannel
/*    */ {
/*    */   private final int status;
/*    */   private final int lastGoodStreamId;
/*    */   
/*    */   Http2GoAwayStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, int status, int lastGoodStreamId) {
/* 34 */     super(framedChannel, data, frameDataRemaining);
/* 35 */     this.status = status;
/* 36 */     this.lastGoodStreamId = lastGoodStreamId;
/* 37 */     lastFrame();
/*    */   }
/*    */   
/*    */   public int getStatus() {
/* 41 */     return this.status;
/*    */   }
/*    */   
/*    */   public int getLastGoodStreamId() {
/* 45 */     return this.lastGoodStreamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2GoAwayStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */