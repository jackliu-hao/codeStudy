/*     */ package com.google.zxing.multi;
/*     */ 
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.ReaderException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
/*     */ import java.util.ArrayList;
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
/*     */ public final class GenericMultipleBarcodeReader
/*     */   implements MultipleBarcodeReader
/*     */ {
/*     */   private static final int MIN_DIMENSION_TO_RECUR = 100;
/*     */   private static final int MAX_DEPTH = 4;
/*     */   private final Reader delegate;
/*     */   
/*     */   public GenericMultipleBarcodeReader(Reader delegate) {
/*  53 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image) throws NotFoundException {
/*  58 */     return decodeMultiple(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result[] decodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException {
/*  64 */     List<Result> results = new ArrayList<>();
/*  65 */     doDecodeMultiple(image, hints, results, 0, 0, 0);
/*  66 */     if (results.isEmpty()) {
/*  67 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*  69 */     return results.<Result>toArray(new Result[results.size()]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void doDecodeMultiple(BinaryBitmap image, Map<DecodeHintType, ?> hints, List<Result> results, int xOffset, int yOffset, int currentDepth) {
/*     */     Result result;
/*  78 */     if (currentDepth > 4) {
/*     */       return;
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/*  84 */       result = this.delegate.decode(image, hints);
/*  85 */     } catch (ReaderException readerException) {
/*     */       return;
/*     */     } 
/*  88 */     boolean bool = false;
/*  89 */     for (Iterator<Result> iterator = results.iterator(); iterator.hasNext();) { if (((Result)iterator.next())
/*  90 */         .getText().equals(result.getText())) {
/*  91 */         bool = true;
/*     */         break;
/*     */       }  }
/*     */     
/*  95 */     if (!bool) {
/*  96 */       results.add(translateResultPoints(result, xOffset, yOffset));
/*     */     }
/*     */     ResultPoint[] resultPoints;
/*  99 */     if ((resultPoints = result.getResultPoints()) == null || resultPoints.length == 0) {
/*     */       return;
/*     */     }
/* 102 */     int width = image.getWidth();
/* 103 */     int height = image.getHeight();
/* 104 */     float minX = width;
/* 105 */     float minY = height;
/* 106 */     float maxX = 0.0F;
/* 107 */     float maxY = 0.0F; ResultPoint[] arrayOfResultPoint1; int i; byte b;
/* 108 */     for (i = (arrayOfResultPoint1 = resultPoints).length, b = 0; b < i; b++) {
/* 109 */       ResultPoint point; if ((point = arrayOfResultPoint1[b]) != null) {
/*     */ 
/*     */         
/* 112 */         float x = point.getX();
/* 113 */         float y = point.getY();
/* 114 */         if (x < minX) {
/* 115 */           minX = x;
/*     */         }
/* 117 */         if (y < minY) {
/* 118 */           minY = y;
/*     */         }
/* 120 */         if (x > maxX) {
/* 121 */           maxX = x;
/*     */         }
/* 123 */         if (y > maxY) {
/* 124 */           maxY = y;
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 129 */     if (minX > 100.0F) {
/* 130 */       doDecodeMultiple(image.crop(0, 0, (int)minX, height), hints, results, xOffset, yOffset, currentDepth + 1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 136 */     if (minY > 100.0F) {
/* 137 */       doDecodeMultiple(image.crop(0, 0, width, (int)minY), hints, results, xOffset, yOffset, currentDepth + 1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 143 */     if (maxX < (width - 100)) {
/* 144 */       doDecodeMultiple(image.crop((int)maxX, 0, width - (int)maxX, height), hints, results, xOffset + (int)maxX, yOffset, currentDepth + 1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 150 */     if (maxY < (height - 100)) {
/* 151 */       doDecodeMultiple(image.crop(0, (int)maxY, width, height - (int)maxY), hints, results, xOffset, yOffset + (int)maxY, currentDepth + 1);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Result translateResultPoints(Result result, int xOffset, int yOffset) {
/*     */     ResultPoint[] oldResultPoints;
/* 160 */     if ((oldResultPoints = result.getResultPoints()) == null) {
/* 161 */       return result;
/*     */     }
/* 163 */     ResultPoint[] newResultPoints = new ResultPoint[oldResultPoints.length];
/* 164 */     for (int i = 0; i < oldResultPoints.length; i++) {
/*     */       ResultPoint oldPoint;
/* 166 */       if ((oldPoint = oldResultPoints[i]) != null) {
/* 167 */         newResultPoints[i] = new ResultPoint(oldPoint.getX() + xOffset, oldPoint.getY() + yOffset);
/*     */       }
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/*     */     Result newResult;
/*     */ 
/*     */     
/* 176 */     (newResult = new Result(result.getText(), result.getRawBytes(), result.getNumBits(), newResultPoints, result.getBarcodeFormat(), result.getTimestamp())).putAllMetadata(result.getResultMetadata());
/* 177 */     return newResult;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\GenericMultipleBarcodeReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */