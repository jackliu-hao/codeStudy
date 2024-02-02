/*     */ package org.xnio;
/*     */ 
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.concurrent.ConcurrentLinkedQueue;
/*     */ import org.wildfly.common.Assert;
/*     */ import org.wildfly.common.cpu.CacheInfo;
/*     */ import org.wildfly.common.function.ExceptionBiConsumer;
/*     */ import org.wildfly.common.function.ExceptionBiFunction;
/*     */ import org.wildfly.common.function.ExceptionConsumer;
/*     */ import org.wildfly.common.function.ExceptionFunction;
/*     */ import org.wildfly.common.function.ExceptionRunnable;
/*     */ import org.wildfly.common.function.ExceptionSupplier;
/*     */ import org.wildfly.common.function.Functions;
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
/*     */ public abstract class ByteBufferPool
/*     */ {
/*  47 */   private static final boolean sliceLargeBuffers = Boolean.parseBoolean(System.getProperty("xnio.buffer.slice-large-buffers", "true"));
/*     */ 
/*     */   
/*  50 */   private final ConcurrentLinkedQueue<ByteBuffer> masterQueue = new ConcurrentLinkedQueue<>();
/*  51 */   private final ThreadLocal<Cache> threadLocalCache = ThreadLocal.withInitial(this::getDefaultCache);
/*  52 */   private final Cache defaultCache = new DefaultCache();
/*     */   private final int size;
/*     */   private final boolean direct;
/*     */   
/*     */   ByteBufferPool(int size, boolean direct) {
/*  57 */     assert Integer.bitCount(size) == 1;
/*  58 */     assert size >= 16;
/*  59 */     assert size <= 1073741824;
/*  60 */     this.size = size;
/*  61 */     this.direct = direct;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int LARGE_SIZE = 1048576;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int MEDIUM_SIZE = 8192;
/*     */ 
/*     */ 
/*     */   
/*     */   public static final int SMALL_SIZE = 64;
/*     */ 
/*     */ 
/*     */   
/*  79 */   static final int CACHE_LINE_SIZE = Math.max(64, CacheInfo.getSmallestDataCacheLineSize());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static final ByteBufferPool LARGE_DIRECT = create(1048576, true);
/*     */ 
/*     */ 
/*     */   
/*  88 */   public static final ByteBufferPool MEDIUM_DIRECT = sliceLargeBuffers ? subPool(LARGE_DIRECT, 8192) : create(8192, true);
/*     */ 
/*     */ 
/*     */   
/*  92 */   public static final ByteBufferPool SMALL_DIRECT = subPool(MEDIUM_DIRECT, 64);
/*     */ 
/*     */ 
/*     */   
/*  96 */   public static final ByteBufferPool LARGE_HEAP = create(1048576, false);
/*     */ 
/*     */ 
/*     */   
/* 100 */   public static final ByteBufferPool MEDIUM_HEAP = create(8192, false);
/*     */ 
/*     */ 
/*     */   
/* 104 */   public static final ByteBufferPool SMALL_HEAP = create(64, false);
/*     */   
/*     */   public static final class Set
/*     */   {
/*     */     private final ByteBufferPool small;
/*     */     private final ByteBufferPool normal;
/*     */     private final ByteBufferPool large;
/*     */     
/*     */     Set(ByteBufferPool small, ByteBufferPool normal, ByteBufferPool large) {
/* 113 */       this.small = small;
/* 114 */       this.normal = normal;
/* 115 */       this.large = large;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBufferPool getSmall() {
/* 124 */       return this.small;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBufferPool getNormal() {
/* 133 */       return this.normal;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public ByteBufferPool getLarge() {
/* 142 */       return this.large;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     public static final Set DIRECT = new Set(ByteBufferPool.SMALL_DIRECT, ByteBufferPool.MEDIUM_DIRECT, ByteBufferPool.LARGE_DIRECT);
/*     */ 
/*     */ 
/*     */     
/* 152 */     public static final Set HEAP = new Set(ByteBufferPool.SMALL_HEAP, ByteBufferPool.MEDIUM_HEAP, ByteBufferPool.LARGE_HEAP);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ByteBuffer allocate() {
/* 161 */     return ((Cache)this.threadLocalCache.get()).allocate();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void allocate(ByteBuffer[] array, int offs) {
/* 171 */     allocate(array, offs, array.length - offs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void allocate(ByteBuffer[] array, int offs, int len) {
/* 182 */     Assert.checkNotNullParam("array", array);
/* 183 */     Assert.checkArrayBounds((Object[])array, offs, len);
/* 184 */     for (int i = 0; i < len; i++) {
/* 185 */       array[offs + i] = allocate();
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
/*     */   public static void free(ByteBuffer buffer) {
/* 197 */     Assert.checkNotNullParam("buffer", buffer);
/* 198 */     int size = buffer.capacity();
/* 199 */     if (Integer.bitCount(size) == 1 && !buffer.isReadOnly()) {
/* 200 */       if (buffer.isDirect()) {
/* 201 */         if (size == 8192) {
/* 202 */           MEDIUM_DIRECT.doFree(buffer);
/* 203 */         } else if (size == 64) {
/* 204 */           SMALL_DIRECT.doFree(buffer);
/* 205 */         } else if (size == 1048576) {
/* 206 */           LARGE_DIRECT.doFree(buffer);
/*     */         }
/*     */       
/* 209 */       } else if (size == 8192) {
/* 210 */         MEDIUM_HEAP.doFree(buffer);
/* 211 */       } else if (size == 64) {
/* 212 */         SMALL_HEAP.doFree(buffer);
/* 213 */       } else if (size == 1048576) {
/* 214 */         LARGE_HEAP.doFree(buffer);
/*     */       } 
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
/*     */ 
/*     */   
/*     */   public static void free(ByteBuffer[] array, int offs, int len) {
/* 229 */     Assert.checkArrayBounds((Object[])array, offs, len);
/* 230 */     for (int i = 0; i < len; i++) {
/* 231 */       ByteBuffer buffer = array[offs + i];
/* 232 */       if (buffer != null) {
/*     */ 
/*     */         
/* 235 */         int size = buffer.capacity();
/* 236 */         if (Integer.bitCount(size) == 1 && !buffer.isReadOnly()) {
/* 237 */           if (buffer.isDirect()) {
/* 238 */             if (!(buffer instanceof java.nio.MappedByteBuffer)) {
/* 239 */               if (size == 8192) {
/* 240 */                 MEDIUM_DIRECT.doFree(buffer);
/* 241 */               } else if (size == 64) {
/* 242 */                 SMALL_DIRECT.doFree(buffer);
/* 243 */               } else if (size == 1048576) {
/* 244 */                 LARGE_DIRECT.doFree(buffer);
/*     */               }
/*     */             
/*     */             }
/* 248 */           } else if (size == 8192) {
/* 249 */             MEDIUM_HEAP.doFree(buffer);
/* 250 */           } else if (size == 64) {
/* 251 */             SMALL_HEAP.doFree(buffer);
/* 252 */           } else if (size == 1048576) {
/* 253 */             LARGE_HEAP.doFree(buffer);
/*     */           } 
/*     */         }
/*     */         
/* 257 */         array[offs + i] = null;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void zeroAndFree(ByteBuffer buffer) {
/* 267 */     Buffers.zero(buffer);
/* 268 */     free(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDirect() {
/* 276 */     return this.direct;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getSize() {
/* 285 */     return this.size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void flushCaches() {
/* 293 */     ((Cache)this.threadLocalCache.get()).flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void flushAllCaches() {
/* 301 */     SMALL_HEAP.flushCaches();
/* 302 */     MEDIUM_HEAP.flushCaches();
/* 303 */     LARGE_HEAP.flushCaches();
/* 304 */     SMALL_DIRECT.flushCaches();
/* 305 */     MEDIUM_DIRECT.flushCaches();
/* 306 */     LARGE_DIRECT.flushCaches();
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
/*     */   public <T, U, E extends Exception> void acceptWithCacheEx(int cacheSize, ExceptionBiConsumer<T, U, E> consumer, T param1, U param2) throws E {
/* 324 */     Assert.checkMinimumParameter("cacheSize", 0, cacheSize);
/* 325 */     Assert.checkNotNullParam("consumer", consumer);
/* 326 */     ThreadLocal<Cache> threadLocalCache = this.threadLocalCache;
/* 327 */     Cache parent = threadLocalCache.get();
/*     */     
/* 329 */     if (cacheSize == 0) {
/* 330 */       consumer.accept(param1, param2); return;
/*     */     } 
/* 332 */     if (cacheSize <= 64) {
/* 333 */       Cache cache1; if (cacheSize == 1) {
/* 334 */         cache1 = new OneCache(parent);
/* 335 */       } else if (cacheSize == 2) {
/* 336 */         cache1 = new TwoCache(parent);
/*     */       } else {
/* 338 */         cache1 = new MultiCache(parent, cacheSize);
/*     */       } 
/* 340 */       threadLocalCache.set(cache1);
/*     */       try {
/* 342 */         consumer.accept(param1, param2);
/*     */         return;
/*     */       } finally {
/* 345 */         threadLocalCache.set(parent);
/* 346 */         cache1.destroy();
/*     */       } 
/*     */     } 
/* 349 */     Cache cache = new MultiCache(parent, 64);
/* 350 */     threadLocalCache.set(cache);
/*     */     try {
/* 352 */       acceptWithCacheEx(cacheSize - 64, consumer, param1, param2);
/*     */       return;
/*     */     } finally {
/* 355 */       cache.destroy();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, E extends Exception> void acceptWithCacheEx(int cacheSize, ExceptionConsumer<T, E> consumer, T param) throws E {
/* 373 */     Assert.checkNotNullParam("consumer", consumer);
/* 374 */     acceptWithCacheEx(cacheSize, Functions.exceptionConsumerBiConsumer(), consumer, param);
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
/*     */   public <E extends Exception> void runWithCacheEx(int cacheSize, ExceptionRunnable<E> runnable) throws E {
/* 388 */     Assert.checkNotNullParam("runnable", runnable);
/* 389 */     acceptWithCacheEx(cacheSize, Functions.exceptionRunnableConsumer(), runnable);
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
/*     */   public void runWithCache(int cacheSize, Runnable runnable) {
/* 401 */     Assert.checkNotNullParam("runnable", runnable);
/*     */     
/* 403 */     acceptWithCacheEx(cacheSize, Runnable::run, runnable);
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
/*     */   public <T, U, R, E extends Exception> R applyWithCacheEx(int cacheSize, ExceptionBiFunction<T, U, R, E> function, T param1, U param2) throws E {
/* 423 */     Assert.checkMinimumParameter("cacheSize", 0, cacheSize);
/* 424 */     Assert.checkNotNullParam("function", function);
/* 425 */     ThreadLocal<Cache> threadLocalCache = this.threadLocalCache;
/* 426 */     Cache parent = threadLocalCache.get();
/*     */     
/* 428 */     if (cacheSize == 0)
/* 429 */       return (R)function.apply(param1, param2); 
/* 430 */     if (cacheSize <= 64) {
/* 431 */       Cache cache1; if (cacheSize == 1) {
/* 432 */         cache1 = new OneCache(parent);
/* 433 */       } else if (cacheSize == 2) {
/* 434 */         cache1 = new TwoCache(parent);
/*     */       } else {
/* 436 */         cache1 = new MultiCache(parent, cacheSize);
/*     */       } 
/* 438 */       threadLocalCache.set(cache1);
/*     */       try {
/* 440 */         return (R)function.apply(param1, param2);
/*     */       } finally {
/* 442 */         threadLocalCache.set(parent);
/* 443 */         cache1.destroy();
/*     */       } 
/*     */     } 
/* 446 */     Cache cache = new MultiCache(parent, 64);
/* 447 */     threadLocalCache.set(cache);
/*     */     try {
/* 449 */       return (R)applyWithCacheEx(cacheSize - 64, (ExceptionBiFunction)function, param1, param2);
/*     */     } finally {
/* 451 */       cache.destroy();
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public <T, R, E extends Exception> R applyWithCacheEx(int cacheSize, ExceptionFunction<T, R, E> function, T param) throws E {
/* 471 */     return applyWithCacheEx(cacheSize, Functions.exceptionFunctionBiFunction(), function, param);
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
/*     */   public <R, E extends Exception> R getWithCacheEx(int cacheSize, ExceptionSupplier<R, E> supplier) throws E {
/* 487 */     return applyWithCacheEx(cacheSize, Functions.exceptionSupplierFunction(), supplier);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   Cache getDefaultCache() {
/* 493 */     return this.defaultCache;
/*     */   }
/*     */   
/*     */   ConcurrentLinkedQueue<ByteBuffer> getMasterQueue() {
/* 497 */     return this.masterQueue;
/*     */   }
/*     */   
/*     */   private ByteBuffer allocateMaster() {
/* 501 */     ByteBuffer byteBuffer = this.masterQueue.poll();
/* 502 */     if (byteBuffer == null) {
/* 503 */       byteBuffer = createBuffer();
/*     */     }
/* 505 */     return byteBuffer;
/*     */   }
/*     */   
/*     */   static ByteBufferPool create(int size, boolean direct) {
/* 509 */     assert Integer.bitCount(size) == 1;
/* 510 */     assert size >= 16;
/* 511 */     assert size <= 1073741824;
/* 512 */     return new ByteBufferPool(size, direct) {
/*     */         ByteBuffer createBuffer() {
/* 514 */           return isDirect() ? ByteBuffer.allocateDirect(getSize()) : ByteBuffer.allocate(getSize());
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */   
/*     */   static ByteBufferPool subPool(final ByteBufferPool parent, int size) {
/* 521 */     assert Integer.bitCount(size) == 1;
/* 522 */     assert Integer.bitCount(parent.getSize()) == 1;
/* 523 */     assert size >= 16;
/* 524 */     assert size < parent.getSize();
/*     */     
/* 526 */     assert parent.getSize() % size == 0;
/* 527 */     return new ByteBufferPool(size, parent.isDirect()) {
/*     */         ByteBuffer createBuffer() {
/* 529 */           synchronized (this) {
/*     */             
/* 531 */             ByteBuffer appearing = getMasterQueue().poll();
/* 532 */             if (appearing != null) {
/* 533 */               return appearing;
/*     */             }
/* 535 */             ByteBuffer parentBuffer = parent.allocate();
/* 536 */             int size = getSize();
/* 537 */             ByteBuffer result = Buffers.slice(parentBuffer, size);
/* 538 */             while (parentBuffer.hasRemaining()) {
/*     */               
/* 540 */               if (size < CACHE_LINE_SIZE) {
/* 541 */                 Buffers.skip(parentBuffer, CACHE_LINE_SIZE - size);
/*     */               }
/* 543 */               doFree(Buffers.slice(parentBuffer, size));
/*     */             } 
/* 545 */             return result;
/*     */           } 
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   final void freeMaster(ByteBuffer buffer) {
/* 554 */     this.masterQueue.add(buffer);
/*     */   }
/*     */   
/*     */   final void doFree(ByteBuffer buffer) {
/* 558 */     assert buffer.capacity() == this.size;
/* 559 */     assert buffer.isDirect() == this.direct;
/* 560 */     buffer.clear();
/* 561 */     ((Cache)this.threadLocalCache.get()).free(buffer);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   abstract ByteBuffer createBuffer();
/*     */ 
/*     */ 
/*     */   
/*     */   static final class OneCache
/*     */     implements Cache
/*     */   {
/*     */     private final ByteBufferPool.Cache parent;
/*     */ 
/*     */     
/*     */     private ByteBuffer buffer;
/*     */ 
/*     */ 
/*     */     
/*     */     OneCache(ByteBufferPool.Cache parent) {
/* 581 */       this.parent = parent;
/*     */     }
/*     */     
/*     */     public void free(ByteBuffer bb) {
/* 585 */       if (this.buffer == null) {
/* 586 */         this.buffer = bb;
/*     */       } else {
/* 588 */         this.parent.free(bb);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flushBuffer(ByteBuffer bb) {
/* 593 */       this.parent.flushBuffer(bb);
/*     */     }
/*     */     
/*     */     public ByteBuffer allocate() {
/* 597 */       if (this.buffer != null)
/* 598 */         try { return this.buffer; }
/*     */         finally
/* 600 */         { this.buffer = null; }
/*     */          
/* 602 */       return this.parent.allocate();
/*     */     }
/*     */ 
/*     */     
/*     */     public void destroy() {
/* 607 */       ByteBuffer buffer = this.buffer;
/* 608 */       if (buffer != null) {
/* 609 */         this.buffer = null;
/* 610 */         this.parent.free(buffer);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flush() {
/* 615 */       ByteBuffer buffer = this.buffer;
/* 616 */       if (buffer != null) {
/* 617 */         this.buffer = null;
/* 618 */         flushBuffer(buffer);
/*     */       } 
/* 620 */       this.parent.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class TwoCache implements Cache {
/*     */     private final ByteBufferPool.Cache parent;
/*     */     private ByteBuffer buffer1;
/*     */     private ByteBuffer buffer2;
/*     */     
/*     */     TwoCache(ByteBufferPool.Cache parent) {
/* 630 */       this.parent = parent;
/*     */     }
/*     */     
/*     */     public void free(ByteBuffer bb) {
/* 634 */       if (this.buffer1 == null) {
/* 635 */         this.buffer1 = bb;
/* 636 */       } else if (this.buffer2 == null) {
/* 637 */         this.buffer2 = bb;
/*     */       } else {
/* 639 */         this.parent.free(bb);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flushBuffer(ByteBuffer bb) {
/* 644 */       this.parent.flushBuffer(bb);
/*     */     }
/*     */     
/*     */     public ByteBuffer allocate() {
/* 648 */       if (this.buffer1 != null)
/* 649 */         try { return this.buffer1; }
/*     */         finally
/* 651 */         { this.buffer1 = null; }
/* 652 */           if (this.buffer2 != null)
/* 653 */         try { return this.buffer2; }
/*     */         finally
/* 655 */         { this.buffer2 = null; }
/*     */          
/* 657 */       return this.parent.allocate();
/*     */     }
/*     */ 
/*     */     
/*     */     public void destroy() {
/* 662 */       ByteBufferPool.Cache parent = this.parent;
/* 663 */       ByteBuffer buffer1 = this.buffer1;
/* 664 */       if (buffer1 != null) {
/* 665 */         parent.free(buffer1);
/* 666 */         this.buffer1 = null;
/*     */       } 
/* 668 */       ByteBuffer buffer2 = this.buffer2;
/* 669 */       if (buffer2 != null) {
/* 670 */         parent.free(buffer2);
/* 671 */         this.buffer2 = null;
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flush() {
/* 676 */       ByteBuffer buffer1 = this.buffer1;
/* 677 */       if (buffer1 != null) {
/* 678 */         flushBuffer(buffer1);
/* 679 */         this.buffer1 = null;
/*     */       } 
/* 681 */       ByteBuffer buffer2 = this.buffer2;
/* 682 */       if (buffer2 != null) {
/* 683 */         flushBuffer(buffer2);
/* 684 */         this.buffer2 = null;
/*     */       } 
/* 686 */       this.parent.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   static final class MultiCache implements Cache {
/*     */     private final ByteBufferPool.Cache parent;
/*     */     private final ByteBuffer[] cache;
/*     */     private final long mask;
/*     */     private long availableBits;
/*     */     
/*     */     MultiCache(ByteBufferPool.Cache parent, int size) {
/* 697 */       this.parent = parent;
/* 698 */       assert 0 < size && size <= 64;
/* 699 */       this.cache = new ByteBuffer[size];
/* 700 */       this.mask = this.availableBits = (size == 64) ? -1L : ((1L << size) - 1L);
/*     */     }
/*     */     
/*     */     public void free(ByteBuffer bb) {
/* 704 */       long posn = Long.lowestOneBit((this.availableBits ^ 0xFFFFFFFFFFFFFFFFL) & this.mask);
/* 705 */       if (posn != 0L) {
/* 706 */         int bit = Long.numberOfTrailingZeros(posn);
/*     */         
/* 708 */         this.availableBits |= posn;
/* 709 */         this.cache[bit] = bb;
/*     */       } else {
/*     */         
/* 712 */         this.parent.free(bb);
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flushBuffer(ByteBuffer bb) {
/* 717 */       this.parent.flushBuffer(bb);
/*     */     }
/*     */     
/*     */     public ByteBuffer allocate() {
/* 721 */       long posn = Long.lowestOneBit(this.availableBits);
/* 722 */       if (posn != 0L) {
/* 723 */         int bit = Long.numberOfTrailingZeros(posn);
/* 724 */         this.availableBits &= posn ^ 0xFFFFFFFFFFFFFFFFL;
/*     */         try {
/* 726 */           return this.cache[bit];
/*     */         } finally {
/* 728 */           this.cache[bit] = null;
/*     */         } 
/*     */       } 
/*     */       
/* 732 */       return this.parent.allocate();
/*     */     }
/*     */ 
/*     */     
/*     */     public void destroy() {
/* 737 */       ByteBuffer[] cache = this.cache;
/* 738 */       ByteBufferPool.Cache parent = this.parent;
/* 739 */       long bits = (this.availableBits ^ 0xFFFFFFFFFFFFFFFFL) & this.mask;
/*     */       try {
/* 741 */         while (bits != 0L) {
/* 742 */           long posn = Long.lowestOneBit(bits);
/* 743 */           int bit = Long.numberOfTrailingZeros(posn);
/* 744 */           parent.free(cache[bit]);
/* 745 */           bits &= posn ^ 0xFFFFFFFFFFFFFFFFL;
/* 746 */           cache[bit] = null;
/*     */         } 
/*     */       } finally {
/*     */         
/* 750 */         this.availableBits = bits;
/*     */       } 
/*     */     }
/*     */     
/*     */     public void flush() {
/* 755 */       ByteBuffer[] cache = this.cache;
/* 756 */       ByteBufferPool.Cache parent = this.parent;
/* 757 */       long bits = (this.availableBits ^ 0xFFFFFFFFFFFFFFFFL) & this.mask;
/*     */       try {
/* 759 */         while (bits != 0L) {
/* 760 */           long posn = Long.lowestOneBit(bits);
/* 761 */           int bit = Long.numberOfTrailingZeros(posn);
/* 762 */           flushBuffer(cache[bit]);
/* 763 */           bits &= posn ^ 0xFFFFFFFFFFFFFFFFL;
/* 764 */           cache[bit] = null;
/*     */         } 
/*     */       } finally {
/*     */         
/* 768 */         this.availableBits = bits;
/*     */       } 
/* 770 */       parent.flush();
/*     */     }
/*     */   }
/*     */   
/*     */   final class DefaultCache implements Cache {
/*     */     public void free(ByteBuffer bb) {
/* 776 */       ByteBufferPool.this.freeMaster(bb);
/*     */     }
/*     */     
/*     */     public ByteBuffer allocate() {
/* 780 */       return ByteBufferPool.this.allocateMaster();
/*     */     }
/*     */     
/*     */     public void flushBuffer(ByteBuffer bb) {
/* 784 */       free(bb);
/*     */     }
/*     */     
/*     */     public void destroy() {}
/*     */     
/*     */     public void flush() {}
/*     */   }
/*     */   
/*     */   static interface Cache {
/*     */     void free(ByteBuffer param1ByteBuffer);
/*     */     
/*     */     void flushBuffer(ByteBuffer param1ByteBuffer);
/*     */     
/*     */     ByteBuffer allocate();
/*     */     
/*     */     void destroy();
/*     */     
/*     */     void flush();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\ByteBufferPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */