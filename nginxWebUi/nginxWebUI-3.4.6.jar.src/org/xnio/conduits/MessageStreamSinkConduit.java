/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.Buffer;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.nio.channels.ReadableByteChannel;
/*    */ import org.xnio.Buffers;
/*    */ import org.xnio.channels.StreamSourceChannel;
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
/*    */ public final class MessageStreamSinkConduit
/*    */   extends AbstractSinkConduit<MessageSinkConduit>
/*    */   implements StreamSinkConduit
/*    */ {
/*    */   public MessageStreamSinkConduit(MessageSinkConduit next) {
/* 40 */     super(next);
/*    */   }
/*    */   
/*    */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 44 */     return src.transferTo(position, count, new ConduitWritableByteChannel(this));
/*    */   }
/*    */   
/*    */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 48 */     return Conduits.transfer((ReadableByteChannel)source, count, throughBuffer, this);
/*    */   }
/*    */   
/*    */   public int write(ByteBuffer src) throws IOException {
/* 52 */     int remaining = src.remaining();
/* 53 */     return this.next.send(src) ? remaining : 0;
/*    */   }
/*    */   
/*    */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/* 57 */     long remaining = Buffers.remaining((Buffer[])srcs, offs, len);
/* 58 */     return this.next.send(srcs, offs, len) ? remaining : 0L;
/*    */   }
/*    */ 
/*    */   
/*    */   public int writeFinal(ByteBuffer src) throws IOException {
/* 63 */     return Conduits.writeFinalBasic(this, src);
/*    */   }
/*    */ 
/*    */   
/*    */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 68 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\MessageStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */