/*     */ package com.google.zxing;
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
/*     */ public final class PlanarYUVLuminanceSource
/*     */   extends LuminanceSource
/*     */ {
/*     */   private static final int THUMBNAIL_SCALE_FACTOR = 2;
/*     */   private final byte[] yuvData;
/*     */   private final int dataWidth;
/*     */   private final int dataHeight;
/*     */   private final int left;
/*     */   private final int top;
/*     */   
/*     */   public PlanarYUVLuminanceSource(byte[] yuvData, int dataWidth, int dataHeight, int left, int top, int width, int height, boolean reverseHorizontal) {
/*  47 */     super(width, height);
/*     */     
/*  49 */     if (left + width > dataWidth || top + height > dataHeight) {
/*  50 */       throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
/*     */     }
/*     */     
/*  53 */     this.yuvData = yuvData;
/*  54 */     this.dataWidth = dataWidth;
/*  55 */     this.dataHeight = dataHeight;
/*  56 */     this.left = left;
/*  57 */     this.top = top;
/*  58 */     if (reverseHorizontal) {
/*  59 */       reverseHorizontal(width, height);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getRow(int y, byte[] row) {
/*  65 */     if (y < 0 || y >= getHeight()) {
/*  66 */       throw new IllegalArgumentException("Requested row is outside the image: " + y);
/*     */     }
/*  68 */     int width = getWidth();
/*  69 */     if (row == null || row.length < width) {
/*  70 */       row = new byte[width];
/*     */     }
/*  72 */     int offset = (y + this.top) * this.dataWidth + this.left;
/*  73 */     System.arraycopy(this.yuvData, offset, row, 0, width);
/*  74 */     return row;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getMatrix() {
/*  79 */     int width = getWidth();
/*  80 */     int height = getHeight();
/*     */ 
/*     */ 
/*     */     
/*  84 */     if (width == this.dataWidth && height == this.dataHeight) {
/*  85 */       return this.yuvData;
/*     */     }
/*     */     
/*     */     int area;
/*  89 */     byte[] matrix = new byte[area = width * height];
/*  90 */     int inputOffset = this.top * this.dataWidth + this.left;
/*     */ 
/*     */     
/*  93 */     if (width == this.dataWidth) {
/*  94 */       System.arraycopy(this.yuvData, inputOffset, matrix, 0, area);
/*  95 */       return matrix;
/*     */     } 
/*     */ 
/*     */     
/*  99 */     for (int y = 0; y < height; y++) {
/* 100 */       int outputOffset = y * width;
/* 101 */       System.arraycopy(this.yuvData, inputOffset, matrix, outputOffset, width);
/* 102 */       inputOffset += this.dataWidth;
/*     */     } 
/* 104 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/* 109 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource crop(int left, int top, int width, int height) {
/* 114 */     return new PlanarYUVLuminanceSource(this.yuvData, this.dataWidth, this.dataHeight, this.left + left, this.top + top, width, height, false);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int[] renderThumbnail() {
/* 125 */     int width = getWidth() / 2;
/* 126 */     int height = getHeight() / 2;
/* 127 */     int[] pixels = new int[width * height];
/* 128 */     byte[] yuv = this.yuvData;
/* 129 */     int inputOffset = this.top * this.dataWidth + this.left;
/*     */     
/* 131 */     for (int y = 0; y < height; y++) {
/* 132 */       int outputOffset = y * width;
/* 133 */       for (int x = 0; x < width; x++) {
/* 134 */         int grey = yuv[inputOffset + (x << 1)] & 0xFF;
/* 135 */         pixels[outputOffset + x] = 0xFF000000 | grey * 65793;
/*     */       } 
/* 137 */       inputOffset += this.dataWidth << 1;
/*     */     } 
/* 139 */     return pixels;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThumbnailWidth() {
/* 146 */     return getWidth() / 2;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThumbnailHeight() {
/* 153 */     return getHeight() / 2;
/*     */   }
/*     */   
/*     */   private void reverseHorizontal(int width, int height) {
/* 157 */     byte[] yuvData = this.yuvData; int rowStart;
/* 158 */     for (int y = 0; y < height; y++, rowStart += this.dataWidth) {
/* 159 */       int middle = rowStart + width / 2;
/* 160 */       for (int x1 = rowStart, x2 = rowStart + width - 1; x1 < middle; x1++, x2--) {
/* 161 */         byte temp = yuvData[x1];
/* 162 */         yuvData[x1] = yuvData[x2];
/* 163 */         yuvData[x2] = temp;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\PlanarYUVLuminanceSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */