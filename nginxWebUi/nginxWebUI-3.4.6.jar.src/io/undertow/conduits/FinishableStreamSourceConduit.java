/*    */ package io.undertow.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.xnio.channels.StreamSinkChannel;
/*    */ import org.xnio.conduits.AbstractStreamSourceConduit;
/*    */ import org.xnio.conduits.StreamSourceConduit;
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
/*    */ public final class FinishableStreamSourceConduit
/*    */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*    */ {
/*    */   private final ConduitListener<? super FinishableStreamSourceConduit> finishListener;
/*    */   private boolean finishCalled = false;
/*    */   
/*    */   public FinishableStreamSourceConduit(StreamSourceConduit next, ConduitListener<? super FinishableStreamSourceConduit> finishListener) {
/* 41 */     super(next);
/* 42 */     this.finishListener = finishListener;
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 46 */     long res = 0L;
/*    */     try {
/* 48 */       return res = ((StreamSourceConduit)this.next).transferTo(position, count, target);
/*    */     } finally {
/* 50 */       exitRead(res);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 55 */     long res = 0L;
/*    */     try {
/* 57 */       return res = ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
/*    */     } finally {
/* 59 */       exitRead(res);
/*    */     } 
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 64 */     long res = 0L;
/*    */     try {
/* 66 */       return res = ((StreamSourceConduit)this.next).read(dsts, offset, length);
/*    */     } finally {
/* 68 */       exitRead(res);
/*    */     } 
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 73 */     int res = 0;
/*    */     try {
/* 75 */       return res = ((StreamSourceConduit)this.next).read(dst);
/*    */     } finally {
/* 77 */       exitRead(res);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private void exitRead(long consumed) {
/* 87 */     if (consumed == -1L && 
/* 88 */       !this.finishCalled) {
/* 89 */       this.finishCalled = true;
/* 90 */       this.finishListener.handleEvent(this);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\FinishableStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */