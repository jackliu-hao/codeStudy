/*     */ package com.google.zxing.multi;
/*     */ 
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.ChecksumException;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.FormatException;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Reader;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.ResultPoint;
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
/*     */ public final class ByQuadrantReader
/*     */   implements Reader
/*     */ {
/*     */   private final Reader delegate;
/*     */   
/*     */   public ByQuadrantReader(Reader delegate) {
/*  44 */     this.delegate = delegate;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image) throws NotFoundException, ChecksumException, FormatException {
/*  50 */     return decode(image, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Result decode(BinaryBitmap image, Map<DecodeHintType, ?> hints) throws NotFoundException, ChecksumException, FormatException {
/*  57 */     int width = image.getWidth();
/*  58 */     int height = image.getHeight();
/*  59 */     int halfWidth = width / 2;
/*  60 */     int halfHeight = height / 2;
/*     */ 
/*     */     
/*     */     try {
/*  64 */       return this.delegate.decode(image.crop(0, 0, halfWidth, halfHeight), hints);
/*  65 */     } catch (NotFoundException notFoundException) {
/*     */       try {
/*     */         Result result;
/*     */ 
/*     */ 
/*     */         
/*  71 */         makeAbsolute((result = this.delegate.decode(image.crop(halfWidth, 0, halfWidth, halfHeight), hints)).getResultPoints(), halfWidth, 0);
/*  72 */         return result;
/*  73 */       } catch (NotFoundException notFoundException1) {
/*     */         try {
/*     */           Result result;
/*     */ 
/*     */ 
/*     */           
/*  79 */           makeAbsolute((result = this.delegate.decode(image.crop(0, halfHeight, halfWidth, halfHeight), hints)).getResultPoints(), 0, halfHeight);
/*  80 */           return result;
/*  81 */         } catch (NotFoundException notFoundException2) {
/*     */           try {
/*     */             Result result;
/*     */ 
/*     */ 
/*     */             
/*  87 */             makeAbsolute((result = this.delegate.decode(image.crop(halfWidth, halfHeight, halfWidth, halfHeight), hints)).getResultPoints(), halfWidth, halfHeight);
/*  88 */             return result;
/*  89 */           } catch (NotFoundException notFoundException3) {
/*     */ 
/*     */ 
/*     */             
/*  93 */             int quarterWidth = halfWidth / 2;
/*  94 */             int quarterHeight = halfHeight / 2;
/*  95 */             BinaryBitmap center = image.crop(quarterWidth, quarterHeight, halfWidth, halfHeight);
/*     */             Result result;
/*  97 */             makeAbsolute((result = this.delegate.decode(center, hints)).getResultPoints(), quarterWidth, quarterHeight);
/*  98 */             return result;
/*     */           } 
/*     */         } 
/*     */       } 
/*     */     }  } public void reset() {
/* 103 */     this.delegate.reset();
/*     */   }
/*     */   
/*     */   private static void makeAbsolute(ResultPoint[] points, int leftOffset, int topOffset) {
/* 107 */     if (points != null)
/* 108 */       for (int i = 0; i < points.length; i++) {
/* 109 */         ResultPoint relative = points[i];
/* 110 */         points[i] = new ResultPoint(relative.getX() + leftOffset, relative.getY() + topOffset);
/*     */       }  
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\multi\ByQuadrantReader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */