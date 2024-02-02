/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
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
/*     */ 
/*     */ 
/*     */ public final class NullStreamSinkChannel
/*     */   implements StreamSinkChannel, WriteListenerSettable<NullStreamSinkChannel>, CloseListenerSettable<NullStreamSinkChannel>
/*     */ {
/*     */   private final XnioIoThread thread;
/*     */   private volatile int state;
/*     */   private ChannelListener<? super NullStreamSinkChannel> writeListener;
/*     */   private ChannelListener<? super NullStreamSinkChannel> closeListener;
/*  51 */   private static final AtomicIntegerFieldUpdater<NullStreamSinkChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(NullStreamSinkChannel.class, "state");
/*     */ 
/*     */   
/*     */   private static final int FLAG_ENTERED = 1;
/*     */ 
/*     */   
/*     */   private static final int FLAG_CLOSED = 2;
/*     */   
/*     */   private static final int FLAG_RESUMED = 4;
/*     */ 
/*     */   
/*     */   public NullStreamSinkChannel(XnioIoThread thread) {
/*  63 */     this.thread = thread;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/*  67 */     return this.thread.getWorker();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/*  71 */     return this.thread;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/*  76 */     return (XnioExecutor)this.thread;
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/*  80 */     int val = enterWrite();
/*     */     try {
/*  82 */       return Math.min(src.size() - position, count);
/*     */     } finally {
/*  84 */       exitWrite(val);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/*  89 */     int val = enterWrite();
/*     */     try {
/*  91 */       return Channels.drain(source, count);
/*     */     } finally {
/*  93 */       throughBuffer.limit(0);
/*  94 */       exitWrite(val);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super NullStreamSinkChannel> writeListener) {
/*  99 */     this.writeListener = writeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super NullStreamSinkChannel> getWriteListener() {
/* 103 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super NullStreamSinkChannel> closeListener) {
/* 107 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super NullStreamSinkChannel> getCloseListener() {
/* 111 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<NullStreamSinkChannel> getWriteSetter() {
/* 115 */     return new WriteListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<NullStreamSinkChannel> getCloseSetter() {
/* 119 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 124 */     return Channels.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 129 */     return Channels.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 134 */     return Channels.writeFinalBasic(this, srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 138 */     int val = enterWrite();
/*     */     try {
/* 140 */       return src.remaining();
/*     */     } finally {
/* 142 */       src.position(src.limit());
/* 143 */       exitWrite(val);
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 148 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 152 */     if (length == 0) {
/* 153 */       return 0L;
/*     */     }
/* 155 */     int val = enterWrite();
/*     */     try {
/* 157 */       long t = 0L;
/*     */       
/* 159 */       for (int i = 0; i < length; i++) {
/* 160 */         ByteBuffer src = srcs[i];
/* 161 */         t += src.remaining();
/* 162 */         src.position(src.limit());
/*     */       } 
/* 164 */       return t;
/*     */     } finally {
/* 166 */       exitWrite(val);
/*     */     } 
/*     */   }
/*     */   public void suspendWrites() {
/*     */     int oldVal;
/*     */     int newVal;
/*     */     do {
/* 173 */       oldVal = this.state;
/* 174 */       if (Bits.allAreClear(oldVal, 4) || Bits.allAreSet(oldVal, 2)) {
/*     */         return;
/*     */       }
/* 177 */       newVal = oldVal & 0xFFFFFFFB;
/* 178 */     } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/*     */     while (true) {
/* 184 */       int oldVal = this.state;
/* 185 */       if (Bits.anyAreSet(oldVal, 6)) {
/*     */         return;
/*     */       }
/* 188 */       int newVal = oldVal | 0x4;
/* 189 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 190 */         this.thread.execute(ChannelListeners.getChannelListenerTask(this, this.writeListener));
/*     */         return;
/*     */       } 
/*     */     }  } public void wakeupWrites() {
/* 194 */     resumeWrites();
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 198 */     int state = this.state;
/* 199 */     return (Bits.allAreSet(state, 4) && Bits.allAreClear(state, 2));
/*     */   }
/*     */ 
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/*     */     while (true) {
/* 205 */       int oldVal = this.state;
/* 206 */       if (Bits.allAreSet(oldVal, 2)) {
/*     */         return;
/*     */       }
/* 209 */       int newVal = oldVal | 0x2;
/* 210 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 211 */         this.writeListener = null;
/* 212 */         ChannelListeners.invokeChannelListener(this, this.closeListener);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {}
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {}
/*     */   
/*     */   public boolean flush() throws IOException {
/* 224 */     return true;
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 228 */     return Bits.allAreClear(this.state, 2);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 232 */     shutdownWrites();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 236 */     return false;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 240 */     return null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 244 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   private int enterWrite() throws ClosedChannelException {
/*     */     while (true) {
/* 250 */       int oldVal = this.state;
/* 251 */       if (Bits.allAreSet(oldVal, 1)) {
/* 252 */         throw new ConcurrentStreamChannelAccessException();
/*     */       }
/* 254 */       if (Bits.allAreSet(oldVal, 2)) {
/* 255 */         throw new ClosedChannelException();
/*     */       }
/* 257 */       int newVal = oldVal | 0x1;
/* 258 */       if (stateUpdater.compareAndSet(this, oldVal, newVal))
/* 259 */         return newVal; 
/*     */     } 
/*     */   }
/*     */   private void exitWrite(int oldVal) {
/* 263 */     int newVal = oldVal & 0xFFFFFFFE;
/* 264 */     while (!stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 265 */       oldVal = this.state;
/* 266 */       newVal = oldVal & 0xFFFFFFFE;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\NullStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */