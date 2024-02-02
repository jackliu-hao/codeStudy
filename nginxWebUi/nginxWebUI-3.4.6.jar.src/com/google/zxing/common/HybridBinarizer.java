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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class HybridBinarizer
/*     */   extends GlobalHistogramBinarizer
/*     */ {
/*     */   private static final int BLOCK_SIZE_POWER = 3;
/*     */   private static final int BLOCK_SIZE = 8;
/*     */   private static final int BLOCK_SIZE_MASK = 7;
/*     */   private static final int MINIMUM_DIMENSION = 40;
/*     */   private static final int MIN_DYNAMIC_RANGE = 24;
/*     */   private BitMatrix matrix;
/*     */   
/*     */   public HybridBinarizer(LuminanceSource source) {
/*  53 */     super(source);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BitMatrix getBlackMatrix() throws NotFoundException {
/*  63 */     if (this.matrix != null) {
/*  64 */       return this.matrix;
/*     */     }
/*     */     LuminanceSource source;
/*  67 */     int width = (source = getLuminanceSource()).getWidth();
/*  68 */     int height = source.getHeight();
/*  69 */     if (width >= 40 && height >= 40) {
/*  70 */       byte[] luminances = source.getMatrix();
/*  71 */       int subWidth = width >> 3;
/*  72 */       if ((width & 0x7) != 0) {
/*  73 */         subWidth++;
/*     */       }
/*  75 */       int subHeight = height >> 3;
/*  76 */       if ((height & 0x7) != 0) {
/*  77 */         subHeight++;
/*     */       }
/*  79 */       int[][] blackPoints = calculateBlackPoints(luminances, subWidth, subHeight, width, height);
/*     */       
/*  81 */       BitMatrix newMatrix = new BitMatrix(width, height);
/*  82 */       calculateThresholdForBlock(luminances, subWidth, subHeight, width, height, blackPoints, newMatrix);
/*  83 */       this.matrix = newMatrix;
/*     */     } else {
/*     */       
/*  86 */       this.matrix = super.getBlackMatrix();
/*     */     } 
/*  88 */     return this.matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public Binarizer createBinarizer(LuminanceSource source) {
/*  93 */     return new HybridBinarizer(source);
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
/*     */   private static void calculateThresholdForBlock(byte[] luminances, int subWidth, int subHeight, int width, int height, int[][] blackPoints, BitMatrix matrix) {
/* 108 */     for (int y = 0; y < subHeight; y++) {
/* 109 */       int yoffset = y << 3;
/* 110 */       int maxYOffset = height - 8;
/* 111 */       if (yoffset > maxYOffset) {
/* 112 */         yoffset = maxYOffset;
/*     */       }
/* 114 */       for (int x = 0; x < subWidth; x++) {
/* 115 */         int xoffset = x << 3;
/* 116 */         int maxXOffset = width - 8;
/* 117 */         if (xoffset > maxXOffset) {
/* 118 */           xoffset = maxXOffset;
/*     */         }
/* 120 */         int left = cap(x, 2, subWidth - 3);
/* 121 */         int top = cap(y, 2, subHeight - 3);
/* 122 */         int sum = 0;
/* 123 */         for (int z = -2; z <= 2; z++) {
/* 124 */           int[] blackRow = blackPoints[top + z];
/* 125 */           sum += blackRow[left - 2] + blackRow[left - 1] + blackRow[left] + blackRow[left + 1] + blackRow[left + 2];
/*     */         } 
/* 127 */         int average = sum / 25;
/* 128 */         thresholdBlock(luminances, xoffset, yoffset, average, width, matrix);
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   private static int cap(int value, int min, int max) {
/* 134 */     return (value < min) ? min : ((value > max) ? max : value);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static void thresholdBlock(byte[] luminances, int xoffset, int yoffset, int threshold, int stride, BitMatrix matrix) {
/*     */     int offset;
/* 146 */     for (int y = 0; y < 8; y++, offset += stride) {
/* 147 */       for (int x = 0; x < 8; x++) {
/*     */         
/* 149 */         if ((luminances[offset + x] & 0xFF) <= threshold) {
/* 150 */           matrix.set(xoffset + x, yoffset + y);
/*     */         }
/*     */       } 
/*     */     } 
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
/*     */   private static int[][] calculateBlackPoints(byte[] luminances, int subWidth, int subHeight, int width, int height) {
/* 166 */     int[][] blackPoints = new int[subHeight][subWidth];
/* 167 */     for (int y = 0; y < subHeight; y++) {
/* 168 */       int yoffset = y << 3;
/* 169 */       int maxYOffset = height - 8;
/* 170 */       if (yoffset > maxYOffset) {
/* 171 */         yoffset = maxYOffset;
/*     */       }
/* 173 */       for (int x = 0; x < subWidth; x++) {
/* 174 */         int xoffset = x << 3;
/* 175 */         int maxXOffset = width - 8;
/* 176 */         if (xoffset > maxXOffset) {
/* 177 */           xoffset = maxXOffset;
/*     */         }
/* 179 */         int sum = 0;
/* 180 */         int min = 255;
/* 181 */         int max = 0; int offset;
/* 182 */         for (int yy = 0; yy < 8; yy++, offset += width) {
/* 183 */           int xx; for (xx = 0; xx < 8; xx++) {
/* 184 */             int pixel = luminances[offset + xx] & 0xFF;
/* 185 */             sum += pixel;
/*     */             
/* 187 */             if (pixel < min) {
/* 188 */               min = pixel;
/*     */             }
/* 190 */             if (pixel > max) {
/* 191 */               max = pixel;
/*     */             }
/*     */           } 
/*     */           
/* 195 */           if (max - min > 24) {
/*     */             
/* 197 */             yy++; for (offset += width; yy < 8; yy++, offset += width) {
/* 198 */               for (xx = 0; xx < 8; xx++) {
/* 199 */                 sum += luminances[offset + xx] & 0xFF;
/*     */               }
/*     */             } 
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 206 */         int average = sum >> 6;
/* 207 */         if (max - min <= 24) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 214 */           average = min / 2;
/*     */           
/* 216 */           if (y > 0 && x > 0) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 224 */             int averageNeighborBlackPoint = (blackPoints[y - 1][x] + 2 * blackPoints[y][x - 1] + blackPoints[y - 1][x - 1]) / 4;
/*     */             
/* 226 */             if (min < averageNeighborBlackPoint) {
/* 227 */               average = averageNeighborBlackPoint;
/*     */             }
/*     */           } 
/*     */         } 
/* 231 */         blackPoints[y][x] = average;
/*     */       } 
/*     */     } 
/* 234 */     return blackPoints;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\common\HybridBinarizer.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */