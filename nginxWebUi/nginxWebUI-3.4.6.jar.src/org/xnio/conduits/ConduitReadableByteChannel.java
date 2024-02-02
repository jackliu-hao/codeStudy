/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.ReadableByteChannel;
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
/*    */ public final class ConduitReadableByteChannel
/*    */   implements ReadableByteChannel
/*    */ {
/*    */   private StreamSourceConduit conduit;
/*    */   
/*    */   public ConduitReadableByteChannel(StreamSourceConduit conduit) {
/* 40 */     this.conduit = conduit;
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 44 */     return this.conduit.read(dst);
/*    */   }
/*    */   
/*    */   public boolean isOpen() {
/* 48 */     return !this.conduit.isReadShutdown();
/*    */   }
/*    */   
/*    */   public void close() throws IOException {
/* 52 */     this.conduit.terminateReads();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitReadableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */