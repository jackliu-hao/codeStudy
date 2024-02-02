/*     */ package com.google.zxing.common;
/*     */ 
/*     */ import com.google.zxing.Binarizer;
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import com.google.zxing.NotFoundException;
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
/*     */ public class GlobalHistogramBinarizer
/*     */   extends Binarizer
/*     */ {
/*     */   private static final int LUMINANCE_BITS = 5;
/*     */   private static final int LUMINANCE_SHIFT = 3;
/*     */   private static final int LUMINANCE_BUCKETS = 32;
/*  39 */   private static final byte[] EMPTY = new byte[0];
/*     */   
/*     */   private byte[] luminances;
/*     */   private final int[] buckets;
/*     */   
/*     */   public GlobalHistogramBinarizer(LuminanceSource source) {
/*  45 */     super(source);
/*  46 */     this.luminances = EMPTY;
/*  47 */     this.buckets = new int[32];
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BitArray getBlackRow(int y, BitArray row) throws NotFoundException {
/*     */     LuminanceSource source;
/*  54 */     int width = (source = getLuminanceSource()).getWidth();
/*  55 */     if (row == null || row.getSize() < width) {
/*  56 */       row = new BitArray(width);
/*     */     } else {
/*  58 */       row.clear();
/*     */     } 
/*     */     
/*  61 */     initArrays(width);
/*  62 */     byte[] localLuminances = source.getRow(y, this.luminances);
/*  63 */     int[] localBuckets = this.buckets;
/*  64 */     for (int x = 0; x < width; x++) {
/*  65 */       localBuckets[(localLuminances[x] & 0xFF) >> 3] = localBuckets[(localLuminances[x] & 0xFF) >> 3] + 1;
/*     */     }
/*  67 */     int blackPoint = estimateBlackPoint(localBuckets);
/*     */     
/*  69 */     if (width < 3) {
/*     */       
/*  71 */       for (int i = 0; i < width; i++) {
/*  72 */         if ((localLuminances[i] & 0xFF) < blackPoint) {
/*  73 */           row.set(i);
/*     */         }
/*     */       } 
/*     */     } else {
/*  77 */       int left = localLuminances[0] & 0xFF;
/*  78 */       int center = localLuminances[1] & 0xFF;
/*  79 */       for (int i = 1; i < width - 1; i++) {
/*  80 */         int right = localLuminances[i + 1] & 0xFF;
/*     */         
/*  82 */         if (((center << 2) - left - right) / 2 < blackPoint) {
/*  83 */           row.set(i);
/*     */         }
/*  85 */         left = center;
/*  86 */         center = right;
/*     */       } 
/*     */     } 
/*  89 */     return row;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public BitMatrix getBlackMatrix() throws NotFoundException {
/*     */     LuminanceSource source;
/*  96 */     int width = (source = getLuminanceSource()).getWidth();
/*  97 */     int height = source.getHeight();
/*  98 */     BitMatrix matrix = new BitMatrix(width, height);
/*     */ 
/*     */ 
/*     */     
/* 102 */     initArrays(width);
/* 103 */     int[] localBuckets = this.buckets;
/* 104 */     for (int y = 1; y < 5; y++) {
/* 105 */       int row = height * y / 5;
/* 106 */       byte[] arrayOfByte = source.getRow(row, this.luminances);
/* 107 */       int right = (width << 2) / 5;
/* 108 */       for (int x = width / 5; x < right; x++) {
/* 109 */         int pixel = arrayOfByte[x] & 0xFF;
/* 110 */         localBuckets[pixel >> 3] = localBuckets[pixel >> 3] + 1;
/*     */       } 
/*     */     } 
/* 113 */     int blackPoint = estimateBlackPoint(localBuckets);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 118 */     byte[] localLuminances = source.getMatrix();
/* 119 */     for (int i = 0; i < height; i++) {
/* 120 */       int offset = i * width;
/* 121 */       for (int x = 0; x < width; x++) {
/* 122 */         if ((localLuminances[offset + x] & 0xFF) < 
/* 123 */           blackPoint) {
/* 124 */           matrix.set(x, i);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 129 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public Binarizer createBinarizer(LuminanceSource source) {
/* 134 */     return new GlobalHistogramBinarizer(source);
/*     */   }
/*     */   
/*     */   private void initArrays(int luminanceSize) {
/* 138 */     if (this.luminances.length < luminanceSize) {
/* 139 */       this.luminances = new byte[luminanceSize];
/*     */     }
/* 141 */     for (int x = 0; x < 32; x++) {
/* 142 */       this.buckets[x] = 0;
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private static int estimateBlackPoint(int[] buckets) throws NotFoundException {
/* 148 */     int numBuckets = buckets.length;
/* 149 */     int maxBucketCount = 0;
/* 150 */     int firstPeak = 0;
/* 151 */     int firstPeakSize = 0;
/* 152 */     for (int x = 0; x < numBuckets; x++) {
/* 153 */       if (buckets[x] > firstPeakSize) {
/* 154 */         firstPeak = x;
/* 155 */         firstPeakSize = buckets[x];
/*     */       } 
/* 157 */       if (buckets[x] > maxBucketCount) {
/* 158 */         maxBucketCount = buckets[x];
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 163 */     int secondPeak = 0;
/* 164 */     int secondPeakScore = 0;
/* 165 */     for (int i = 0; i < numBuckets; i++) {
/* 166 */       int distanceToBiggest = i - firstPeak;
/*     */       
/*     */       int score;
/* 169 */       if ((score = buckets[i] * distanceToBiggest * distanceToBiggest) > secondPeakScore) {
/* 170 */         secondPeak = i;
/* 171 */         secondPeakScore = score;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 176 */     if (firstPeak > secondPeak) {
/* 177 */       int temp = firstPeak;
/* 178 */       firstPeak = secondPeak;
/* 179 */       secondPeak = temp;
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 184 */     if (secondPeak - firstPeak <= numBuckets / 16) {
/* 185 */       throw NotFoundException.getNotFoundInstance();
/*     */     }
/*     */ 
/*     */     
/* 189 */     int bestValley = secondPeak - 1;
/* 190 */     int bestValleyScore = -1;
/* 191 */     for (int j = secondPeak - 1; j > firstPeak; j--) {
/*     */       int score;
/*     */       
/* 194 */       if ((score = (j - firstPeak) * (j - firstPeak) * (secondPeak - j) * (maxBucketCount - buckets[j])) > bestValleyScore) {
/* 195 */         bestValley = j;
/* 196 */         bestValleyScore = score;
/*     */       } 
/*     */     } 
/*     */     
/* 200 */     return bestValley << 3;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\GlobalHistogramBinarizer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */