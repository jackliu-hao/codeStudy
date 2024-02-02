/*     */ package io.undertow.websockets.core.function;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
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
/*     */ public class ChannelFunctionStreamSourceChannel
/*     */   implements StreamSourceChannel
/*     */ {
/*     */   private final StreamSourceChannel channel;
/*     */   private final ChannelFunction[] functions;
/*     */   
/*     */   public ChannelFunctionStreamSourceChannel(StreamSourceChannel channel, ChannelFunction... functions) {
/*  41 */     this.channel = channel;
/*  42 */     this.functions = functions;
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  47 */     return this.channel.transferTo(position, count, new ChannelFunctionFileChannel(target, this.functions));
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  52 */     return target.transferFrom(this, count, throughBuffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
/*  57 */     return this.channel.getReadSetter();
/*     */   }
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
/*  62 */     return this.channel.getCloseSetter();
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*  67 */     long r = 0L;
/*  68 */     for (int a = offset; a < length; a++) {
/*  69 */       int i = read(dsts[a]);
/*  70 */       if (i < 1) {
/*     */         break;
/*     */       }
/*  73 */       r += i;
/*     */     } 
/*  75 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/*  80 */     long r = 0L;
/*  81 */     for (ByteBuffer buf : dsts) {
/*  82 */       int i = read(buf);
/*  83 */       if (i < 1) {
/*     */         break;
/*     */       }
/*  86 */       r += i;
/*     */     } 
/*  88 */     return r;
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/*  93 */     this.channel.suspendReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/*  98 */     this.channel.resumeReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadResumed() {
/* 103 */     return this.channel.isReadResumed();
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/* 108 */     this.channel.wakeupReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 113 */     this.channel.shutdownReads();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 118 */     this.channel.awaitReadable();
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 123 */     this.channel.awaitReadable(time, timeUnit);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioExecutor getReadThread() {
/* 128 */     return this.channel.getReadThread();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 134 */     int position = dst.position();
/* 135 */     int r = this.channel.read(dst);
/* 136 */     if (r > 0) {
/* 137 */       afterReading(dst, position, r);
/*     */     }
/* 139 */     return r;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 145 */     return this.channel.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 150 */     return this.channel.getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 155 */     return this.channel.supportsOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 160 */     return (T)this.channel.getOption(option);
/*     */   }
/*     */ 
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IOException {
/* 165 */     return (T)this.channel.setOption(option, value);
/*     */   }
/*     */   
/*     */   private void afterReading(ByteBuffer buffer, int position, int length) throws IOException {
/* 169 */     for (ChannelFunction func : this.functions) {
/* 170 */       func.afterRead(buffer, position, length);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isOpen() {
/* 176 */     return this.channel.isOpen();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() throws IOException {
/* 181 */     this.channel.close();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\websockets\core\function\ChannelFunctionStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */