/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.FileChannel;
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
/*     */ public class AssembledStreamChannel
/*     */   implements StreamChannel
/*     */ {
/*     */   private final CloseableChannel closeable;
/*     */   private final StreamSourceChannel source;
/*     */   private final StreamSinkChannel sink;
/*     */   private final ChannelListener.Setter<AssembledStreamChannel> readSetter;
/*     */   private final ChannelListener.Setter<AssembledStreamChannel> writeSetter;
/*     */   private final ChannelListener.Setter<AssembledStreamChannel> closeSetter;
/*     */   
/*     */   public AssembledStreamChannel(CloseableChannel closeable, StreamSourceChannel source, StreamSinkChannel sink) {
/*  57 */     if (source.getWorker() != sink.getWorker() || source.getWorker() != closeable.getWorker()) {
/*  58 */       throw Messages.msg.differentWorkers();
/*     */     }
/*  60 */     this.closeable = closeable;
/*  61 */     this.source = source;
/*  62 */     this.sink = sink;
/*  63 */     this.readSetter = ChannelListeners.getDelegatingSetter(source.getReadSetter(), this);
/*  64 */     this.writeSetter = ChannelListeners.getDelegatingSetter(sink.getWriteSetter(), this);
/*  65 */     this.closeSetter = ChannelListeners.getDelegatingSetter(closeable.getCloseSetter(), this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public AssembledStreamChannel(StreamSourceChannel source, StreamSinkChannel sink) {
/*  75 */     this(new AssembledChannel(source, sink), source, sink);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledStreamChannel> getReadSetter() {
/*  81 */     return this.readSetter;
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/*  85 */     this.source.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/*  89 */     this.source.resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/*  93 */     return this.source.isReadResumed();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/*  97 */     this.source.wakeupReads();
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 101 */     this.source.shutdownReads();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 105 */     this.source.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 109 */     this.source.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 114 */     return this.source.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 118 */     return this.source.getIoThread();
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 122 */     return this.source.read(dst);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 126 */     return this.source.read(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 130 */     return this.source.read(dsts);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 134 */     return this.source.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 138 */     return this.source.transferTo(count, throughBuffer, target);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 144 */     return this.sink.transferFrom(src, position, count);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 148 */     return this.sink.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledStreamChannel> getWriteSetter() {
/* 152 */     return this.writeSetter;
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 156 */     return this.sink.write(src);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 160 */     return this.sink.write(srcs, offset, length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 164 */     return this.sink.write(srcs);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 168 */     this.sink.suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 172 */     this.sink.resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 176 */     return this.sink.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 180 */     this.sink.wakeupWrites();
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 184 */     this.sink.shutdownWrites();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 188 */     this.sink.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 192 */     this.sink.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 197 */     return this.sink.getWriteThread();
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 201 */     return this.sink.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ChannelListener.Setter<? extends AssembledStreamChannel> getCloseSetter() {
/* 207 */     return this.closeSetter;
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 212 */     return this.sink.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 217 */     return this.sink.writeFinal(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 222 */     return this.sink.writeFinal(srcs);
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 226 */     return this.closeable.getWorker();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 230 */     this.closeable.close();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 234 */     return this.closeable.isOpen();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 238 */     return this.closeable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 242 */     return this.closeable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 246 */     return this.closeable.setOption(option, value);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\AssembledStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */