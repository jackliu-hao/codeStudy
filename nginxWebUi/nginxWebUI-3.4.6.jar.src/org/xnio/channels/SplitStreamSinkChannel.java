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
/*     */ public final class SplitStreamSinkChannel
/*     */   implements StreamSinkChannel, WriteListenerSettable<SplitStreamSinkChannel>, CloseListenerSettable<SplitStreamSinkChannel>
/*     */ {
/*     */   private final StreamSinkChannel delegate;
/*     */   private volatile int state;
/*     */   private ChannelListener<? super SplitStreamSinkChannel> writeListener;
/*     */   private ChannelListener<? super SplitStreamSinkChannel> closeListener;
/*     */   private static final int FLAG_DELEGATE_CONFIG = 1;
/*     */   private static final int FLAG_CLOSE_REQ = 2;
/*     */   private static final int FLAG_CLOSE_COMP = 4;
/*  52 */   private static final AtomicIntegerFieldUpdater<SplitStreamSinkChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(SplitStreamSinkChannel.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplitStreamSinkChannel(StreamSinkChannel delegate, boolean delegateConfig) {
/*  61 */     this.delegate = delegate;
/*  62 */     this.state = delegateConfig ? 1 : 0;
/*  63 */     delegate.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
/*     */           public void handleEvent(StreamSinkChannel channel) {
/*  65 */             ChannelListeners.invokeChannelListener(SplitStreamSinkChannel.this, SplitStreamSinkChannel.this.writeListener);
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public SplitStreamSinkChannel(StreamSinkChannel delegate) {
/*  76 */     this(delegate, false);
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super SplitStreamSinkChannel> writeListener) {
/*  80 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super SplitStreamSinkChannel> getWriteListener() {
/*  84 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super SplitStreamSinkChannel> closeListener) {
/*  88 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super SplitStreamSinkChannel> getCloseListener() {
/*  92 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends SplitStreamSinkChannel> getCloseSetter() {
/*  96 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 101 */     return this.delegate.writeFinal(src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 106 */     return this.delegate.writeFinal(srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 111 */     return this.delegate.writeFinal(srcs);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends SplitStreamSinkChannel> getWriteSetter() {
/* 115 */     return new WriteListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/*     */     while (true) {
/* 121 */       int oldVal = this.state;
/* 122 */       if (Bits.allAreSet(oldVal, 2)) {
/*     */         return;
/*     */       }
/* 125 */       int newVal = oldVal | 0x2;
/* 126 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 127 */         this.delegate.shutdownWrites();
/*     */         return;
/*     */       } 
/*     */     }  } public boolean isOpen() {
/* 131 */     return Bits.allAreClear(this.state, 2);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 135 */     shutdownWrites();
/*     */     
/* 137 */     flush();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 142 */     int oldVal = this.state;
/* 143 */     if (Bits.allAreSet(oldVal, 4)) {
/* 144 */       return true;
/*     */     }
/* 146 */     boolean flushed = this.delegate.flush();
/* 147 */     if (!flushed) {
/* 148 */       return false;
/*     */     }
/*     */     while (true) {
/* 151 */       if (Bits.allAreClear(oldVal, 2)) {
/* 152 */         return true;
/*     */       }
/* 154 */       int newVal = oldVal | 0x4;
/* 155 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 156 */         ChannelListeners.invokeChannelListener(this, this.closeListener);
/* 157 */         return true;
/*     */       } 
/*     */     } 
/*     */   } public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 161 */     return this.delegate.transferFrom(src, position, count);
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 165 */     return this.delegate.transferFrom(source, count, throughBuffer);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 169 */     return this.delegate.write(src);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 173 */     return this.delegate.write(srcs, offset, length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 177 */     return this.delegate.write(srcs);
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 181 */     this.delegate.suspendWrites();
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 185 */     this.delegate.resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 189 */     return this.delegate.isWriteResumed();
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 193 */     this.delegate.wakeupWrites();
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 197 */     this.delegate.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 201 */     this.delegate.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 206 */     return this.delegate.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 210 */     return this.delegate.getIoThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 214 */     return this.delegate.getWorker();
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 218 */     return Bits.allAreSet(this.state, 1) ? this.delegate.<T>setOption(option, value) : null;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 222 */     return Bits.allAreSet(this.state, 1) ? this.delegate.<T>getOption(option) : null;
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 226 */     return Bits.allAreSet(this.state, 1) ? this.delegate.supportsOption(option) : false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\SplitStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */