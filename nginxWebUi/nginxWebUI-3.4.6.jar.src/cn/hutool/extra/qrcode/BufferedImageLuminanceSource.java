/*     */ package cn.hutool.extra.qrcode;
/*     */ 
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.ImageObserver;
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
/*     */   private final BufferedImage image;
/*     */   private final int left;
/*     */   private final int top;
/*     */   
/*     */   public BufferedImageLuminanceSource(BufferedImage image) {
/*  29 */     this(image, 0, 0, image.getWidth(), image.getHeight());
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
/*     */   public BufferedImageLuminanceSource(BufferedImage image, int left, int top, int width, int height) {
/*  42 */     super(width, height);
/*     */     
/*  44 */     int sourceWidth = image.getWidth();
/*  45 */     int sourceHeight = image.getHeight();
/*  46 */     if (left + width > sourceWidth || top + height > sourceHeight) {
/*  47 */       throw new IllegalArgumentException("Crop rectangle does not fit within image data.");
/*     */     }
/*     */     
/*  50 */     for (int y = top; y < top + height; y++) {
/*  51 */       for (int x = left; x < left + width; x++) {
/*  52 */         if ((image.getRGB(x, y) & 0xFF000000) == 0) {
/*  53 */           image.setRGB(x, y, -1);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/*  58 */     this.image = new BufferedImage(sourceWidth, sourceHeight, 10);
/*  59 */     this.image.getGraphics().drawImage(image, 0, 0, null);
/*  60 */     this.left = left;
/*  61 */     this.top = top;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getRow(int y, byte[] row) {
/*  66 */     if (y < 0 || y >= getHeight()) {
/*  67 */       throw new IllegalArgumentException("Requested row is outside the image: " + y);
/*     */     }
/*  69 */     int width = getWidth();
/*  70 */     if (row == null || row.length < width) {
/*  71 */       row = new byte[width];
/*     */     }
/*  73 */     this.image.getRaster().getDataElements(this.left, this.top + y, width, 1, row);
/*  74 */     return row;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte[] getMatrix() {
/*  79 */     int width = getWidth();
/*  80 */     int height = getHeight();
/*  81 */     int area = width * height;
/*  82 */     byte[] matrix = new byte[area];
/*  83 */     this.image.getRaster().getDataElements(this.left, this.top, width, height, matrix);
/*  84 */     return matrix;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCropSupported() {
/*  89 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public LuminanceSource crop(int left, int top, int width, int height) {
/*  94 */     return new BufferedImageLuminanceSource(this.image, this.left + left, this.top + top, width, height);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isRotateSupported() {
/*  99 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LuminanceSource rotateCounterClockwise() {
/* 106 */     int sourceWidth = this.image.getWidth();
/* 107 */     int sourceHeight = this.image.getHeight();
/*     */     
/* 109 */     AffineTransform transform = new AffineTransform(0.0D, -1.0D, 1.0D, 0.0D, 0.0D, sourceWidth);
/*     */     
/* 111 */     BufferedImage rotatedImage = new BufferedImage(sourceHeight, sourceWidth, 10);
/*     */     
/* 113 */     Graphics2D g = rotatedImage.createGraphics();
/* 114 */     g.drawImage(this.image, transform, (ImageObserver)null);
/* 115 */     g.dispose();
/*     */     
/* 117 */     int width = getWidth();
/* 118 */     return new BufferedImageLuminanceSource(rotatedImage, this.top, sourceWidth - this.left + width, getHeight(), width);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\qrcode\BufferedImageLuminanceSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */