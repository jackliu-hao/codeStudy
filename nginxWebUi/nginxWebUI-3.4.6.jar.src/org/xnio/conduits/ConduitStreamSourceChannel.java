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
/*     */ import org.xnio.channels.ReadListenerSettable;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ConduitStreamSourceChannel
/*     */   implements StreamSourceChannel, ReadListenerSettable<ConduitStreamSourceChannel>, CloseListenerSettable<ConduitStreamSourceChannel>, Cloneable
/*     */ {
/*     */   private final Configurable configurable;
/*     */   private StreamSourceConduit conduit;
/*     */   private ChannelListener<? super ConduitStreamSourceChannel> readListener;
/*     */   private ChannelListener<? super ConduitStreamSourceChannel> closeListener;
/*     */   
/*     */   public ConduitStreamSourceChannel(Configurable configurable, StreamSourceConduit conduit) {
/*  55 */     this.configurable = configurable;
/*  56 */     this.conduit = conduit;
/*  57 */     conduit.setReadReadyHandler(new ReadReadyHandler.ChannelListenerHandler<>(this));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public StreamSourceConduit getConduit() {
/*  66 */     return this.conduit;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setConduit(StreamSourceConduit conduit) {
/*  75 */     this.conduit = conduit;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/*  79 */     return !this.conduit.isReadShutdown();
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  83 */     return this.conduit.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  87 */     return this.conduit.transferTo(count, throughBuffer, target);
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super ConduitStreamSourceChannel> readListener) {
/*  91 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitStreamSourceChannel> getReadListener() {
/*  95 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super ConduitStreamSourceChannel> closeListener) {
/*  99 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super ConduitStreamSourceChannel> getCloseListener() {
/* 103 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitStreamSourceChannel> getReadSetter() {
/* 107 */     return (ChannelListener.Setter<ConduitStreamSourceChannel>)new ReadListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<ConduitStreamSourceChannel> getCloseSetter() {
/* 111 */     return (ChannelListener.Setter<ConduitStreamSourceChannel>)new CloseListenerSettable.Setter(this);
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 115 */     return this.conduit.getWorker();
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 119 */     return this.conduit.read(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 123 */     return this.conduit.read(dsts, 0, dsts.length);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 127 */     return this.conduit.read(dst);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 131 */     this.conduit.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 135 */     this.conduit.resumeReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 139 */     return this.conduit.isReadResumed();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 143 */     this.conduit.wakeupReads();
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 147 */     this.conduit.terminateReads();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 151 */     this.conduit.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 155 */     this.conduit.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 160 */     return (XnioExecutor)this.conduit.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 164 */     return this.conduit.getReadThread();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 168 */     this.conduit.terminateReads();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 172 */     return this.configurable.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 176 */     return (T)this.configurable.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 180 */     return (T)this.configurable.setOption(option, value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ConduitStreamSourceChannel clone() {
/*     */     try {
/* 190 */       return (ConduitStreamSourceChannel)super.clone();
/* 191 */     } catch (CloneNotSupportedException e) {
/* 192 */       throw new IllegalStateException(e);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\conduits\ConduitStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */