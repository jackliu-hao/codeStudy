/*     */ package io.undertow.server.handlers.cache;
/*     */ 
/*     */ import io.undertow.util.ConcurrentDirectDeque;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
/*     */ import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LRUCache<K, V>
/*     */ {
/*     */   private static final int SAMPLE_INTERVAL = 5;
/*     */   private final int maxEntries;
/*     */   private final ConcurrentMap<K, CacheEntry<K, V>> cache;
/*     */   private final ConcurrentDirectDeque<CacheEntry<K, V>> accessQueue;
/*     */   private final int maxAge;
/*     */   private final boolean fifo;
/*     */   
/*     */   public LRUCache(int maxEntries, int maxAge) {
/*  58 */     this.maxAge = maxAge;
/*  59 */     this.cache = new ConcurrentHashMap<>(16);
/*  60 */     this.accessQueue = ConcurrentDirectDeque.newInstance();
/*  61 */     this.maxEntries = maxEntries;
/*  62 */     this.fifo = false;
/*     */   }
/*     */   public LRUCache(int maxEntries, int maxAge, boolean fifo) {
/*  65 */     this.maxAge = maxAge;
/*  66 */     this.cache = new ConcurrentHashMap<>(16);
/*  67 */     this.accessQueue = ConcurrentDirectDeque.newInstance();
/*  68 */     this.maxEntries = maxEntries;
/*  69 */     this.fifo = fifo;
/*     */   }
/*     */   
/*     */   public void add(K key, V newValue) {
/*  73 */     CacheEntry<K, V> value = this.cache.get(key);
/*  74 */     if (value == null) {
/*     */       long expires;
/*  76 */       if (this.maxAge == -1) {
/*  77 */         expires = -1L;
/*     */       } else {
/*  79 */         expires = System.currentTimeMillis() + this.maxAge;
/*     */       } 
/*  81 */       value = new CacheEntry<>(key, newValue, expires);
/*  82 */       CacheEntry<K, V> result = this.cache.putIfAbsent(key, value);
/*  83 */       if (result != null) {
/*  84 */         value = result;
/*  85 */         value.setValue(newValue);
/*     */       } 
/*  87 */       bumpAccess(value);
/*  88 */       if (this.cache.size() > this.maxEntries) {
/*     */         
/*  90 */         CacheEntry<K, V> oldest = (CacheEntry<K, V>)this.accessQueue.poll();
/*  91 */         if (oldest != value) {
/*  92 */           remove(oldest.key());
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public V get(K key) {
/*  99 */     CacheEntry<K, V> cacheEntry = this.cache.get(key);
/* 100 */     if (cacheEntry == null) {
/* 101 */       return null;
/*     */     }
/* 103 */     long expires = cacheEntry.getExpires();
/* 104 */     if (expires != -1L && 
/* 105 */       System.currentTimeMillis() > expires) {
/* 106 */       remove(key);
/* 107 */       return null;
/*     */     } 
/*     */ 
/*     */     
/* 111 */     if (!this.fifo && 
/* 112 */       cacheEntry.hit() % 5 == 0) {
/* 113 */       bumpAccess(cacheEntry);
/*     */     }
/*     */ 
/*     */     
/* 117 */     return cacheEntry.getValue();
/*     */   }
/*     */   
/*     */   private void bumpAccess(CacheEntry<K, V> cacheEntry) {
/* 121 */     Object prevToken = cacheEntry.claimToken();
/* 122 */     if (!Boolean.FALSE.equals(prevToken)) {
/* 123 */       if (prevToken != null) {
/* 124 */         this.accessQueue.removeToken(prevToken);
/*     */       }
/*     */       
/* 127 */       Object token = null;
/*     */       try {
/* 129 */         token = this.accessQueue.offerLastAndReturnToken(cacheEntry);
/* 130 */       } catch (Throwable throwable) {}
/*     */ 
/*     */ 
/*     */       
/* 134 */       if (!cacheEntry.setToken(token) && token != null) {
/* 135 */         this.accessQueue.removeToken(token);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public V remove(K key) {
/* 141 */     CacheEntry<K, V> remove = this.cache.remove(key);
/* 142 */     if (remove != null) {
/* 143 */       Object old = remove.clearToken();
/* 144 */       if (old != null) {
/* 145 */         this.accessQueue.removeToken(old);
/*     */       }
/* 147 */       return remove.getValue();
/*     */     } 
/* 149 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 154 */     this.cache.clear();
/* 155 */     this.accessQueue.clear();
/*     */   }
/*     */   
/*     */   public static final class CacheEntry<K, V>
/*     */   {
/* 160 */     private static final Object CLAIM_TOKEN = new Object();
/*     */     
/* 162 */     private static final AtomicIntegerFieldUpdater<CacheEntry> hitsUpdater = AtomicIntegerFieldUpdater.newUpdater(CacheEntry.class, "hits");
/*     */     
/* 164 */     private static final AtomicReferenceFieldUpdater<CacheEntry, Object> tokenUpdator = AtomicReferenceFieldUpdater.newUpdater(CacheEntry.class, Object.class, "accessToken");
/*     */     
/*     */     private final K key;
/*     */     private volatile V value;
/*     */     private final long expires;
/* 169 */     private volatile int hits = 1;
/*     */     private volatile Object accessToken;
/*     */     
/*     */     private CacheEntry(K key, V value, long expires) {
/* 173 */       this.key = key;
/* 174 */       this.value = value;
/* 175 */       this.expires = expires;
/*     */     }
/*     */     
/*     */     public void setValue(V value) {
/* 179 */       this.value = value;
/*     */     }
/*     */     
/*     */     public V getValue() {
/* 183 */       return this.value;
/*     */     }
/*     */     
/*     */     public int hit() {
/*     */       while (true) {
/* 188 */         int i = this.hits;
/*     */         
/* 190 */         if (hitsUpdater.weakCompareAndSet(this, i++, i)) {
/* 191 */           return i;
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/*     */     public K key() {
/* 198 */       return this.key;
/*     */     }
/*     */     
/*     */     Object claimToken() {
/*     */       while (true) {
/* 203 */         Object current = this.accessToken;
/* 204 */         if (current == CLAIM_TOKEN) {
/* 205 */           return Boolean.FALSE;
/*     */         }
/*     */         
/* 208 */         if (tokenUpdator.compareAndSet(this, current, CLAIM_TOKEN)) {
/* 209 */           return current;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     boolean setToken(Object token) {
/* 215 */       return tokenUpdator.compareAndSet(this, CLAIM_TOKEN, token);
/*     */     }
/*     */     
/*     */     Object clearToken() {
/* 219 */       Object old = tokenUpdator.getAndSet(this, null);
/* 220 */       return (old == CLAIM_TOKEN) ? null : old;
/*     */     }
/*     */     
/*     */     public long getExpires() {
/* 224 */       return this.expires;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\cache\LRUCache.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */