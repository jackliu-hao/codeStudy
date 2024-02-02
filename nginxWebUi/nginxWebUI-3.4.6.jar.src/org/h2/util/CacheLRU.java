/*     */ package org.h2.util;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CacheLRU
/*     */   implements Cache
/*     */ {
/*     */   static final String TYPE_NAME = "LRU";
/*     */   private final CacheWriter writer;
/*     */   private final boolean fifo;
/*  30 */   private final CacheObject head = new CacheHead();
/*     */ 
/*     */   
/*     */   private final int mask;
/*     */ 
/*     */   
/*     */   private CacheObject[] values;
/*     */ 
/*     */   
/*     */   private int recordCount;
/*     */ 
/*     */   
/*     */   private final int len;
/*     */ 
/*     */   
/*     */   private long maxMemory;
/*     */   
/*     */   private long memory;
/*     */ 
/*     */   
/*     */   CacheLRU(CacheWriter paramCacheWriter, int paramInt, boolean paramBoolean) {
/*  51 */     this.writer = paramCacheWriter;
/*  52 */     this.fifo = paramBoolean;
/*  53 */     setMaxMemory(paramInt);
/*     */ 
/*     */     
/*     */     try {
/*  57 */       long l = this.maxMemory / 64L;
/*  58 */       if (l > 2147483647L) {
/*  59 */         throw new IllegalArgumentException();
/*     */       }
/*  61 */       this.len = MathUtils.nextPowerOf2((int)l);
/*  62 */     } catch (IllegalArgumentException illegalArgumentException) {
/*  63 */       throw new IllegalStateException("This much cache memory is not supported: " + paramInt + "kb", illegalArgumentException);
/*     */     } 
/*  65 */     this.mask = this.len - 1;
/*  66 */     clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Cache getCache(CacheWriter paramCacheWriter, String paramString, int paramInt) {
/*     */     CacheTQ cacheTQ;
/*     */     CacheSecondLevel cacheSecondLevel;
/*  79 */     SoftValuesHashMap<Object, Object> softValuesHashMap = null;
/*  80 */     if (paramString.startsWith("SOFT_")) {
/*  81 */       softValuesHashMap = new SoftValuesHashMap<>();
/*  82 */       paramString = paramString.substring("SOFT_".length());
/*     */     } 
/*     */     
/*  85 */     if ("LRU".equals(paramString)) {
/*  86 */       CacheLRU cacheLRU = new CacheLRU(paramCacheWriter, paramInt, false);
/*  87 */     } else if ("TQ".equals(paramString)) {
/*  88 */       cacheTQ = new CacheTQ(paramCacheWriter, paramInt);
/*     */     } else {
/*  90 */       throw DbException.getInvalidValueException("CACHE_TYPE", paramString);
/*     */     } 
/*  92 */     if (softValuesHashMap != null) {
/*  93 */       cacheSecondLevel = new CacheSecondLevel(cacheTQ, (Map)softValuesHashMap);
/*     */     }
/*  95 */     return cacheSecondLevel;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear() {
/* 100 */     this.head.cacheNext = this.head.cachePrevious = this.head;
/*     */     
/* 102 */     this.values = null;
/* 103 */     this.values = new CacheObject[this.len];
/* 104 */     this.recordCount = 0;
/* 105 */     this.memory = this.len * 8L;
/*     */   }
/*     */ 
/*     */   
/*     */   public void put(CacheObject paramCacheObject) {
/* 110 */     if (SysProperties.CHECK) {
/* 111 */       int j = paramCacheObject.getPos();
/* 112 */       CacheObject cacheObject = find(j);
/* 113 */       if (cacheObject != null) {
/* 114 */         throw DbException.getInternalError("try to add a record twice at pos " + j);
/*     */       }
/*     */     } 
/* 117 */     int i = paramCacheObject.getPos() & this.mask;
/* 118 */     paramCacheObject.cacheChained = this.values[i];
/* 119 */     this.values[i] = paramCacheObject;
/* 120 */     this.recordCount++;
/* 121 */     this.memory += paramCacheObject.getMemory();
/* 122 */     addToFront(paramCacheObject);
/* 123 */     removeOldIfRequired();
/*     */   }
/*     */ 
/*     */   
/*     */   public CacheObject update(int paramInt, CacheObject paramCacheObject) {
/* 128 */     CacheObject cacheObject = find(paramInt);
/* 129 */     if (cacheObject == null) {
/* 130 */       put(paramCacheObject);
/*     */     } else {
/* 132 */       if (cacheObject != paramCacheObject) {
/* 133 */         throw DbException.getInternalError("old!=record pos:" + paramInt + " old:" + cacheObject + " new:" + paramCacheObject);
/*     */       }
/* 135 */       if (!this.fifo) {
/* 136 */         removeFromLinkedList(paramCacheObject);
/* 137 */         addToFront(paramCacheObject);
/*     */       } 
/*     */     } 
/* 140 */     return cacheObject;
/*     */   }
/*     */ 
/*     */   
/*     */   private void removeOldIfRequired() {
/* 145 */     if (this.memory >= this.maxMemory) {
/* 146 */       removeOld();
/*     */     }
/*     */   }
/*     */   
/*     */   private void removeOld() {
/* 151 */     byte b = 0;
/* 152 */     ArrayList<CacheObject> arrayList = new ArrayList();
/* 153 */     long l = this.memory;
/* 154 */     int i = this.recordCount;
/* 155 */     boolean bool = false;
/* 156 */     CacheObject cacheObject = this.head.cacheNext;
/*     */     
/* 158 */     while (i > 16) {
/*     */ 
/*     */       
/* 161 */       if (arrayList.isEmpty() ? (
/* 162 */         l <= this.maxMemory) : (
/*     */ 
/*     */ 
/*     */         
/* 166 */         l * 4L <= this.maxMemory * 3L)) {
/*     */         break;
/*     */       }
/*     */       
/* 170 */       CacheObject cacheObject1 = cacheObject;
/* 171 */       cacheObject = cacheObject1.cacheNext;
/* 172 */       b++;
/* 173 */       if (b >= this.recordCount) {
/* 174 */         if (!bool) {
/* 175 */           this.writer.flushLog();
/* 176 */           bool = true;
/* 177 */           b = 0;
/*     */         
/*     */         }
/*     */         else {
/*     */           
/* 182 */           this.writer.getTrace()
/* 183 */             .info("cannot remove records, cache size too small? records:" + this.recordCount + " memory:" + this.memory);
/*     */           
/*     */           break;
/*     */         } 
/*     */       }
/* 188 */       if (cacheObject1 == this.head) {
/* 189 */         throw DbException.getInternalError("try to remove head");
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 194 */       if (!cacheObject1.canRemove()) {
/* 195 */         removeFromLinkedList(cacheObject1);
/* 196 */         addToFront(cacheObject1);
/*     */         continue;
/*     */       } 
/* 199 */       i--;
/* 200 */       l -= cacheObject1.getMemory();
/* 201 */       if (cacheObject1.isChanged()) {
/* 202 */         arrayList.add(cacheObject1); continue;
/*     */       } 
/* 204 */       remove(cacheObject1.getPos());
/*     */     } 
/*     */     
/* 207 */     if (!arrayList.isEmpty()) {
/* 208 */       if (!bool) {
/* 209 */         this.writer.flushLog();
/*     */       }
/* 211 */       Collections.sort(arrayList);
/* 212 */       long l1 = this.maxMemory;
/* 213 */       int j = arrayList.size();
/*     */ 
/*     */       
/*     */       try {
/* 217 */         this.maxMemory = Long.MAX_VALUE;
/* 218 */         for (b = 0; b < j; b++) {
/* 219 */           CacheObject cacheObject1 = arrayList.get(b);
/* 220 */           this.writer.writeBack(cacheObject1);
/*     */         } 
/*     */       } finally {
/* 223 */         this.maxMemory = l1;
/*     */       } 
/* 225 */       for (b = 0; b < j; b++) {
/* 226 */         CacheObject cacheObject1 = arrayList.get(b);
/* 227 */         remove(cacheObject1.getPos());
/* 228 */         if (cacheObject1.cacheNext != null) {
/* 229 */           throw DbException.getInternalError();
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private void addToFront(CacheObject paramCacheObject) {
/* 236 */     if (paramCacheObject == this.head) {
/* 237 */       throw DbException.getInternalError("try to move head");
/*     */     }
/* 239 */     paramCacheObject.cacheNext = this.head;
/* 240 */     paramCacheObject.cachePrevious = this.head.cachePrevious;
/* 241 */     paramCacheObject.cachePrevious.cacheNext = paramCacheObject;
/* 242 */     this.head.cachePrevious = paramCacheObject;
/*     */   }
/*     */   
/*     */   private void removeFromLinkedList(CacheObject paramCacheObject) {
/* 246 */     if (paramCacheObject == this.head) {
/* 247 */       throw DbException.getInternalError("try to remove head");
/*     */     }
/* 249 */     paramCacheObject.cachePrevious.cacheNext = paramCacheObject.cacheNext;
/* 250 */     paramCacheObject.cacheNext.cachePrevious = paramCacheObject.cachePrevious;
/*     */ 
/*     */     
/* 253 */     paramCacheObject.cacheNext = null;
/* 254 */     paramCacheObject.cachePrevious = null;
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
/*     */   public boolean remove(int paramInt) {
/*     */     // Byte code:
/*     */     //   0: iload_1
/*     */     //   1: aload_0
/*     */     //   2: getfield mask : I
/*     */     //   5: iand
/*     */     //   6: istore_2
/*     */     //   7: aload_0
/*     */     //   8: getfield values : [Lorg/h2/util/CacheObject;
/*     */     //   11: iload_2
/*     */     //   12: aaload
/*     */     //   13: astore_3
/*     */     //   14: aload_3
/*     */     //   15: ifnonnull -> 20
/*     */     //   18: iconst_0
/*     */     //   19: ireturn
/*     */     //   20: aload_3
/*     */     //   21: invokevirtual getPos : ()I
/*     */     //   24: iload_1
/*     */     //   25: if_icmpne -> 41
/*     */     //   28: aload_0
/*     */     //   29: getfield values : [Lorg/h2/util/CacheObject;
/*     */     //   32: iload_2
/*     */     //   33: aload_3
/*     */     //   34: getfield cacheChained : Lorg/h2/util/CacheObject;
/*     */     //   37: aastore
/*     */     //   38: goto -> 72
/*     */     //   41: aload_3
/*     */     //   42: astore #4
/*     */     //   44: aload_3
/*     */     //   45: getfield cacheChained : Lorg/h2/util/CacheObject;
/*     */     //   48: astore_3
/*     */     //   49: aload_3
/*     */     //   50: ifnonnull -> 55
/*     */     //   53: iconst_0
/*     */     //   54: ireturn
/*     */     //   55: aload_3
/*     */     //   56: invokevirtual getPos : ()I
/*     */     //   59: iload_1
/*     */     //   60: if_icmpne -> 41
/*     */     //   63: aload #4
/*     */     //   65: aload_3
/*     */     //   66: getfield cacheChained : Lorg/h2/util/CacheObject;
/*     */     //   69: putfield cacheChained : Lorg/h2/util/CacheObject;
/*     */     //   72: aload_0
/*     */     //   73: dup
/*     */     //   74: getfield recordCount : I
/*     */     //   77: iconst_1
/*     */     //   78: isub
/*     */     //   79: putfield recordCount : I
/*     */     //   82: aload_0
/*     */     //   83: dup
/*     */     //   84: getfield memory : J
/*     */     //   87: aload_3
/*     */     //   88: invokevirtual getMemory : ()I
/*     */     //   91: i2l
/*     */     //   92: lsub
/*     */     //   93: putfield memory : J
/*     */     //   96: aload_0
/*     */     //   97: aload_3
/*     */     //   98: invokespecial removeFromLinkedList : (Lorg/h2/util/CacheObject;)V
/*     */     //   101: getstatic org/h2/engine/SysProperties.CHECK : Z
/*     */     //   104: ifeq -> 148
/*     */     //   107: aload_3
/*     */     //   108: aconst_null
/*     */     //   109: putfield cacheChained : Lorg/h2/util/CacheObject;
/*     */     //   112: aload_0
/*     */     //   113: iload_1
/*     */     //   114: invokevirtual find : (I)Lorg/h2/util/CacheObject;
/*     */     //   117: astore #4
/*     */     //   119: aload #4
/*     */     //   121: ifnull -> 148
/*     */     //   124: new java/lang/StringBuilder
/*     */     //   127: dup
/*     */     //   128: invokespecial <init> : ()V
/*     */     //   131: ldc 'not removed: '
/*     */     //   133: invokevirtual append : (Ljava/lang/String;)Ljava/lang/StringBuilder;
/*     */     //   136: aload #4
/*     */     //   138: invokevirtual append : (Ljava/lang/Object;)Ljava/lang/StringBuilder;
/*     */     //   141: invokevirtual toString : ()Ljava/lang/String;
/*     */     //   144: invokestatic getInternalError : (Ljava/lang/String;)Ljava/lang/RuntimeException;
/*     */     //   147: athrow
/*     */     //   148: iconst_1
/*     */     //   149: ireturn
/*     */     // Line number table:
/*     */     //   Java source line number -> byte code offset
/*     */     //   #259	-> 0
/*     */     //   #260	-> 7
/*     */     //   #261	-> 14
/*     */     //   #262	-> 18
/*     */     //   #264	-> 20
/*     */     //   #265	-> 28
/*     */     //   #269	-> 41
/*     */     //   #270	-> 44
/*     */     //   #271	-> 49
/*     */     //   #272	-> 53
/*     */     //   #274	-> 55
/*     */     //   #275	-> 63
/*     */     //   #277	-> 72
/*     */     //   #278	-> 82
/*     */     //   #279	-> 96
/*     */     //   #280	-> 101
/*     */     //   #281	-> 107
/*     */     //   #282	-> 112
/*     */     //   #283	-> 119
/*     */     //   #284	-> 124
/*     */     //   #287	-> 148
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
/*     */   public CacheObject find(int paramInt) {
/* 292 */     CacheObject cacheObject = this.values[paramInt & this.mask];
/* 293 */     while (cacheObject != null && cacheObject.getPos() != paramInt) {
/* 294 */       cacheObject = cacheObject.cacheChained;
/*     */     }
/* 296 */     return cacheObject;
/*     */   }
/*     */ 
/*     */   
/*     */   public CacheObject get(int paramInt) {
/* 301 */     CacheObject cacheObject = find(paramInt);
/* 302 */     if (cacheObject != null && 
/* 303 */       !this.fifo) {
/* 304 */       removeFromLinkedList(cacheObject);
/* 305 */       addToFront(cacheObject);
/*     */     } 
/*     */     
/* 308 */     return cacheObject;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<CacheObject> getAllChanged() {
/* 345 */     ArrayList<CacheObject> arrayList = new ArrayList();
/* 346 */     CacheObject cacheObject = this.head.cacheNext;
/* 347 */     while (cacheObject != this.head) {
/* 348 */       if (cacheObject.isChanged()) {
/* 349 */         arrayList.add(cacheObject);
/*     */       }
/* 351 */       cacheObject = cacheObject.cacheNext;
/*     */     } 
/* 353 */     return arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setMaxMemory(int paramInt) {
/* 358 */     long l = paramInt * 1024L / 4L;
/* 359 */     this.maxMemory = (l < 0L) ? 0L : l;
/*     */ 
/*     */     
/* 362 */     removeOldIfRequired();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getMaxMemory() {
/* 367 */     return (int)(this.maxMemory * 4L / 1024L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMemory() {
/* 378 */     return (int)(this.memory * 4L / 1024L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CacheLRU.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */