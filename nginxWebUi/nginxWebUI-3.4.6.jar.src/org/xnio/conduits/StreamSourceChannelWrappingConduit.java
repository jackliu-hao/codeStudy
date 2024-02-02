/*    */ package org.xnio.conduits;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.nio.ByteBuffer;
/*    */ import java.nio.channels.Channel;
/*    */ import java.nio.channels.FileChannel;
/*    */ import java.util.concurrent.TimeUnit;
/*    */ import org.xnio.ChannelListener;
/*    */ import org.xnio.XnioIoThread;
/*    */ import org.xnio.XnioWorker;
/*    */ import org.xnio.channels.StreamSinkChannel;
/*    */ import org.xnio.channels.StreamSourceChannel;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class StreamSourceChannelWrappingConduit
/*    */   implements StreamSourceConduit
/*    */ {
/*    */   private final StreamSourceChannel channel;
/*    */   
/*    */   public StreamSourceChannelWrappingConduit(StreamSourceChannel channel) {
/* 27 */     this.channel = channel;
/*    */   }
/*    */   
/*    */   public void terminateReads() throws IOException {
/* 31 */     this.channel.shutdownReads();
/*    */   }
/*    */   
/*    */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 35 */     return this.channel.transferTo(position, count, target);
/*    */   }
/*    */   
/*    */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 39 */     return this.channel.transferTo(count, throughBuffer, target);
/*    */   }
/*    */   
/*    */   public int read(ByteBuffer dst) throws IOException {
/* 43 */     return this.channel.read(dst);
/*    */   }
/*    */   
/*    */   public long read(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 47 */     return this.channel.read(dsts, offs, len);
/*    */   }
/*    */   
/*    */   public boolean isReadShutdown() {
/* 51 */     return !this.channel.isOpen();
/*    */   }
/*    */   
/*    */   public void resumeReads() {
/* 55 */     this.channel.resumeReads();
/*    */   }
/*    */   
/*    */   public void suspendReads() {
/* 59 */     this.channel.suspendReads();
/*    */   }
/*    */   
/*    */   public void wakeupReads() {
/* 63 */     this.channel.wakeupReads();
/*    */   }
/*    */   
/*    */   public boolean isReadResumed() {
/* 67 */     return this.channel.isReadResumed();
/*    */   }
/*    */   
/*    */   public void awaitReadable() throws IOException {
/* 71 */     this.channel.awaitReadable();
/*    */   }
/*    */   
/*    */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 75 */     this.channel.awaitReadable(time, timeUnit);
/*    */   }
/*    */   
/*    */   public XnioIoThread getReadThread() {
/* 79 */     return this.channel.getIoThread();
/*    */   }
/*    */   
/*    */   public void setReadReadyHandler(final ReadReadyHandler handler) {
/* 83 */     this.channel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
/*    */           public void handleEvent(StreamSourceChannel channel) {
/* 85 */             handler.readReady();
/*    */           }
/*    */         });
/*    */   }
/*    */   
/*    */   public XnioWorker getWorker() {
/* 91 */     return this.channel.getWorker();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\StreamSourceChannelWrappingConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */