/*     */ package com.google.zxing.datamatrix;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.DetectorResult;
/*     */ import com.google.zxing.datamatrix.decoder.Decoder;
/*     */ import com.google.zxing.datamatrix.detector.Detector;
/*     */ import java.util.List;
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
/*     */ public final class DataMatrixReader
/*     */   implements Reader
/*     */ {
/*  45 */   private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
/*     */   
/*  47 */   private final Decoder decoder = new Decoder();
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
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
/*  59 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*     */     DecoderResult decoderResult;
/*     */     ResultPoint[] points;
/*  67 */     if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
/*  68 */       BitMatrix bits = extractPureBits(image.getBlackMatrix());
/*  69 */       decoderResult = this.decoder.decode(bits);
/*  70 */       points = NO_POINTS;
/*     */     } else {
/*  72 */       DetectorResult detectorResult = (new Detector(image.getBlackMatrix())).detect();
/*  73 */       decoderResult = this.decoder.decode(detectorResult.getBits());
/*  74 */       points = detectorResult.getPoints();
/*     */     } 
/*  76 */     Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.DATA_MATRIX);
/*     */     
/*     */     List<byte[]> byteSegments;
/*  79 */     if ((byteSegments = decoderResult.getByteSegments()) != null) {
/*  80 */       result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
/*     */     }
/*     */     String ecLevel;
/*  83 */     if ((ecLevel = decoderResult.getECLevel()) != null) {
/*  84 */       result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
/*     */     }
/*  86 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static BitMatrix extractPureBits(BitMatrix image) throws NotFoundException {
/* 104 */     int[] leftTopBlack = image.getTopLeftOnBit();
/* 105 */     int[] rightBottomBlack = image.getBottomRightOnBit();
/* 106 */     if (leftTopBlack == null || rightBottomBlack == null) {
/* 107 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 110 */     int moduleSize = moduleSize(leftTopBlack, image);
/*     */     
/* 112 */     int top = leftTopBlack[1];
/* 113 */     int bottom = rightBottomBlack[1];
/* 114 */     int left = leftTopBlack[0];
/*     */ 
/*     */     
/* 117 */     int matrixWidth = (rightBottomBlack[0] - left + 1) / moduleSize;
/* 118 */     int matrixHeight = (bottom - top + 1) / moduleSize;
/* 119 */     if (matrixWidth <= 0 || matrixHeight <= 0) {
/* 120 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 126 */     int nudge = moduleSize / 2;
/* 127 */     top += nudge;
/* 128 */     left += nudge;
/*     */ 
/*     */     
/* 131 */     BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
/* 132 */     for (int y = 0; y < matrixHeight; y++) {
/* 133 */       int iOffset = top + y * moduleSize;
/* 134 */       for (int x = 0; x < matrixWidth; x++) {
/* 135 */         if (image.get(left + x * moduleSize, iOffset)) {
/* 136 */           bits.set(x, y);
/*     */         }
/*     */       } 
/*     */     } 
/* 140 */     return bits;
/*     */   }
/*     */   
/*     */   private static int moduleSize(int[] leftTopBlack, BitMatrix image) throws NotFoundException {
/* 144 */     int width = image.getWidth();
/* 145 */     int x = leftTopBlack[0];
/* 146 */     int y = leftTopBlack[1];
/* 147 */     while (x < width && image.get(x, y)) {
/* 148 */       x++;
/*     */     }
/* 150 */     if (x == width) {
/* 151 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/*     */     int moduleSize;
/* 155 */     if ((moduleSize = x - leftTopBlack[0]) == 0) {
/* 156 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 158 */     return moduleSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\DataMatrixReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */