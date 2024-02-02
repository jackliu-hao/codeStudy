/*      */ package cn.hutool.core.img;
/*      */ 
/*      */ import cn.hutool.core.codec.Base64;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.IoUtil;
/*      */ import cn.hutool.core.io.resource.Resource;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.NumberUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.RandomUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import cn.hutool.core.util.URLUtil;
/*      */ import java.awt.Color;
/*      */ import java.awt.Font;
/*      */ import java.awt.Graphics;
/*      */ import java.awt.Graphics2D;
/*      */ import java.awt.GraphicsConfiguration;
/*      */ import java.awt.GraphicsDevice;
/*      */ import java.awt.GraphicsEnvironment;
/*      */ import java.awt.HeadlessException;
/*      */ import java.awt.Image;
/*      */ import java.awt.Point;
/*      */ import java.awt.Rectangle;
/*      */ import java.awt.Toolkit;
/*      */ import java.awt.color.ColorSpace;
/*      */ import java.awt.font.FontRenderContext;
/*      */ import java.awt.geom.AffineTransform;
/*      */ import java.awt.geom.Rectangle2D;
/*      */ import java.awt.image.AffineTransformOp;
/*      */ import java.awt.image.BufferedImage;
/*      */ import java.awt.image.BufferedImageOp;
/*      */ import java.awt.image.ColorConvertOp;
/*      */ import java.awt.image.ColorModel;
/*      */ import java.awt.image.FilteredImageSource;
/*      */ import java.awt.image.ImageFilter;
/*      */ import java.awt.image.ImageObserver;
/*      */ import java.awt.image.RenderedImage;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.net.URL;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Random;
/*      */ import javax.imageio.IIOImage;
/*      */ import javax.imageio.ImageIO;
/*      */ import javax.imageio.ImageReader;
/*      */ import javax.imageio.ImageTypeSpecifier;
/*      */ import javax.imageio.ImageWriteParam;
/*      */ import javax.imageio.ImageWriter;
/*      */ import javax.imageio.stream.ImageInputStream;
/*      */ import javax.imageio.stream.ImageOutputStream;
/*      */ import javax.swing.ImageIcon;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ImgUtil
/*      */ {
/*      */   public static final String IMAGE_TYPE_GIF = "gif";
/*      */   public static final String IMAGE_TYPE_JPG = "jpg";
/*      */   public static final String IMAGE_TYPE_JPEG = "jpeg";
/*      */   public static final String IMAGE_TYPE_BMP = "bmp";
/*      */   public static final String IMAGE_TYPE_PNG = "png";
/*      */   public static final String IMAGE_TYPE_PSD = "psd";
/*      */   private static final int RGB_COLOR_BOUND = 256;
/*      */   
/*      */   public static void scale(File srcImageFile, File destImageFile, float scale) {
/*   95 */     scale(read(srcImageFile), destImageFile, scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(InputStream srcStream, OutputStream destStream, float scale) {
/*  108 */     scale(read(srcStream), destStream, scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, float scale) {
/*  121 */     scale(read(srcStream), destStream, scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(Image srcImg, File destFile, float scale) throws IORuntimeException {
/*  135 */     Img.from(srcImg).setTargetImageType(FileUtil.extName(destFile)).scale(scale).write(destFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(Image srcImg, OutputStream out, float scale) throws IORuntimeException {
/*  149 */     scale(srcImg, getImageOutputStream(out), scale);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(Image srcImg, ImageOutputStream destImageStream, float scale) throws IORuntimeException {
/*  163 */     writeJpg(scale(srcImg, scale), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image scale(Image srcImg, float scale) {
/*  175 */     return Img.from(srcImg).scale(scale).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image scale(Image srcImg, int width, int height) {
/*  189 */     return Img.from(srcImg).scale(width, height).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(File srcImageFile, File destImageFile, int width, int height, Color fixedColor) throws IORuntimeException {
/*  204 */     Img.from(srcImageFile)
/*  205 */       .setTargetImageType(FileUtil.extName(destImageFile))
/*  206 */       .scale(width, height, fixedColor)
/*  207 */       .write(destImageFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(InputStream srcStream, OutputStream destStream, int width, int height, Color fixedColor) throws IORuntimeException {
/*  222 */     scale(read(srcStream), getImageOutputStream(destStream), width, height, fixedColor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(ImageInputStream srcStream, ImageOutputStream destStream, int width, int height, Color fixedColor) throws IORuntimeException {
/*  237 */     scale(read(srcStream), destStream, width, height, fixedColor);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void scale(Image srcImage, ImageOutputStream destImageStream, int width, int height, Color fixedColor) throws IORuntimeException {
/*  252 */     writeJpg(scale(srcImage, width, height, fixedColor), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image scale(Image srcImage, int width, int height, Color fixedColor) {
/*  266 */     return Img.from(srcImage).scale(width, height, fixedColor).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(File srcImgFile, File destImgFile, Rectangle rectangle) {
/*  280 */     cut(read(srcImgFile), destImgFile, rectangle);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(InputStream srcStream, OutputStream destStream, Rectangle rectangle) {
/*  292 */     cut(read(srcStream), destStream, rectangle);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(ImageInputStream srcStream, ImageOutputStream destStream, Rectangle rectangle) {
/*  304 */     cut(read(srcStream), destStream, rectangle);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(Image srcImage, File destFile, Rectangle rectangle) throws IORuntimeException {
/*  317 */     write(cut(srcImage, rectangle), destFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(Image srcImage, OutputStream out, Rectangle rectangle) throws IORuntimeException {
/*  330 */     cut(srcImage, getImageOutputStream(out), rectangle);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cut(Image srcImage, ImageOutputStream destImageStream, Rectangle rectangle) throws IORuntimeException {
/*  343 */     writeJpg(cut(srcImage, rectangle), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image cut(Image srcImage, Rectangle rectangle) {
/*  355 */     return Img.from(srcImage).setPositionBaseCentre(false).cut(rectangle).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image cut(Image srcImage, int x, int y) {
/*  368 */     return cut(srcImage, x, y, -1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image cut(Image srcImage, int x, int y, int radius) {
/*  382 */     return Img.from(srcImage).cut(x, y, radius).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void slice(File srcImageFile, File descDir, int destWidth, int destHeight) {
/*  394 */     slice(read(srcImageFile), descDir, destWidth, destHeight);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void slice(Image srcImage, File descDir, int destWidth, int destHeight) {
/*      */     int cols, rows;
/*  406 */     if (destWidth <= 0) {
/*  407 */       destWidth = 200;
/*      */     }
/*  409 */     if (destHeight <= 0) {
/*  410 */       destHeight = 150;
/*      */     }
/*  412 */     int srcWidth = srcImage.getWidth(null);
/*  413 */     int srcHeight = srcImage.getHeight(null);
/*      */     
/*  415 */     if (srcWidth < destWidth) {
/*  416 */       destWidth = srcWidth;
/*      */     }
/*  418 */     if (srcHeight < destHeight) {
/*  419 */       destHeight = srcHeight;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  425 */     if (srcWidth % destWidth == 0) {
/*  426 */       cols = srcWidth / destWidth;
/*      */     } else {
/*  428 */       cols = (int)Math.floor(srcWidth / destWidth) + 1;
/*      */     } 
/*  430 */     if (srcHeight % destHeight == 0) {
/*  431 */       rows = srcHeight / destHeight;
/*      */     } else {
/*  433 */       rows = (int)Math.floor(srcHeight / destHeight) + 1;
/*      */     } 
/*      */ 
/*      */     
/*  437 */     for (int i = 0; i < rows; i++) {
/*  438 */       for (int j = 0; j < cols; j++) {
/*      */ 
/*      */         
/*  441 */         Image tag = cut(srcImage, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
/*      */         
/*  443 */         write(tag, FileUtil.file(descDir, "_r" + i + "_c" + j + ".jpg"));
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sliceByRowsAndCols(File srcImageFile, File destDir, int rows, int cols) {
/*      */     try {
/*  458 */       sliceByRowsAndCols(ImageIO.read(srcImageFile), destDir, rows, cols);
/*  459 */     } catch (IOException e) {
/*  460 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void sliceByRowsAndCols(Image srcImage, File destDir, int rows, int cols) {
/*  473 */     if (false == destDir.exists()) {
/*  474 */       FileUtil.mkdir(destDir);
/*  475 */     } else if (false == destDir.isDirectory()) {
/*  476 */       throw new IllegalArgumentException("Destination Dir must be a Directory !");
/*      */     } 
/*      */     
/*      */     try {
/*  480 */       if (rows <= 0 || rows > 20) {
/*  481 */         rows = 2;
/*      */       }
/*  483 */       if (cols <= 0 || cols > 20) {
/*  484 */         cols = 2;
/*      */       }
/*      */       
/*  487 */       BufferedImage bi = toBufferedImage(srcImage);
/*  488 */       int srcWidth = bi.getWidth();
/*  489 */       int srcHeight = bi.getHeight();
/*      */       
/*  491 */       int destWidth = NumberUtil.partValue(srcWidth, cols);
/*  492 */       int destHeight = NumberUtil.partValue(srcHeight, rows);
/*      */ 
/*      */ 
/*      */       
/*  496 */       for (int i = 0; i < rows; i++) {
/*  497 */         for (int j = 0; j < cols; j++) {
/*  498 */           Image tag = cut(bi, new Rectangle(j * destWidth, i * destHeight, destWidth, destHeight));
/*      */           
/*  500 */           ImageIO.write(toRenderedImage(tag), "jpeg", new File(destDir, "_r" + i + "_c" + j + ".jpg"));
/*      */         } 
/*      */       } 
/*  503 */     } catch (IOException e) {
/*  504 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void convert(File srcImageFile, File destImageFile) {
/*  517 */     Assert.notNull(srcImageFile);
/*  518 */     Assert.notNull(destImageFile);
/*  519 */     Assert.isFalse(srcImageFile.equals(destImageFile), "Src file is equals to dest file!", new Object[0]);
/*      */     
/*  521 */     String srcExtName = FileUtil.extName(srcImageFile);
/*  522 */     String destExtName = FileUtil.extName(destImageFile);
/*  523 */     if (StrUtil.equalsIgnoreCase(srcExtName, destExtName))
/*      */     {
/*  525 */       FileUtil.copy(srcImageFile, destImageFile, true);
/*      */     }
/*      */     
/*  528 */     ImageOutputStream imageOutputStream = null;
/*      */     try {
/*  530 */       imageOutputStream = getImageOutputStream(destImageFile);
/*  531 */       convert(read(srcImageFile), destExtName, imageOutputStream, StrUtil.equalsIgnoreCase("png", srcExtName));
/*      */     } finally {
/*  533 */       IoUtil.close(imageOutputStream);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void convert(InputStream srcStream, String formatName, OutputStream destStream) {
/*  547 */     write(read(srcStream), formatName, getImageOutputStream(destStream));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void convert(Image srcImage, String formatName, ImageOutputStream destImageStream, boolean isSrcPng) {
/*      */     try {
/*  562 */       ImageIO.write(isSrcPng ? copyImage(srcImage, 1) : toBufferedImage(srcImage), formatName, destImageStream);
/*  563 */     } catch (IOException e) {
/*  564 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(File srcImageFile, File destImageFile) {
/*  577 */     gray(read(srcImageFile), destImageFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(InputStream srcStream, OutputStream destStream) {
/*  589 */     gray(read(srcStream), getImageOutputStream(destStream));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(ImageInputStream srcStream, ImageOutputStream destStream) {
/*  601 */     gray(read(srcStream), destStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(Image srcImage, File outFile) {
/*  612 */     write(gray(srcImage), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(Image srcImage, OutputStream out) {
/*  624 */     gray(srcImage, getImageOutputStream(out));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void gray(Image srcImage, ImageOutputStream destImageStream) throws IORuntimeException {
/*  637 */     writeJpg(gray(srcImage), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image gray(Image srcImage) {
/*  648 */     return Img.from(srcImage).gray().getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(File srcImageFile, File destImageFile) {
/*  660 */     binary(read(srcImageFile), destImageFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(InputStream srcStream, OutputStream destStream, String imageType) {
/*  673 */     binary(read(srcStream), getImageOutputStream(destStream), imageType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(ImageInputStream srcStream, ImageOutputStream destStream, String imageType) {
/*  686 */     binary(read(srcStream), destStream, imageType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(Image srcImage, File outFile) {
/*  697 */     write(binary(srcImage), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(Image srcImage, OutputStream out, String imageType) {
/*  710 */     binary(srcImage, getImageOutputStream(out), imageType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void binary(Image srcImage, ImageOutputStream destImageStream, String imageType) throws IORuntimeException {
/*  724 */     write(binary(srcImage), imageType, destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image binary(Image srcImage) {
/*  735 */     return Img.from(srcImage).binary().getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(File imageFile, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) {
/*  753 */     pressText(read(imageFile), destFile, pressText, color, font, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(InputStream srcStream, OutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
/*  770 */     pressText(read(srcStream), getImageOutputStream(destStream), pressText, color, font, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(ImageInputStream srcStream, ImageOutputStream destStream, String pressText, Color color, Font font, int x, int y, float alpha) {
/*  787 */     pressText(read(srcStream), destStream, pressText, color, font, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(Image srcImage, File destFile, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
/*  806 */     write(pressText(srcImage, pressText, color, font, x, y, alpha), destFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(Image srcImage, OutputStream to, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
/*  825 */     pressText(srcImage, getImageOutputStream(to), pressText, color, font, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressText(Image srcImage, ImageOutputStream destImageStream, String pressText, Color color, Font font, int x, int y, float alpha) throws IORuntimeException {
/*  843 */     writeJpg(pressText(srcImage, pressText, color, font, x, y, alpha), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image pressText(Image srcImage, String pressText, Color color, Font font, int x, int y, float alpha) {
/*  861 */     return Img.from(srcImage).pressText(pressText, color, font, x, y, alpha).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(File srcImageFile, File destImageFile, Image pressImg, int x, int y, float alpha) {
/*  875 */     pressImage(read(srcImageFile), destImageFile, pressImg, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(InputStream srcStream, OutputStream destStream, Image pressImg, int x, int y, float alpha) {
/*  890 */     pressImage(read(srcStream), getImageOutputStream(destStream), pressImg, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(ImageInputStream srcStream, ImageOutputStream destStream, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
/*  906 */     pressImage(read(srcStream), destStream, pressImg, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(Image srcImage, File outFile, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
/*  923 */     write(pressImage(srcImage, pressImg, x, y, alpha), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(Image srcImage, OutputStream out, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
/*  940 */     pressImage(srcImage, getImageOutputStream(out), pressImg, x, y, alpha);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void pressImage(Image srcImage, ImageOutputStream destImageStream, Image pressImg, int x, int y, float alpha) throws IORuntimeException {
/*  956 */     writeJpg(pressImage(srcImage, pressImg, x, y, alpha), destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image pressImage(Image srcImage, Image pressImg, int x, int y, float alpha) {
/*  971 */     return Img.from(srcImage).pressImage(pressImg, x, y, alpha).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image pressImage(Image srcImage, Image pressImg, Rectangle rectangle, float alpha) {
/*  986 */     return Img.from(srcImage).pressImage(pressImg, rectangle, alpha).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void rotate(File imageFile, int degree, File outFile) throws IORuntimeException {
/* 1002 */     rotate(read(imageFile), degree, outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void rotate(Image image, int degree, File outFile) throws IORuntimeException {
/* 1016 */     write(rotate(image, degree), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void rotate(Image image, int degree, OutputStream out) throws IORuntimeException {
/* 1030 */     writeJpg(rotate(image, degree), getImageOutputStream(out));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void rotate(Image image, int degree, ImageOutputStream out) throws IORuntimeException {
/* 1044 */     writeJpg(rotate(image, degree), out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image rotate(Image image, int degree) {
/* 1057 */     return Img.from(image).rotate(degree).getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void flip(File imageFile, File outFile) throws IORuntimeException {
/* 1071 */     flip(read(imageFile), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void flip(Image image, File outFile) throws IORuntimeException {
/* 1083 */     write(flip(image), outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void flip(Image image, OutputStream out) throws IORuntimeException {
/* 1095 */     flip(image, getImageOutputStream(out));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void flip(Image image, ImageOutputStream out) throws IORuntimeException {
/* 1107 */     writeJpg(flip(image), out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image flip(Image image) {
/* 1118 */     return Img.from(image).flip().getImg();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void compress(File imageFile, File outFile, float quality) throws IORuntimeException {
/* 1133 */     Img.from(imageFile).setQuality(quality).write(outFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static RenderedImage toRenderedImage(Image img) {
/* 1147 */     if (img instanceof RenderedImage) {
/* 1148 */       return (RenderedImage)img;
/*      */     }
/*      */     
/* 1151 */     return copyImage(img, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage toBufferedImage(Image img) {
/* 1162 */     if (img instanceof BufferedImage) {
/* 1163 */       return (BufferedImage)img;
/*      */     }
/*      */     
/* 1166 */     return copyImage(img, 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage toBufferedImage(Image image, String imageType) {
/* 1180 */     int type = "png".equalsIgnoreCase(imageType) ? 2 : 1;
/*      */ 
/*      */     
/* 1183 */     return toBufferedImage(image, type);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage toBufferedImage(Image image, int imageType) {
/* 1197 */     if (image instanceof BufferedImage) {
/* 1198 */       BufferedImage bufferedImage1 = (BufferedImage)image;
/* 1199 */       if (imageType != bufferedImage1.getType()) {
/* 1200 */         bufferedImage1 = copyImage(image, imageType);
/*      */       }
/* 1202 */       return bufferedImage1;
/*      */     } 
/*      */     
/* 1205 */     BufferedImage bufferedImage = copyImage(image, imageType);
/* 1206 */     return bufferedImage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage copyImage(Image img, int imageType) {
/* 1230 */     return copyImage(img, imageType, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage copyImage(Image img, int imageType, Color backgroundColor) {
/* 1258 */     img = (new ImageIcon(img)).getImage();
/*      */ 
/*      */     
/* 1261 */     BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), imageType);
/* 1262 */     Graphics2D bGr = GraphicsUtil.createGraphics(bimage, backgroundColor);
/* 1263 */     bGr.drawImage(img, 0, 0, (ImageObserver)null);
/* 1264 */     bGr.dispose();
/*      */     
/* 1266 */     return bimage;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage createCompatibleImage(int width, int height, int transparency) throws HeadlessException {
/* 1279 */     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
/* 1280 */     GraphicsDevice gs = ge.getDefaultScreenDevice();
/* 1281 */     GraphicsConfiguration gc = gs.getDefaultConfiguration();
/* 1282 */     return gc.createCompatibleImage(width, height, transparency);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage toImage(String base64) throws IORuntimeException {
/* 1293 */     return toImage(Base64.decode(base64));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage toImage(byte[] imageBytes) throws IORuntimeException {
/* 1304 */     return read(new ByteArrayInputStream(imageBytes));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteArrayInputStream toStream(Image image, String imageType) {
/* 1316 */     return IoUtil.toStream(toBytes(image, imageType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toBase64DataUri(Image image, String imageType) {
/* 1328 */     return URLUtil.getDataUri("image/" + imageType, "base64", 
/*      */         
/* 1330 */         toBase64(image, imageType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toBase64(Image image, String imageType) {
/* 1342 */     return Base64.encode(toBytes(image, imageType));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] toBytes(Image image, String imageType) {
/* 1354 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 1355 */     write(image, imageType, out);
/* 1356 */     return out.toByteArray();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void createImage(String str, Font font, Color backgroundColor, Color fontColor, ImageOutputStream out) throws IORuntimeException {
/* 1370 */     writePng(createImage(str, font, backgroundColor, fontColor, 2), out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage createImage(String str, Font font, Color backgroundColor, Color fontColor, int imageType) throws IORuntimeException {
/* 1386 */     Rectangle2D r = getRectangle(str, font);
/*      */     
/* 1388 */     int unitHeight = (int)Math.floor(r.getHeight());
/*      */     
/* 1390 */     int width = (int)Math.round(r.getWidth()) + 1;
/*      */     
/* 1392 */     int height = unitHeight + 3;
/*      */ 
/*      */     
/* 1395 */     BufferedImage image = new BufferedImage(width, height, imageType);
/* 1396 */     Graphics g = image.getGraphics();
/* 1397 */     if (null != backgroundColor) {
/*      */       
/* 1399 */       g.setColor(backgroundColor);
/* 1400 */       g.fillRect(0, 0, width, height);
/*      */     } 
/* 1402 */     g.setColor((Color)ObjectUtil.defaultIfNull(fontColor, Color.BLACK));
/* 1403 */     g.setFont(font);
/* 1404 */     g.drawString(str, 0, font.getSize());
/* 1405 */     g.dispose();
/*      */     
/* 1407 */     return image;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Rectangle2D getRectangle(String str, Font font) {
/* 1419 */     return font.getStringBounds(str, new FontRenderContext(
/* 1420 */           AffineTransform.getScaleInstance(1.0D, 1.0D), false, false));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Font createFont(File fontFile) {
/* 1434 */     return FontUtil.createFont(fontFile);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Font createFont(InputStream fontStream) {
/* 1446 */     return FontUtil.createFont(fontStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Graphics2D createGraphics(BufferedImage image, Color color) {
/* 1459 */     return GraphicsUtil.createGraphics(image, color);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeJpg(Image image, ImageOutputStream destImageStream) throws IORuntimeException {
/* 1470 */     write(image, "jpg", destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writePng(Image image, ImageOutputStream destImageStream) throws IORuntimeException {
/* 1481 */     write(image, "png", destImageStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writeJpg(Image image, OutputStream out) throws IORuntimeException {
/* 1493 */     write(image, "jpg", out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void writePng(Image image, OutputStream out) throws IORuntimeException {
/* 1505 */     write(image, "png", out);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(ImageInputStream srcStream, String formatName, ImageOutputStream destStream) {
/* 1518 */     write(read(srcStream), formatName, destStream);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Image image, String imageType, OutputStream out) throws IORuntimeException {
/* 1532 */     write(image, imageType, getImageOutputStream(out));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean write(Image image, String imageType, ImageOutputStream destImageStream) throws IORuntimeException {
/* 1547 */     return write(image, imageType, destImageStream, 1.0F);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean write(Image image, String imageType, ImageOutputStream destImageStream, float quality) throws IORuntimeException {
/* 1562 */     if (StrUtil.isBlank(imageType)) {
/* 1563 */       imageType = "jpg";
/*      */     }
/*      */     
/* 1566 */     BufferedImage bufferedImage = toBufferedImage(image, imageType);
/* 1567 */     ImageWriter writer = getWriter(bufferedImage, imageType);
/* 1568 */     return write(bufferedImage, writer, destImageStream, quality);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void write(Image image, File targetFile) throws IORuntimeException {
/* 1580 */     FileUtil.touch(targetFile);
/* 1581 */     ImageOutputStream out = null;
/*      */     try {
/* 1583 */       out = getImageOutputStream(targetFile);
/* 1584 */       write(image, FileUtil.extName(targetFile), out);
/*      */     } finally {
/* 1586 */       IoUtil.close(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean write(Image image, ImageWriter writer, ImageOutputStream output, float quality) {
/* 1601 */     if (writer == null) {
/* 1602 */       return false;
/*      */     }
/*      */     
/* 1605 */     writer.setOutput(output);
/* 1606 */     RenderedImage renderedImage = toRenderedImage(image);
/*      */     
/* 1608 */     ImageWriteParam imgWriteParams = null;
/* 1609 */     if (quality > 0.0F && quality < 1.0F) {
/* 1610 */       imgWriteParams = writer.getDefaultWriteParam();
/* 1611 */       if (imgWriteParams.canWriteCompressed()) {
/* 1612 */         imgWriteParams.setCompressionMode(2);
/* 1613 */         imgWriteParams.setCompressionQuality(quality);
/* 1614 */         ColorModel colorModel = renderedImage.getColorModel();
/* 1615 */         imgWriteParams.setDestinationType(new ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
/*      */       } 
/*      */     } 
/*      */     
/*      */     try {
/* 1620 */       if (null != imgWriteParams) {
/* 1621 */         writer.write(null, new IIOImage(renderedImage, null, null), imgWriteParams);
/*      */       } else {
/* 1623 */         writer.write(renderedImage);
/*      */       } 
/* 1625 */       output.flush();
/* 1626 */     } catch (IOException e) {
/* 1627 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 1629 */       writer.dispose();
/*      */     } 
/* 1631 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageReader getReader(String type) {
/* 1641 */     Iterator<ImageReader> iterator = ImageIO.getImageReadersByFormatName(type);
/* 1642 */     if (iterator.hasNext()) {
/* 1643 */       return iterator.next();
/*      */     }
/* 1645 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(String imageFilePath) {
/* 1656 */     return read(FileUtil.file(imageFilePath));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(File imageFile) {
/*      */     BufferedImage result;
/*      */     try {
/* 1669 */       result = ImageIO.read(imageFile);
/* 1670 */     } catch (IOException e) {
/* 1671 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1674 */     if (null == result) {
/* 1675 */       throw new IllegalArgumentException("Image type of file [" + imageFile.getName() + "] is not supported!");
/*      */     }
/*      */     
/* 1678 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image getImage(URL url) {
/* 1689 */     return Toolkit.getDefaultToolkit().getImage(url);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(Resource resource) {
/* 1700 */     return read(resource.getStream());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(InputStream imageStream) {
/*      */     BufferedImage result;
/*      */     try {
/* 1713 */       result = ImageIO.read(imageStream);
/* 1714 */     } catch (IOException e) {
/* 1715 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1718 */     if (null == result) {
/* 1719 */       throw new IllegalArgumentException("Image type is not supported!");
/*      */     }
/*      */     
/* 1722 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(ImageInputStream imageStream) {
/*      */     BufferedImage result;
/*      */     try {
/* 1735 */       result = ImageIO.read(imageStream);
/* 1736 */     } catch (IOException e) {
/* 1737 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1740 */     if (null == result) {
/* 1741 */       throw new IllegalArgumentException("Image type is not supported!");
/*      */     }
/*      */     
/* 1744 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage read(URL imageUrl) {
/*      */     BufferedImage result;
/*      */     try {
/* 1757 */       result = ImageIO.read(imageUrl);
/* 1758 */     } catch (IOException e) {
/* 1759 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1762 */     if (null == result) {
/* 1763 */       throw new IllegalArgumentException("Image type of [" + imageUrl + "] is not supported!");
/*      */     }
/*      */     
/* 1766 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageOutputStream getImageOutputStream(OutputStream out) throws IORuntimeException {
/*      */     ImageOutputStream result;
/*      */     try {
/* 1780 */       result = ImageIO.createImageOutputStream(out);
/* 1781 */     } catch (IOException e) {
/* 1782 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1785 */     if (null == result) {
/* 1786 */       throw new IllegalArgumentException("Image type is not supported!");
/*      */     }
/*      */     
/* 1789 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageOutputStream getImageOutputStream(File outFile) throws IORuntimeException {
/*      */     ImageOutputStream result;
/*      */     try {
/* 1803 */       result = ImageIO.createImageOutputStream(outFile);
/* 1804 */     } catch (IOException e) {
/* 1805 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1808 */     if (null == result) {
/* 1809 */       throw new IllegalArgumentException("Image type of file [" + outFile.getName() + "] is not supported!");
/*      */     }
/*      */     
/* 1812 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageInputStream getImageInputStream(InputStream in) throws IORuntimeException {
/*      */     ImageOutputStream result;
/*      */     try {
/* 1826 */       result = ImageIO.createImageOutputStream(in);
/* 1827 */     } catch (IOException e) {
/* 1828 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/* 1831 */     if (null == result) {
/* 1832 */       throw new IllegalArgumentException("Image type is not supported!");
/*      */     }
/*      */     
/* 1835 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageWriter getWriter(Image img, String formatName) {
/* 1847 */     ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(toBufferedImage(img, formatName));
/* 1848 */     Iterator<ImageWriter> iter = ImageIO.getImageWriters(type, formatName);
/* 1849 */     return iter.hasNext() ? iter.next() : null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ImageWriter getWriter(String formatName) {
/* 1860 */     ImageWriter writer = null;
/* 1861 */     Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName(formatName);
/* 1862 */     if (iter.hasNext()) {
/* 1863 */       writer = iter.next();
/*      */     }
/* 1865 */     if (null == writer) {
/*      */       
/* 1867 */       iter = ImageIO.getImageWritersBySuffix(formatName);
/* 1868 */       if (iter.hasNext()) {
/* 1869 */         writer = iter.next();
/*      */       }
/*      */     } 
/* 1872 */     return writer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toHex(Color color) {
/* 1885 */     return toHex(color.getRed(), color.getGreen(), color.getBlue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toHex(int r, int g, int b) {
/* 1898 */     if (r < 0 || r > 255 || g < 0 || g > 255 || b < 0 || b > 255) {
/* 1899 */       throw new IllegalArgumentException("RGB must be 0~255!");
/*      */     }
/* 1901 */     return String.format("#%02X%02X%02X", new Object[] { Integer.valueOf(r), Integer.valueOf(g), Integer.valueOf(b) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Color hexToColor(String hex) {
/* 1912 */     return getColor(Integer.parseInt(StrUtil.removePrefix(hex, "#"), 16));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Color getColor(int rgb) {
/* 1923 */     return new Color(rgb);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Color getColor(String colorName) {
/* 1942 */     if (StrUtil.isBlank(colorName)) {
/* 1943 */       return null;
/*      */     }
/* 1945 */     colorName = colorName.toUpperCase();
/*      */     
/* 1947 */     if ("BLACK".equals(colorName))
/* 1948 */       return Color.BLACK; 
/* 1949 */     if ("WHITE".equals(colorName))
/* 1950 */       return Color.WHITE; 
/* 1951 */     if ("LIGHTGRAY".equals(colorName) || "LIGHT_GRAY".equals(colorName))
/* 1952 */       return Color.LIGHT_GRAY; 
/* 1953 */     if ("GRAY".equals(colorName))
/* 1954 */       return Color.GRAY; 
/* 1955 */     if ("DARKGRAY".equals(colorName) || "DARK_GRAY".equals(colorName))
/* 1956 */       return Color.DARK_GRAY; 
/* 1957 */     if ("RED".equals(colorName))
/* 1958 */       return Color.RED; 
/* 1959 */     if ("PINK".equals(colorName))
/* 1960 */       return Color.PINK; 
/* 1961 */     if ("ORANGE".equals(colorName))
/* 1962 */       return Color.ORANGE; 
/* 1963 */     if ("YELLOW".equals(colorName))
/* 1964 */       return Color.YELLOW; 
/* 1965 */     if ("GREEN".equals(colorName))
/* 1966 */       return Color.GREEN; 
/* 1967 */     if ("MAGENTA".equals(colorName))
/* 1968 */       return Color.MAGENTA; 
/* 1969 */     if ("CYAN".equals(colorName))
/* 1970 */       return Color.CYAN; 
/* 1971 */     if ("BLUE".equals(colorName))
/* 1972 */       return Color.BLUE; 
/* 1973 */     if ("DARKGOLD".equals(colorName))
/*      */     {
/* 1975 */       return hexToColor("#9e7e67"); } 
/* 1976 */     if ("LIGHTGOLD".equals(colorName))
/*      */     {
/* 1978 */       return hexToColor("#ac9c85"); } 
/* 1979 */     if (StrUtil.startWith(colorName, '#'))
/* 1980 */       return hexToColor(colorName); 
/* 1981 */     if (StrUtil.startWith(colorName, '$'))
/*      */     {
/* 1983 */       return hexToColor("#" + colorName.substring(1));
/*      */     }
/*      */     
/* 1986 */     List<String> rgb = StrUtil.split(colorName, ',');
/* 1987 */     if (3 == rgb.size()) {
/* 1988 */       Integer r = Convert.toInt(rgb.get(0));
/* 1989 */       Integer g = Convert.toInt(rgb.get(1));
/* 1990 */       Integer b = Convert.toInt(rgb.get(2));
/* 1991 */       if (false == ArrayUtil.hasNull((Object[])new Integer[] { r, g, b })) {
/* 1992 */         return new Color(r.intValue(), g.intValue(), b.intValue());
/*      */       }
/*      */     } else {
/* 1995 */       return null;
/*      */     } 
/*      */     
/* 1998 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Color randomColor() {
/* 2008 */     return randomColor(null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Color randomColor(Random random) {
/* 2019 */     if (null == random) {
/* 2020 */       random = RandomUtil.getRandom();
/*      */     }
/* 2022 */     return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Point getPointBaseCentre(Rectangle rectangle, int backgroundWidth, int backgroundHeight) {
/* 2035 */     return new Point(rectangle.x + 
/* 2036 */         Math.abs(backgroundWidth - rectangle.width) / 2, rectangle.y + 
/* 2037 */         Math.abs(backgroundHeight - rectangle.height) / 2);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getMainColor(BufferedImage image, int[]... rgbFilters) {
/* 2051 */     Map<String, Long> countMap = new HashMap<>();
/* 2052 */     int width = image.getWidth();
/* 2053 */     int height = image.getHeight();
/* 2054 */     int minx = image.getMinX();
/* 2055 */     int miny = image.getMinY();
/* 2056 */     for (int i = minx; i < width; i++) {
/* 2057 */       for (int j = miny; j < height; j++) {
/* 2058 */         int pixel = image.getRGB(i, j);
/* 2059 */         int r = (pixel & 0xFF0000) >> 16;
/* 2060 */         int g = (pixel & 0xFF00) >> 8;
/* 2061 */         int b = pixel & 0xFF;
/* 2062 */         if (!matchFilters(r, g, b, rgbFilters))
/*      */         {
/*      */           
/* 2065 */           countMap.merge(r + "-" + g + "-" + b, Long.valueOf(1L), Long::sum); } 
/*      */       } 
/*      */     } 
/* 2068 */     String maxColor = null;
/* 2069 */     long maxCount = 0L;
/* 2070 */     for (Map.Entry<String, Long> entry : countMap.entrySet()) {
/* 2071 */       String key = entry.getKey();
/* 2072 */       Long count = entry.getValue();
/* 2073 */       if (count.longValue() > maxCount) {
/* 2074 */         maxColor = key;
/* 2075 */         maxCount = count.longValue();
/*      */       } 
/*      */     } 
/* 2078 */     String[] splitRgbStr = StrUtil.splitToArray(maxColor, '-');
/* 2079 */     String rHex = Integer.toHexString(Integer.parseInt(splitRgbStr[0]));
/* 2080 */     String gHex = Integer.toHexString(Integer.parseInt(splitRgbStr[1]));
/* 2081 */     String bHex = Integer.toHexString(Integer.parseInt(splitRgbStr[2]));
/* 2082 */     rHex = (rHex.length() == 1) ? ("0" + rHex) : rHex;
/* 2083 */     gHex = (gHex.length() == 1) ? ("0" + gHex) : gHex;
/* 2084 */     bHex = (bHex.length() == 1) ? ("0" + bHex) : bHex;
/* 2085 */     return "#" + rHex + gHex + bHex;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean matchFilters(int r, int g, int b, int[]... rgbFilters) {
/* 2097 */     if (rgbFilters != null && rgbFilters.length > 0) {
/* 2098 */       for (int[] rgbFilter : rgbFilters) {
/* 2099 */         if (r == rgbFilter[0] && g == rgbFilter[1] && b == rgbFilter[2]) {
/* 2100 */           return true;
/*      */         }
/*      */       } 
/*      */     }
/* 2104 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) {
/* 2124 */     return BackgroundRemoval.backgroundRemoval(inputPath, outputPath, tolerance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean backgroundRemoval(File input, File output, int tolerance) {
/* 2142 */     return BackgroundRemoval.backgroundRemoval(input, output, tolerance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) {
/* 2161 */     return BackgroundRemoval.backgroundRemoval(input, output, override, tolerance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
/* 2179 */     return BackgroundRemoval.backgroundRemoval(bufferedImage, override, tolerance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
/* 2197 */     return BackgroundRemoval.backgroundRemoval(outputStream, override, tolerance);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage colorConvert(ColorSpace colorSpace, BufferedImage image) {
/* 2210 */     return filter(new ColorConvertOp(colorSpace, null), image);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage transform(AffineTransform xform, BufferedImage image) {
/* 2223 */     return filter(new AffineTransformOp(xform, null), image);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedImage filter(BufferedImageOp op, BufferedImage image) {
/* 2235 */     return op.filter(image, null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Image filter(ImageFilter filter, Image image) {
/* 2247 */     return Toolkit.getDefaultToolkit().createImage(new FilteredImageSource(image
/* 2248 */           .getSource(), filter));
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\ImgUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */