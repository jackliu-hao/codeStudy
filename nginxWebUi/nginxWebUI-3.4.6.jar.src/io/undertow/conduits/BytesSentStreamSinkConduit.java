/*    */ package io.undertow.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.xnio.channels.StreamSourceChannel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BytesSentStreamSinkConduit
/*    */   extends AbstractStreamSinkConduit<StreamSinkConduit>
/*    */ {
/*    */   private final ByteActivityCallback callback;
/*    */   
/*    */   public BytesSentStreamSinkConduit(StreamSinkConduit next, ByteActivityCallback callback) {
/* 43 */     super(next);
/* 44 */     this.callback = callback;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 49 */     long l = ((StreamSinkConduit)this.next).transferFrom(src, position, count);
/* 50 */     if (l > 0L) {
/* 51 */       this.callback.activity(l);
/*    */     }
/* 53 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 58 */     long l = ((StreamSinkConduit)this.next).transferFrom(source, count, throughBuffer);
/* 59 */     if (l > 0L) {
/* 60 */       this.callback.activity(l);
/*    */     }
/* 62 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 67 */     int i = ((StreamSinkConduit)this.next).write(src);
/* 68 */     if (i > 0) {
/* 69 */       this.callback.activity(i);
/*    */     }
/* 71 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 76 */     long l = ((StreamSinkConduit)this.next).write(srcs, offs, len);
/* 77 */     if (l > 0L) {
/* 78 */       this.callback.activity(l);
/*    */     }
/* 80 */     return l;
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeFinal(ByteBuffer src) throws IOException {
/* 85 */     int i = ((StreamSinkConduit)this.next).writeFinal(src);
/* 86 */     if (i > 0) {
/* 87 */       this.callback.activity(i);
/*    */     }
/* 89 */     return i;
/*    */   }
/*    */ 
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 94 */     long l = ((StreamSinkConduit)this.next).writeFinal(srcs, offset, length);
/* 95 */     if (l > 0L) {
/* 96 */       this.callback.activity(l);
/*    */     }
/* 98 */     return l;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\BytesSentStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */