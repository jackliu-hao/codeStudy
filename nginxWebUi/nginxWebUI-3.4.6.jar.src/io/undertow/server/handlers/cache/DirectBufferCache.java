/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.util.ConcurrentDirectDeque;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ import org.xnio.BufferAllocator;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DirectBufferCache
/*     */ {
/*     */   private static final int SAMPLE_INTERVAL = 5;
/*     */   private final LimitedBufferSlicePool pool;
/*     */   private final ConcurrentMap<Object, CacheEntry> cache;
/*     */   private final ConcurrentDirectDeque<CacheEntry> accessQueue;
/*     */   private final int sliceSize;
/*     */   private final int maxAge;
/*     */   
/*     */   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory) {
/*  58 */     this(sliceSize, slicesPerPage, maxMemory, BufferAllocator.DIRECT_BYTE_BUFFER_ALLOCATOR);
/*     */   }
/*     */   
/*     */   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory, BufferAllocator<ByteBuffer> bufferAllocator) {
/*  62 */     this(sliceSize, slicesPerPage, maxMemory, bufferAllocator, -1);
/*     */   }
/*     */   
/*     */   public DirectBufferCache(int sliceSize, int slicesPerPage, int maxMemory, BufferAllocator<ByteBuffer> bufferAllocator, int maxAge) {
/*  66 */     this.sliceSize = sliceSize;
/*  67 */     this.pool = new LimitedBufferSlicePool(bufferAllocator, sliceSize, sliceSize * slicesPerPage, maxMemory / sliceSize * slicesPerPage);
/*  68 */     this.cache = new ConcurrentHashMap<>(16);
/*  69 */     this.accessQueue = ConcurrentDirectDeque.newInstance();
/*  70 */     this.maxAge = maxAge;
/*     */   }
/*     */   
/*     */   public CacheEntry add(Object key, int size) {
/*  74 */     return add(key, size, this.maxAge);
/*     */   }
/*     */   
/*     */   public CacheEntry add(Object key, int size, int maxAge) {
/*  78 */     CacheEntry value = this.cache.get(key);
/*  79 */     if (value == null) {
/*  80 */       value = new CacheEntry(key, size, this, maxAge);
/*  81 */       CacheEntry result = this.cache.putIfAbsent(key, value);
/*  82 */       if (result != null) {
/*  83 */         value = result;
/*     */       } else {
/*  85 */         bumpAccess(value);
/*     */       } 
/*     */     } 
/*     */     
/*  89 */     return value;
/*     */   }
/*     */   
/*     */   public CacheEntry get(Object key) {
/*  93 */     CacheEntry cacheEntry = this.cache.get(key);
/*  94 */     if (cacheEntry == null) {
/*  95 */       return null;
/*     */     }
/*     */     
/*  98 */     long expires = cacheEntry.getExpires();
/*  99 */     if (expires != -1L && 
/* 100 */       System.currentTimeMillis() > expires) {
/* 101 */       remove(key);
/* 102 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 106 */     if (cacheEntry.hit() % 5 == 0) {
/*     */       
/* 108 */       bumpAccess(cacheEntry);
/*     */       
/* 110 */       if (!cacheEntry.allocate()) {
/*     */         
/* 112 */         int reclaimSize = cacheEntry.size();
/* 113 */         for (CacheEntry oldest : this.accessQueue) {
/* 114 */           if (oldest == cacheEntry) {
/*     */             continue;
/*     */           }
/*     */           
/* 118 */           if ((oldest.buffers()).length > 0) {
/* 119 */             reclaimSize -= oldest.size();
/*     */           }
/*     */           
/* 122 */           remove(oldest.key());
/*     */           
/* 124 */           if (reclaimSize <= 0) {
/*     */             break;
/*     */           }
/*     */         } 
/*     */ 
/*     */         
/* 130 */         cacheEntry.allocate();
/*     */       } 
/*     */     } 
/*     */     
/* 134 */     return cacheEntry;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Object> getAllKeys() {
/* 144 */     return new HashSet(this.cache.keySet());
/*     */   }
/*     */   
/*     */   private void bumpAccess(CacheEntry cacheEntry) {
/* 148 */     Object prevToken = cacheEntry.claimToken();
/* 149 */     if (!Boolean.FALSE.equals(prevToken)) {
/* 150 */       if (prevToken != null) {
/* 151 */         this.accessQueue.removeToken(prevToken);
/*     */       }
/*     */       
/* 154 */       Object token = null;
/*     */       try {
/* 156 */         token = this.accessQueue.offerLastAndReturnToken(cacheEntry);
/* 157 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/* 161 */       if (!cacheEntry.setToken(token) && token != null) {
/* 162 */         this.accessQueue.removeToken(token);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void remove(Object key) {
/* 169 */     CacheEntry remove = this.cache.remove(key);
/* 170 */     if (remove != null) {
/* 171 */       Object old = remove.clearToken();
/* 172 */       if (old != null) {
/* 173 */         this.accessQueue.removeToken(old);
/*     */       }
/* 175 */       remove.dereference();
/*     */     } 
/*     */   }
/*     */   
/*     */   public static final class CacheEntry {
/* 180 */     private static final LimitedBufferSlicePool.PooledByteBuffer[] EMPTY_BUFFERS = new LimitedBufferSlicePool.PooledByteBuffer[0];
/* 181 */     private static final LimitedBufferSlicePool.PooledByteBuffer[] INIT_BUFFERS = new LimitedBufferSlicePool.PooledByteBuffer[0];
/* 182 */     private static final Object CLAIM_TOKEN = new Object();
/*     */     
/* 184 */     private static final AtomicIntegerFieldUpdater<CacheEntry> hitsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "hits");
/* 185 */     private static final AtomicIntegerFieldUpdater<CacheEntry> refsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "refs");
/* 186 */     private static final AtomicIntegerFieldUpdater<CacheEntry> enabledUpdator = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "enabled");
/*     */     
/* 188 */     private static final AtomicReferenceFieldUpdater<CacheEntry, LimitedBufferSlicePool.PooledByteBuffer[]> bufsUpdater = (AtomicReferenceFieldUpdater)AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, (Class)LimitedBufferSlicePool.PooledByteBuffer[].class, "buffers");
/* 189 */     private static final AtomicReferenceFieldUpdater<CacheEntry, Object> tokenUpdator = AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, Object.class, "accessToken");
/*     */     
/*     */     private final Object key;
/*     */     private final int size;
/*     */     private final DirectBufferCache cache;
/*     */     private final int maxAge;
/* 195 */     private volatile LimitedBufferSlicePool.PooledByteBuffer[] buffers = INIT_BUFFERS;
/* 196 */     private volatile int refs = 1;
/* 197 */     private volatile int hits = 1;
/*     */     private volatile Object accessToken;
/*     */     private volatile int enabled;
/* 200 */     private volatile long expires = -1L;
/*     */     
/*     */     private CacheEntry(Object key, int size, DirectBufferCache cache, int maxAge) {
/* 203 */       this.key = key;
/* 204 */       this.size = size;
/* 205 */       this.cache = cache;
/* 206 */       this.maxAge = maxAge;
/*     */     }
/*     */     
/*     */     public int size() {
/* 210 */       return this.size;
/*     */     }
/*     */     
/*     */     public LimitedBufferSlicePool.PooledByteBuffer[] buffers() {
/* 214 */       return this.buffers;
/*     */     }
/*     */     
/*     */     public int hit() {
/*     */       while (true) {
/* 219 */         int i = this.hits;
/*     */         
/* 221 */         if (hitsUpdater.weakCompareAndSet(this, i++, i)) {
/* 222 */           return i;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public Object key() {
/* 229 */       return this.key;
/*     */     }
/*     */     
/*     */     public boolean enabled() {
/* 233 */       return (this.enabled == 2);
/*     */     }
/*     */     
/*     */     public void enable() {
/* 237 */       if (this.maxAge == -1) {
/* 238 */         this.expires = -1L;
/*     */       } else {
/* 240 */         this.expires = System.currentTimeMillis() + this.maxAge;
/*     */       } 
/* 242 */       this.enabled = 2;
/*     */     }
/*     */     
/*     */     public void disable() {
/* 246 */       this.enabled = 0;
/*     */     }
/*     */     
/*     */     public boolean claimEnable() {
/* 250 */       return enabledUpdator.compareAndSet(this, 0, 1);
/*     */     }
/*     */     
/*     */     public boolean reference() {
/*     */       while (true) {
/* 255 */         int refs = this.refs;
/* 256 */         if (refs < 1) {
/* 257 */           return false;
/*     */         }
/*     */         
/* 260 */         if (refsUpdater.compareAndSet(this, refs++, refs)) {
/* 261 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean dereference() {
/*     */       while (true) {
/* 268 */         int refs = this.refs;
/* 269 */         if (refs < 1) {
/* 270 */           return false;
/*     */         }
/*     */         
/* 273 */         if (refsUpdater.compareAndSet(this, refs--, refs)) {
/* 274 */           if (refs == 0) {
/* 275 */             destroy();
/*     */           }
/* 277 */           return true;
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/*     */     public boolean allocate() {
/* 283 */       if (this.buffers.length > 0) {
/* 284 */         return true;
/*     */       }
/* 286 */       if (!bufsUpdater.compareAndSet(this, INIT_BUFFERS, EMPTY_BUFFERS)) {
/* 287 */         return true;
/*     */       }
/*     */       
/* 290 */       int reserveSize = this.size;
/* 291 */       int n = 1;
/* 292 */       DirectBufferCache bufferCache = this.cache;
/* 293 */       while ((reserveSize -= bufferCache.sliceSize) > 0) {
/* 294 */         n++;
/*     */       }
/*     */ 
/*     */       
/* 298 */       LimitedBufferSlicePool slicePool = bufferCache.pool;
/* 299 */       if (!slicePool.canAllocate(n)) {
/* 300 */         this.buffers = INIT_BUFFERS;
/* 301 */         return false;
/*     */       } 
/*     */       
/* 304 */       LimitedBufferSlicePool.PooledByteBuffer[] buffers = new LimitedBufferSlicePool.PooledByteBuffer[n];
/* 305 */       for (int i = 0; i < n; i++) {
/* 306 */         LimitedBufferSlicePool.PooledByteBuffer allocate = slicePool.allocate();
/* 307 */         if (allocate == null) {
/* 308 */           while (--i >= 0) {
/* 309 */             buffers[i].free();
/*     */           }
/*     */           
/* 312 */           this.buffers = INIT_BUFFERS;
/* 313 */           return false;
/*     */         } 
/* 315 */         buffers[i] = allocate;
/*     */       } 
/*     */       
/* 318 */       this.buffers = buffers;
/* 319 */       return true;
/*     */     }
/*     */     
/*     */     private void destroy() {
/* 323 */       this.buffers = EMPTY_BUFFERS;
/* 324 */       for (LimitedBufferSlicePool.PooledByteBuffer buffer : this.buffers) {
/* 325 */         buffer.free();
/*     */       }
/*     */     }
/*     */     
/*     */     Object claimToken() {
/*     */       while (true) {
/* 331 */         Object current = this.accessToken;
/* 332 */         if (current == CLAIM_TOKEN) {
/* 333 */           return Boolean.FALSE;
/*     */         }
/*     */         
/* 336 */         if (tokenUpdator.compareAndSet(this, current, CLAIM_TOKEN)) {
/* 337 */           return current;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     boolean setToken(Object token) {
/* 343 */       return tokenUpdator.compareAndSet(this, CLAIM_TOKEN, token);
/*     */     }
/*     */     
/*     */     Object clearToken() {
/* 347 */       Object old = tokenUpdator.getAndSet(this, null);
/* 348 */       return (old == CLAIM_TOKEN) ? null : old;
/*     */     }
/*     */     
/*     */     long getExpires() {
/* 352 */       return this.expires;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\DirectBufferCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */