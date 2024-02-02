/*     */ package com.google.zxing.qrcode.decoder;
/*     */ 
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ enum DataMask
/*     */ {
/*  39 */   DATA_MASK_000
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  42 */       return ((i + j & 0x1) == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  49 */   DATA_MASK_001
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  52 */       return ((i & 0x1) == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  59 */   DATA_MASK_010
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  62 */       return (j % 3 == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  69 */   DATA_MASK_011
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  72 */       return ((i + j) % 3 == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  79 */   DATA_MASK_100
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  82 */       return ((i / 2 + j / 3 & 0x1) == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  90 */   DATA_MASK_101
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/*  93 */       return (i * j % 6 == 0);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 101 */   DATA_MASK_110
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/* 104 */       return (i * j % 6 < 3);
/*     */     }
/*     */   },
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 112 */   DATA_MASK_111
/*     */   {
/*     */     boolean isMasked(int i, int j) {
/* 115 */       return ((i + j + i * j % 3 & 0x1) == 0);
/*     */     }
/*     */   };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   final void unmaskBitMatrix(BitMatrix bits, int dimension) {
/* 130 */     for (int i = 0; i < dimension; i++) {
/* 131 */       for (int j = 0; j < dimension; j++) {
/* 132 */         if (isMasked(i, j))
/* 133 */           bits.flip(j, i); 
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   abstract boolean isMasked(int paramInt1, int paramInt2);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\DataMask.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */