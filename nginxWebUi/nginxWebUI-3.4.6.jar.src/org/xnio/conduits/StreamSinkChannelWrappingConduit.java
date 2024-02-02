/*     */ package org.xnio.conduits;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class StreamSinkChannelWrappingConduit
/*     */   implements StreamSinkConduit
/*     */ {
/*     */   private final StreamSinkChannel channel;
/*     */   
/*     */   public StreamSinkChannelWrappingConduit(StreamSinkChannel channel) {
/*  45 */     this.channel = channel;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  49 */     return this.channel.transferFrom(src, position, count);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  53 */     return this.channel.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/*  57 */     return this.channel.write(src);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offs, int len) throws IOException {
/*  61 */     return this.channel.write(srcs, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/*  66 */     return this.channel.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/*  71 */     return this.channel.writeFinal(srcs, offset, length);
/*     */   }
/*     */   
/*     */   public void terminateWrites() throws IOException {
/*  75 */     this.channel.shutdownWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteShutdown() {
/*  79 */     return !this.channel.isOpen();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/*  83 */     this.channel.resumeWrites();
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/*  87 */     this.channel.suspendWrites();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/*  91 */     this.channel.wakeupWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/*  95 */     return this.channel.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/*  99 */     this.channel.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 103 */     this.channel.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 107 */     return this.channel.getIoThread();
/*     */   }
/*     */   
/*     */   public void setWriteReadyHandler(final WriteReadyHandler handler) {
/* 111 */     this.channel.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
/*     */           public void handleEvent(StreamSinkChannel channel) {
/* 113 */             handler.writeReady();
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 119 */     this.channel.close();
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 123 */     return this.channel.flush();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 127 */     return this.channel.getWorker();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\StreamSinkChannelWrappingConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */