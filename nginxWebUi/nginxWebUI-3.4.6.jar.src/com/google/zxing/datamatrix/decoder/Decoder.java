/*     */ package com.google.zxing.datamatrix.decoder;
/*     */ 
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class Decoder
/*     */ {
/*  38 */   private final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DecoderResult decode(boolean[][] image) throws FormatException, ChecksumException {
/*  51 */     int dimension = image.length;
/*  52 */     BitMatrix bits = new BitMatrix(dimension);
/*  53 */     for (int i = 0; i < dimension; i++) {
/*  54 */       for (int j = 0; j < dimension; j++) {
/*  55 */         if (image[i][j]) {
/*  56 */           bits.set(j, i);
/*     */         }
/*     */       } 
/*     */     } 
/*  60 */     return decode(bits);
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
/*     */   public DecoderResult decode(BitMatrix bits) throws FormatException, ChecksumException {
/*     */     BitMatrixParser parser;
/*  76 */     Version version = (parser = new BitMatrixParser(bits)).getVersion();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  81 */     DataBlock[] dataBlocks = DataBlock.getDataBlocks(parser.readCodewords(), version);
/*     */ 
/*     */     
/*  84 */     int totalBytes = 0; DataBlock[] arrayOfDataBlock1; int i; byte b;
/*  85 */     for (i = (arrayOfDataBlock1 = dataBlocks).length, b = 0; b < i; ) { DataBlock db = arrayOfDataBlock1[b];
/*  86 */       totalBytes += db.getNumDataCodewords(); b++; }
/*     */     
/*  88 */     byte[] resultBytes = new byte[totalBytes];
/*     */     
/*  90 */     int dataBlocksCount = dataBlocks.length;
/*     */     
/*  92 */     for (int j = 0; j < dataBlocksCount; j++) {
/*     */       DataBlock dataBlock;
/*  94 */       byte[] codewordBytes = (dataBlock = dataBlocks[j]).getCodewords();
/*  95 */       int numDataCodewords = dataBlock.getNumDataCodewords();
/*  96 */       correctErrors(codewordBytes, numDataCodewords);
/*  97 */       for (int k = 0; k < numDataCodewords; k++)
/*     */       {
/*  99 */         resultBytes[k * dataBlocksCount + j] = codewordBytes[k];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 104 */     return DecodedBitStreamParser.decode(resultBytes);
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
/*     */   private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException {
/* 118 */     int numCodewords, codewordsInts[] = new int[numCodewords = codewordBytes.length]; int i;
/* 119 */     for (i = 0; i < numCodewords; i++) {
/* 120 */       codewordsInts[i] = codewordBytes[i] & 0xFF;
/*     */     }
/*     */     try {
/* 123 */       this.rsDecoder.decode(codewordsInts, codewordBytes.length - numDataCodewords);
/* 124 */     } catch (ReedSolomonException reedSolomonException) {
/* 125 */       throw ChecksumException.getChecksumInstance();
/*     */     } 
/*     */ 
/*     */     
/* 129 */     for (i = 0; i < numDataCodewords; i++)
/* 130 */       codewordBytes[i] = (byte)codewordsInts[i]; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\decoder\Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */