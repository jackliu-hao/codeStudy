/*    */ package io.undertow.server.protocol.framed;
/*    */ 
/*    */ import io.undertow.connector.PooledByteBuffer;
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
/*    */ public class SendFrameHeader
/*    */ {
/*    */   private final int reminingInBuffer;
/*    */   private final PooledByteBuffer byteBuffer;
/*    */   private final boolean anotherFrameRequired;
/*    */   private final ByteBuffer trailer;
/*    */   
/*    */   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer, boolean anotherFrameRequired) {
/* 36 */     this(reminingInBuffer, byteBuffer, anotherFrameRequired, null);
/*    */   }
/*    */   
/*    */   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer, boolean anotherFrameRequired, ByteBuffer trailer) {
/* 40 */     this.byteBuffer = byteBuffer;
/* 41 */     this.reminingInBuffer = reminingInBuffer;
/* 42 */     this.anotherFrameRequired = anotherFrameRequired;
/* 43 */     this.trailer = trailer;
/*    */   }
/*    */   
/*    */   public SendFrameHeader(int reminingInBuffer, PooledByteBuffer byteBuffer) {
/* 47 */     this.byteBuffer = byteBuffer;
/* 48 */     this.reminingInBuffer = reminingInBuffer;
/* 49 */     this.anotherFrameRequired = false;
/* 50 */     this.trailer = null;
/*    */   }
/*    */   
/*    */   public SendFrameHeader(PooledByteBuffer byteBuffer) {
/* 54 */     this.byteBuffer = byteBuffer;
/* 55 */     this.reminingInBuffer = 0;
/* 56 */     this.anotherFrameRequired = false;
/* 57 */     this.trailer = null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PooledByteBuffer getByteBuffer() {
/* 65 */     return this.byteBuffer;
/*    */   }
/*    */   
/*    */   public ByteBuffer getTrailer() {
/* 69 */     return this.trailer;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getRemainingInBuffer() {
/* 77 */     return this.reminingInBuffer;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isAnotherFrameRequired() {
/* 86 */     return this.anotherFrameRequired;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\SendFrameHeader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */