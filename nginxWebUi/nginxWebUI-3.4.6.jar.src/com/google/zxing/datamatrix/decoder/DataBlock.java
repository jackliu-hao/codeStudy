/*     */ package com.google.zxing.datamatrix.decoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ final class DataBlock
/*     */ {
/*     */   private final int numDataCodewords;
/*     */   private final byte[] codewords;
/*     */   
/*     */   private DataBlock(int numDataCodewords, byte[] codewords) {
/*  32 */     this.numDataCodewords = numDataCodewords;
/*  33 */     this.codewords = codewords;
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
/*     */   static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version) {
/*  49 */     Version.ECBlocks ecBlocks = version.getECBlocks();
/*     */ 
/*     */     
/*  52 */     int totalBlocks = 0; Version.ECB[] ecBlockArray, arrayOfECB1; int k;
/*     */     byte b1;
/*  54 */     for (k = (arrayOfECB1 = ecBlockArray = ecBlocks.getECBlocks()).length, b1 = 0; b1 < k; ) { Version.ECB ecBlock = arrayOfECB1[b1];
/*  55 */       totalBlocks += ecBlock.getCount();
/*     */       
/*     */       b1++; }
/*     */     
/*  59 */     DataBlock[] result = new DataBlock[totalBlocks];
/*  60 */     int numResultBlocks = 0; Version.ECB[] arrayOfECB2; int m; byte b2;
/*  61 */     for (m = (arrayOfECB2 = ecBlockArray).length, b2 = 0; b2 < m; ) { Version.ECB ecBlock = arrayOfECB2[b2];
/*  62 */       for (int i1 = 0; i1 < ecBlock.getCount(); i1++) {
/*  63 */         int numDataCodewords = ecBlock.getDataCodewords();
/*  64 */         int numBlockCodewords = ecBlocks.getECCodewords() + numDataCodewords;
/*  65 */         result[numResultBlocks++] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       b2++; }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  76 */     int longerBlocksNumDataCodewords, shorterBlocksNumDataCodewords = (longerBlocksNumDataCodewords = (result[0]).codewords.length - ecBlocks.getECCodewords()) - 1;
/*     */ 
/*     */     
/*  79 */     int rawCodewordsOffset = 0;
/*  80 */     for (int i = 0; i < shorterBlocksNumDataCodewords; i++) {
/*  81 */       for (int i1 = 0; i1 < numResultBlocks; i1++) {
/*  82 */         (result[i1]).codewords[i] = rawCodewords[rawCodewordsOffset++];
/*     */       }
/*     */     } 
/*     */     
/*     */     boolean specialVersion;
/*     */     
/*  88 */     int numLongerBlocks = (specialVersion = (version.getVersionNumber() == 24)) ? 8 : numResultBlocks;
/*  89 */     for (int j = 0; j < numLongerBlocks; j++) {
/*  90 */       (result[j]).codewords[longerBlocksNumDataCodewords - 1] = rawCodewords[rawCodewordsOffset++];
/*     */     }
/*     */ 
/*     */     
/*  94 */     int max = (result[0]).codewords.length;
/*  95 */     for (int n = longerBlocksNumDataCodewords; n < max; n++) {
/*  96 */       for (int i1 = 0; i1 < numResultBlocks; i1++) {
/*  97 */         int jOffset = specialVersion ? ((i1 + 8) % numResultBlocks) : i1;
/*  98 */         int iOffset = (specialVersion && jOffset > 7) ? (n - 1) : n;
/*  99 */         (result[jOffset]).codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
/*     */       } 
/*     */     } 
/*     */     
/* 103 */     if (rawCodewordsOffset != rawCodewords.length) {
/* 104 */       throw new IllegalArgumentException();
/*     */     }
/*     */     
/* 107 */     return result;
/*     */   }
/*     */   
/*     */   int getNumDataCodewords() {
/* 111 */     return this.numDataCodewords;
/*     */   }
/*     */   
/*     */   byte[] getCodewords() {
/* 115 */     return this.codewords;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\decoder\DataBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */