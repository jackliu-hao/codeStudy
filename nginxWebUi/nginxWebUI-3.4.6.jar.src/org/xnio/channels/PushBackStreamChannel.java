/*     */ package org.xnio.channels;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.Channel;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import org.xnio.Buffers;
/*     */ import org.xnio.ChannelListener;
/*     */ import org.xnio.ChannelListeners;
/*     */ import org.xnio.Option;
/*     */ import org.xnio.Pooled;
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
/*     */ 
/*     */ 
/*     */ public final class PushBackStreamChannel
/*     */   implements StreamSourceChannel, WrappedChannel<StreamSourceChannel>
/*     */ {
/*     */   private final StreamSourceChannel firstChannel;
/*     */   private StreamSourceChannel channel;
/*     */   private ChannelListener<? super PushBackStreamChannel> readListener;
/*     */   private ChannelListener<? super PushBackStreamChannel> closeListener;
/*     */   
/*     */   public PushBackStreamChannel(StreamSourceChannel channel) {
/*  55 */     this.channel = this.firstChannel = channel;
/*  56 */     this.firstChannel.getReadSetter().set(new ChannelListener<StreamSourceChannel>() {
/*     */           public void handleEvent(StreamSourceChannel channel) {
/*  58 */             ChannelListeners.invokeChannelListener(PushBackStreamChannel.this, PushBackStreamChannel.this.readListener);
/*     */           }
/*     */         });
/*  61 */     this.firstChannel.getCloseSetter().set(new ChannelListener<StreamSourceChannel>() {
/*     */           public void handleEvent(StreamSourceChannel channel) {
/*  63 */             ChannelListeners.invokeChannelListener(PushBackStreamChannel.this, PushBackStreamChannel.this.closeListener);
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public void setReadListener(ChannelListener<? super PushBackStreamChannel> readListener) {
/*  69 */     this.readListener = readListener;
/*     */   }
/*     */   
/*     */   public void setCloseListener(ChannelListener<? super PushBackStreamChannel> closeListener) {
/*  73 */     this.closeListener = closeListener;
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends PushBackStreamChannel> getReadSetter() {
/*  77 */     return new ChannelListener.Setter<PushBackStreamChannel>() {
/*     */         public void set(ChannelListener<? super PushBackStreamChannel> listener) {
/*  79 */           PushBackStreamChannel.this.setReadListener(listener);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public ChannelListener.Setter<? extends PushBackStreamChannel> getCloseSetter() {
/*  85 */     return new ChannelListener.Setter<PushBackStreamChannel>() {
/*     */         public void set(ChannelListener<? super PushBackStreamChannel> listener) {
/*  87 */           PushBackStreamChannel.this.setCloseListener(listener);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public long transferTo(long position, long count, FileChannel target) throws IOException {
/*  93 */     StreamSourceChannel channel = this.channel;
/*  94 */     if (channel == null) {
/*  95 */       return 0L;
/*     */     }
/*  97 */     return channel.transferTo(position, count, target);
/*     */   }
/*     */   
/*     */   public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/* 101 */     StreamSourceChannel channel = this.channel;
/* 102 */     if (channel == null) {
/* 103 */       return -1L;
/*     */     }
/* 105 */     return channel.transferTo(count, throughBuffer, target);
/*     */   }
/*     */   
/*     */   public int read(ByteBuffer dst) throws IOException {
/* 109 */     StreamSourceChannel channel = this.channel;
/* 110 */     if (channel == null) {
/* 111 */       return -1;
/*     */     }
/* 113 */     return channel.read(dst);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts) throws IOException {
/* 117 */     StreamSourceChannel channel = this.channel;
/* 118 */     if (channel == null) {
/* 119 */       return -1L;
/*     */     }
/* 121 */     return channel.read(dsts);
/*     */   }
/*     */   
/*     */   public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/* 125 */     StreamSourceChannel channel = this.channel;
/* 126 */     if (channel == null) {
/* 127 */       return -1L;
/*     */     }
/* 129 */     return channel.read(dsts, offset, length);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void unget(Pooled<ByteBuffer> buffer) {
/* 140 */     StreamSourceChannel old = this.channel;
/* 141 */     if (old == null) {
/* 142 */       buffer.free();
/*     */       return;
/*     */     } 
/* 145 */     this.channel = new BufferHolder(old, buffer);
/*     */   }
/*     */   
/*     */   public void suspendReads() {
/* 149 */     this.firstChannel.suspendReads();
/*     */   }
/*     */   
/*     */   public void resumeReads() {
/* 153 */     StreamSourceChannel channel = this.channel;
/* 154 */     if (channel != null) {
/* 155 */       channel.resumeReads();
/*     */     }
/*     */   }
/*     */   
/*     */   public boolean isReadResumed() {
/* 160 */     return this.firstChannel.isReadResumed();
/*     */   }
/*     */   
/*     */   public void wakeupReads() {
/* 164 */     this.firstChannel.wakeupReads();
/*     */   }
/*     */   
/*     */   public void shutdownReads() throws IOException {
/* 168 */     StreamSourceChannel old = this.channel;
/* 169 */     if (old != null) {
/* 170 */       this.channel = null;
/* 171 */       old.shutdownReads();
/*     */     } 
/*     */   }
/*     */   
/*     */   public void awaitReadable() throws IOException {
/* 176 */     StreamSourceChannel channel = this.channel;
/* 177 */     if (channel != null) {
/* 178 */       channel.awaitReadable();
/*     */     }
/*     */   }
/*     */   
/*     */   public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {
/* 183 */     StreamSourceChannel channel = this.channel;
/* 184 */     if (channel != null) {
/* 185 */       channel.awaitReadable(time, timeUnit);
/*     */     }
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public XnioExecutor getReadThread() {
/* 191 */     return this.firstChannel.getReadThread();
/*     */   }
/*     */   
/*     */   public XnioIoThread getIoThread() {
/* 195 */     return this.firstChannel.getIoThread();
/*     */   }
/*     */   
/*     */   public XnioWorker getWorker() {
/* 199 */     return this.firstChannel.getWorker();
/*     */   }
/*     */   
/*     */   public boolean isOpen() {
/* 203 */     return this.firstChannel.isOpen();
/*     */   }
/*     */   
/*     */   public void close() throws IOException {
/* 207 */     StreamSourceChannel old = this.channel;
/* 208 */     if (old != null) {
/* 209 */       this.channel = null;
/* 210 */       old.close();
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean supportsOption(Option<?> option) {
/* 215 */     return this.firstChannel.supportsOption(option);
/*     */   }
/*     */   
/*     */   public <T> T getOption(Option<T> option) throws IOException {
/* 219 */     return this.firstChannel.getOption(option);
/*     */   }
/*     */   
/*     */   public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 223 */     return this.firstChannel.setOption(option, value);
/*     */   }
/*     */   
/*     */   public StreamSourceChannel getChannel() {
/* 227 */     return this.firstChannel;
/*     */   }
/*     */   
/*     */   class BufferHolder implements StreamSourceChannel {
/*     */     private final StreamSourceChannel next;
/*     */     private final Pooled<ByteBuffer> buffer;
/*     */     
/*     */     BufferHolder(StreamSourceChannel next, Pooled<ByteBuffer> buffer) {
/* 235 */       this.next = next;
/* 236 */       this.buffer = buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     public long transferTo(long position, long count, FileChannel target) throws IOException {
/*     */       long cnt;
/*     */       try {
/* 243 */         ByteBuffer src = (ByteBuffer)this.buffer.getResource();
/* 244 */         int pos = src.position();
/* 245 */         int rem = src.remaining();
/* 246 */         if (rem > count) {
/*     */           try {
/* 248 */             src.limit(pos + (int)count);
/* 249 */             return target.write(src, position);
/*     */           } finally {
/* 251 */             src.limit(pos + rem);
/*     */           } 
/*     */         }
/* 254 */         cnt = target.write(src, position);
/* 255 */         if (cnt == rem) {
/*     */           
/* 257 */           moveToNext();
/*     */         } else {
/* 259 */           return cnt;
/*     */         } 
/* 261 */         position += cnt;
/* 262 */         count -= cnt;
/*     */       }
/* 264 */       catch (IllegalStateException ignored) {
/* 265 */         moveToNext();
/* 266 */         cnt = 0L;
/*     */       } 
/* 268 */       return cnt + this.next.transferTo(position, count, target);
/*     */     }
/*     */     
/*     */     public long transferTo(long count, ByteBuffer throughBuffer, StreamSinkChannel target) throws IOException {
/*     */       long cnt;
/* 273 */       throughBuffer.clear();
/*     */       
/*     */       try {
/* 276 */         ByteBuffer src = (ByteBuffer)this.buffer.getResource();
/* 277 */         int pos = src.position();
/* 278 */         int rem = src.remaining();
/* 279 */         if (rem > count) {
/*     */           try {
/* 281 */             src.limit(pos + (int)count);
/* 282 */             throughBuffer.limit(0);
/* 283 */             return target.write(src);
/*     */           } finally {
/* 285 */             src.limit(pos + rem);
/*     */           } 
/*     */         }
/* 288 */         cnt = target.write(src);
/* 289 */         if (cnt == rem) {
/*     */           
/* 291 */           moveToNext();
/*     */         } else {
/* 293 */           return cnt;
/*     */         }
/*     */       
/* 296 */       } catch (IllegalStateException ignored) {
/* 297 */         moveToNext();
/* 298 */         cnt = 0L;
/*     */       } 
/* 300 */       long res = this.next.transferTo(count - cnt, throughBuffer, target);
/* 301 */       return (res > 0L) ? (cnt + res) : ((cnt > 0L) ? cnt : res);
/*     */     }
/*     */     
/*     */     public long read(ByteBuffer[] dsts, int offset, int length) throws IOException {
/*     */       long cnt;
/*     */       try {
/* 307 */         ByteBuffer src = (ByteBuffer)this.buffer.getResource();
/* 308 */         cnt = Buffers.copy(dsts, offset, length, src);
/* 309 */         if (src.hasRemaining()) {
/* 310 */           return cnt;
/*     */         }
/* 312 */         StreamSourceChannel next = PushBackStreamChannel.this.channel = this.next;
/* 313 */         this.buffer.free();
/* 314 */         if (cnt > 0L && next == PushBackStreamChannel.this.firstChannel)
/*     */         {
/* 316 */           return cnt;
/*     */         }
/* 318 */       } catch (IllegalStateException ignored) {
/* 319 */         moveToNext();
/* 320 */         cnt = 0L;
/*     */       } 
/* 322 */       long res = this.next.read(dsts, offset, length);
/* 323 */       return (res > 0L) ? (res + cnt) : ((cnt > 0L) ? cnt : res);
/*     */     }
/*     */     
/*     */     public long read(ByteBuffer[] dsts) throws IOException {
/* 327 */       return read(dsts, 0, dsts.length);
/*     */     }
/*     */     
/*     */     public int read(ByteBuffer dst) throws IOException {
/*     */       int cnt;
/* 332 */       if (!dst.hasRemaining()) {
/* 333 */         return 0;
/*     */       }
/*     */       try {
/* 336 */         ByteBuffer src = (ByteBuffer)this.buffer.getResource();
/* 337 */         cnt = Buffers.copy(dst, src);
/* 338 */         if (src.hasRemaining()) {
/* 339 */           return cnt;
/*     */         }
/* 341 */         StreamSourceChannel next = moveToNext();
/* 342 */         if (cnt > 0 && next == PushBackStreamChannel.this.firstChannel)
/*     */         {
/* 344 */           return cnt;
/*     */         }
/* 346 */       } catch (IllegalStateException ignored) {
/* 347 */         moveToNext();
/* 348 */         cnt = 0;
/*     */       } 
/* 350 */       int res = this.next.read(dst);
/* 351 */       return (res > 0) ? (res + cnt) : ((cnt > 0) ? cnt : res);
/*     */     }
/*     */     
/*     */     public void close() throws IOException {
/* 355 */       this.buffer.free();
/* 356 */       this.next.close();
/*     */     }
/*     */ 
/*     */     
/*     */     public void resumeReads() {
/* 361 */       PushBackStreamChannel.this.firstChannel.wakeupReads();
/*     */     }
/*     */     
/*     */     public void shutdownReads() throws IOException {
/* 365 */       this.buffer.free();
/* 366 */       this.next.shutdownReads();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public void awaitReadable() throws IOException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public void awaitReadable(long time, TimeUnit timeUnit) throws IOException {}
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isOpen() {
/* 380 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public ChannelListener.Setter<? extends StreamSourceChannel> getReadSetter() {
/* 384 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public ChannelListener.Setter<? extends StreamSourceChannel> getCloseSetter() {
/* 388 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public void suspendReads() {
/* 392 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public boolean isReadResumed() {
/* 396 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public void wakeupReads() {
/* 400 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     @Deprecated
/*     */     public XnioExecutor getReadThread() {
/* 405 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public XnioIoThread getIoThread() {
/* 409 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public XnioWorker getWorker() {
/* 413 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public boolean supportsOption(Option<?> option) {
/* 417 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public <T> T getOption(Option<T> option) throws IOException {
/* 421 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     public <T> T setOption(Option<T> option, T value) throws IllegalArgumentException, IOException {
/* 425 */       throw new UnsupportedOperationException();
/*     */     }
/*     */     
/*     */     private final StreamSourceChannel moveToNext() {
/* 429 */       this.buffer.free();
/* 430 */       return PushBackStreamChannel.this.channel = this.next;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\channels\PushBackStreamChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */