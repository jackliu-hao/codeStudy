/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import org.xnio.channels.StreamSinkChannel;
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
/*    */ public abstract class AbstractStreamSourceConduit<D extends StreamSourceConduit>
/*    */   extends AbstractSourceConduit<D>
/*    */   implements StreamSourceConduit
/*    */ {
/*    */   protected AbstractStreamSourceConduit(D next) {
/* 39 */     super(next);
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 43 */     return ((StreamSourceConduit)this.next).transferTo(position, count, target);
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 47 */     return ((StreamSourceConduit)this.next).transferTo(count, throughBuffer, target);
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 51 */     return ((StreamSourceConduit)this.next).read(dst);
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 55 */     return ((StreamSourceConduit)this.next).read(dsts, offs, len);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\AbstractStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */