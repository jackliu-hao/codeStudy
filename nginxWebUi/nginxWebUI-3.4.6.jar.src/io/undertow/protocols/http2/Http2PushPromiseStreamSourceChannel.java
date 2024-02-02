/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
/*    */ import io.undertow.util.HeaderMap;
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
/*    */ public class Http2PushPromiseStreamSourceChannel
/*    */   extends AbstractHttp2StreamSourceChannel
/*    */ {
/*    */   private final HeaderMap headers;
/*    */   private final int pushedStreamId;
/*    */   private final int associatedStreamId;
/*    */   
/*    */   Http2PushPromiseStreamSourceChannel(Http2Channel framedChannel, PooledByteBuffer data, long frameDataRemaining, HeaderMap headers, int pushedStreamId, int associatedStreamId) {
/* 36 */     super(framedChannel, data, frameDataRemaining);
/* 37 */     this.headers = headers;
/* 38 */     this.pushedStreamId = pushedStreamId;
/* 39 */     this.associatedStreamId = associatedStreamId;
/* 40 */     lastFrame();
/*    */   }
/*    */   
/*    */   public HeaderMap getHeaders() {
/* 44 */     return this.headers;
/*    */   }
/*    */   
/*    */   public int getPushedStreamId() {
/* 48 */     return this.pushedStreamId;
/*    */   }
/*    */   
/*    */   public int getAssociatedStreamId() {
/* 52 */     return this.associatedStreamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PushPromiseStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */