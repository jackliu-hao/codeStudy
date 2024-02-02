/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import io.undertow.util.HeaderMap;
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
/*    */ public class Http2PushPromiseStreamSinkChannel
/*    */   extends Http2DataStreamSinkChannel
/*    */ {
/*    */   private final int pushedStreamId;
/*    */   
/*    */   Http2PushPromiseStreamSinkChannel(Http2Channel channel, HeaderMap requestHeaders, int associatedStreamId, int pushedStreamId) {
/* 35 */     super(channel, associatedStreamId, requestHeaders, 5);
/* 36 */     this.pushedStreamId = pushedStreamId;
/*    */   }
/*    */ 
/*    */   
/*    */   protected void writeBeforeHeaderBlock(ByteBuffer buffer) {
/* 41 */     buffer.put((byte)(this.pushedStreamId >> 24 & 0xFF));
/* 42 */     buffer.put((byte)(this.pushedStreamId >> 16 & 0xFF));
/* 43 */     buffer.put((byte)(this.pushedStreamId >> 8 & 0xFF));
/* 44 */     buffer.put((byte)(this.pushedStreamId & 0xFF));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int grabFlowControlBytes(int bytes) {
/* 53 */     return bytes;
/*    */   }
/*    */   
/*    */   public int getPushedStreamId() {
/* 57 */     return this.pushedStreamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PushPromiseStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */