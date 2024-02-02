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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BytesReceivedStreamSourceConduit
/*    */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*    */ {
/*    */   private final ByteActivityCallback callback;
/*    */   
/*    */   public BytesReceivedStreamSourceConduit(StreamSourceConduit next, ByteActivityCallback callback) {
/* 43 */     super(next);
/* 44 */     this.callback = callback;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 49 */     long l = super.transferTo(position, count, target);
/* 50 */     if (l > 0L) {
/* 51 */       this.callback.activity(l);
/*    */     }
/* 53 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 58 */     long l = super.transferTo(count, throughBuffer, target);
/* 59 */     if (l > 0L) {
/* 60 */       this.callback.activity(l);
/*    */     }
/* 62 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 67 */     int i = super.read(dst);
/* 68 */     if (i > 0) {
/* 69 */       this.callback.activity(i);
/*    */     }
/* 71 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 76 */     long l = super.read(dsts, offs, len);
/* 77 */     if (l > 0L) {
/* 78 */       this.callback.activity(l);
/*    */     }
/* 80 */     return l;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\BytesReceivedStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */