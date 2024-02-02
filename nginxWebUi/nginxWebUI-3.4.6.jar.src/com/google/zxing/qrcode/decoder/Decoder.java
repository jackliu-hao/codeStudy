/*     */ package com.google.zxing.qrcode.decoder;
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
/*     */ public final class Decoder
/*     */ {
/*  41 */   private final ReedSolomonDecoder rsDecoder = new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
/*     */ 
/*     */   
/*     */   public DecoderResult decode(boolean[][] image) throws ChecksumException, FormatException {
/*  45 */     return decode(image, (Map<DecodeHintType, ?>)null);
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
/*     */   public DecoderResult decode(boolean[][] image, Map<DecodeHintType, ?> hints) throws ChecksumException, FormatException {
/*  60 */     int dimension = image.length;
/*  61 */     BitMatrix bits = new BitMatrix(dimension);
/*  62 */     for (int i = 0; i < dimension; i++) {
/*  63 */       for (int j = 0; j < dimension; j++) {
/*  64 */         if (image[i][j]) {
/*  65 */           bits.set(j, i);
/*     */         }
/*     */       } 
/*     */     } 
/*  69 */     return decode(bits, hints);
/*     */   }
/*     */   
/*     */   public DecoderResult decode(BitMatrix bits) throws ChecksumException, FormatException {
/*  73 */     return decode(bits, (Map<DecodeHintType, ?>)null);
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
/*     */   public DecoderResult decode(BitMatrix bits, Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
/*  89 */     BitMatrixParser parser = new BitMatrixParser(bits);
/*  90 */     formatException = null;
/*  91 */     checksumException = null;
/*     */     try {
/*  93 */       return decode(parser, hints);
/*     */     }
/*  95 */     catch (FormatException formatException) {
/*     */     
/*  97 */     } catch (ChecksumException checksumException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 103 */       parser.remask();
/*     */ 
/*     */       
/* 106 */       parser.setMirror(true);
/*     */ 
/*     */       
/* 109 */       parser.readVersion();
/*     */ 
/*     */       
/* 112 */       parser.readFormatInformation();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 121 */       parser.mirror();
/*     */ 
/*     */       
/*     */       DecoderResult result;
/*     */       
/* 126 */       (result = decode(parser, hints)).setOther(new QRCodeDecoderMetaData(true));
/*     */       
/* 128 */       return result;
/*     */     }
/* 130 */     catch (FormatException|ChecksumException readerException) {
/*     */       ChecksumException e;
/* 132 */       if (formatException != null) {
/* 133 */         throw formatException;
/*     */       }
/* 135 */       if (checksumException != null) {
/* 136 */         throw checksumException;
/*     */       }
/* 138 */       throw e;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private DecoderResult decode(BitMatrixParser parser, Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
/* 145 */     Version version = parser.readVersion();
/* 146 */     ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 151 */     DataBlock[] dataBlocks = DataBlock.getDataBlocks(parser.readCodewords(), version, ecLevel);
/*     */ 
/*     */     
/* 154 */     int totalBytes = 0; DataBlock[] arrayOfDataBlock1; int i; byte b1;
/* 155 */     for (i = (arrayOfDataBlock1 = dataBlocks).length, b1 = 0; b1 < i; ) { DataBlock dataBlock = arrayOfDataBlock1[b1];
/* 156 */       totalBytes += dataBlock.getNumDataCodewords(); b1++; }
/*     */     
/* 158 */     byte[] resultBytes = new byte[totalBytes];
/* 159 */     int resultOffset = 0; DataBlock[] arrayOfDataBlock2;
/*     */     int j;
/*     */     byte b2;
/* 162 */     for (j = (arrayOfDataBlock2 = dataBlocks).length, b2 = 0; b2 < j; b2++) {
/* 163 */       DataBlock dataBlock; byte[] codewordBytes = (dataBlock = arrayOfDataBlock2[b2]).getCodewords();
/* 164 */       int numDataCodewords = dataBlock.getNumDataCodewords();
/* 165 */       correctErrors(codewordBytes, numDataCodewords);
/* 166 */       for (int k = 0; k < numDataCodewords; k++) {
/* 167 */         resultBytes[resultOffset++] = codewordBytes[k];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 172 */     return DecodedBitStreamParser.decode(resultBytes, version, ecLevel, hints);
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
/* 186 */     int numCodewords, codewordsInts[] = new int[numCodewords = codewordBytes.length]; int i;
/* 187 */     for (i = 0; i < numCodewords; i++) {
/* 188 */       codewordsInts[i] = codewordBytes[i] & 0xFF;
/*     */     }
/*     */     try {
/* 191 */       this.rsDecoder.decode(codewordsInts, codewordBytes.length - numDataCodewords);
/* 192 */     } catch (ReedSolomonException reedSolomonException) {
/* 193 */       throw ChecksumException.getChecksumInstance();
/*     */     } 
/*     */ 
/*     */     
/* 197 */     for (i = 0; i < numDataCodewords; i++)
/* 198 */       codewordBytes[i] = (byte)codewordsInts[i]; 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\decoder\Decoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */