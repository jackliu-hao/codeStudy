/*     */ package com.google.zxing.multi.qrcode;
/*     */ 
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultMetadataType;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import com.google.zxing.common.DecoderResult;
/*     */ import com.google.zxing.common.DetectorResult;
/*     */ import com.google.zxing.multi.MultipleBarcodeReader;
/*     */ import com.google.zxing.multi.qrcode.detector.MultiDetector;
/*     */ import com.google.zxing.qrcode.QRCodeReader;
/*     */ import com.google.zxing.qrcode.decoder.QRCodeDecoderMetaData;
/*     */ import java.io.Serializable;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.Iterator;
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
/*     */ public final class QRCodeMultiReader
/*     */   extends QRCodeReader
/*     */   implements MultipleBarcodeReader
/*     */ {
/*  50 */   private static final Result[] EMPTY_RESULT_ARRAY = new Result[0];
/*  51 */   private static final ResultPoint[] NO_POINTS = new ResultPoint[0];
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
/*  55 */     return decodeMultiple(image, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  60 */     List<Result> results = new ArrayList<>(); DetectorResult[] arrayOfDetectorResult; int i;
/*     */     byte b;
/*  62 */     for (i = (arrayOfDetectorResult = (new MultiDetector(image.getBlackMatrix())).detectMulti(hints)).length, b = 0; b < i; ) { DetectorResult detectorResult = arrayOfDetectorResult[b];
/*     */       try {
/*  64 */         DecoderResult decoderResult = getDecoder().decode(detectorResult.getBits(), hints);
/*  65 */         ResultPoint[] points = detectorResult.getPoints();
/*     */         
/*  67 */         if (decoderResult.getOther() instanceof QRCodeDecoderMetaData) {
/*  68 */           ((QRCodeDecoderMetaData)decoderResult.getOther()).applyMirroredCorrection(points);
/*     */         }
/*  70 */         Result result = new Result(decoderResult.getText(), decoderResult.getRawBytes(), points, BarcodeFormat.QR_CODE);
/*     */         
/*     */         List<byte[]> byteSegments;
/*  73 */         if ((byteSegments = decoderResult.getByteSegments()) != null) {
/*  74 */           result.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegments);
/*     */         }
/*     */         String ecLevel;
/*  77 */         if ((ecLevel = decoderResult.getECLevel()) != null) {
/*  78 */           result.putMetadata(ResultMetadataType.ERROR_CORRECTION_LEVEL, ecLevel);
/*     */         }
/*  80 */         if (decoderResult.hasStructuredAppend()) {
/*  81 */           result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE, 
/*  82 */               Integer.valueOf(decoderResult.getStructuredAppendSequenceNumber()));
/*  83 */           result.putMetadata(ResultMetadataType.STRUCTURED_APPEND_PARITY, 
/*  84 */               Integer.valueOf(decoderResult.getStructuredAppendParity()));
/*     */         } 
/*  86 */         results.add(result);
/*  87 */       } catch (ReaderException readerException) {}
/*     */       
/*     */       b++; }
/*     */     
/*  91 */     if (results.isEmpty()) {
/*  92 */       return EMPTY_RESULT_ARRAY;
/*     */     }
/*  94 */     return processStructuredAppend(results)
/*  95 */       .<Result>toArray(new Result[processStructuredAppend(results).size()]);
/*     */   }
/*     */ 
/*     */   
/*     */   private static List<Result> processStructuredAppend(List<Result> results) {
/* 100 */     boolean hasSA = false;
/*     */ 
/*     */     
/* 103 */     for (Iterator<Result> iterator1 = results.iterator(); iterator1.hasNext();) { if (((Result)iterator1.next())
/* 104 */         .getResultMetadata().containsKey(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)) {
/* 105 */         hasSA = true;
/*     */         break;
/*     */       }  }
/*     */     
/* 109 */     if (!hasSA) {
/* 110 */       return results;
/*     */     }
/*     */ 
/*     */     
/* 114 */     List<Result> newResults = new ArrayList<>();
/* 115 */     List<Result> saResults = new ArrayList<>();
/* 116 */     for (Result result : results) {
/* 117 */       newResults.add(result);
/* 118 */       if (result.getResultMetadata().containsKey(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)) {
/* 119 */         saResults.add(result);
/*     */       }
/*     */     } 
/*     */     
/* 123 */     Collections.sort(saResults, new SAComparator());
/* 124 */     StringBuilder concatedText = new StringBuilder();
/* 125 */     int rawBytesLen = 0;
/* 126 */     int byteSegmentLength = 0;
/* 127 */     for (Result saResult : saResults) {
/* 128 */       concatedText.append(saResult.getText());
/* 129 */       rawBytesLen += (saResult.getRawBytes()).length;
/* 130 */       if (saResult.getResultMetadata().containsKey(ResultMetadataType.BYTE_SEGMENTS))
/*     */       {
/* 132 */         for (byte[] segment : saResult
/* 133 */           .getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS))
/*     */         {
/* 135 */           byteSegmentLength += segment.length;
/*     */         }
/*     */       }
/*     */     } 
/* 139 */     byte[] newRawBytes = new byte[rawBytesLen];
/* 140 */     byte[] newByteSegment = new byte[byteSegmentLength];
/* 141 */     int newRawBytesIndex = 0;
/* 142 */     int byteSegmentIndex = 0;
/* 143 */     for (Iterator<Result> iterator2 = saResults.iterator(); iterator2.hasNext(); ) {
/* 144 */       Result saResult; System.arraycopy((saResult = iterator2.next()).getRawBytes(), 0, newRawBytes, newRawBytesIndex, (saResult.getRawBytes()).length);
/* 145 */       int i = newRawBytesIndex + (saResult.getRawBytes()).length;
/* 146 */       if (saResult.getResultMetadata().containsKey(ResultMetadataType.BYTE_SEGMENTS))
/*     */       {
/*     */ 
/*     */         
/* 150 */         for (Iterator<byte[]> iterator = ((Iterable)saResult.getResultMetadata().get(ResultMetadataType.BYTE_SEGMENTS)).iterator(); iterator.hasNext(); ) {
/* 151 */           byte[] segment; System.arraycopy(segment = iterator.next(), 0, newByteSegment, byteSegmentIndex, segment.length);
/* 152 */           byteSegmentIndex += segment.length;
/*     */         } 
/*     */       }
/*     */     } 
/* 156 */     Result newResult = new Result(concatedText.toString(), newRawBytes, NO_POINTS, BarcodeFormat.QR_CODE);
/* 157 */     if (byteSegmentLength > 0) {
/*     */       Collection<byte[]> byteSegmentList;
/* 159 */       (byteSegmentList = (Collection)new ArrayList<>()).add(newByteSegment);
/* 160 */       newResult.putMetadata(ResultMetadataType.BYTE_SEGMENTS, byteSegmentList);
/*     */     } 
/* 162 */     newResults.add(newResult);
/* 163 */     return newResults;
/*     */   }
/*     */   
/*     */   private static final class SAComparator implements Serializable, Comparator<Result> { private SAComparator() {}
/*     */     
/*     */     public int compare(Result a, Result b) {
/* 169 */       int aNumber = ((Integer)a.getResultMetadata().get(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)).intValue();
/* 170 */       int bNumber = ((Integer)b.getResultMetadata().get(ResultMetadataType.STRUCTURED_APPEND_SEQUENCE)).intValue();
/* 171 */       if (aNumber < bNumber) {
/* 172 */         return -1;
/*     */       }
/* 174 */       if (aNumber > bNumber) {
/* 175 */         return 1;
/*     */       }
/* 177 */       return 0;
/*     */     } }
/*     */ 
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\qrcode\QRCodeMultiReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */