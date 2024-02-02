/*     */ package cn.hutool.core.text;
/*     */ 
/*     */ import cn.hutool.core.lang.hash.MurmurHash;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.locks.StampedLock;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Simhash
/*     */ {
/*  28 */   private final int bitNum = 64;
/*     */   
/*     */   private final int fracCount;
/*     */   
/*     */   private final int fracBitNum;
/*     */   
/*     */   private final int hammingThresh;
/*     */   
/*     */   private final List<Map<String, List<Long>>> storage;
/*  37 */   private final StampedLock lock = new StampedLock();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Simhash() {
/*  43 */     this(4, 3);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Simhash(int fracCount, int hammingThresh) {
/*  53 */     this.fracCount = fracCount;
/*  54 */     this.fracBitNum = 64 / fracCount;
/*  55 */     this.hammingThresh = hammingThresh;
/*  56 */     this.storage = new ArrayList<>(fracCount);
/*  57 */     for (int i = 0; i < fracCount; i++) {
/*  58 */       this.storage.add(new HashMap<>());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long hash(Collection<? extends CharSequence> segList) {
/*  69 */     getClass(); int bitNum = 64;
/*     */     
/*  71 */     int[] weight = new int[bitNum];
/*     */     
/*  73 */     for (CharSequence seg : segList) {
/*  74 */       long wordHash = MurmurHash.hash64(seg);
/*  75 */       for (int j = 0; j < bitNum; j++) {
/*  76 */         if ((wordHash >> j & 0x1L) == 1L) {
/*  77 */           weight[j] = weight[j] + 1;
/*     */         } else {
/*  79 */           weight[j] = weight[j] - 1;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/*  84 */     StringBuilder sb = new StringBuilder();
/*  85 */     for (int i = 0; i < bitNum; i++) {
/*  86 */       sb.append((weight[i] > 0) ? 1 : 0);
/*     */     }
/*     */     
/*  89 */     return (new BigInteger(sb.toString(), 2)).longValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Collection<? extends CharSequence> segList) {
/*  99 */     long simhash = hash(segList);
/* 100 */     List<String> fracList = splitSimhash(Long.valueOf(simhash));
/* 101 */     int hammingThresh = this.hammingThresh;
/*     */ 
/*     */ 
/*     */     
/* 105 */     long stamp = this.lock.readLock();
/*     */     try {
/* 107 */       for (int i = 0; i < this.fracCount; i++) {
/* 108 */         String frac = fracList.get(i);
/* 109 */         Map<String, List<Long>> fracMap = this.storage.get(i);
/* 110 */         if (fracMap.containsKey(frac)) {
/* 111 */           for (Long simhash2 : fracMap.get(frac)) {
/*     */             
/* 113 */             if (hamming(Long.valueOf(simhash), simhash2) < hammingThresh) {
/* 114 */               return true;
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } finally {
/* 120 */       this.lock.unlockRead(stamp);
/*     */     } 
/* 122 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void store(Long simhash) {
/* 131 */     int fracCount = this.fracCount;
/* 132 */     List<Map<String, List<Long>>> storage = this.storage;
/* 133 */     List<String> lFrac = splitSimhash(simhash);
/*     */ 
/*     */ 
/*     */     
/* 137 */     long stamp = this.lock.writeLock();
/*     */     try {
/* 139 */       for (int i = 0; i < fracCount; i++) {
/* 140 */         String frac = lFrac.get(i);
/* 141 */         Map<String, List<Long>> fracMap = storage.get(i);
/* 142 */         if (fracMap.containsKey(frac)) {
/* 143 */           ((List<Long>)fracMap.get(frac)).add(simhash);
/*     */         } else {
/* 145 */           List<Long> ls = new ArrayList<>();
/* 146 */           ls.add(simhash);
/* 147 */           fracMap.put(frac, ls);
/*     */         } 
/*     */       } 
/*     */     } finally {
/* 151 */       this.lock.unlockWrite(stamp);
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
/*     */   private int hamming(Long s1, Long s2) {
/* 164 */     getClass(); int bitNum = 64;
/* 165 */     int dis = 0;
/* 166 */     for (int i = 0; i < bitNum; i++) {
/* 167 */       if ((s1.longValue() >> i & 0x1L) != (s2.longValue() >> i & 0x1L))
/* 168 */         dis++; 
/*     */     } 
/* 170 */     return dis;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private List<String> splitSimhash(Long simhash) {
/* 180 */     getClass(); int bitNum = 64;
/* 181 */     int fracBitNum = this.fracBitNum;
/*     */     
/* 183 */     List<String> ls = new ArrayList<>();
/* 184 */     StringBuilder sb = new StringBuilder();
/* 185 */     for (int i = 0; i < bitNum; i++) {
/* 186 */       sb.append(simhash.longValue() >> i & 0x1L);
/* 187 */       if ((i + 1) % fracBitNum == 0) {
/* 188 */         ls.add(sb.toString());
/* 189 */         sb.setLength(0);
/*     */       } 
/*     */     } 
/* 192 */     return ls;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\Simhash.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */