/*     */ package com.google.zxing.pdf417;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.EncodeHintType;
/*     */ import com.google.zxing.Writer;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.pdf417.encoder.Compaction;
/*     */ import com.google.zxing.pdf417.encoder.Dimensions;
/*     */ import com.google.zxing.pdf417.encoder.PDF417;
/*     */ import java.nio.charset.Charset;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PDF417Writer
/*     */   implements Writer
/*     */ {
/*     */   static final int WHITE_SPACE = 30;
/*     */   static final int DEFAULT_ERROR_CORRECTION_LEVEL = 2;
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
/*  53 */     if (format != BarcodeFormat.PDF_417) {
/*  54 */       throw new IllegalArgumentException("Can only encode PDF_417, but got " + format);
/*     */     }
/*     */     
/*  57 */     PDF417 encoder = new PDF417();
/*  58 */     int margin = 30;
/*  59 */     int errorCorrectionLevel = 2;
/*     */     
/*  61 */     if (hints != null) {
/*  62 */       if (hints.containsKey(EncodeHintType.PDF417_COMPACT)) {
/*  63 */         encoder.setCompact(Boolean.valueOf(hints.get(EncodeHintType.PDF417_COMPACT).toString()).booleanValue());
/*     */       }
/*  65 */       if (hints.containsKey(EncodeHintType.PDF417_COMPACTION)) {
/*  66 */         encoder.setCompaction(Compaction.valueOf(hints.get(EncodeHintType.PDF417_COMPACTION).toString()));
/*     */       }
/*  68 */       if (hints.containsKey(EncodeHintType.PDF417_DIMENSIONS)) {
/*  69 */         Dimensions dimensions = (Dimensions)hints.get(EncodeHintType.PDF417_DIMENSIONS);
/*  70 */         encoder.setDimensions(dimensions.getMaxCols(), dimensions
/*  71 */             .getMinCols(), dimensions
/*  72 */             .getMaxRows(), dimensions
/*  73 */             .getMinRows());
/*     */       } 
/*  75 */       if (hints.containsKey(EncodeHintType.MARGIN)) {
/*  76 */         margin = Integer.parseInt(hints.get(EncodeHintType.MARGIN).toString());
/*     */       }
/*  78 */       if (hints.containsKey(EncodeHintType.ERROR_CORRECTION)) {
/*  79 */         errorCorrectionLevel = Integer.parseInt(hints.get(EncodeHintType.ERROR_CORRECTION).toString());
/*     */       }
/*  81 */       if (hints.containsKey(EncodeHintType.CHARACTER_SET)) {
/*  82 */         Charset encoding = Charset.forName(hints.get(EncodeHintType.CHARACTER_SET).toString());
/*  83 */         encoder.setEncoding(encoding);
/*     */       } 
/*     */     } 
/*     */     
/*  87 */     return bitMatrixFromEncoder(encoder, contents, errorCorrectionLevel, width, height, margin);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height) throws WriterException {
/*  95 */     return encode(contents, format, width, height, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitMatrix bitMatrixFromEncoder(PDF417 encoder, String contents, int errorCorrectionLevel, int width, int height, int margin) throws WriterException {
/*     */     int scale;
/* 107 */     encoder.generateBarcodeLogic(contents, errorCorrectionLevel);
/*     */ 
/*     */     
/* 110 */     byte[][] originalScale = encoder.getBarcodeMatrix().getScaledMatrix(1, 4);
/* 111 */     boolean rotated = false;
/* 112 */     if ((((height > width) ? 1 : 0) ^ (((originalScale[0]).length < originalScale.length) ? 1 : 0)) != 0) {
/* 113 */       originalScale = rotateArray(originalScale);
/* 114 */       rotated = true;
/*     */     } 
/*     */     
/* 117 */     int scaleX = width / (originalScale[0]).length;
/* 118 */     int scaleY = height / originalScale.length;
/*     */ 
/*     */     
/* 121 */     if (scaleX < scaleY) {
/* 122 */       scale = scaleX;
/*     */     } else {
/* 124 */       scale = scaleY;
/*     */     } 
/*     */     
/* 127 */     if (scale > 1) {
/*     */       
/* 129 */       byte[][] scaledMatrix = encoder.getBarcodeMatrix().getScaledMatrix(scale, scale << 2);
/* 130 */       if (rotated) {
/* 131 */         scaledMatrix = rotateArray(scaledMatrix);
/*     */       }
/* 133 */       return bitMatrixFrombitArray(scaledMatrix, margin);
/*     */     } 
/* 135 */     return bitMatrixFrombitArray(originalScale, margin);
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
/*     */   private static BitMatrix bitMatrixFrombitArray(byte[][] input, int margin) {
/*     */     BitMatrix output;
/* 148 */     (output = new BitMatrix((input[0]).length + 2 * margin, input.length + 2 * margin)).clear();
/* 149 */     for (int y = 0, yOutput = output.getHeight() - margin - 1; y < input.length; y++, yOutput--) {
/* 150 */       for (int x = 0; x < (input[0]).length; x++) {
/*     */         
/* 152 */         if (input[y][x] == 1) {
/* 153 */           output.set(x + margin, yOutput);
/*     */         }
/*     */       } 
/*     */     } 
/* 157 */     return output;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[][] rotateArray(byte[][] bitarray) {
/* 164 */     byte[][] temp = new byte[(bitarray[0]).length][bitarray.length];
/* 165 */     for (int ii = 0; ii < bitarray.length; ii++) {
/*     */ 
/*     */       
/* 168 */       int inverseii = bitarray.length - ii - 1;
/* 169 */       for (int jj = 0; jj < (bitarray[0]).length; jj++) {
/* 170 */         temp[jj][inverseii] = bitarray[ii][jj];
/*     */       }
/*     */     } 
/* 173 */     return temp;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\PDF417Writer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */