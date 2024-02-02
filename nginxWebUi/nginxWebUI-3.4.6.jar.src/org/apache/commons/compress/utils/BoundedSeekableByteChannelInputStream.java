/*    */ package org.apache.commons.compress.utils;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.SeekableByteChannel;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class BoundedSeekableByteChannelInputStream
/*    */   extends BoundedArchiveInputStream
/*    */ {
/*    */   private final SeekableByteChannel channel;
/*    */   
/*    */   public BoundedSeekableByteChannelInputStream(long start, long remaining, SeekableByteChannel channel) {
/* 43 */     super(start, remaining);
/* 44 */     this.channel = channel;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int read(long pos, ByteBuffer buf) throws IOException {
/*    */     int read;
/* 50 */     synchronized (this.channel) {
/* 51 */       this.channel.position(pos);
/* 52 */       read = this.channel.read(buf);
/*    */     } 
/* 54 */     buf.flip();
/* 55 */     return read;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compres\\utils\BoundedSeekableByteChannelInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */