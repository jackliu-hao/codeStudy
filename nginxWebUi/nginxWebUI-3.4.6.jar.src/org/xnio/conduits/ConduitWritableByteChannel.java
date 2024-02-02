/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.WritableByteChannel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class ConduitWritableByteChannel
/*    */   implements WritableByteChannel
/*    */ {
/*    */   private StreamSinkConduit conduit;
/*    */   
/*    */   public ConduitWritableByteChannel(StreamSinkConduit conduit) {
/* 40 */     this.conduit = conduit;
/*    */   }
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 44 */     return this.conduit.write(src);
/*    */   }
/*    */   
/*    */   public boolean isOpen() {
/* 48 */     return !this.conduit.isWriteShutdown();
/*    */   }
/*    */   
/*    */   public void close() throws IOException {
/* 52 */     this.conduit.truncateWrites();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitWritableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */