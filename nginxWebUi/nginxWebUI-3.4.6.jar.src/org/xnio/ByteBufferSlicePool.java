/*     */ package org.xnio;
/*     */ 
/*     */ import java.lang.ref.WeakReference;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Queue;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
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
/*     */ public final class ByteBufferSlicePool
/*     */   implements Pool<ByteBuffer>
/*     */ {
/*     */   private static final int LOCAL_LENGTH;
/*     */   
/*     */   static {
/*     */     int val;
/*  55 */     String value = AccessController.<String>doPrivileged(new ReadPropertyAction("xnio.bufferpool.threadlocal.size", "12"));
/*     */     
/*     */     try {
/*  58 */       val = Integer.parseInt(value);
/*  59 */     } catch (NumberFormatException ignored) {
/*  60 */       val = 12;
/*     */     } 
/*  62 */     LOCAL_LENGTH = val;
/*     */   }
/*     */   
/*  65 */   private static final Queue<ByteBuffer> FREE_DIRECT_BUFFERS = new ConcurrentLinkedQueue<>();
/*     */ 
/*     */   
/*  68 */   private final Set<Ref> refSet = Collections.synchronizedSet(new HashSet<>());
/*     */   private final Queue<Slice> sliceQueue;
/*     */   private final BufferAllocator<ByteBuffer> allocator;
/*     */   private final int bufferSize;
/*     */   private final int buffersPerRegion;
/*     */   private final int threadLocalQueueSize;
/*     */   private final List<ByteBuffer> directBuffers;
/*  75 */   private final ThreadLocal<ThreadLocalCache> localQueueHolder = new ThreadLocalCacheWrapper(this);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize, int threadLocalQueueSize) {
/*  86 */     if (bufferSize <= 0) {
/*  87 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/*  89 */     if (maxRegionSize < bufferSize) {
/*  90 */       throw Messages.msg.parameterOutOfRange("bufferSize");
/*     */     }
/*  92 */     this.buffersPerRegion = maxRegionSize / bufferSize;
/*  93 */     this.bufferSize = bufferSize;
/*  94 */     this.allocator = allocator;
/*  95 */     this.sliceQueue = new ConcurrentLinkedQueue<>();
/*  96 */     this.threadLocalQueueSize = threadLocalQueueSize;
/*     */     
/*  98 */     if (allocator == BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR) {
/*  99 */       this.directBuffers = Collections.synchronizedList(new ArrayList<>());
/*     */     } else {
/* 101 */       this.directBuffers = null;
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
/*     */   public ByteBufferSlicePool(BufferAllocator<ByteBuffer> allocator, int bufferSize, int maxRegionSize) {
/* 113 */     this(allocator, bufferSize, maxRegionSize, LOCAL_LENGTH);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBufferSlicePool(int bufferSize, int maxRegionSize) {
/* 123 */     this(BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR, bufferSize, maxRegionSize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Pooled<ByteBuffer> allocate() {
/* 129 */     if (this.threadLocalQueueSize > 0) {
/* 130 */       ThreadLocalCache localCache = this.localQueueHolder.get();
/* 131 */       if (localCache.outstanding != this.threadLocalQueueSize) {
/* 132 */         localCache.outstanding++;
/*     */       }
/* 134 */       Slice slice1 = localCache.queue.poll();
/* 135 */       if (slice1 != null) {
/* 136 */         return new PooledByteBuffer(slice1, slice1.slice());
/*     */       }
/*     */     } 
/* 139 */     Queue<Slice> sliceQueue = this.sliceQueue;
/* 140 */     Slice slice = sliceQueue.poll();
/* 141 */     if (slice != null) {
/* 142 */       return new PooledByteBuffer(slice, slice.slice());
/*     */     }
/* 144 */     synchronized (sliceQueue) {
/* 145 */       slice = sliceQueue.poll();
/* 146 */       if (slice != null) {
/* 147 */         return new PooledByteBuffer(slice, slice.slice());
/*     */       }
/* 149 */       Slice newSlice = allocateSlices(this.buffersPerRegion, this.bufferSize);
/* 150 */       return new PooledByteBuffer(newSlice, newSlice.slice());
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private Slice allocateSlices(int buffersPerRegion, int bufferSize) {
/* 156 */     if (this.directBuffers != null) {
/* 157 */       ByteBuffer region = FREE_DIRECT_BUFFERS.poll();
/*     */       try {
/* 159 */         if (region != null) {
/* 160 */           return sliceReusedBuffer(region, buffersPerRegion, bufferSize);
/*     */         }
/* 162 */         region = this.allocator.allocate(buffersPerRegion * bufferSize);
/* 163 */         return sliceAllocatedBuffer(region, buffersPerRegion, bufferSize);
/*     */       }
/*     */       finally {
/*     */         
/* 167 */         this.directBuffers.add(region);
/*     */       } 
/*     */     } 
/* 170 */     return sliceAllocatedBuffer(this.allocator
/* 171 */         .allocate(buffersPerRegion * bufferSize), buffersPerRegion, bufferSize);
/*     */   }
/*     */ 
/*     */   
/*     */   private Slice sliceReusedBuffer(ByteBuffer region, int buffersPerRegion, int bufferSize) {
/* 176 */     int maxI = Math.min(buffersPerRegion, region.capacity() / bufferSize);
/*     */     
/* 178 */     int idx = bufferSize;
/* 179 */     for (int i = 1; i < maxI; i++) {
/* 180 */       this.sliceQueue.add(new Slice(region, idx, bufferSize));
/* 181 */       idx += bufferSize;
/*     */     } 
/*     */     
/* 184 */     if (maxI == 0)
/* 185 */       return allocateSlices(buffersPerRegion, bufferSize); 
/* 186 */     if (maxI < buffersPerRegion)
/* 187 */       this.sliceQueue.add(allocateSlices(buffersPerRegion - maxI, bufferSize)); 
/* 188 */     return new Slice(region, 0, bufferSize);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private Slice sliceAllocatedBuffer(ByteBuffer region, int buffersPerRegion, int bufferSize) {
/* 194 */     int idx = bufferSize;
/* 195 */     for (int i = 1; i < buffersPerRegion; i++) {
/* 196 */       this.sliceQueue.add(new Slice(region, idx, bufferSize));
/* 197 */       idx += bufferSize;
/*     */     } 
/* 199 */     return new Slice(region, 0, bufferSize);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clean() {
/* 208 */     ThreadLocalCache localCache = this.localQueueHolder.get();
/* 209 */     if (!localCache.queue.isEmpty()) {
/* 210 */       localCache.queue.clear();
/*     */     }
/* 212 */     if (!this.sliceQueue.isEmpty()) {
/* 213 */       this.sliceQueue.clear();
/*     */     }
/*     */     
/* 216 */     if (this.directBuffers != null)
/*     */     {
/* 218 */       FREE_DIRECT_BUFFERS.addAll(this.directBuffers);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getBufferSize() {
/* 226 */     return this.bufferSize;
/*     */   }
/*     */   
/*     */   private ThreadLocalCache createThreadLocalCache() {
/* 230 */     return new ThreadLocalCache(this);
/*     */   }
/*     */   
/*     */   private void freeThreadLocalCache(ThreadLocalCache cache) {
/* 234 */     ArrayDeque<Slice> deque = cache.queue;
/* 235 */     Slice slice = deque.poll();
/* 236 */     while (slice != null) {
/* 237 */       doFree(slice);
/* 238 */       slice = deque.poll();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void doFree(Slice region) {
/* 243 */     if (this.threadLocalQueueSize > 0) {
/* 244 */       ThreadLocalCache localCache = this.localQueueHolder.get();
/* 245 */       boolean cacheOk = false;
/* 246 */       if (localCache.outstanding > 0) {
/* 247 */         localCache.outstanding--;
/* 248 */         cacheOk = true;
/*     */       } 
/* 250 */       ArrayDeque<Slice> localQueue = localCache.queue;
/* 251 */       if (localQueue.size() == this.threadLocalQueueSize || !cacheOk) {
/* 252 */         this.sliceQueue.add(region);
/*     */       } else {
/* 254 */         localQueue.add(region);
/*     */       } 
/*     */     } else {
/* 257 */       this.sliceQueue.add(region);
/*     */     } 
/*     */   }
/*     */   
/*     */   private final class PooledByteBuffer implements Pooled<ByteBuffer> {
/*     */     private final ByteBufferSlicePool.Slice region;
/*     */     ByteBuffer buffer;
/*     */     
/*     */     PooledByteBuffer(ByteBufferSlicePool.Slice region, ByteBuffer buffer) {
/* 266 */       this.region = region;
/* 267 */       this.buffer = buffer;
/*     */     }
/*     */     
/*     */     public void discard() {
/* 271 */       ByteBuffer buffer = this.buffer;
/* 272 */       this.buffer = null;
/* 273 */       if (buffer != null)
/*     */       {
/* 275 */         ByteBufferSlicePool.this.refSet.add(new ByteBufferSlicePool.Ref(buffer, this.region));
/*     */       }
/*     */     }
/*     */     
/*     */     public void free() {
/* 280 */       ByteBuffer buffer = this.buffer;
/* 281 */       this.buffer = null;
/* 282 */       if (buffer != null)
/*     */       {
/* 284 */         ByteBufferSlicePool.this.doFree(this.region);
/*     */       }
/*     */     }
/*     */     
/*     */     public ByteBuffer getResource() {
/* 289 */       ByteBuffer buffer = this.buffer;
/* 290 */       if (buffer == null) {
/* 291 */         throw Messages.msg.bufferFreed();
/*     */       }
/* 293 */       return buffer;
/*     */     }
/*     */     
/*     */     public void close() {
/* 297 */       free();
/*     */     }
/*     */     
/*     */     public String toString() {
/* 301 */       return "Pooled buffer " + this.buffer;
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static final class Slice
/*     */   {
/*     */     private final ByteBuffer parent;
/*     */ 
/*     */     
/*     */     private Slice(ByteBuffer parent, int start, int size) {
/* 313 */       this.parent = (ByteBuffer)parent.duplicate().position(start).limit(start + size);
/*     */     }
/*     */     
/*     */     ByteBuffer slice() {
/* 317 */       return this.parent.slice();
/*     */     }
/*     */   }
/*     */   
/*     */   final class Ref extends AutomaticReference<ByteBuffer> {
/*     */     private final ByteBufferSlicePool.Slice region;
/*     */     
/*     */     private Ref(ByteBuffer referent, ByteBufferSlicePool.Slice region) {
/* 325 */       super(referent, AutomaticReference.PERMIT);
/* 326 */       this.region = region;
/*     */     }
/*     */     
/*     */     protected void free() {
/* 330 */       ByteBufferSlicePool.this.doFree(this.region);
/* 331 */       ByteBufferSlicePool.this.refSet.remove(this);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   static final class ThreadLocalCache
/*     */   {
/*     */     final WeakReference<ByteBufferSlicePool> pool;
/*     */ 
/*     */     
/*     */     final ArrayDeque<ByteBufferSlicePool.Slice> queue;
/*     */ 
/*     */     
/* 345 */     int outstanding = 0;
/*     */     
/*     */     ThreadLocalCache(ByteBufferSlicePool pool) {
/* 348 */       this.pool = new WeakReference<>(pool);
/* 349 */       this.queue = new ArrayDeque<ByteBufferSlicePool.Slice>(pool.threadLocalQueueSize)
/*     */         {
/*     */           
/*     */           protected void finalize()
/*     */           {
/* 354 */             ByteBufferSlicePool pool = ByteBufferSlicePool.ThreadLocalCache.this.pool.get();
/* 355 */             if (pool == null)
/*     */               return; 
/* 357 */             ArrayDeque<ByteBufferSlicePool.Slice> deque = ByteBufferSlicePool.ThreadLocalCache.this.queue;
/* 358 */             ByteBufferSlicePool.Slice slice = deque.poll();
/* 359 */             while (slice != null) {
/* 360 */               pool.doFree(slice);
/* 361 */               slice = deque.poll();
/*     */             } 
/*     */           }
/*     */         };
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static class ThreadLocalCacheWrapper
/*     */     extends ThreadLocal<ThreadLocalCache>
/*     */   {
/*     */     private final WeakReference<ByteBufferSlicePool> pool;
/*     */ 
/*     */     
/*     */     ThreadLocalCacheWrapper(ByteBufferSlicePool pool) {
/* 376 */       this.pool = new WeakReference<>(pool);
/*     */     }
/*     */     
/*     */     protected ByteBufferSlicePool.ThreadLocalCache initialValue() {
/* 380 */       ByteBufferSlicePool pool = this.pool.get();
/* 381 */       if (pool != null)
/*     */       {
/* 383 */         return pool.createThreadLocalCache();
/*     */       }
/* 385 */       return null;
/*     */     }
/*     */     
/*     */     public void remove() {
/* 389 */       ByteBufferSlicePool pool = this.pool.get();
/* 390 */       ByteBufferSlicePool.ThreadLocalCache cache = get();
/* 391 */       if (pool != null && cache != null)
/*     */       {
/* 393 */         pool.freeThreadLocalCache(cache);
/*     */       }
/* 395 */       super.remove();
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ByteBufferSlicePool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */