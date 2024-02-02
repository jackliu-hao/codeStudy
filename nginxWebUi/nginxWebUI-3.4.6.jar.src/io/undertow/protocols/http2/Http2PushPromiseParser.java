/*    */ package io.undertow.protocols.http2;
/*    */ 
/*    */ import java.nio.ByteBuffer;
/*    */ import org.xnio.Bits;
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
/*    */ class Http2PushPromiseParser
/*    */   extends Http2HeaderBlockParser
/*    */ {
/* 32 */   private int paddingLength = 0;
/*    */   private int promisedStreamId;
/*    */   private static final int STREAM_MASK = -129;
/*    */   
/*    */   Http2PushPromiseParser(int frameLength, HpackDecoder hpackDecoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
/* 37 */     super(frameLength, hpackDecoder, client, maxHeaders, streamId, maxHeaderListSize);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean handleBeforeHeader(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
/* 42 */     boolean hasPadding = Bits.anyAreSet(headerParser.flags, 8);
/* 43 */     int reqLength = (hasPadding ? 1 : 0) + 4;
/* 44 */     if (resource.remaining() < reqLength) {
/* 45 */       return false;
/*    */     }
/* 47 */     if (hasPadding) {
/* 48 */       this.paddingLength = resource.get() & 0xFF;
/*    */     }
/* 50 */     this.promisedStreamId = (resource.get() & 0xFFFFFF7F) << 24;
/* 51 */     this.promisedStreamId += (resource.get() & 0xFF) << 16;
/* 52 */     this.promisedStreamId += (resource.get() & 0xFF) << 8;
/* 53 */     this.promisedStreamId += resource.get() & 0xFF;
/* 54 */     return true;
/*    */   }
/*    */   
/*    */   protected int getPaddingLength() {
/* 58 */     return this.paddingLength;
/*    */   }
/*    */   
/*    */   public int getPromisedStreamId() {
/* 62 */     return this.promisedStreamId;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PushPromiseParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */