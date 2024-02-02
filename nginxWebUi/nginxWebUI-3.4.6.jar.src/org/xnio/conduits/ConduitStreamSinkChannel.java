/*     */ package org.xnio.conduits;
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
/*     */ import org.xnio.channels.CloseListenerSettable;
/*     */ import org.xnio.channels.Configurable;
/*     */ import org.xnio.channels.StreamSinkChannel;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.WriteListenerSettable;
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
/*     */ public final class ConduitStreamSinkChannel
/*     */   implements StreamSinkChannel, WriteListenerSettable<ConduitStreamSinkChannel>, CloseListenerSettable<ConduitStreamSinkChannel>, Cloneable
/*     */ {
/*     */   private final Configurable configurable;
/*     */   private StreamSinkConduit conduit;
/*     */   private ChannelListener<? super ConduitStreamSinkChannel> writeListener;
/*     */   private ChannelListener<? super ConduitStreamSinkChannel> closeListener;
/*     */   
/*     */   public ConduitStreamSinkChannel(Configurable configurable, StreamSinkConduit conduit) {
/*  55 */     this.configurable = configurable;
/*  56 */     this.conduit = conduit;
/*  57 */     conduit.setWriteReadyHandler(new WriteReadyHandler.ChannelListenerHandler<>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamSinkConduit getConduit() {
/*  66 */     return this.conduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConduit(StreamSinkConduit conduit) {
/*  75 */     this.conduit = conduit;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitStreamSinkChannel> getWriteListener() {
/*  79 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super ConduitStreamSinkChannel> writeListener) {
/*  83 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitStreamSinkChannel> getCloseListener() {
/*  87 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super ConduitStreamSinkChannel> closeListener) {
/*  91 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitStreamSinkChannel> getWriteSetter() {
/*  95 */     return (ChannelListener.Setter<ConduitStreamSinkChannel>)new WriteListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitStreamSinkChannel> getCloseSetter() {
/*  99 */     return (ChannelListener.Setter<ConduitStreamSinkChannel>)new CloseListenerSettable.Setter(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 104 */     return this.conduit.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 109 */     return this.conduit.writeFinal(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 114 */     return this.conduit.writeFinal(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 118 */     this.conduit.suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 122 */     this.conduit.resumeWrites();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 126 */     this.conduit.wakeupWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 130 */     return this.conduit.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 134 */     this.conduit.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 138 */     this.conduit.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 142 */     return this.conduit.transferFrom(src, position, count);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 146 */     return this.conduit.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer dst) throws IOException {
/* 150 */     return this.conduit.write(dst);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 154 */     return this.conduit.write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] dsts, int offs, int len) throws IOException {
/* 158 */     return this.conduit.write(dsts, offs, len);
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 162 */     return this.conduit.flush();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 166 */     return this.configurable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 170 */     return (T)this.configurable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 174 */     return (T)this.configurable.setOption(option, value);
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 178 */     this.conduit.terminateWrites();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 182 */     return !this.conduit.isWriteShutdown();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 186 */     this.conduit.truncateWrites();
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 191 */     return (XnioExecutor)this.conduit.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 195 */     return this.conduit.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 199 */     return this.conduit.getWorker();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitStreamSinkChannel clone() {
/*     */     try {
/* 209 */       return (ConduitStreamSinkChannel)super.clone();
/* 210 */     } catch (CloneNotSupportedException e) {
/* 211 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */