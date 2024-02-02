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
/*    */ class Http2DataFrameParser
/*    */   extends Http2PushBackParser
/*    */ {
/* 31 */   private int padding = 0;
/*    */   
/*    */   Http2DataFrameParser(int frameLength) {
/* 34 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser headerParser) throws ConnectionErrorException {
/* 39 */     if (Bits.anyAreClear(headerParser.flags, 8)) {
/* 40 */       finish();
/*    */       return;
/*    */     } 
/* 43 */     if (headerParser.length == 0)
/*    */     {
/*    */       
/* 46 */       throw new ConnectionErrorException(1);
/*    */     }
/* 48 */     if (resource.remaining() > 0) {
/* 49 */       this.padding = resource.get() & 0xFF;
/* 50 */       headerParser.length--;
/* 51 */       if (this.padding > headerParser.length) {
/* 52 */         throw new ConnectionErrorException(1);
/*    */       }
/* 54 */       finish();
/*    */     } 
/*    */   }
/*    */   
/*    */   int getPadding() {
/* 59 */     return this.padding;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2DataFrameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */