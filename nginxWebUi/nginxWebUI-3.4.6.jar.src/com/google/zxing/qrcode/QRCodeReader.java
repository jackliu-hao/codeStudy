/*     */ package com.google.zxing.qrcode;
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
/*     */ import com.google.zxing.qrcode.decoder.Decoder;
/*     */ import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
/*     */ import com.google.zxing.qrcode.detector.Detector;
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
/*     */ public class QRCodeReader
/*     */   implements Reader
/*     */ {
/*  46 */   private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
/*     */   
/*  48 */   private final Decoder decoder = new Decoder();
/*     */   
/*     */   protected final Decoder getDecoder() {
/*  51 */     return this.decoder;
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
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
/*  64 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public final Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*     */     DecoderResult decoderResult;
/*     */     ResultPoint[] points;
/*  72 */     if (hints != null && hints.containsKey(DecodeHintType.PURE_BARCODE)) {
/*  73 */       BitMatrix bits = extractPureBits(image.getBlackMatrix());
/*  74 */       decoderResult = this.decoder.decode(bits, hints);
/*  75 */       points = NO_POINTS;
/*     */     } else {
/*  77 */       DetectorResult detectorResult = (new Detector(image.getBlackMatrix())).detect(hints);
/*  78 */       decoderResult = this.decoder.decode(detectorResult.getBits(), hints);
/*  79 */       points = detectorResult.getPoints();
/*     */     } 
/*     */ 
/*     */     
/*  83 */     if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
/*  84 */       ((QRCodeDecoderMetaData)decoderResult.getOther()).applyMirroredCorrection(points);
/*     */     }
/*     */     
/*  87 */     Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
/*     */     List<byte[]> byteSegments;
/*  89 */     if ((byteSegments = decoderResult.getByteSegments()) != null) {
/*  90 */       result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
/*     */     }
/*     */     String ecLevel;
/*  93 */     if ((ecLevel = decoderResult.getECLevel()) != null) {
/*  94 */       result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
/*     */     }
/*  96 */     if (decoderResult.hasStructuredAppend()) {
/*  97 */       result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE, 
/*  98 */           Integer.valueOf(decoderResult.getStructuredAppendSequenceNumber()));
/*  99 */       result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_PARITY, 
/* 100 */           Integer.valueOf(decoderResult.getStructuredAppendParity()));
/*     */     } 
/* 102 */     return result;
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
/* 120 */     int[] leftTopBlack = image.getTopLeftOnBit();
/* 121 */     int[] rightBottomBlack = image.getBottomRightOnBit();
/* 122 */     if (leftTopBlack == null || rightBottomBlack == null) {
/* 123 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 126 */     float moduleSize = moduleSize(leftTopBlack, image);
/*     */     
/* 128 */     int top = leftTopBlack[1];
/* 129 */     int bottom = rightBottomBlack[1];
/* 130 */     int left = leftTopBlack[0];
/* 131 */     int right = rightBottomBlack[0];
/*     */ 
/*     */     
/* 134 */     if (left >= right || top >= bottom) {
/* 135 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */     
/* 138 */     if (bottom - top != right - left)
/*     */     {
/*     */ 
/*     */       
/* 142 */       if ((right = left + bottom - top) >= image.getWidth())
/*     */       {
/* 144 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/*     */     }
/*     */     
/* 148 */     int matrixWidth = Math.round((right - left + 1) / moduleSize);
/* 149 */     int matrixHeight = Math.round((bottom - top + 1) / moduleSize);
/* 150 */     if (matrixWidth <= 0 || matrixHeight <= 0) {
/* 151 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 153 */     if (matrixHeight != matrixWidth)
/*     */     {
/* 155 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 161 */     int nudge = (int)(moduleSize / 2.0F);
/* 162 */     top += nudge;
/*     */ 
/*     */ 
/*     */     
/*     */     int nudgedTooFarRight;
/*     */ 
/*     */     
/* 169 */     if ((nudgedTooFarRight = (left = left + nudge) + (int)((matrixWidth - 1) * moduleSize) - right) > 0) {
/* 170 */       if (nudgedTooFarRight > nudge)
/*     */       {
/* 172 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 174 */       left -= nudgedTooFarRight;
/*     */     } 
/*     */     
/*     */     int nudgedTooFarDown;
/* 178 */     if ((nudgedTooFarDown = top + (int)((matrixHeight - 1) * moduleSize) - bottom) > 0) {
/* 179 */       if (nudgedTooFarDown > nudge)
/*     */       {
/* 181 */         throw NotFoundException.getNotFoundInstance();
/*     */       }
/* 183 */       top -= nudgedTooFarDown;
/*     */     } 
/*     */ 
/*     */     
/* 187 */     BitMatrix bits = new BitMatrix(matrixWidth, matrixHeight);
/* 188 */     for (int y = 0; y < matrixHeight; y++) {
/* 189 */       int iOffset = top + (int)(y * moduleSize);
/* 190 */       for (int x = 0; x < matrixWidth; x++) {
/* 191 */         if (image.get(left + (int)(x * moduleSize), iOffset)) {
/* 192 */           bits.set(x, y);
/*     */         }
/*     */       } 
/*     */     } 
/* 196 */     return bits;
/*     */   }
/*     */   
/*     */   private static float moduleSize(int[] leftTopBlack, BitMatrix image) throws NotFoundException {
/* 200 */     int height = image.getHeight();
/* 201 */     int width = image.getWidth();
/* 202 */     int x = leftTopBlack[0];
/* 203 */     int y = leftTopBlack[1];
/* 204 */     boolean inBlack = true;
/* 205 */     int transitions = 0;
/* 206 */     while (x < width && y < height) {
/* 207 */       if (inBlack != image.get(x, y))
/* 208 */         if (++transitions != 5)
/*     */         
/*     */         { 
/* 211 */           inBlack = !inBlack; }
/*     */         else { break; }
/* 213 */           x++;
/* 214 */       y++;
/*     */     } 
/* 216 */     if (x == width || y == height) {
/* 217 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/* 219 */     return (x - leftTopBlack[0]) / 7.0F;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\qrcode\QRCodeReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */