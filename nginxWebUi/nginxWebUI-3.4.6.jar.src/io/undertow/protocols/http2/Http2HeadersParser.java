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
/*    */ class Http2HeadersParser
/*    */   extends Http2HeaderBlockParser
/*    */ {
/*    */   private static final int DEPENDENCY_MASK = -129;
/* 32 */   private int paddingLength = 0;
/* 33 */   private int dependentStreamId = 0;
/* 34 */   private int weight = 16;
/*    */   private boolean headersEndStream = false;
/*    */   private boolean exclusive;
/*    */   
/*    */   Http2HeadersParser(int frameLength, HpackDecoder hpackDecoder, boolean client, int maxHeaders, int streamId, int maxHeaderListSize) {
/* 39 */     super(frameLength, hpackDecoder, client, maxHeaders, streamId, maxHeaderListSize);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean handleBeforeHeader(ByteBuffer resource, Http2FrameHeaderParser headerParser) {
/* 44 */     boolean hasPadding = Bits.anyAreSet(headerParser.flags, 8);
/* 45 */     boolean hasPriority = Bits.anyAreSet(headerParser.flags, 32);
/* 46 */     this.headersEndStream = Bits.allAreSet(headerParser.flags, 1);
/* 47 */     int reqLength = (hasPadding ? 1 : 0) + (hasPriority ? 5 : 0);
/* 48 */     if (reqLength == 0) {
/* 49 */       return true;
/*    */     }
/* 51 */     if (resource.remaining() < reqLength) {
/* 52 */       return false;
/*    */     }
/* 54 */     if (hasPadding) {
/* 55 */       this.paddingLength = resource.get() & 0xFF;
/*    */     }
/* 57 */     if (hasPriority) {
/* 58 */       if (resource.remaining() < 4) {
/* 59 */         return false;
/*    */       }
/* 61 */       byte b = resource.get();
/* 62 */       this.exclusive = ((b & 0x80) != 0);
/* 63 */       this.dependentStreamId = (b & 0xFFFFFF7F & 0xFF) << 24;
/* 64 */       this.dependentStreamId += (resource.get() & 0xFF) << 16;
/* 65 */       this.dependentStreamId += (resource.get() & 0xFF) << 8;
/* 66 */       this.dependentStreamId += resource.get() & 0xFF;
/* 67 */       this.weight = resource.get() & 0xFF;
/*    */     } 
/* 69 */     return true;
/*    */   }
/*    */   
/*    */   protected int getPaddingLength() {
/* 73 */     return this.paddingLength;
/*    */   }
/*    */   
/*    */   int getDependentStreamId() {
/* 77 */     return this.dependentStreamId;
/*    */   }
/*    */   
/*    */   int getWeight() {
/* 81 */     return this.weight;
/*    */   }
/*    */   
/*    */   boolean isHeadersEndStream() {
/* 85 */     return this.headersEndStream;
/*    */   }
/*    */   
/*    */   public boolean isExclusive() {
/* 89 */     return this.exclusive;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2HeadersParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */