/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheTQ
/*     */   implements Cache
/*     */ {
/*     */   static final String TYPE_NAME = "TQ";
/*     */   private final Cache lru;
/*     */   private final Cache fifo;
/*  25 */   private final SmallLRUCache<Integer, Object> recentlyUsed = SmallLRUCache.newInstance(1024);
/*  26 */   private int lastUsed = -1;
/*     */   
/*     */   private int maxMemory;
/*     */   
/*     */   CacheTQ(CacheWriter paramCacheWriter, int paramInt) {
/*  31 */     this.maxMemory = paramInt;
/*  32 */     this.lru = new CacheLRU(paramCacheWriter, (int)(paramInt * 0.8D), false);
/*  33 */     this.fifo = new CacheLRU(paramCacheWriter, (int)(paramInt * 0.2D), true);
/*  34 */     setMaxMemory(4 * paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/*  39 */     this.lru.clear();
/*  40 */     this.fifo.clear();
/*  41 */     this.recentlyUsed.clear();
/*  42 */     this.lastUsed = -1;
/*     */   }
/*     */ 
/*     */   
/*     */   public CacheObject find(int paramInt) {
/*  47 */     CacheObject cacheObject = this.lru.find(paramInt);
/*  48 */     if (cacheObject == null) {
/*  49 */       cacheObject = this.fifo.find(paramInt);
/*     */     }
/*  51 */     return cacheObject;
/*     */   }
/*     */ 
/*     */   
/*     */   public CacheObject get(int paramInt) {
/*  56 */     CacheObject cacheObject = this.lru.find(paramInt);
/*  57 */     if (cacheObject != null) {
/*  58 */       return cacheObject;
/*     */     }
/*  60 */     cacheObject = this.fifo.find(paramInt);
/*  61 */     if (cacheObject != null && !cacheObject.isStream()) {
/*  62 */       if (this.recentlyUsed.get(Integer.valueOf(paramInt)) != null) {
/*  63 */         if (this.lastUsed != paramInt) {
/*  64 */           this.fifo.remove(paramInt);
/*  65 */           this.lru.put(cacheObject);
/*     */         } 
/*     */       } else {
/*  68 */         this.recentlyUsed.put(Integer.valueOf(paramInt), this);
/*     */       } 
/*  70 */       this.lastUsed = paramInt;
/*     */     } 
/*  72 */     return cacheObject;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<CacheObject> getAllChanged() {
/*  77 */     ArrayList<CacheObject> arrayList1 = this.lru.getAllChanged();
/*  78 */     ArrayList<CacheObject> arrayList2 = this.fifo.getAllChanged();
/*  79 */     ArrayList<CacheObject> arrayList3 = new ArrayList(arrayList1.size() + arrayList2.size());
/*  80 */     arrayList3.addAll(arrayList1);
/*  81 */     arrayList3.addAll(arrayList2);
/*  82 */     return arrayList3;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxMemory() {
/*  87 */     return this.maxMemory;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMemory() {
/*  92 */     return this.lru.getMemory() + this.fifo.getMemory();
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(CacheObject paramCacheObject) {
/*  97 */     if (paramCacheObject.isStream()) {
/*  98 */       this.fifo.put(paramCacheObject);
/*  99 */     } else if (this.recentlyUsed.get(Integer.valueOf(paramCacheObject.getPos())) != null) {
/* 100 */       this.lru.put(paramCacheObject);
/*     */     } else {
/* 102 */       this.fifo.put(paramCacheObject);
/* 103 */       this.lastUsed = paramCacheObject.getPos();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean remove(int paramInt) {
/* 109 */     boolean bool = this.lru.remove(paramInt);
/* 110 */     if (!bool) {
/* 111 */       bool = this.fifo.remove(paramInt);
/*     */     }
/* 113 */     this.recentlyUsed.remove(Integer.valueOf(paramInt));
/* 114 */     return bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxMemory(int paramInt) {
/* 119 */     this.maxMemory = paramInt;
/* 120 */     this.lru.setMaxMemory((int)(paramInt * 0.8D));
/* 121 */     this.fifo.setMaxMemory((int)(paramInt * 0.2D));
/* 122 */     this.recentlyUsed.setMaxSize(4 * paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public CacheObject update(int paramInt, CacheObject paramCacheObject) {
/* 127 */     if (this.lru.find(paramInt) != null) {
/* 128 */       return this.lru.update(paramInt, paramCacheObject);
/*     */     }
/* 130 */     return this.fifo.update(paramInt, paramCacheObject);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CacheTQ.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */