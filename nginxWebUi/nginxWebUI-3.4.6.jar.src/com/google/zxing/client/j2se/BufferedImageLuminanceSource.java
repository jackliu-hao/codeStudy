/*     */ package com.google.zxing.client.j2se;
/*     */ 
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.awt.image.WritableRaster;
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
/*     */ public final class BufferedImageLuminanceSource
/*     */   extends LuminanceSource
/*     */ {
/*     */   private static final double MINUS_45_IN_RADIANS = -0.7853981633974483D;
/*     */   private final BufferedImage image;
/*     */   private final int left;
/*     */   private final int top;
/*     */   
/*     */   public BufferedImageLuminanceSource(BufferedImage image) {
/*  42 */     this(image, 0, 0, image.getWidth(), image.getHeight());
/*     */   }
/*     */   
/*     */   public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
/*  46 */     super(width, height);
/*     */     
/*  48 */     if (image.getType() == 10) {
/*  49 */       this.image = image;
/*     */     } else {
/*  51 */       int sourceWidth = image.getWidth();
/*  52 */       int sourceHeight = image.getHeight();
/*  53 */       if (left + width > sourceWidth || top + height > sourceHeight) {
/*  54 */         throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
/*     */       }
/*     */       
/*  57 */       this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
/*     */       
/*  59 */       WritableRaster raster = this.image.getRaster();
/*  60 */       int[] buffer = new int[width];
/*  61 */       for (int y = top; y < top + height; y++) {
/*  62 */         image.getRGB(left, y, width, 1, buffer, 0, sourceWidth);
/*  63 */         for (int x = 0; x < width; x++) {
/*  64 */           int pixel = buffer[x];
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  69 */           if ((pixel & 0xFF000000) == 0) {
/*  70 */             pixel = -1;
/*     */           }
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  76 */           buffer[x] = 306 * (pixel >> 16 & 0xFF) + 601 * (pixel >> 8 & 0xFF) + 117 * (pixel & 0xFF) + 512 >> 10;
/*     */         } 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  82 */         raster.setPixels(left, y, width, 1, buffer);
/*     */       } 
/*     */     } 
/*     */     
/*  86 */     this.left = left;
/*  87 */     this.top = top;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getRow(int y, byte[] row) {
/*  92 */     if (y < 0 || y >= getHeight()) {
/*  93 */       throw new IllegalArgumentException("Requested row is outside the image: " + y);
/*     */     }
/*  95 */     int width = getWidth();
/*  96 */     if (row == null || row.length < width) {
/*  97 */       row = new byte[width];
/*     */     }
/*     */     
/* 100 */     this.image.getRaster().getDataElements(this.left, this.top + y, width, 1, row);
/* 101 */     return row;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getMatrix() {
/* 106 */     int width = getWidth();
/* 107 */     int height = getHeight();
/* 108 */     int area = width * height;
/* 109 */     byte[] matrix = new byte[area];
/*     */     
/* 111 */     this.image.getRaster().getDataElements(this.left, this.top, width, height, matrix);
/* 112 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/* 117 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource crop(int left, int top, int width, int height) {
/* 122 */     return new BufferedImageLuminanceSource(this.image, this.left + left, this.top + top, width, height);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isRotateSupported() {
/* 132 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource rotateCounterClockwise() {
/* 137 */     int sourceWidth = this.image.getWidth();
/* 138 */     int sourceHeight = this.image.getHeight();
/*     */ 
/*     */     
/* 141 */     AffineTransform transform = new AffineTransform(0.0D, -1.0D, 1.0D, 0.0D, 0.0D, sourceWidth);
/*     */ 
/*     */     
/* 144 */     BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, 10);
/*     */ 
/*     */     
/* 147 */     Graphics2D g = rotatedImage.createGraphics();
/* 148 */     g.drawImage(this.image, transform, (ImageObserver)null);
/* 149 */     g.dispose();
/*     */ 
/*     */     
/* 152 */     int width = getWidth();
/* 153 */     return new BufferedImageLuminanceSource(rotatedImage, this.top, sourceWidth - this.left + width, getHeight(), width);
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource rotateCounterClockwise45() {
/* 158 */     int width = getWidth();
/* 159 */     int height = getHeight();
/*     */     
/* 161 */     int oldCenterX = this.left + width / 2;
/* 162 */     int oldCenterY = this.top + height / 2;
/*     */ 
/*     */     
/* 165 */     AffineTransform transform = AffineTransform.getRotateInstance(-0.7853981633974483D, oldCenterX, oldCenterY);
/*     */     
/* 167 */     int sourceDimension = Math.max(this.image.getWidth(), this.image.getHeight());
/* 168 */     BufferedImage rotatedImage = new BufferedImage(sourceDimension, sourceDimension, 10);
/*     */ 
/*     */     
/* 171 */     Graphics2D g = rotatedImage.createGraphics();
/* 172 */     g.drawImage(this.image, transform, (ImageObserver)null);
/* 173 */     g.dispose();
/*     */     
/* 175 */     int halfDimension = Math.max(width, height) / 2;
/* 176 */     int newLeft = Math.max(0, oldCenterX - halfDimension);
/* 177 */     int newTop = Math.max(0, oldCenterY - halfDimension);
/* 178 */     int newRight = Math.min(sourceDimension - 1, oldCenterX + halfDimension);
/* 179 */     int newBottom = Math.min(sourceDimension - 1, oldCenterY + halfDimension);
/*     */     
/* 181 */     return new BufferedImageLuminanceSource(rotatedImage, newLeft, newTop, newRight - newLeft, newBottom - newTop);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\BufferedImageLuminanceSource.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */