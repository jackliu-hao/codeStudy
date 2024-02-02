/*     */ package com.google.zxing.maxicode.decoder;
/*     */ 
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.reedsolomon.GenericGF;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
/*     */ import com.google.zxing.common.reedsolomon.ReedSolomonException;
/*     */ import java.util.Map;
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
/*     */ public final class Decoder
/*     */ {
/*     */   private static final int ALL = 0;
/*     */   private static final int EVEN = 1;
/*     */   private static final int ODD = 2;
/*  45 */   private final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GenericGF.MAXICODE_FIELD_64);
/*     */ 
/*     */   
/*     */   public DecoderResult decode(BitMatrix bits) throws ChecksumException, FormatException {
/*  49 */     return decode(bits, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public DecoderResult decode(BitMatrix bits, Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
/*  55 */     byte[] datawords, codewords = (new BitMatrixParser(bits)).readCodewords();
/*     */     
/*  57 */     correctErrors(codewords, 0, 10, 10, 0);
/*     */     
/*     */     int mode;
/*  60 */     switch (mode = codewords[0] & 0xF) {
/*     */       case 2:
/*     */       case 3:
/*     */       case 4:
/*  64 */         correctErrors(codewords, 20, 84, 40, 1);
/*  65 */         correctErrors(codewords, 20, 84, 40, 2);
/*  66 */         datawords = new byte[94];
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
/*  77 */         System.arraycopy(codewords, 0, datawords, 0, 10);
/*  78 */         System.arraycopy(codewords, 20, datawords, 10, datawords.length - 10);
/*     */         
/*  80 */         return DecodedBitStreamParser.decode(datawords, mode);case 5: correctErrors(codewords, 20, 68, 56, 1); correctErrors(codewords, 20, 68, 56, 2); datawords = new byte[78]; System.arraycopy(codewords, 0, datawords, 0, 10); System.arraycopy(codewords, 20, datawords, 10, datawords.length - 10); return DecodedBitStreamParser.decode(datawords, mode);
/*     */     } 
/*     */     throw FormatException.getFormatInstance();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void correctErrors(byte[] codewordBytes, int start, int dataCodewords, int ecCodewords, int mode) throws ChecksumException {
/*  88 */     int codewords = dataCodewords + ecCodewords;
/*     */ 
/*     */     
/*  91 */     int divisor = (mode == 0) ? 1 : 2;
/*     */ 
/*     */     
/*  94 */     int[] codewordsInts = new int[codewords / divisor]; int i;
/*  95 */     for (i = 0; i < codewords; i++) {
/*  96 */       if (mode == 0 || i % 2 == mode - 1) {
/*  97 */         codewordsInts[i / divisor] = codewordBytes[i + start] & 0xFF;
/*     */       }
/*     */     } 
/*     */     try {
/* 101 */       this.rsDecoder.decode(codewordsInts, ecCodewords / divisor);
/* 102 */     } catch (ReedSolomonException reedSolomonException) {
/* 103 */       throw ChecksumException.getChecksumInstance();
/*     */     } 
/*     */ 
/*     */     
/* 107 */     for (i = 0; i < dataCodewords; i++) {
/* 108 */       if (mode == 0 || i % 2 == mode - 1)
/* 109 */         codewordBytes[i + start] = (byte)codewordsInts[i / divisor]; 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\maxicode\decoder\Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */