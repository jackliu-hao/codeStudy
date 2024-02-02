/*    */ package io.undertow.protocols.http2;
/*    */ 
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
/*    */ 
/*    */ public class Http2HeadersStreamSinkChannel
/*    */   extends Http2DataStreamSinkChannel
/*    */ {
/*    */   public Http2HeadersStreamSinkChannel(Http2Channel channel, int streamId) {
/* 32 */     super(channel, streamId, 1);
/*    */   }
/*    */   
/*    */   public Http2HeadersStreamSinkChannel(Http2Channel channel, int streamId, HeaderMap headers) {
/* 36 */     super(channel, streamId, headers, 1);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2HeadersStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */