/*    */ package io.undertow.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.Buffer;
/*    */ import java.nio.ByteBuffer;
/*    */ import org.xnio.Buffers;
/*    */ import org.xnio.conduits.AbstractStreamSinkConduit;
/*    */ import org.xnio.conduits.StreamSinkConduit;
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
/*    */ public final class FinishableStreamSinkConduit
/*    */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*    */ {
/*    */   private final ConduitListener<? super FinishableStreamSinkConduit> finishListener;
/* 37 */   private int shutdownState = 0;
/*    */   
/*    */   public FinishableStreamSinkConduit(StreamSinkConduit delegate, ConduitListener<? super FinishableStreamSinkConduit> finishListener) {
/* 40 */     super(delegate);
/* 41 */     this.finishListener = finishListener;
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeFinal(ByteBuffer src) throws IOException {
/* 46 */     int res = ((StreamSinkConduit)this.next).writeFinal(src);
/* 47 */     if (!src.hasRemaining() && 
/* 48 */       this.shutdownState == 0) {
/* 49 */       this.shutdownState = 1;
/*    */     }
/*    */     
/* 52 */     return res;
/*    */   }
/*    */ 
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 57 */     long res = ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
/* 58 */     if (!Buffers.hasRemaining((Buffer[])srcs, offset, length) && 
/* 59 */       this.shutdownState == 0) {
/* 60 */       this.shutdownState = 1;
/*    */     }
/*    */     
/* 63 */     return res;
/*    */   }
/*    */   
/*    */   public void terminateWrites() throws IOException {
/* 67 */     super.terminateWrites();
/* 68 */     if (this.shutdownState == 0) {
/* 69 */       this.shutdownState = 1;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public void truncateWrites() throws IOException {
/* 75 */     ((StreamSinkConduit)this.next).truncateWrites();
/* 76 */     if (this.shutdownState != 2) {
/* 77 */       this.shutdownState = 2;
/* 78 */       this.finishListener.handleEvent(this);
/*    */     } 
/*    */   }
/*    */   
/*    */   public boolean flush() throws IOException {
/* 83 */     boolean val = ((StreamSinkConduit)this.next).flush();
/* 84 */     if (val && this.shutdownState == 1) {
/* 85 */       this.shutdownState = 2;
/* 86 */       this.finishListener.handleEvent(this);
/*    */     } 
/* 88 */     return val;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\FinishableStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */