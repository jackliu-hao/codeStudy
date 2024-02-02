/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
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
/*     */ public class EmptyStreamSourceChannel
/*     */   implements StreamSourceChannel, ReadListenerSettable<EmptyStreamSourceChannel>, CloseListenerSettable<EmptyStreamSourceChannel>
/*     */ {
/*     */   private final XnioIoThread thread;
/*     */   
/*  45 */   private final Runnable readRunnable = new Runnable() {
/*     */       public void run() {
/*  47 */         ChannelListener<? super EmptyStreamSourceChannel> listener = EmptyStreamSourceChannel.this.readListener;
/*  48 */         if (listener == null) {
/*  49 */           EmptyStreamSourceChannel.this.suspendReads();
/*     */           return;
/*     */         } 
/*  52 */         ChannelListeners.invokeChannelListener(EmptyStreamSourceChannel.this, listener);
/*  53 */         int oldVal = EmptyStreamSourceChannel.this.state;
/*  54 */         if (Bits.allAreSet(oldVal, 4) && Bits.allAreClear(oldVal, 3)) {
/*  55 */           EmptyStreamSourceChannel.this.thread.execute(this);
/*     */         }
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   private volatile int state;
/*     */   
/*     */   private ChannelListener<? super EmptyStreamSourceChannel> readListener;
/*     */   
/*     */   private ChannelListener<? super EmptyStreamSourceChannel> closeListener;
/*     */   private static final int CLOSED = 1;
/*     */   private static final int EMPTIED = 2;
/*     */   private static final int RESUMED = 4;
/*  69 */   private static final AtomicIntegerFieldUpdater<EmptyStreamSourceChannel> stateUpdater = AtomicIntegerFieldUpdater.newUpdater(EmptyStreamSourceChannel.class, "state");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public EmptyStreamSourceChannel(XnioIoThread thread) {
/*  77 */     this.thread = thread;
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  81 */     return 0L;
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*  85 */     throughBuffer.limit(0);
/*  86 */     emptied();
/*  87 */     return -1L;
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super EmptyStreamSourceChannel> readListener) {
/*  91 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super EmptyStreamSourceChannel> getReadListener() {
/*  95 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super EmptyStreamSourceChannel> closeListener) {
/*  99 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super EmptyStreamSourceChannel> getCloseListener() {
/* 103 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends EmptyStreamSourceChannel> getReadSetter() {
/* 107 */     return new ReadListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends EmptyStreamSourceChannel> getCloseSetter() {
/* 111 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */   private void emptied() {
/*     */     int oldVal;
/*     */     int newVal;
/*     */     do {
/* 117 */       oldVal = this.state;
/* 118 */       if (Bits.allAreSet(oldVal, 2)) {
/*     */         return;
/*     */       }
/* 121 */       newVal = oldVal | 0x2;
/* 122 */     } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 126 */     emptied();
/* 127 */     return -1L;
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 131 */     emptied();
/* 132 */     return -1L;
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 136 */     emptied();
/* 137 */     return -1;
/*     */   }
/*     */   public void suspendReads() {
/*     */     int oldVal;
/*     */     int newVal;
/*     */     do {
/* 143 */       oldVal = this.state;
/* 144 */       if (Bits.allAreClear(oldVal, 4)) {
/*     */         return;
/*     */       }
/* 147 */       newVal = oldVal & 0xFFFFFFFB;
/* 148 */     } while (!stateUpdater.compareAndSet(this, oldVal, newVal));
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeReads() {
/*     */     while (true) {
/* 154 */       int oldVal = this.state;
/* 155 */       if (Bits.anyAreSet(oldVal, 5)) {
/*     */         return;
/*     */       }
/* 158 */       int newVal = 4;
/* 159 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 160 */         if (Bits.allAreClear(oldVal, 2))
/* 161 */           this.thread.execute(this.readRunnable); 
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   } public boolean isReadResumed() {
/* 166 */     return Bits.allAreSet(this.state, 4);
/*     */   }
/*     */ 
/*     */   
/*     */   public void wakeupReads() {
/*     */     while (true) {
/* 172 */       int oldVal = this.state;
/* 173 */       if (Bits.anyAreSet(oldVal, 1)) {
/*     */         return;
/*     */       }
/* 176 */       int newVal = 4;
/* 177 */       if (stateUpdater.compareAndSet(this, oldVal, newVal)) {
/* 178 */         this.thread.execute(this.readRunnable);
/*     */         return;
/*     */       } 
/*     */     }  } public void shutdownReads() throws IOException {
/* 182 */     int oldVal = stateUpdater.getAndSet(this, 3);
/* 183 */     if (Bits.allAreClear(oldVal, 1)) {
/* 184 */       this.thread.execute(ChannelListeners.getChannelListenerTask(this, this.closeListener));
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitReadable() throws IOException {}
/*     */ 
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {}
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 198 */     return (XnioExecutor)this.thread;
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 202 */     return this.thread;
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 206 */     return this.thread.getWorker();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 210 */     return Bits.allAreClear(this.state, 1);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 214 */     int oldVal = stateUpdater.getAndSet(this, 3);
/* 215 */     if (Bits.allAreClear(oldVal, 1)) {
/* 216 */       this.thread.execute(ChannelListeners.getChannelListenerTask(this, this.closeListener));
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 221 */     return false;
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 225 */     return null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 229 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\EmptyStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */