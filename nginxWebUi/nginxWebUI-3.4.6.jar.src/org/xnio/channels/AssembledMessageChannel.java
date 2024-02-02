/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio._private.Messages;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class AssembledMessageChannel
/*     */   implements MessageChannel
/*     */ {
/*     */   private final CloseableChannel closeable;
/*     */   private final ReadableMessageChannel readable;
/*     */   private final WritableMessageChannel writable;
/*     */   private final ChannelListener.Setter<AssembledMessageChannel> readSetter;
/*     */   private final ChannelListener.Setter<AssembledMessageChannel> writeSetter;
/*     */   private final ChannelListener.Setter<AssembledMessageChannel> closeSetter;
/*     */   
/*     */   public AssembledMessageChannel(CloseableChannel closeable, ReadableMessageChannel readable, WritableMessageChannel writable) {
/*  56 */     if (readable.getWorker() != writable.getWorker() || readable.getWorker() != closeable.getWorker()) {
/*  57 */       throw Messages.msg.differentWorkers();
/*     */     }
/*  59 */     this.closeable = closeable;
/*  60 */     this.readable = readable;
/*  61 */     this.writable = writable;
/*  62 */     this.readSetter = ChannelListeners.getDelegatingSetter(readable.getReadSetter(), this);
/*  63 */     this.writeSetter = ChannelListeners.getDelegatingSetter(writable.getWriteSetter(), this);
/*  64 */     this.closeSetter = ChannelListeners.getDelegatingSetter(closeable.getCloseSetter(), this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AssembledMessageChannel(ReadableMessageChannel readable, WritableMessageChannel writable) {
/*  74 */     this(new AssembledChannel(readable, writable), readable, writable);
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioIoThread getIoThread() {
/*  79 */     return this.readable.getIoThread();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledMessageChannel> getReadSetter() {
/*  85 */     return this.readSetter;
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  89 */     this.readable.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  93 */     this.readable.resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/*  97 */     return this.readable.isReadResumed();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 101 */     this.readable.wakeupReads();
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 105 */     this.readable.shutdownReads();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 109 */     this.readable.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 113 */     this.readable.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 118 */     return this.readable.getReadThread();
/*     */   }
/*     */   
/*     */   public int receive(ByteBuffer buffer) throws IOException {
/* 122 */     return this.readable.receive(buffer);
/*     */   }
/*     */   
/*     */   public long receive(ByteBuffer[] buffers) throws IOException {
/* 126 */     return this.readable.receive(buffers);
/*     */   }
/*     */   
/*     */   public long receive(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 130 */     return this.readable.receive(buffers, offs, len);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledMessageChannel> getWriteSetter() {
/* 136 */     return this.writeSetter;
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 140 */     this.writable.suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 144 */     this.writable.resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 148 */     return this.writable.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 152 */     this.writable.wakeupWrites();
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 156 */     this.writable.shutdownWrites();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 160 */     this.writable.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 164 */     this.writable.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 169 */     return this.writable.getWriteThread();
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer buffer) throws IOException {
/* 173 */     return this.writable.send(buffer);
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer[] buffers) throws IOException {
/* 177 */     return this.writable.send(buffers);
/*     */   }
/*     */   
/*     */   public boolean send(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 181 */     return this.writable.send(buffers, offs, len);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer buffer) throws IOException {
/* 186 */     return this.writable.sendFinal(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers) throws IOException {
/* 191 */     return this.writable.sendFinal(buffers);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean sendFinal(ByteBuffer[] buffers, int offs, int len) throws IOException {
/* 196 */     return this.writable.sendFinal(buffers, offs, len);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 200 */     return this.writable.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledMessageChannel> getCloseSetter() {
/* 206 */     return this.closeSetter;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 210 */     return this.closeable.getWorker();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 214 */     this.closeable.close();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 218 */     return this.closeable.isOpen();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 222 */     return this.closeable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 226 */     return this.closeable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 230 */     return this.closeable.setOption(option, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledMessageChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */