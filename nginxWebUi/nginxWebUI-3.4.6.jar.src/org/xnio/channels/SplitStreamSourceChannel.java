/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.XnioExecutor;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
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
/*     */ public final class SplitStreamSourceChannel
/*     */   implements StreamSourceChannel, ReadListenerSettable<SplitStreamSourceChannel>, CloseListenerSettable<SplitStreamSourceChannel>
/*     */ {
/*     */   private final StreamSourceChannel delegate;
/*     */   private volatile int state;
/*     */   private ChannelListener<? super SplitStreamSourceChannel> readListener;
/*     */   private ChannelListener<? super SplitStreamSourceChannel> closeListener;
/*     */   private static final int FLAG_DELEGATE_CONFIG = 1;
/*     */   private static final int FLAG_CLOSED = 2;
/*  51 */   private static final AtomicIntegerFieldUpdater<SplitStreamSourceChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(SplitStreamSourceChannel.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplitStreamSourceChannel(StreamSourceChannel delegate, boolean delegateConfig) {
/*  60 */     this.delegate = delegate;
/*  61 */     delegate.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
/*     */           public void handleEvent(StreamSourceChannel channel) {
/*  63 */             ChannelListeners.invokeChannelListener(SplitStreamSourceChannel.this, SplitStreamSourceChannel.this.readListener);
/*     */           }
/*     */         });
/*  66 */     this.state = delegateConfig ? 1 : 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplitStreamSourceChannel(StreamSourceChannel delegate) {
/*  75 */     this(delegate, false);
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super SplitStreamSourceChannel> readListener) {
/*  79 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super SplitStreamSourceChannel> getReadListener() {
/*  83 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super SplitStreamSourceChannel> closeListener) {
/*  87 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super SplitStreamSourceChannel> getCloseListener() {
/*  91 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends SplitStreamSourceChannel> getReadSetter() {
/*  95 */     return new ReadListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends SplitStreamSourceChannel> getCloseSetter() {
/*  99 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 103 */     return this.delegate.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 107 */     return this.delegate.transferTo(count, throughBuffer, target);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 111 */     return this.delegate.read(dst);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 115 */     return this.delegate.read(dsts, offset, length);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 119 */     return this.delegate.read(dsts);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 123 */     this.delegate.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 127 */     this.delegate.resumeReads();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 131 */     this.delegate.wakeupReads();
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 135 */     return this.delegate.isReadResumed();
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 139 */     this.delegate.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 143 */     this.delegate.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 147 */     return this.delegate.getWorker();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 151 */     return Bits.allAreSet(this.state, 1) ? this.delegate.supportsOption(option) : false;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 155 */     return Bits.allAreSet(this.state, 1) ? this.delegate.<T>getOption(option) : null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 159 */     return Bits.allAreSet(this.state, 1) ? this.delegate.<T>setOption(option, value) : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownReads() throws IOException {
/*     */     while (true) {
/* 165 */       int oldVal = this.state;
/* 166 */       if (Bits.allAreSet(oldVal, 2)) {
/*     */         return;
/*     */       }
/* 169 */       int newVal = oldVal | 0x2;
/* 170 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 171 */         this.delegate.shutdownReads();
/* 172 */         ChannelListeners.invokeChannelListener(this, this.closeListener);
/*     */         return;
/*     */       } 
/*     */     }  } @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 177 */     return this.delegate.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 181 */     return this.delegate.getIoThread();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 185 */     return Bits.allAreClear(this.state, 2);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 189 */     shutdownReads();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SplitStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */