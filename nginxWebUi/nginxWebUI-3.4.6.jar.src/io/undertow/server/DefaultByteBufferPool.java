/*     */ package io.undertow.server;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.connector.ByteBufferPool;
/*     */ import io.undertow.connector.PooledByteBuffer;
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
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
/*     */ public class DefaultByteBufferPool
/*     */   implements ByteBufferPool
/*     */ {
/*  42 */   private final ThreadLocal<ThreadLocalData> threadLocalCache = new ThreadLocal<>();
/*     */   
/*  44 */   private final List<WeakReference<ThreadLocalData>> threadLocalDataList = new ArrayList<>();
/*  45 */   private final ConcurrentLinkedQueue<ByteBuffer> queue = new ConcurrentLinkedQueue<>();
/*     */   
/*     */   private final boolean direct;
/*     */   
/*     */   private final int bufferSize;
/*     */   private final int maximumPoolSize;
/*     */   private final int threadLocalCacheSize;
/*     */   private final int leakDectionPercent;
/*     */   private int count;
/*  54 */   private volatile int currentQueueLength = 0;
/*     */   
/*  56 */   private static final AtomicIntegerFieldUpdater<DefaultByteBufferPool> currentQueueLengthUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultByteBufferPool.class, "currentQueueLength");
/*     */   
/*  58 */   private volatile int reclaimedThreadLocals = 0;
/*     */   
/*  60 */   private static final AtomicIntegerFieldUpdater<DefaultByteBufferPool> reclaimedThreadLocalsUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultByteBufferPool.class, "reclaimedThreadLocals");
/*     */ 
/*     */ 
/*     */   
/*     */   private volatile boolean closed;
/*     */ 
/*     */   
/*     */   private final DefaultByteBufferPool arrayBackedPool;
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultByteBufferPool(boolean direct, int bufferSize) {
/*  72 */     this(direct, bufferSize, -1, 12, 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultByteBufferPool(boolean direct, int bufferSize, int maximumPoolSize, int threadLocalCacheSize, int leakDecetionPercent) {
/*  81 */     this.direct = direct;
/*  82 */     this.bufferSize = bufferSize;
/*  83 */     this.maximumPoolSize = maximumPoolSize;
/*  84 */     this.threadLocalCacheSize = threadLocalCacheSize;
/*  85 */     this.leakDectionPercent = leakDecetionPercent;
/*  86 */     if (direct) {
/*  87 */       this.arrayBackedPool = new DefaultByteBufferPool(false, bufferSize, maximumPoolSize, 0, leakDecetionPercent);
/*     */     } else {
/*  89 */       this.arrayBackedPool = this;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DefaultByteBufferPool(boolean direct, int bufferSize, int maximumPoolSize, int threadLocalCacheSize) {
/* 101 */     this(direct, bufferSize, maximumPoolSize, threadLocalCacheSize, 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 106 */     return this.bufferSize;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 111 */     return this.direct;
/*     */   }
/*     */ 
/*     */   
/*     */   public PooledByteBuffer allocate() {
/* 116 */     if (this.closed) {
/* 117 */       throw UndertowMessages.MESSAGES.poolIsClosed();
/*     */     }
/* 119 */     ByteBuffer buffer = null;
/* 120 */     ThreadLocalData local = null;
/* 121 */     if (this.threadLocalCacheSize > 0) {
/* 122 */       local = this.threadLocalCache.get();
/* 123 */       if (local != null) {
/* 124 */         buffer = local.buffers.poll();
/*     */       } else {
/* 126 */         local = new ThreadLocalData();
/* 127 */         synchronized (this.threadLocalDataList) {
/* 128 */           if (this.closed) {
/* 129 */             throw UndertowMessages.MESSAGES.poolIsClosed();
/*     */           }
/* 131 */           cleanupThreadLocalData();
/* 132 */           this.threadLocalDataList.add(new WeakReference<>(local));
/* 133 */           this.threadLocalCache.set(local);
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 138 */     if (buffer == null) {
/* 139 */       buffer = this.queue.poll();
/* 140 */       if (buffer != null) {
/* 141 */         currentQueueLengthUpdater.decrementAndGet(this);
/*     */       }
/*     */     } 
/* 144 */     if (buffer == null) {
/* 145 */       if (this.direct) {
/* 146 */         buffer = ByteBuffer.allocateDirect(this.bufferSize);
/*     */       } else {
/* 148 */         buffer = ByteBuffer.allocate(this.bufferSize);
/*     */       } 
/*     */     }
/* 151 */     if (local != null && 
/* 152 */       local.allocationDepth < this.threadLocalCacheSize) {
/* 153 */       local.allocationDepth++;
/*     */     }
/*     */     
/* 156 */     buffer.clear();
/* 157 */     return new DefaultPooledBuffer(this, buffer, (this.leakDectionPercent == 0) ? false : ((++this.count % 100 < this.leakDectionPercent)));
/*     */   }
/*     */ 
/*     */   
/*     */   public ByteBufferPool getArrayBackedPool() {
/* 162 */     return this.arrayBackedPool;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void cleanupThreadLocalData() {
/* 168 */     int size = this.threadLocalDataList.size();
/*     */     
/* 170 */     if (this.reclaimedThreadLocals > size / 4) {
/* 171 */       int j = 0; int i;
/* 172 */       for (i = 0; i < size; i++) {
/* 173 */         WeakReference<ThreadLocalData> ref = this.threadLocalDataList.get(i);
/* 174 */         if (ref.get() != null) {
/* 175 */           this.threadLocalDataList.set(j++, ref);
/*     */         }
/*     */       } 
/* 178 */       for (i = size - 1; i >= j; i--)
/*     */       {
/* 180 */         this.threadLocalDataList.remove(i);
/*     */       }
/* 182 */       reclaimedThreadLocalsUpdater.addAndGet(this, -1 * (size - j));
/*     */     } 
/*     */   }
/*     */   
/*     */   private void freeInternal(ByteBuffer buffer) {
/* 187 */     if (this.closed) {
/* 188 */       DirectByteBufferDeallocator.free(buffer);
/*     */       return;
/*     */     } 
/* 191 */     ThreadLocalData local = this.threadLocalCache.get();
/* 192 */     if (local != null && 
/* 193 */       local.allocationDepth > 0) {
/* 194 */       local.allocationDepth--;
/* 195 */       if (local.buffers.size() < this.threadLocalCacheSize) {
/* 196 */         local.buffers.add(buffer);
/*     */         
/*     */         return;
/*     */       } 
/*     */     } 
/* 201 */     queueIfUnderMax(buffer);
/*     */   }
/*     */ 
/*     */   
/*     */   private void queueIfUnderMax(ByteBuffer buffer) {
/*     */     while (true) {
/* 207 */       int size = this.currentQueueLength;
/* 208 */       if (size > this.maximumPoolSize) {
/* 209 */         DirectByteBufferDeallocator.free(buffer);
/*     */         return;
/*     */       } 
/* 212 */       if (currentQueueLengthUpdater.compareAndSet(this, size, size + 1)) {
/* 213 */         this.queue.add(buffer);
/*     */         return;
/*     */       } 
/*     */     } 
/*     */   } public void close() {
/* 218 */     if (this.closed) {
/*     */       return;
/*     */     }
/* 221 */     this.closed = true;
/* 222 */     this.queue.clear();
/*     */     
/* 224 */     synchronized (this.threadLocalDataList) {
/* 225 */       for (WeakReference<ThreadLocalData> ref : this.threadLocalDataList) {
/* 226 */         ThreadLocalData local = ref.get();
/* 227 */         if (local != null) {
/* 228 */           local.buffers.clear();
/*     */         }
/* 230 */         ref.clear();
/*     */       } 
/* 232 */       this.threadLocalDataList.clear();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void finalize() throws Throwable {
/* 238 */     super.finalize();
/* 239 */     close();
/*     */   }
/*     */   
/*     */   private static class DefaultPooledBuffer
/*     */     implements PooledByteBuffer
/*     */   {
/*     */     private final DefaultByteBufferPool pool;
/*     */     private final DefaultByteBufferPool.LeakDetector leakDetector;
/*     */     private ByteBuffer buffer;
/* 248 */     private volatile int referenceCount = 1;
/* 249 */     private static final AtomicIntegerFieldUpdater<DefaultPooledBuffer> referenceCountUpdater = AtomicIntegerFieldUpdater.newUpdater(DefaultPooledBuffer.class, "referenceCount");
/*     */     
/*     */     DefaultPooledBuffer(DefaultByteBufferPool pool, ByteBuffer buffer, boolean detectLeaks) {
/* 252 */       this.pool = pool;
/* 253 */       this.buffer = buffer;
/* 254 */       this.leakDetector = detectLeaks ? new DefaultByteBufferPool.LeakDetector() : null;
/*     */     }
/*     */ 
/*     */     
/*     */     public ByteBuffer getBuffer() {
/* 259 */       if (this.referenceCount == 0) {
/* 260 */         throw UndertowMessages.MESSAGES.bufferAlreadyFreed();
/*     */       }
/* 262 */       return this.buffer;
/*     */     }
/*     */ 
/*     */     
/*     */     public void close() {
/* 267 */       if (referenceCountUpdater.compareAndSet(this, 1, 0)) {
/* 268 */         if (this.leakDetector != null) {
/* 269 */           this.leakDetector.closed = true;
/*     */         }
/* 271 */         this.pool.freeInternal(this.buffer);
/* 272 */         this.buffer = null;
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isOpen() {
/* 278 */       return (this.referenceCount > 0);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 283 */       return "DefaultPooledBuffer{buffer=" + this.buffer + ", referenceCount=" + this.referenceCount + '}';
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private class ThreadLocalData
/*     */   {
/* 291 */     ArrayDeque<ByteBuffer> buffers = new ArrayDeque<>(DefaultByteBufferPool.this.threadLocalCacheSize);
/* 292 */     int allocationDepth = 0;
/*     */ 
/*     */     
/*     */     protected void finalize() throws Throwable {
/* 296 */       super.finalize();
/* 297 */       DefaultByteBufferPool.reclaimedThreadLocalsUpdater.incrementAndGet(DefaultByteBufferPool.this);
/* 298 */       if (this.buffers != null) {
/*     */         ByteBuffer buffer;
/*     */         
/* 301 */         while ((buffer = this.buffers.poll()) != null)
/* 302 */           DefaultByteBufferPool.this.queueIfUnderMax(buffer); 
/*     */       } 
/*     */     }
/*     */     
/*     */     private ThreadLocalData() {}
/*     */   }
/*     */   
/*     */   private static class LeakDetector {
/*     */     volatile boolean closed = false;
/*     */     private final Throwable allocationPoint;
/*     */     
/*     */     private LeakDetector() {
/* 314 */       this.allocationPoint = new Throwable("Buffer leak detected");
/*     */     }
/*     */ 
/*     */     
/*     */     protected void finalize() throws Throwable {
/* 319 */       super.finalize();
/* 320 */       if (!this.closed)
/* 321 */         this.allocationPoint.printStackTrace(); 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\DefaultByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */