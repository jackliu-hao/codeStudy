/*     */ package com.google.zxing.pdf417;
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
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.multi.MultipleBarcodeReader;
/*     */ import com.google.zxing.pdf417.decoder.PDF417ScanningDecoder;
/*     */ import com.google.zxing.pdf417.detector.Detector;
/*     */ import com.google.zxing.pdf417.detector.PDF417DetectorResult;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class PDF417Reader
/*     */   implements Reader, MultipleBarcodeReader
/*     */ {
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException, ChecksumException {
/*  55 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
/*     */     Result[] result;
/*  62 */     if ((result = decode(image, hints, false)) == null || result.length == 0 || result[0] == null) {
/*  63 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*  65 */     return result[0];
/*     */   }
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
/*  70 */     return decodeMultiple(image, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*     */     try {
/*  76 */       return decode(image, hints, true);
/*  77 */     } catch (FormatException|ChecksumException formatException) {
/*  78 */       throw NotFoundException.getNotFoundInstance();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static Result[] decode(BinaryBitmap image, Map<DecodeHintType, ?> hints, boolean multiple) throws NotFoundException, FormatException, ChecksumException {
/*  84 */     List<Result> results = new ArrayList<>();
/*     */     PDF417DetectorResult detectorResult;
/*  86 */     for (ResultPoint[] points : (detectorResult = Detector.detect(image, hints, multiple)).getPoints()) {
/*  87 */       DecoderResult decoderResult = PDF417ScanningDecoder.decode(detectorResult.getBits(), points[4], points[5], points[6], points[7], 
/*  88 */           getMinCodewordWidth(points), getMaxCodewordWidth(points));
/*     */       Result result;
/*  90 */       (result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.PDF_417)).putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, decoderResult.getECLevel());
/*     */       PDF417ResultMetadata pdf417ResultMetadata;
/*  92 */       if ((pdf417ResultMetadata = (PDF417ResultMetadata)decoderResult.getOther()) != null) {
/*  93 */         result.putMetadata(ResultMetadataType.PDF417_EXTRA_METADATA, pdf417ResultMetadata);
/*     */       }
/*  95 */       results.add(result);
/*     */     } 
/*  97 */     return results.<Result>toArray(new Result[results.size()]);
/*     */   }
/*     */   
/*     */   private static int getMaxWidth(ResultPoint p1, ResultPoint p2) {
/* 101 */     if (p1 == null || p2 == null) {
/* 102 */       return 0;
/*     */     }
/* 104 */     return (int)Math.abs(p1.getX() - p2.getX());
/*     */   }
/*     */   
/*     */   private static int getMinWidth(ResultPoint p1, ResultPoint p2) {
/* 108 */     if (p1 == null || p2 == null) {
/* 109 */       return Integer.MAX_VALUE;
/*     */     }
/* 111 */     return (int)Math.abs(p1.getX() - p2.getX());
/*     */   }
/*     */   
/*     */   private static int getMaxCodewordWidth(ResultPoint[] p) {
/* 115 */     return Math.max(
/* 116 */         Math.max(getMaxWidth(p[0], p[4]), getMaxWidth(p[6], p[2]) * 17 / 18), 
/*     */         
/* 118 */         Math.max(getMaxWidth(p[1], p[5]), getMaxWidth(p[7], p[3]) * 17 / 18));
/*     */   }
/*     */ 
/*     */   
/*     */   private static int getMinCodewordWidth(ResultPoint[] p) {
/* 123 */     return Math.min(
/* 124 */         Math.min(getMinWidth(p[0], p[4]), getMinWidth(p[6], p[2]) * 17 / 18), 
/*     */         
/* 126 */         Math.min(getMinWidth(p[1], p[5]), getMinWidth(p[7], p[3]) * 17 / 18));
/*     */   }
/*     */   
/*     */   public void reset() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\pdf417\PDF417Reader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */