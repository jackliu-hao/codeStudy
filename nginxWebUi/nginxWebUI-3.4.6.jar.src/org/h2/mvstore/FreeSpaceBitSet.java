/*     */ package org.h2.mvstore;
/*     */ 
/*     */ import java.util.BitSet;
/*     */ import org.h2.util.MathUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FreeSpaceBitSet
/*     */ {
/*     */   private static final boolean DETAILED_INFO = false;
/*     */   private final int firstFreeBlock;
/*     */   private final int blockSize;
/*  32 */   private final BitSet set = new BitSet();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int failureFlags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FreeSpaceBitSet(int paramInt1, int paramInt2) {
/*  51 */     this.firstFreeBlock = paramInt1;
/*  52 */     this.blockSize = paramInt2;
/*  53 */     clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void clear() {
/*  60 */     this.set.clear();
/*  61 */     this.set.set(0, this.firstFreeBlock);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isUsed(long paramLong, int paramInt) {
/*  72 */     int i = getBlock(paramLong);
/*  73 */     int j = getBlockCount(paramInt);
/*  74 */     for (int k = i; k < i + j; k++) {
/*  75 */       if (!this.set.get(k)) {
/*  76 */         return false;
/*     */       }
/*     */     } 
/*  79 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isFree(long paramLong, int paramInt) {
/*  90 */     int i = getBlock(paramLong);
/*  91 */     int j = getBlockCount(paramInt);
/*  92 */     for (int k = i; k < i + j; k++) {
/*  93 */       if (this.set.get(k)) {
/*  94 */         return false;
/*     */       }
/*     */     } 
/*  97 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long allocate(int paramInt) {
/* 107 */     return allocate(paramInt, 0L, 0L);
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
/*     */   long allocate(int paramInt, long paramLong1, long paramLong2) {
/* 120 */     return getPos(allocate(getBlockCount(paramInt), (int)paramLong1, (int)paramLong2, true));
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
/*     */   long predictAllocation(int paramInt, long paramLong1, long paramLong2) {
/* 133 */     return allocate(paramInt, (int)paramLong1, (int)paramLong2, false);
/*     */   }
/*     */   
/*     */   boolean isFragmented() {
/* 137 */     return (Integer.bitCount(this.failureFlags & 0xF) > 1);
/*     */   }
/*     */   
/*     */   private int allocate(int paramInt1, int paramInt2, int paramInt3, boolean paramBoolean) {
/* 141 */     int i = 0;
/* 142 */     int j = 0; while (true) {
/* 143 */       int k = this.set.nextClearBit(j);
/* 144 */       int m = this.set.nextSetBit(k + 1);
/* 145 */       int n = m - k;
/* 146 */       if (m < 0 || n >= paramInt1) {
/* 147 */         if ((paramInt3 < 0 || k < paramInt3) && k + paramInt1 > paramInt2) {
/* 148 */           if (paramInt3 < 0) {
/* 149 */             k = getAfterLastBlock();
/* 150 */             m = -1;
/*     */           } else {
/* 152 */             j = paramInt3;
/*     */             continue;
/*     */           } 
/*     */         }
/* 156 */         assert this.set.nextSetBit(k) == -1 || this.set.nextSetBit(k) >= k + paramInt1 : "Double alloc: " + 
/* 157 */           Integer.toHexString(k) + "/" + Integer.toHexString(paramInt1) + " " + this;
/* 158 */         if (paramBoolean) {
/* 159 */           this.set.set(k, k + paramInt1);
/*     */         } else {
/* 161 */           this.failureFlags <<= 1;
/* 162 */           if (m < 0 && i > 4 * paramInt1) {
/* 163 */             this.failureFlags |= 0x1;
/*     */           }
/*     */         } 
/* 166 */         return k;
/*     */       } 
/* 168 */       i += n;
/* 169 */       j = m;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void markUsed(long paramLong, int paramInt) {
/* 180 */     int i = getBlock(paramLong);
/* 181 */     int j = getBlockCount(paramInt);
/*     */     
/* 183 */     if (this.set.nextSetBit(i) != -1 && this.set.nextSetBit(i) < i + j) {
/* 184 */       throw DataUtils.newMVStoreException(6, "Double mark: " + 
/*     */           
/* 186 */           Integer.toHexString(i) + "/" + 
/* 187 */           Integer.toHexString(j) + " " + this, new Object[0]);
/*     */     }
/* 189 */     this.set.set(i, i + j);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void free(long paramLong, int paramInt) {
/* 199 */     int i = getBlock(paramLong);
/* 200 */     int j = getBlockCount(paramInt);
/* 201 */     assert this.set.nextClearBit(i) >= i + j : "Double free: " + 
/* 202 */       Integer.toHexString(i) + "/" + Integer.toHexString(j) + " " + this;
/* 203 */     this.set.clear(i, i + j);
/*     */   }
/*     */   
/*     */   private long getPos(int paramInt) {
/* 207 */     return paramInt * this.blockSize;
/*     */   }
/*     */   
/*     */   private int getBlock(long paramLong) {
/* 211 */     return (int)(paramLong / this.blockSize);
/*     */   }
/*     */   
/*     */   private int getBlockCount(int paramInt) {
/* 215 */     return MathUtils.roundUpInt(paramInt, this.blockSize) / this.blockSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getFillRate() {
/* 225 */     return getProjectedFillRate(0);
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
/*     */   int getProjectedFillRate(int paramInt) {
/* 244 */     byte b = 3;
/*     */     while (true) {
/* 246 */       if (--b == 0) {
/* 247 */         return 100;
/*     */       }
/* 249 */       int j = this.set.length();
/* 250 */       int i = this.set.cardinality();
/* 251 */       if (j == this.set.length() && i <= j) {
/* 252 */         i -= this.firstFreeBlock + paramInt;
/* 253 */         j -= this.firstFreeBlock;
/* 254 */         return (i == 0) ? 0 : (int)((100L * i + j - 1L) / j);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getFirstFree() {
/* 263 */     return getPos(this.set.nextClearBit(0));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   long getLastFree() {
/* 272 */     return getPos(getAfterLastBlock());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   int getAfterLastBlock() {
/* 282 */     return this.set.previousSetBit(this.set.size() - 1) + 1;
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
/*     */   int getMovePriority(int paramInt) {
/* 295 */     int j, i = this.set.previousClearBit(paramInt);
/*     */     
/* 297 */     if (i < 0) {
/* 298 */       i = this.firstFreeBlock;
/* 299 */       j = 0;
/*     */     } else {
/* 301 */       j = i - this.set.previousSetBit(i);
/*     */     } 
/*     */     
/* 304 */     int k = this.set.nextClearBit(paramInt);
/* 305 */     int m = this.set.nextSetBit(k);
/* 306 */     if (m >= 0) {
/* 307 */       j += m - k;
/*     */     }
/* 309 */     return (k - i - 1) * 1000 / (j + 1);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 314 */     StringBuilder stringBuilder = new StringBuilder();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 334 */     stringBuilder.append('[');
/* 335 */     int i = 0; while (true) {
/* 336 */       if (i) {
/* 337 */         stringBuilder.append(", ");
/*     */       }
/* 339 */       int j = this.set.nextClearBit(i);
/* 340 */       stringBuilder.append(Integer.toHexString(j)).append('-');
/* 341 */       int k = this.set.nextSetBit(j + 1);
/* 342 */       if (k < 0) {
/*     */         break;
/*     */       }
/* 345 */       stringBuilder.append(Integer.toHexString(k - 1));
/* 346 */       i = k + 1;
/*     */     } 
/* 348 */     stringBuilder.append(']');
/* 349 */     return stringBuilder.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\FreeSpaceBitSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */