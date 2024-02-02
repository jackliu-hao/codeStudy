/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.IoUtils;
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
/*     */ public final class FixedLengthStreamSinkChannel
/*     */   implements StreamSinkChannel, ProtectedWrappedChannel<StreamSinkChannel>, WriteListenerSettable<FixedLengthStreamSinkChannel>, CloseListenerSettable<FixedLengthStreamSinkChannel>
/*     */ {
/*     */   private final StreamSinkChannel delegate;
/*     */   private final Object guard;
/*     */   private final ChannelListener<? super FixedLengthStreamSinkChannel> finishListener;
/*     */   private ChannelListener<? super FixedLengthStreamSinkChannel> writeListener;
/*     */   private ChannelListener<? super FixedLengthStreamSinkChannel> closeListener;
/*     */   private int state;
/*     */   private long count;
/*     */   private static final int FLAG_CLOSE_REQUESTED = 1;
/*     */   private static final int FLAG_CLOSE_COMPLETE = 2;
/*     */   private static final int FLAG_CONFIGURABLE = 4;
/*     */   private static final int FLAG_PASS_CLOSE = 8;
/*     */   
/*     */   public FixedLengthStreamSinkChannel(StreamSinkChannel delegate, long contentLength, boolean configurable, boolean propagateClose, ChannelListener<? super FixedLengthStreamSinkChannel> finishListener, Object guard) {
/*  70 */     if (contentLength < 0L) {
/*  71 */       throw Messages.msg.parameterOutOfRange("contentLength");
/*     */     }
/*  73 */     if (delegate == null) {
/*  74 */       throw Messages.msg.nullParameter("delegate");
/*     */     }
/*  76 */     this.guard = guard;
/*  77 */     this.delegate = delegate;
/*  78 */     this.finishListener = finishListener;
/*  79 */     this.state = (configurable ? 4 : 0) | (propagateClose ? 8 : 0);
/*  80 */     this.count = contentLength;
/*  81 */     delegate.getWriteSetter().set(new ChannelListener<StreamSinkChannel>() {
/*     */           public void handleEvent(StreamSinkChannel channel) {
/*  83 */             ChannelListeners.invokeChannelListener(FixedLengthStreamSinkChannel.this, FixedLengthStreamSinkChannel.this.writeListener);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void setWriteListener(ChannelListener<? super FixedLengthStreamSinkChannel> listener) {
/*  89 */     this.writeListener = listener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super FixedLengthStreamSinkChannel> getWriteListener() {
/*  93 */     return this.writeListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super FixedLengthStreamSinkChannel> listener) {
/*  97 */     this.closeListener = listener;
/*     */   }
/*     */   
/*     */   public ChannelListener<? super FixedLengthStreamSinkChannel> getCloseListener() {
/* 101 */     return this.closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<FixedLengthStreamSinkChannel> getWriteSetter() {
/* 105 */     return new WriteListenerSettable.Setter<>(this);
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<FixedLengthStreamSinkChannel> getCloseSetter() {
/* 109 */     return new CloseListenerSettable.Setter<>(this);
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 114 */     return write(src, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 119 */     return write(srcs, offset, length, true);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs) throws IOException {
/* 124 */     return write(srcs, 0, srcs.length, true);
/*     */   }
/*     */   
/*     */   public StreamSinkChannel getChannel(Object guard) {
/* 128 */     Object ourGuard = this.guard;
/* 129 */     if (ourGuard == null || guard == ourGuard) {
/* 130 */       return this.delegate;
/*     */     }
/* 132 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getWriteThread() {
/* 138 */     return this.delegate.getWriteThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 142 */     return this.delegate.getIoThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 146 */     return this.delegate.getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 151 */     return write(src, false);
/*     */   }
/*     */   
/*     */   private int write(ByteBuffer src, boolean finalWrite) throws IOException {
/* 155 */     if (Bits.allAreSet(this.state, 1)) {
/* 156 */       throw new ClosedChannelException();
/*     */     }
/* 158 */     if (!src.hasRemaining()) {
/* 159 */       return 0;
/*     */     }
/* 161 */     int res = 0;
/* 162 */     long remaining = this.count;
/* 163 */     if (remaining == 0L) {
/* 164 */       throw Messages.msg.fixedOverflow();
/*     */     }
/*     */     try {
/* 167 */       int lim = src.limit();
/* 168 */       int pos = src.position();
/* 169 */       if ((lim - pos) > remaining) {
/* 170 */         src.limit((int)(remaining - pos));
/*     */         try {
/* 172 */           return res = doWrite(src, finalWrite);
/*     */         } finally {
/* 174 */           src.limit(lim);
/*     */         } 
/*     */       } 
/* 177 */       return res = doWrite(src, finalWrite);
/*     */     } finally {
/*     */       
/* 180 */       this.count = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs) throws IOException {
/* 185 */     return write(srcs, 0, srcs.length);
/*     */   }
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 189 */     return write(srcs, offset, length, false);
/*     */   }
/*     */   
/*     */   private long write(ByteBuffer[] srcs, int offset, int length, boolean writeFinal) throws IOException {
/* 193 */     if (Bits.allAreSet(this.state, 1)) {
/* 194 */       throw new ClosedChannelException();
/*     */     }
/* 196 */     if (length == 0)
/* 197 */       return 0L; 
/* 198 */     if (length == 1) {
/* 199 */       return write(srcs[offset]);
/*     */     }
/* 201 */     long remaining = this.count;
/* 202 */     if (remaining == 0L) {
/* 203 */       throw Messages.msg.fixedOverflow();
/*     */     }
/* 205 */     long res = 0L;
/*     */ 
/*     */     
/*     */     try {
/* 209 */       long t = 0L;
/* 210 */       for (int i = 0; i < length; i++) {
/* 211 */         ByteBuffer buffer = srcs[i + offset];
/*     */         
/*     */         int lim;
/* 214 */         t += ((lim = buffer.limit()) - buffer.position());
/* 215 */         if (t > remaining) {
/*     */           
/* 217 */           buffer.limit(lim - (int)(t - remaining));
/*     */           try {
/* 219 */             return res = doWrite(srcs, offset, i + 1, writeFinal);
/*     */           } finally {
/*     */             
/* 222 */             buffer.limit(lim);
/*     */           } 
/*     */         } 
/*     */       } 
/* 226 */       if (t == 0L) {
/* 227 */         return 0L;
/*     */       }
/*     */       
/* 230 */       return res = doWrite(srcs, offset, length, writeFinal);
/*     */     } finally {
/* 232 */       this.count = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   private long doWrite(ByteBuffer[] srcs, int offset, int length, boolean writeFinal) throws IOException {
/* 237 */     if (writeFinal) {
/* 238 */       return this.delegate.writeFinal(srcs, offset, length);
/*     */     }
/* 240 */     return this.delegate.write(srcs, offset, length);
/*     */   }
/*     */   
/*     */   private int doWrite(ByteBuffer src, boolean finalWrite) throws IOException {
/* 244 */     if (finalWrite) {
/* 245 */       return this.delegate.writeFinal(src);
/*     */     }
/* 247 */     return this.delegate.write(src);
/*     */   }
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 251 */     if (Bits.allAreSet(this.state, 1)) {
/* 252 */       throw new ClosedChannelException();
/*     */     }
/* 254 */     if (count == 0L) return 0L; 
/* 255 */     long remaining = this.count;
/* 256 */     if (remaining == 0L) {
/* 257 */       throw Messages.msg.fixedOverflow();
/*     */     }
/* 259 */     long res = 0L;
/*     */     try {
/* 261 */       return res = this.delegate.transferFrom(src, position, Math.min(count, remaining));
/*     */     } finally {
/* 263 */       this.count = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 268 */     if (Bits.allAreSet(this.state, 1)) {
/* 269 */       throw new ClosedChannelException();
/*     */     }
/* 271 */     if (count == 0L) return 0L; 
/* 272 */     long remaining = this.count;
/* 273 */     if (remaining == 0L) {
/* 274 */       throw Messages.msg.fixedOverflow();
/*     */     }
/* 276 */     long res = 0L;
/*     */     try {
/* 278 */       return res = this.delegate.transferFrom(source, Math.min(count, remaining), throughBuffer);
/*     */     } finally {
/* 280 */       this.count = remaining - res;
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean flush() throws IOException {
/* 285 */     int state = this.state;
/* 286 */     if (Bits.anyAreSet(state, 2)) {
/* 287 */       return true;
/*     */     }
/* 289 */     boolean flushed = false;
/*     */     try {
/* 291 */       return flushed = this.delegate.flush();
/*     */     } finally {
/* 293 */       if (flushed && Bits.allAreSet(state, 1)) {
/* 294 */         this.state = state | 0x2;
/* 295 */         callFinish();
/* 296 */         callClosed();
/* 297 */         if (this.count != 0L) {
/* 298 */           throw Messages.msg.fixedUnderflow(this.count);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public void suspendWrites() {
/* 305 */     if (Bits.allAreClear(this.state, 2)) {
/* 306 */       this.delegate.suspendWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   public void resumeWrites() {
/* 311 */     if (Bits.allAreClear(this.state, 2)) {
/* 312 */       this.delegate.resumeWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isWriteResumed() {
/* 317 */     return (Bits.allAreClear(this.state, 2) && this.delegate.isWriteResumed());
/*     */   }
/*     */   
/*     */   public void wakeupWrites() {
/* 321 */     if (Bits.allAreClear(this.state, 2)) {
/* 322 */       this.delegate.wakeupWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   public void shutdownWrites() throws IOException {
/* 327 */     int state = this.state;
/* 328 */     if (Bits.allAreSet(state, 1)) {
/*     */       return;
/*     */     }
/* 331 */     this.state = state | 0x1;
/* 332 */     if (Bits.allAreSet(state, 8)) {
/* 333 */       this.delegate.shutdownWrites();
/*     */     }
/*     */   }
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 338 */     this.delegate.awaitWritable();
/*     */   }
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 342 */     this.delegate.awaitWritable(time, timeUnit);
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 346 */     return Bits.allAreClear(this.state, 1);
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 350 */     int state = this.state;
/* 351 */     if (Bits.allAreSet(state, 2)) {
/*     */       return;
/*     */     }
/* 354 */     this.state = state | 0x1 | 0x2;
/*     */     try {
/* 356 */       long count = this.count;
/* 357 */       if (count != 0L) {
/* 358 */         if (Bits.allAreSet(state, 8)) {
/* 359 */           IoUtils.safeClose(this.delegate);
/*     */         }
/* 361 */         throw Messages.msg.fixedUnderflow(count);
/*     */       } 
/* 363 */       if (Bits.allAreSet(state, 8)) {
/* 364 */         this.delegate.close();
/*     */       }
/*     */     } finally {
/* 367 */       callClosed();
/* 368 */       callFinish();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 373 */     return (Bits.allAreSet(this.state, 4) && this.delegate.supportsOption(option));
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 377 */     return Bits.allAreSet(this.state, 4) ? this.delegate.<T>getOption(option) : null;
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 381 */     return Bits.allAreSet(this.state, 4) ? this.delegate.<T>setOption(option, value) : null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getRemaining() {
/* 390 */     return this.count;
/*     */   }
/*     */   
/*     */   private void callFinish() {
/* 394 */     ChannelListeners.invokeChannelListener(this, this.finishListener);
/*     */   }
/*     */   
/*     */   private void callClosed() {
/* 398 */     ChannelListeners.invokeChannelListener(this, this.closeListener);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\FixedLengthStreamSinkChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */