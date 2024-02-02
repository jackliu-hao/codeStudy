/*     */ package com.google.zxing.qrcode.decoder;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ 
/*     */   
/*     */   static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version, ErrorCorrectionLevel ecLevel) {
/*  51 */     if (rawCodewords.length != version.getTotalCodewords()) {
/*  52 */       throw new IllegalArgumentException();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*  57 */     Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
/*     */ 
/*     */     
/*  60 */     int totalBlocks = 0; Version.ECB[] ecBlockArray, arrayOfECB1; int k;
/*     */     byte b1;
/*  62 */     for (k = (arrayOfECB1 = ecBlockArray = ecBlocks.getECBlocks()).length, b1 = 0; b1 < k; ) { Version.ECB ecBlock = arrayOfECB1[b1];
/*  63 */       totalBlocks += ecBlock.getCount();
/*     */       
/*     */       b1++; }
/*     */     
/*  67 */     DataBlock[] result = new DataBlock[totalBlocks];
/*  68 */     int numResultBlocks = 0; Version.ECB[] arrayOfECB2; int m; byte b2;
/*  69 */     for (m = (arrayOfECB2 = ecBlockArray).length, b2 = 0; b2 < m; ) { Version.ECB ecBlock = arrayOfECB2[b2];
/*  70 */       for (int i1 = 0; i1 < ecBlock.getCount(); i1++) {
/*  71 */         int numDataCodewords = ecBlock.getDataCodewords();
/*  72 */         int numBlockCodewords = ecBlocks.getECCodewordsPerBlock() + numDataCodewords;
/*  73 */         result[numResultBlocks++] = new DataBlock(numDataCodewords, new byte[numBlockCodewords]);
/*     */       } 
/*     */       
/*     */       b2++; }
/*     */ 
/*     */     
/*  79 */     int shorterBlocksTotalCodewords = (result[0]).codewords.length;
/*  80 */     int longerBlocksStartAt = result.length - 1;
/*  81 */     while (longerBlocksStartAt >= 0 && 
/*  82 */       (result[longerBlocksStartAt]).codewords.length != 
/*  83 */       shorterBlocksTotalCodewords)
/*     */     {
/*     */       
/*  86 */       longerBlocksStartAt--;
/*     */     }
/*  88 */     longerBlocksStartAt++;
/*     */     
/*  90 */     int shorterBlocksNumDataCodewords = shorterBlocksTotalCodewords - ecBlocks.getECCodewordsPerBlock();
/*     */ 
/*     */     
/*  93 */     int rawCodewordsOffset = 0;
/*  94 */     for (int i = 0; i < shorterBlocksNumDataCodewords; i++) {
/*  95 */       for (int i1 = 0; i1 < numResultBlocks; i1++) {
/*  96 */         (result[i1]).codewords[i] = rawCodewords[rawCodewordsOffset++];
/*     */       }
/*     */     } 
/*     */     
/* 100 */     for (int j = longerBlocksStartAt; j < numResultBlocks; j++) {
/* 101 */       (result[j]).codewords[shorterBlocksNumDataCodewords] = rawCodewords[rawCodewordsOffset++];
/*     */     }
/*     */     
/* 104 */     int max = (result[0]).codewords.length;
/* 105 */     for (int n = shorterBlocksNumDataCodewords; n < max; n++) {
/* 106 */       for (int i1 = 0; i1 < numResultBlocks; i1++) {
/* 107 */         int iOffset = (i1 < longerBlocksStartAt) ? n : (n + 1);
/* 108 */         (result[i1]).codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
/*     */       } 
/*     */     } 
/* 111 */     return result;
/*     */   }
/*     */   
/*     */   int getNumDataCodewords() {
/* 115 */     return this.numDataCodewords;
/*     */   }
/*     */   
/*     */   byte[] getCodewords() {
/* 119 */     return this.codewords;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\DataBlock.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */