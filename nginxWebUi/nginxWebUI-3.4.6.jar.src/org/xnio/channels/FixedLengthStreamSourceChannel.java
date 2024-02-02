/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
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
/*     */ public final class FixedLengthStreamSourceChannel
/*     */   implements StreamSourceChannel, ProtectedWrappedChannel<StreamSourceChannel>, ReadListenerSettable<FixedLengthStreamSourceChannel>, CloseListenerSettable<FixedLengthStreamSourceChannel>
/*     */ {
/*     */   private final StreamSourceChannel delegate;
/*     */   private final Object guard;
/*     */   private final ChannelListener<? super FixedLengthStreamSourceChannel> finishListener;
/*     */   private ChannelListener<? super FixedLengthStreamSourceChannel> readListener;
/*     */   private ChannelListener<? super FixedLengthStreamSourceChannel> closeListener;
/*     */   private int state;
/*     */   private long remaining;
/*     */   private static final int FLAG_CLOSED = 1;
/*     */   private static final int FLAG_FINISHED = 2;
/*     */   private static final int FLAG_CONFIGURABLE = 4;
/*     */   private static final int FLAG_PASS_CLOSE = 8;
/*     */   
/*     */   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
/*  75 */     this(delegate, contentLength, false, finishListener, guard);
/*     */   }
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
/*     */   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, boolean configurable, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
/*  94 */     this(delegate, contentLength, configurable, false, finishListener, guard);
/*     */   }
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
/*     */   public FixedLengthStreamSourceChannel(StreamSourceChannel delegate, long contentLength, boolean configurable, boolean propagateClose, ChannelListener<? super FixedLengthStreamSourceChannel> finishListener, Object guard) {
/* 114 */     this.guard = guard;
/* 115 */     this.finishListener = finishListener;
/* 116 */     if (contentLength < 0L) {
/* 117 */       throw Messages.msg.parameterOutOfRange("contentLength");
/*     */     }
/* 119 */     this.delegate = delegate;
/* 120 */     this.remaining = contentLength;
/* 121 */     delegate.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
/*     */           public void handleEvent(StreamSourceChannel channel) {
/* 123 */             ChannelListeners.invokeChannelListener(FixedLengthStreamSourceChannel.this, FixedLengthStreamSourceChannel.this.readListener);
/*     */           }
/*     */         });
/* 126 */     this.state = (configurable ? 4 : 0) | (propagateClose ? 8 : 0);
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super FixedLengthStreamSourceChannel> readListener) {
/* 130 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super FixedLengthStreamSourceChannel> getReadListener() {
/* 134 */     return this.readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super FixedLengthStreamSourceChannel> closeListener) {
/* 138 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super FixedLengthStreamSourceChannel> getCloseListener() {
/* 142 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<FixedLengthStreamSourceChannel> getReadSetter() {
/* 146 */     return new ReadListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<FixedLengthStreamSourceChannel> getCloseSetter() {
/* 150 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/* 154 */     long remaining = this.remaining;
/* 155 */     if (Bits.anyAreSet(this.state, 3) || remaining == 0L || count == 0L) {
/* 156 */       return 0L;
/*     */     }
/* 158 */     long res = 0L;
/*     */     try {
/* 160 */       return res = this.delegate.transferTo(position, Math.min(count, remaining), target);
/*     */     } finally {
/* 162 */       if (res > 0L && (
/* 163 */         this.remaining = remaining - res) == 0L) {
/* 164 */         this.state |= 0x2;
/* 165 */         callFinish();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 172 */     long remaining = this.remaining;
/* 173 */     if (Bits.anyAreSet(this.state, 3) || remaining == 0L) {
/* 174 */       return -1L;
/*     */     }
/* 176 */     if (count == 0L) {
/* 177 */       return 0L;
/*     */     }
/* 179 */     long res = 0L;
/*     */     try {
/* 181 */       return res = this.delegate.transferTo(Math.min(count, remaining), throughBuffer, target);
/*     */     } finally {
/* 183 */       if (res > 0L && (
/* 184 */         this.remaining = remaining - res) == 0L) {
/* 185 */         this.state |= 0x2;
/* 186 */         callFinish();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 193 */     long remaining = this.remaining;
/* 194 */     if (Bits.anyAreSet(this.state, 3) || remaining == 0L) {
/* 195 */       return -1L;
/*     */     }
/* 197 */     if (length == 0)
/* 198 */       return 0L; 
/* 199 */     if (length == 1) {
/* 200 */       return read(dsts[offset]);
/*     */     }
/* 202 */     long res = 0L;
/*     */ 
/*     */     
/*     */     try {
/* 206 */       long t = 0L;
/* 207 */       for (int i = 0; i < length; i++) {
/* 208 */         ByteBuffer buffer = dsts[i + offset];
/*     */         
/*     */         int lim;
/* 211 */         t += ((lim = buffer.limit()) - buffer.position());
/* 212 */         if (t > remaining) {
/*     */           
/* 214 */           buffer.limit(lim - (int)(t - remaining));
/*     */           try {
/* 216 */             return res = this.delegate.read(dsts, offset, i + 1);
/*     */           } finally {
/*     */             
/* 219 */             buffer.limit(lim);
/*     */           } 
/*     */         } 
/*     */       } 
/*     */       
/* 224 */       return res = (t == 0L) ? 0L : this.delegate.read(dsts, offset, length);
/*     */     } finally {
/* 226 */       if (res > 0L && (
/* 227 */         this.remaining = remaining - res) == 0L) {
/* 228 */         this.state |= 0x2;
/* 229 */         callFinish();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 236 */     return read(dsts, 0, dsts.length);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 240 */     long remaining = this.remaining;
/* 241 */     if (Bits.anyAreSet(this.state, 3) || remaining == 0L) {
/* 242 */       return -1;
/*     */     }
/* 244 */     int res = 0;
/*     */     try {
/* 246 */       int lim = dst.limit();
/* 247 */       int pos = dst.position();
/* 248 */       if ((lim - pos) > remaining) {
/* 249 */         dst.limit((int)(remaining - pos));
/*     */         try {
/* 251 */           return res = this.delegate.read(dst);
/*     */         } finally {
/* 253 */           dst.limit(lim);
/*     */         } 
/*     */       } 
/* 256 */       return res = this.delegate.read(dst);
/*     */     } finally {
/*     */       
/* 259 */       if (res > 0 && (
/* 260 */         this.remaining = remaining - res) == 0L) {
/* 261 */         this.state |= 0x2;
/* 262 */         callFinish();
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendReads() {
/* 269 */     if (Bits.allAreClear(this.state, 3)) {
/* 270 */       this.delegate.suspendReads();
/*     */     }
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 275 */     if (Bits.allAreClear(this.state, 3)) {
/* 276 */       this.delegate.resumeReads();
/*     */     } else {
/* 278 */       this.delegate.getIoThread().execute(ChannelListeners.getChannelListenerTask(this, this.readListener));
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 283 */     return (Bits.allAreClear(this.state, 3) && this.delegate.isReadResumed());
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 287 */     if (Bits.allAreClear(this.state, 3)) {
/* 288 */       this.delegate.wakeupReads();
/*     */     } else {
/* 290 */       this.delegate.getIoThread().execute(ChannelListeners.getChannelListenerTask(this, this.readListener));
/*     */     } 
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 295 */     int state = this.state;
/* 296 */     if (Bits.allAreClear(state, 1))
/* 297 */       try { this.state = state | 0x1 | 0x2;
/* 298 */         if (Bits.allAreSet(state, 8)) {
/* 299 */           this.delegate.shutdownReads();
/*     */         } }
/*     */       finally
/* 302 */       { if (Bits.allAreClear(state, 2)) callFinish(); 
/* 303 */         callClosed(); }
/*     */        
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 308 */     if (Bits.anyAreSet(this.state, 3)) {
/*     */       return;
/*     */     }
/* 311 */     this.delegate.awaitReadable();
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 315 */     if (Bits.anyAreSet(this.state, 3)) {
/*     */       return;
/*     */     }
/* 318 */     this.delegate.awaitReadable(time, timeUnit);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 323 */     return this.delegate.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 327 */     return this.delegate.getIoThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 331 */     return this.delegate.getWorker();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 335 */     return Bits.allAreClear(this.state, 1);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 339 */     shutdownReads();
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 343 */     return (Bits.allAreSet(this.state, 4) && this.delegate.supportsOption(option));
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 347 */     return Bits.allAreSet(this.state, 4) ? this.delegate.<T>getOption(option) : null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 351 */     return Bits.allAreSet(this.state, 4) ? this.delegate.<T>setOption(option, value) : null;
/*     */   }
/*     */   
/*     */   public StreamSourceChannel getChannel(Object guard) {
/* 355 */     Object ourGuard = this.guard;
/* 356 */     if (ourGuard == null || guard == ourGuard) {
/* 357 */       return this.delegate;
/*     */     }
/* 359 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 369 */     return this.remaining;
/*     */   }
/*     */   
/*     */   private void callFinish() {
/* 373 */     ChannelListeners.invokeChannelListener(this, this.finishListener);
/*     */   }
/*     */   
/*     */   private void callClosed() {
/* 377 */     ChannelListeners.invokeChannelListener(this, this.closeListener);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\FixedLengthStreamSourceChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */