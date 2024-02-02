/*     */ package com.google.zxing.aztec;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.ResultPointCallback;
/*     */ import com.google.zxing.aztec.decoder.Decoder;
/*     */ import com.google.zxing.aztec.detector.Detector;
/*     */ import com.google.zxing.common.DecoderResult;
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
/*     */ public final class AztecReader
/*     */   implements Reader
/*     */ {
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, FormatException {
/*  52 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException {
/*  59 */     notFoundException = null;
/*  60 */     formatException = null;
/*  61 */     Detector detector = new Detector(image.getBlackMatrix());
/*  62 */     ResultPoint[] points = null;
/*  63 */     DecoderResult decoderResult = null;
/*     */     try {
/*     */       AztecDetectorResult detectorResult;
/*  66 */       points = (detectorResult = detector.detect(false)).getPoints();
/*  67 */       decoderResult = (new Decoder()).decode(detectorResult);
/*     */     }
/*  69 */     catch (NotFoundException notFoundException) {
/*     */     
/*  71 */     } catch (FormatException formatException) {}
/*     */     
/*  73 */     if (decoderResult == null) {
/*     */       try {
/*     */         AztecDetectorResult detectorResult;
/*  76 */         points = (detectorResult = detector.detect(true)).getPoints();
/*  77 */         decoderResult = (new Decoder()).decode(detectorResult);
/*  78 */       } catch (NotFoundException|FormatException readerException) {
/*  79 */         FormatException e; if (notFoundException != null) {
/*  80 */           throw notFoundException;
/*     */         }
/*  82 */         if (formatException != null) {
/*  83 */           throw formatException;
/*     */         }
/*  85 */         throw e;
/*     */       } 
/*     */     }
/*     */     ResultPointCallback rpcb;
/*  89 */     if (hints != null && (
/*     */       
/*  91 */       rpcb = (ResultPointCallback)hints.get(DecodeHintType.NEED_RESULT_POINT_CALLBACK)) != null) {
/*  92 */       ResultPoint[] arrayOfResultPoint; int i; byte b; for (i = (arrayOfResultPoint = points).length, b = 0; b < i; ) { ResultPoint point = arrayOfResultPoint[b];
/*  93 */         rpcb.foundPossibleResultPoint(point);
/*     */ 
/*     */ 
/*     */         
/*     */         b++; }
/*     */     
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 103 */     Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), decoderResult.getNumBits(), points, BarcodeFormat.AZTEC, System.currentTimeMillis());
/*     */     
/*     */     List<byte[]> byteSegments;
/* 106 */     if ((byteSegments = decoderResult.getByteSegments()) != null) {
/* 107 */       result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
/*     */     }
/*     */     String ecLevel;
/* 110 */     if ((ecLevel = decoderResult.getECLevel()) != null) {
/* 111 */       result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
/*     */     }
/*     */     
/* 114 */     return result;
/*     */   }
/*     */   
/*     */   public void reset() {}
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\aztec\AztecReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */