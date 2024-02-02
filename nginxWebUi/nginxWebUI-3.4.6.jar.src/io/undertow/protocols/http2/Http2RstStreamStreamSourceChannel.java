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
/*    */ public class Http2RstStreamStreamSourceChannel
/*    */   extends AbstractHttp2StreamSourceChannel
/*    */ {
/*    */   private final int errorCode;
/*    */   private final int streamId;
/*    */   
/*    */   Http2RstStreamStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, int errorCode, int streamId) {
/* 34 */     super(framedChannel, data, 0L);
/* 35 */     this.errorCode = errorCode;
/* 36 */     this.streamId = streamId;
/* 37 */     lastFrame();
/*    */   }
/*    */   
/*    */   public int getErrorCode() {
/* 41 */     return this.errorCode;
/*    */   }
/*    */   
/*    */   public int getStreamId() {
/* 45 */     return this.streamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2RstStreamStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */