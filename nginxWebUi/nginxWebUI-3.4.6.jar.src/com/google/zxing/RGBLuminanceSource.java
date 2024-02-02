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
/*     */ public final class RGBLuminanceSource
/*     */   extends LuminanceSource
/*     */ {
/*     */   private final byte[] luminances;
/*     */   private final int dataWidth;
/*     */   private final int dataHeight;
/*     */   private final int left;
/*     */   private final int top;
/*     */   
/*     */   public RGBLuminanceSource(int width, int height, int[] pixels) {
/*  35 */     super(width, height);
/*     */     
/*  37 */     this.dataWidth = width;
/*  38 */     this.dataHeight = height;
/*  39 */     this.left = 0;
/*  40 */     this.top = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  46 */     int size = width * height;
/*  47 */     this.luminances = new byte[size];
/*  48 */     for (int offset = 0; offset < size; offset++) {
/*     */       
/*  50 */       int pixel, r = (pixel = pixels[offset]) >> 16 & 0xFF;
/*  51 */       int g2 = pixel >> 7 & 0x1FE;
/*  52 */       int b = pixel & 0xFF;
/*     */       
/*  54 */       this.luminances[offset] = (byte)((r + g2 + b) / 4);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private RGBLuminanceSource(byte[] pixels, int dataWidth, int dataHeight, int left, int top, int width, int height) {
/*  65 */     super(width, height);
/*  66 */     if (left + width > dataWidth || top + height > dataHeight) {
/*  67 */       throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
/*     */     }
/*  69 */     this.luminances = pixels;
/*  70 */     this.dataWidth = dataWidth;
/*  71 */     this.dataHeight = dataHeight;
/*  72 */     this.left = left;
/*  73 */     this.top = top;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getRow(int y, byte[] row) {
/*  78 */     if (y < 0 || y >= getHeight()) {
/*  79 */       throw new IllegalArgumentException("Requested row is outside the image: " + y);
/*     */     }
/*  81 */     int width = getWidth();
/*  82 */     if (row == null || row.length < width) {
/*  83 */       row = new byte[width];
/*     */     }
/*  85 */     int offset = (y + this.top) * this.dataWidth + this.left;
/*  86 */     System.arraycopy(this.luminances, offset, row, 0, width);
/*  87 */     return row;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getMatrix() {
/*  92 */     int width = getWidth();
/*  93 */     int height = getHeight();
/*     */ 
/*     */ 
/*     */     
/*  97 */     if (width == this.dataWidth && height == this.dataHeight) {
/*  98 */       return this.luminances;
/*     */     }
/*     */     
/*     */     int area;
/* 102 */     byte[] matrix = new byte[area = width * height];
/* 103 */     int inputOffset = this.top * this.dataWidth + this.left;
/*     */ 
/*     */     
/* 106 */     if (width == this.dataWidth) {
/* 107 */       System.arraycopy(this.luminances, inputOffset, matrix, 0, area);
/* 108 */       return matrix;
/*     */     } 
/*     */ 
/*     */     
/* 112 */     for (int y = 0; y < height; y++) {
/* 113 */       int outputOffset = y * width;
/* 114 */       System.arraycopy(this.luminances, inputOffset, matrix, outputOffset, width);
/* 115 */       inputOffset += this.dataWidth;
/*     */     } 
/* 117 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/* 122 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource crop(int left, int top, int width, int height) {
/* 127 */     return new RGBLuminanceSource(this.luminances, this.dataWidth, this.dataHeight, this.left + left, this.top + top, width, height);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\RGBLuminanceSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */