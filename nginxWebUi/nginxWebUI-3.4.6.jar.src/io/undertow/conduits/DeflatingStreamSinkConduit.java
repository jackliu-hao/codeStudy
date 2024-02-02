/*     */ package io.undertow.conduits;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import io.undertow.server.Connectors;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.ConduitFactory;
/*     */ import io.undertow.util.Headers;
/*     */ import io.undertow.util.NewInstanceObjectPool;
/*     */ import io.undertow.util.ObjectPool;
/*     */ import io.undertow.util.PooledObject;
/*     */ import io.undertow.util.SimpleObjectPool;
/*     */ import java.io.IOException;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.ClosedChannelException;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.nio.channels.ReadableByteChannel;
/*     */ import java.nio.channels.WritableByteChannel;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.zip.Deflater;
/*     */ import org.xnio.Bits;
/*     */ import org.xnio.IoUtils;
/*     */ import org.xnio.XnioIoThread;
/*     */ import org.xnio.XnioWorker;
/*     */ import org.xnio.channels.StreamSourceChannel;
/*     */ import org.xnio.channels.SuspendableWriteChannel;
/*     */ import org.xnio.conduits.ConduitWritableByteChannel;
/*     */ import org.xnio.conduits.Conduits;
/*     */ import org.xnio.conduits.StreamSinkConduit;
/*     */ import org.xnio.conduits.WriteReadyHandler;
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
/*     */ public class DeflatingStreamSinkConduit
/*     */   implements StreamSinkConduit
/*     */ {
/*     */   protected volatile Deflater deflater;
/*     */   protected final PooledObject<Deflater> pooledObject;
/*     */   private final ConduitFactory<StreamSinkConduit> conduitFactory;
/*     */   private final HttpServerExchange exchange;
/*     */   private StreamSinkConduit next;
/*     */   private WriteReadyHandler writeReadyHandler;
/*     */   protected PooledByteBuffer currentBuffer;
/*     */   private ByteBuffer additionalBuffer;
/*  78 */   private int state = 0;
/*     */   
/*     */   private static final int SHUTDOWN = 1;
/*     */   private static final int NEXT_SHUTDOWN = 2;
/*     */   private static final int FLUSHING_BUFFER = 4;
/*     */   private static final int WRITES_RESUMED = 8;
/*     */   private static final int CLOSED = 16;
/*     */   private static final int WRITTEN_TRAILER = 32;
/*     */   
/*     */   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange) {
/*  88 */     this(conduitFactory, exchange, 8);
/*     */   }
/*     */   
/*     */   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, int deflateLevel) {
/*  92 */     this(conduitFactory, exchange, newInstanceDeflaterPool(deflateLevel));
/*     */   }
/*     */   
/*     */   public DeflatingStreamSinkConduit(ConduitFactory<StreamSinkConduit> conduitFactory, HttpServerExchange exchange, ObjectPool<Deflater> deflaterPool) {
/*  96 */     this.pooledObject = deflaterPool.allocate();
/*  97 */     this.deflater = (Deflater)this.pooledObject.getObject();
/*  98 */     this.currentBuffer = exchange.getConnection().getByteBufferPool().allocate();
/*  99 */     this.exchange = exchange;
/* 100 */     this.conduitFactory = conduitFactory;
/* 101 */     setWriteReadyHandler((WriteReadyHandler)new WriteReadyHandler.ChannelListenerHandler((SuspendableWriteChannel)Connectors.getConduitSinkChannel(exchange)));
/*     */   }
/*     */   
/*     */   public static ObjectPool<Deflater> newInstanceDeflaterPool(int deflateLevel) {
/* 105 */     return (ObjectPool<Deflater>)new NewInstanceObjectPool(() -> new Deflater(deflateLevel, true), Deflater::end);
/*     */   }
/*     */   
/*     */   public static ObjectPool<Deflater> simpleDeflaterPool(int poolSize, int deflateLevel) {
/* 109 */     return (ObjectPool<Deflater>)new SimpleObjectPool(poolSize, () -> new Deflater(deflateLevel, true), Deflater::reset, Deflater::end);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public int write(ByteBuffer src) throws IOException {
/* 115 */     if (Bits.anyAreSet(this.state, 17) || this.currentBuffer == null) {
/* 116 */       throw new ClosedChannelException();
/*     */     }
/*     */     try {
/* 119 */       if (!performFlushIfRequired()) {
/* 120 */         return 0;
/*     */       }
/* 122 */       if (src.remaining() == 0) {
/* 123 */         return 0;
/*     */       }
/*     */       
/* 126 */       if (!this.deflater.needsInput()) {
/* 127 */         deflateData(false);
/* 128 */         if (!this.deflater.needsInput()) {
/* 129 */           return 0;
/*     */         }
/*     */       } 
/* 132 */       byte[] data = new byte[src.remaining()];
/* 133 */       src.get(data);
/* 134 */       preDeflate(data);
/* 135 */       this.deflater.setInput(data);
/* 136 */       Connectors.updateResponseBytesSent(this.exchange, (0 - data.length));
/* 137 */       deflateData(false);
/* 138 */       return data.length;
/* 139 */     } catch (IOException|RuntimeException|Error e) {
/* 140 */       freeBuffer();
/* 141 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void preDeflate(byte[] data) {}
/*     */ 
/*     */   
/*     */   public long write(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 151 */     if (Bits.anyAreSet(this.state, 17) || this.currentBuffer == null) {
/* 152 */       throw new ClosedChannelException();
/*     */     }
/*     */     try {
/* 155 */       int total = 0;
/* 156 */       for (int i = offset; i < offset + length; i++) {
/* 157 */         if (srcs[i].hasRemaining()) {
/* 158 */           int ret = write(srcs[i]);
/* 159 */           total += ret;
/* 160 */           if (ret == 0) {
/* 161 */             return total;
/*     */           }
/*     */         } 
/*     */       } 
/* 165 */       return total;
/* 166 */     } catch (IOException|RuntimeException|Error e) {
/* 167 */       freeBuffer();
/* 168 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int writeFinal(ByteBuffer src) throws IOException {
/* 174 */     return Conduits.writeFinalBasic(this, src);
/*     */   }
/*     */ 
/*     */   
/*     */   public long writeFinal(ByteBuffer[] srcs, int offset, int length) throws IOException {
/* 179 */     return Conduits.writeFinalBasic(this, srcs, offset, length);
/*     */   }
/*     */ 
/*     */   
/*     */   public long transferFrom(FileChannel src, long position, long count) throws IOException {
/* 184 */     if (Bits.anyAreSet(this.state, 17)) {
/* 185 */       throw new ClosedChannelException();
/*     */     }
/* 187 */     if (!performFlushIfRequired()) {
/* 188 */       return 0L;
/*     */     }
/* 190 */     return src.transferTo(position, count, (WritableByteChannel)new ConduitWritableByteChannel(this));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public long transferFrom(StreamSourceChannel source, long count, ByteBuffer throughBuffer) throws IOException {
/* 196 */     if (Bits.anyAreSet(this.state, 17)) {
/* 197 */       throw new ClosedChannelException();
/*     */     }
/* 199 */     if (!performFlushIfRequired()) {
/* 200 */       return 0L;
/*     */     }
/* 202 */     return IoUtils.transfer((ReadableByteChannel)source, count, throughBuffer, (WritableByteChannel)new ConduitWritableByteChannel(this));
/*     */   }
/*     */ 
/*     */   
/*     */   public XnioWorker getWorker() {
/* 207 */     return this.exchange.getConnection().getWorker();
/*     */   }
/*     */ 
/*     */   
/*     */   public void suspendWrites() {
/* 212 */     if (this.next == null) {
/* 213 */       this.state &= 0xFFFFFFF7;
/*     */     } else {
/* 215 */       this.next.suspendWrites();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isWriteResumed() {
/* 222 */     if (this.next == null) {
/* 223 */       return Bits.anyAreSet(this.state, 8);
/*     */     }
/* 225 */     return this.next.isWriteResumed();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void wakeupWrites() {
/* 231 */     if (this.next == null) {
/* 232 */       resumeWrites();
/*     */     } else {
/* 234 */       this.next.wakeupWrites();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void resumeWrites() {
/* 240 */     if (this.next == null) {
/* 241 */       this.state |= 0x8;
/* 242 */       queueWriteListener();
/*     */     } else {
/* 244 */       this.next.resumeWrites();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void queueWriteListener() {
/* 249 */     this.exchange.getConnection().getIoThread().execute(new Runnable()
/*     */         {
/*     */           public void run() {
/* 252 */             if (DeflatingStreamSinkConduit.this.writeReadyHandler != null) {
/*     */               try {
/* 254 */                 DeflatingStreamSinkConduit.this.writeReadyHandler.writeReady();
/*     */               } finally {
/*     */                 
/* 257 */                 if (DeflatingStreamSinkConduit.this.next == null && DeflatingStreamSinkConduit.this.isWriteResumed()) {
/* 258 */                   DeflatingStreamSinkConduit.this.queueWriteListener();
/*     */                 }
/*     */               } 
/*     */             }
/*     */           }
/*     */         });
/*     */   }
/*     */ 
/*     */   
/*     */   public void terminateWrites() throws IOException {
/* 268 */     if (this.deflater != null) {
/* 269 */       this.deflater.finish();
/*     */     }
/* 271 */     this.state |= 0x1;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isWriteShutdown() {
/* 276 */     return Bits.anyAreSet(this.state, 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public void awaitWritable() throws IOException {
/* 281 */     if (this.next == null) {
/*     */       return;
/*     */     }
/* 284 */     this.next.awaitWritable();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void awaitWritable(long time, TimeUnit timeUnit) throws IOException {
/* 290 */     if (this.next == null) {
/*     */       return;
/*     */     }
/* 293 */     this.next.awaitWritable(time, timeUnit);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public XnioIoThread getWriteThread() {
/* 299 */     return this.exchange.getConnection().getIoThread();
/*     */   }
/*     */ 
/*     */   
/*     */   public void setWriteReadyHandler(WriteReadyHandler handler) {
/* 304 */     this.writeReadyHandler = handler;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean flush() throws IOException {
/* 309 */     if (this.currentBuffer == null) {
/* 310 */       if (Bits.anyAreSet(this.state, 2)) {
/* 311 */         return this.next.flush();
/*     */       }
/* 313 */       return true;
/*     */     } 
/*     */     
/*     */     try {
/* 317 */       boolean nextCreated = false;
/*     */       try {
/* 319 */         if (Bits.anyAreSet(this.state, 1)) {
/* 320 */           if (Bits.anyAreSet(this.state, 2)) {
/* 321 */             return this.next.flush();
/*     */           }
/* 323 */           if (!performFlushIfRequired()) {
/* 324 */             return false;
/*     */           }
/*     */           
/* 327 */           if (!this.deflater.finished()) {
/* 328 */             deflateData(false);
/*     */             
/* 330 */             if (!this.deflater.finished()) {
/* 331 */               return false;
/*     */             }
/*     */           } 
/* 334 */           ByteBuffer buffer = this.currentBuffer.getBuffer();
/* 335 */           if (Bits.allAreClear(this.state, 32)) {
/* 336 */             this.state |= 0x20;
/* 337 */             byte[] data = getTrailer();
/* 338 */             if (data != null) {
/* 339 */               Connectors.updateResponseBytesSent(this.exchange, data.length);
/* 340 */               if (this.additionalBuffer != null) {
/* 341 */                 byte[] newData = new byte[this.additionalBuffer.remaining() + data.length];
/* 342 */                 int pos = 0;
/* 343 */                 while (this.additionalBuffer.hasRemaining()) {
/* 344 */                   newData[pos++] = this.additionalBuffer.get();
/*     */                 }
/* 346 */                 for (byte aData : data) {
/* 347 */                   newData[pos++] = aData;
/*     */                 }
/* 349 */                 this.additionalBuffer = ByteBuffer.wrap(newData);
/* 350 */               } else if (Bits.anyAreSet(this.state, 4) && buffer.capacity() - buffer.remaining() >= data.length) {
/* 351 */                 buffer.compact();
/* 352 */                 buffer.put(data);
/* 353 */                 buffer.flip();
/* 354 */               } else if (data.length <= buffer.remaining() && !Bits.anyAreSet(this.state, 4)) {
/* 355 */                 buffer.put(data);
/*     */               } else {
/* 357 */                 this.additionalBuffer = ByteBuffer.wrap(data);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */ 
/*     */           
/* 363 */           if (!Bits.anyAreSet(this.state, 4)) {
/* 364 */             buffer.flip();
/* 365 */             this.state |= 0x4;
/* 366 */             if (this.next == null) {
/* 367 */               nextCreated = true;
/* 368 */               this.next = createNextChannel();
/*     */             } 
/*     */           } 
/* 371 */           if (performFlushIfRequired()) {
/* 372 */             this.state |= 0x2;
/* 373 */             freeBuffer();
/* 374 */             this.next.terminateWrites();
/* 375 */             return this.next.flush();
/*     */           } 
/* 377 */           return false;
/*     */         } 
/*     */ 
/*     */         
/* 381 */         if (Bits.allAreClear(this.state, 4)) {
/* 382 */           if (this.next == null) {
/* 383 */             nextCreated = true;
/* 384 */             this.next = createNextChannel();
/*     */           } 
/* 386 */           deflateData(true);
/* 387 */           if (Bits.allAreClear(this.state, 4)) {
/*     */             
/* 389 */             this.currentBuffer.getBuffer().flip();
/* 390 */             this.state |= 0x4;
/*     */           } 
/*     */         } 
/* 393 */         if (!performFlushIfRequired()) {
/* 394 */           return false;
/*     */         }
/* 396 */         return this.next.flush();
/*     */       } finally {
/*     */         
/* 399 */         if (nextCreated && 
/* 400 */           Bits.anyAreSet(this.state, 8) && !Bits.anyAreSet(this.state, 2)) {
/*     */           try {
/* 402 */             this.next.resumeWrites();
/* 403 */           } catch (Throwable e) {
/* 404 */             UndertowLogger.REQUEST_LOGGER.debug("Failed to resume", e);
/*     */           }
/*     */         
/*     */         }
/*     */       } 
/* 409 */     } catch (IOException|RuntimeException|Error e) {
/* 410 */       freeBuffer();
/* 411 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected byte[] getTrailer() {
/* 419 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean performFlushIfRequired() throws IOException {
/* 428 */     if (Bits.anyAreSet(this.state, 4)) {
/* 429 */       ByteBuffer[] bufs = new ByteBuffer[(this.additionalBuffer == null) ? 1 : 2];
/* 430 */       long totalLength = 0L;
/* 431 */       bufs[0] = this.currentBuffer.getBuffer();
/* 432 */       totalLength += bufs[0].remaining();
/* 433 */       if (this.additionalBuffer != null) {
/* 434 */         bufs[1] = this.additionalBuffer;
/* 435 */         totalLength += bufs[1].remaining();
/*     */       } 
/* 437 */       if (totalLength > 0L) {
/* 438 */         long total = 0L;
/* 439 */         long res = 0L;
/*     */         do {
/* 441 */           res = this.next.write(bufs, 0, bufs.length);
/* 442 */           total += res;
/* 443 */           if (res == 0L) {
/* 444 */             return false;
/*     */           }
/* 446 */         } while (total < totalLength);
/*     */       } 
/* 448 */       this.additionalBuffer = null;
/* 449 */       this.currentBuffer.getBuffer().clear();
/* 450 */       this.state &= 0xFFFFFFFB;
/*     */     } 
/* 452 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   private StreamSinkConduit createNextChannel() {
/* 457 */     if (this.deflater.finished() && Bits.allAreSet(this.state, 32)) {
/*     */ 
/*     */       
/* 460 */       int remaining = this.currentBuffer.getBuffer().remaining();
/* 461 */       if (this.additionalBuffer != null) {
/* 462 */         remaining += this.additionalBuffer.remaining();
/*     */       }
/* 464 */       if (!this.exchange.getResponseHeaders().contains(Headers.TRANSFER_ENCODING)) {
/* 465 */         this.exchange.getResponseHeaders().put(Headers.CONTENT_LENGTH, Integer.toString(remaining));
/*     */       }
/*     */     } else {
/* 468 */       this.exchange.getResponseHeaders().remove(Headers.CONTENT_LENGTH);
/*     */     } 
/* 470 */     return (StreamSinkConduit)this.conduitFactory.create();
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
/*     */   private void deflateData(boolean force) throws IOException {
/* 482 */     boolean nextCreated = false;
/* 483 */     try (PooledByteBuffer arrayPooled = this.exchange.getConnection().getByteBufferPool().getArrayBackedPool().allocate()) {
/* 484 */       PooledByteBuffer pooled = this.currentBuffer;
/* 485 */       ByteBuffer outputBuffer = pooled.getBuffer();
/*     */       
/* 487 */       boolean shutdown = Bits.anyAreSet(this.state, 1);
/* 488 */       ByteBuffer buf = arrayPooled.getBuffer();
/* 489 */       while (force || !this.deflater.needsInput() || (shutdown && !this.deflater.finished())) {
/* 490 */         int count = this.deflater.deflate(buf.array(), buf.arrayOffset(), buf.remaining(), force ? 2 : 0);
/* 491 */         Connectors.updateResponseBytesSent(this.exchange, count);
/* 492 */         if (count != 0) {
/* 493 */           int remaining = outputBuffer.remaining();
/* 494 */           if (remaining > count) {
/* 495 */             outputBuffer.put(buf.array(), buf.arrayOffset(), count); continue;
/*     */           } 
/* 497 */           if (remaining == count) {
/* 498 */             outputBuffer.put(buf.array(), buf.arrayOffset(), count);
/*     */           } else {
/* 500 */             outputBuffer.put(buf.array(), buf.arrayOffset(), remaining);
/* 501 */             this.additionalBuffer = ByteBuffer.allocate(count - remaining);
/* 502 */             this.additionalBuffer.put(buf.array(), buf.arrayOffset() + remaining, count - remaining);
/* 503 */             this.additionalBuffer.flip();
/*     */           } 
/* 505 */           outputBuffer.flip();
/* 506 */           this.state |= 0x4;
/* 507 */           if (this.next == null) {
/* 508 */             nextCreated = true;
/* 509 */             this.next = createNextChannel();
/*     */           } 
/* 511 */           if (!performFlushIfRequired()) {
/*     */             return;
/*     */           }
/*     */           continue;
/*     */         } 
/* 516 */         force = false;
/*     */       } 
/*     */     } finally {
/*     */       
/* 520 */       if (nextCreated && 
/* 521 */         Bits.anyAreSet(this.state, 8)) {
/* 522 */         this.next.resumeWrites();
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void truncateWrites() throws IOException {
/* 531 */     freeBuffer();
/* 532 */     this.state |= 0x10;
/* 533 */     this.next.truncateWrites();
/*     */   }
/*     */   
/*     */   private void freeBuffer() {
/* 537 */     if (this.currentBuffer != null) {
/* 538 */       this.currentBuffer.close();
/* 539 */       this.currentBuffer = null;
/* 540 */       this.state &= 0xFFFFFFFB;
/*     */     } 
/* 542 */     if (this.deflater != null) {
/* 543 */       this.deflater = null;
/* 544 */       this.pooledObject.close();
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\conduits\DeflatingStreamSinkConduit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */