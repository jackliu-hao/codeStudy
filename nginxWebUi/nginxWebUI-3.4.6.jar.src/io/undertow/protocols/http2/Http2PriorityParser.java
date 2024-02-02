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
/*    */ class Http2PriorityParser
/*    */   extends Http2PushBackParser
/*    */ {
/*    */   private int streamDependency;
/*    */   private int weight;
/*    */   private boolean exclusive;
/*    */   
/*    */   Http2PriorityParser(int frameLength) {
/* 37 */     super(frameLength);
/*    */   }
/*    */ 
/*    */   
/*    */   protected void handleData(ByteBuffer resource, Http2FrameHeaderParser frameHeaderParser) {
/* 42 */     if (resource.remaining() < 5) {
/*    */       return;
/*    */     }
/* 45 */     int read = Http2ProtocolUtils.readInt(resource);
/* 46 */     if (Bits.anyAreSet(read, -2147483648)) {
/* 47 */       this.exclusive = true;
/* 48 */       this.streamDependency = read & Integer.MAX_VALUE;
/*    */     } else {
/* 50 */       this.exclusive = false;
/* 51 */       this.streamDependency = read;
/*    */     } 
/* 53 */     this.weight = resource.get();
/*    */   }
/*    */   
/*    */   public int getWeight() {
/* 57 */     return this.weight;
/*    */   }
/*    */   
/*    */   public int getStreamDependency() {
/* 61 */     return this.streamDependency;
/*    */   }
/*    */   
/*    */   public boolean isExclusive() {
/* 65 */     return this.exclusive;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\http2\Http2PriorityParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */