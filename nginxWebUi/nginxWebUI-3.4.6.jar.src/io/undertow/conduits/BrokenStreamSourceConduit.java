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
/*    */ public class BrokenStreamSourceConduit
/*    */   extends AbstractStreamSourceConduit<StreamSourceConduit>
/*    */ {
/*    */   private final IOException exception;
/*    */   
/*    */   public BrokenStreamSourceConduit(StreamSourceConduit next, IOException exception) {
/* 43 */     super(next);
/* 44 */     this.exception = exception;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 49 */     throw this.exception;
/*    */   }
/*    */ 
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 54 */     throw this.exception;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 60 */     throw this.exception;
/*    */   }
/*    */ 
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 65 */     throw this.exception;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\BrokenStreamSourceConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */