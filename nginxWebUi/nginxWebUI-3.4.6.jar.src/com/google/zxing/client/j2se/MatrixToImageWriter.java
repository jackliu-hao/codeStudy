/*     */ package com.google.zxing.client.j2se;
/*     */ 
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.file.Path;
/*     */ import javax.imageio.ImageIO;
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
/*     */ public final class MatrixToImageWriter
/*     */ {
/*  37 */   private static final MatrixToImageConfig DEFAULT_CONFIG = new MatrixToImageConfig();
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
/*     */   public static BufferedImage toBufferedImage(BitMatrix matrix) {
/*  49 */     return toBufferedImage(matrix, DEFAULT_CONFIG);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage toBufferedImage(BitMatrix matrix, MatrixToImageConfig config) {
/*  60 */     int width = matrix.getWidth();
/*  61 */     int height = matrix.getHeight();
/*  62 */     BufferedImage image = new BufferedImage(width, height, config.getBufferedImageColorModel());
/*  63 */     int onColor = config.getPixelOnColor();
/*  64 */     int offColor = config.getPixelOffColor();
/*  65 */     int[] pixels = new int[width * height];
/*  66 */     int index = 0;
/*  67 */     for (int y = 0; y < height; y++) {
/*  68 */       for (int x = 0; x < width; x++) {
/*  69 */         pixels[index++] = matrix.get(x, y) ? onColor : offColor;
/*     */       }
/*     */     } 
/*  72 */     image.setRGB(0, 0, width, height, pixels, 0, width);
/*  73 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static void writeToFile(BitMatrix matrix, String format, File file) throws IOException {
/*  85 */     writeToPath(matrix, format, file.toPath());
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
/*     */   public static void writeToPath(BitMatrix matrix, String format, Path file) throws IOException {
/*  98 */     writeToPath(matrix, format, file, DEFAULT_CONFIG);
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
/*     */   @Deprecated
/*     */   public static void writeToFile(BitMatrix matrix, String format, File file, MatrixToImageConfig config) throws IOException {
/* 112 */     writeToPath(matrix, format, file.toPath(), config);
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
/*     */   public static void writeToPath(BitMatrix matrix, String format, Path file, MatrixToImageConfig config) throws IOException {
/* 126 */     BufferedImage image = toBufferedImage(matrix, config);
/* 127 */     if (!ImageIO.write(image, format, file.toFile())) {
/* 128 */       throw new IOException("Could not write an image of format " + format + " to " + file);
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
/*     */   public static void writeToStream(BitMatrix matrix, String format, OutputStream stream) throws IOException {
/* 142 */     writeToStream(matrix, format, stream, DEFAULT_CONFIG);
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
/*     */   public static void writeToStream(BitMatrix matrix, String format, OutputStream stream, MatrixToImageConfig config) throws IOException {
/* 156 */     BufferedImage image = toBufferedImage(matrix, config);
/* 157 */     if (!ImageIO.write(image, format, stream))
/* 158 */       throw new IOException("Could not write an image of format " + format); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\client\j2se\MatrixToImageWriter.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */