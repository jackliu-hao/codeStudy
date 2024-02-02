/*     */ package cn.hutool.core.img;
/*     */ 
/*     */ import cn.hutool.core.io.FileUtil;
/*     */ import cn.hutool.core.io.IORuntimeException;
/*     */ import cn.hutool.core.io.IoUtil;
/*     */ import cn.hutool.core.io.resource.Resource;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.util.NumberUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.awt.AlphaComposite;
/*     */ import java.awt.BasicStroke;
/*     */ import java.awt.Color;
/*     */ import java.awt.Dimension;
/*     */ import java.awt.Font;
/*     */ import java.awt.Graphics2D;
/*     */ import java.awt.Image;
/*     */ import java.awt.Point;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.RenderingHints;
/*     */ import java.awt.Stroke;
/*     */ import java.awt.color.ColorSpace;
/*     */ import java.awt.geom.AffineTransform;
/*     */ import java.awt.geom.Ellipse2D;
/*     */ import java.awt.geom.RoundRectangle2D;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.awt.image.CropImageFilter;
/*     */ import java.awt.image.ImageFilter;
/*     */ import java.awt.image.ImageObserver;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.Serializable;
/*     */ import java.net.URL;
/*     */ import java.nio.file.Path;
/*     */ import javax.imageio.stream.ImageInputStream;
/*     */ import javax.imageio.stream.ImageOutputStream;
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
/*     */ public class Img
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*     */   private final BufferedImage srcImage;
/*     */   private Image targetImage;
/*     */   private String targetImageType;
/*     */   private boolean positionBaseCentre = true;
/*  62 */   private float quality = -1.0F;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(Path imagePath) {
/*  71 */     return from(imagePath.toFile());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(File imageFile) {
/*  81 */     return new Img(ImgUtil.read(imageFile));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(Resource resource) {
/*  92 */     return from(resource.getStream());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(InputStream in) {
/* 102 */     return new Img(ImgUtil.read(in));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(ImageInputStream imageStream) {
/* 112 */     return new Img(ImgUtil.read(imageStream));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(URL imageUrl) {
/* 122 */     return new Img(ImgUtil.read(imageUrl));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Img from(Image image) {
/* 132 */     return new Img(ImgUtil.toBufferedImage(image));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img(BufferedImage srcImage) {
/* 141 */     this(srcImage, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img(BufferedImage srcImage, String targetImageType) {
/* 152 */     this.srcImage = srcImage;
/* 153 */     if (null == targetImageType) {
/* 154 */       if (srcImage.getType() == 2 || srcImage
/* 155 */         .getType() == 3 || srcImage
/* 156 */         .getType() == 6 || srcImage
/* 157 */         .getType() == 7) {
/*     */         
/* 159 */         targetImageType = "png";
/*     */       } else {
/* 161 */         targetImageType = "jpg";
/*     */       } 
/*     */     }
/* 164 */     this.targetImageType = targetImageType;
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
/*     */   public Img setTargetImageType(String imgType) {
/* 176 */     this.targetImageType = imgType;
/* 177 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img setPositionBaseCentre(boolean positionBaseCentre) {
/* 188 */     this.positionBaseCentre = positionBaseCentre;
/* 189 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img setQuality(double quality) {
/* 200 */     return setQuality((float)quality);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img setQuality(float quality) {
/* 211 */     if (quality > 0.0F && quality < 1.0F) {
/* 212 */       this.quality = quality;
/*     */     } else {
/* 214 */       this.quality = 1.0F;
/*     */     } 
/* 216 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img scale(float scale) {
/* 226 */     if (scale < 0.0F)
/*     */     {
/* 228 */       scale = -scale;
/*     */     }
/* 230 */     Image srcImg = getValidSrcImg();
/*     */ 
/*     */     
/* 233 */     if ("png".equals(this.targetImageType)) {
/*     */       
/* 235 */       double scaleDouble = NumberUtil.toDouble(Float.valueOf(scale));
/* 236 */       this.targetImage = ImgUtil.transform(AffineTransform.getScaleInstance(scaleDouble, scaleDouble), 
/* 237 */           ImgUtil.toBufferedImage(srcImg, this.targetImageType));
/*     */     } else {
/*     */       
/* 240 */       int width = NumberUtil.mul(Integer.valueOf(srcImg.getWidth(null)), Float.valueOf(scale)).intValue();
/*     */       
/* 242 */       int height = NumberUtil.mul(Integer.valueOf(srcImg.getHeight(null)), Float.valueOf(scale)).intValue();
/* 243 */       scale(width, height);
/*     */     } 
/* 245 */     return this;
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
/*     */   public Img scale(int width, int height) {
/* 257 */     return scale(width, height, 4);
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
/*     */   public Img scale(int width, int height, int scaleType) {
/* 271 */     Image srcImg = getValidSrcImg();
/*     */     
/* 273 */     int srcHeight = srcImg.getHeight(null);
/* 274 */     int srcWidth = srcImg.getWidth(null);
/* 275 */     if (srcHeight == height && srcWidth == width) {
/*     */       
/* 277 */       this.targetImage = srcImg;
/* 278 */       return this;
/*     */     } 
/*     */     
/* 281 */     if ("png".equals(this.targetImageType)) {
/*     */       
/* 283 */       double sx = NumberUtil.div(width, srcWidth);
/* 284 */       double sy = NumberUtil.div(height, srcHeight);
/* 285 */       this.targetImage = ImgUtil.transform(AffineTransform.getScaleInstance(sx, sy), 
/* 286 */           ImgUtil.toBufferedImage(srcImg, this.targetImageType));
/*     */     } else {
/* 288 */       this.targetImage = srcImg.getScaledInstance(width, height, scaleType);
/*     */     } 
/*     */     
/* 291 */     return this;
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
/*     */   public Img scale(int width, int height, Color fixedColor) {
/* 304 */     Image srcImage = getValidSrcImg();
/* 305 */     int srcHeight = srcImage.getHeight(null);
/* 306 */     int srcWidth = srcImage.getWidth(null);
/* 307 */     double heightRatio = NumberUtil.div(height, srcHeight);
/* 308 */     double widthRatio = NumberUtil.div(width, srcWidth);
/*     */ 
/*     */     
/* 311 */     if (NumberUtil.equals(heightRatio, widthRatio)) {
/*     */       
/* 313 */       scale(width, height);
/* 314 */     } else if (widthRatio < heightRatio) {
/*     */       
/* 316 */       scale(width, (int)(srcHeight * widthRatio));
/*     */     } else {
/*     */       
/* 319 */       scale((int)(srcWidth * heightRatio), height);
/*     */     } 
/*     */ 
/*     */     
/* 323 */     srcImage = getValidSrcImg();
/* 324 */     srcHeight = srcImage.getHeight(null);
/* 325 */     srcWidth = srcImage.getWidth(null);
/*     */     
/* 327 */     BufferedImage image = new BufferedImage(width, height, getTypeInt());
/* 328 */     Graphics2D g = image.createGraphics();
/*     */ 
/*     */     
/* 331 */     if (null != fixedColor) {
/* 332 */       g.setBackground(fixedColor);
/* 333 */       g.clearRect(0, 0, width, height);
/*     */     } 
/*     */ 
/*     */     
/* 337 */     g.drawImage(srcImage, (width - srcWidth) / 2, (height - srcHeight) / 2, srcWidth, srcHeight, fixedColor, null);
/*     */     
/* 339 */     g.dispose();
/* 340 */     this.targetImage = image;
/* 341 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img cut(Rectangle rectangle) {
/* 351 */     Image srcImage = getValidSrcImg();
/* 352 */     fixRectangle(rectangle, srcImage.getWidth(null), srcImage.getHeight(null));
/*     */     
/* 354 */     ImageFilter cropFilter = new CropImageFilter(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
/* 355 */     this.targetImage = ImgUtil.filter(cropFilter, srcImage);
/* 356 */     return this;
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
/*     */   public Img cut(int x, int y) {
/* 368 */     return cut(x, y, -1);
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
/*     */   public Img cut(int x, int y, int radius) {
/* 381 */     Image srcImage = getValidSrcImg();
/* 382 */     int width = srcImage.getWidth(null);
/* 383 */     int height = srcImage.getHeight(null);
/*     */ 
/*     */     
/* 386 */     int diameter = (radius > 0) ? (radius * 2) : Math.min(width, height);
/* 387 */     BufferedImage targetImage = new BufferedImage(diameter, diameter, 2);
/* 388 */     Graphics2D g = targetImage.createGraphics();
/* 389 */     g.setClip(new Ellipse2D.Double(0.0D, 0.0D, diameter, diameter));
/*     */     
/* 391 */     if (this.positionBaseCentre) {
/* 392 */       x = x - width / 2 + diameter / 2;
/* 393 */       y = y - height / 2 + diameter / 2;
/*     */     } 
/* 395 */     g.drawImage(srcImage, x, y, (ImageObserver)null);
/* 396 */     g.dispose();
/* 397 */     this.targetImage = targetImage;
/* 398 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img round(double arc) {
/* 409 */     Image srcImage = getValidSrcImg();
/* 410 */     int width = srcImage.getWidth(null);
/* 411 */     int height = srcImage.getHeight(null);
/*     */ 
/*     */     
/* 414 */     arc = NumberUtil.mul(arc, Math.min(width, height));
/*     */     
/* 416 */     BufferedImage targetImage = new BufferedImage(width, height, 2);
/* 417 */     Graphics2D g2 = targetImage.createGraphics();
/* 418 */     g2.setComposite(AlphaComposite.Src);
/*     */     
/* 420 */     g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
/* 421 */     g2.fill(new RoundRectangle2D.Double(0.0D, 0.0D, width, height, arc, arc));
/* 422 */     g2.setComposite(AlphaComposite.SrcAtop);
/* 423 */     g2.drawImage(srcImage, 0, 0, (ImageObserver)null);
/* 424 */     g2.dispose();
/* 425 */     this.targetImage = targetImage;
/* 426 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img gray() {
/* 435 */     this.targetImage = ImgUtil.colorConvert(ColorSpace.getInstance(1003), getValidSrcBufferedImg());
/* 436 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img binary() {
/* 445 */     this.targetImage = ImgUtil.copyImage(getValidSrcImg(), 12);
/* 446 */     return this;
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
/*     */   
/*     */   public Img pressText(String pressText, Color color, Font font, int x, int y, float alpha) {
/* 462 */     return pressText(pressText, color, font, new Point(x, y), alpha);
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
/*     */   public Img pressText(String pressText, Color color, Font font, Point point, float alpha) {
/* 477 */     BufferedImage targetImage = ImgUtil.toBufferedImage(getValidSrcImg(), this.targetImageType);
/*     */     
/* 479 */     if (null == font)
/*     */     {
/* 481 */       font = FontUtil.createSansSerifFont((int)(targetImage.getHeight() * 0.75D));
/*     */     }
/*     */     
/* 484 */     Graphics2D g = targetImage.createGraphics();
/*     */     
/* 486 */     g.setComposite(AlphaComposite.getInstance(10, alpha));
/*     */ 
/*     */     
/* 489 */     if (this.positionBaseCentre) {
/*     */       
/* 491 */       GraphicsUtil.drawString(g, pressText, font, color, new Rectangle(point.x, point.y, targetImage
/* 492 */             .getWidth(), targetImage.getHeight()));
/*     */     } else {
/*     */       
/* 495 */       GraphicsUtil.drawString(g, pressText, font, color, point);
/*     */     } 
/*     */ 
/*     */     
/* 499 */     g.dispose();
/* 500 */     this.targetImage = targetImage;
/*     */     
/* 502 */     return this;
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
/*     */   
/*     */   public Img pressTextFull(String pressText, Color color, Font font, int lineHeight, int degree, float alpha) {
/*     */     Dimension dimension;
/* 519 */     BufferedImage targetImage = ImgUtil.toBufferedImage(getValidSrcImg(), this.targetImageType);
/*     */     
/* 521 */     if (null == font)
/*     */     {
/* 523 */       font = FontUtil.createSansSerifFont((int)(targetImage.getHeight() * 0.75D));
/*     */     }
/* 525 */     int targetHeight = targetImage.getHeight();
/* 526 */     int targetWidth = targetImage.getWidth();
/*     */ 
/*     */     
/* 529 */     Graphics2D g = targetImage.createGraphics();
/* 530 */     g.setColor(color);
/*     */     
/* 532 */     g.rotate(Math.toRadians(degree), (targetWidth >> 1), (targetHeight >> 1));
/* 533 */     g.setComposite(AlphaComposite.getInstance(10, alpha));
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 538 */       dimension = FontUtil.getDimension(g.getFontMetrics(font), pressText);
/* 539 */     } catch (Exception e) {
/*     */       
/* 541 */       dimension = new Dimension(targetWidth / 3, targetHeight / 3);
/*     */     } 
/* 543 */     int intervalHeight = dimension.height * lineHeight;
/*     */     
/* 545 */     int y = -targetHeight >> 1;
/* 546 */     while (y < targetHeight * 1.5D) {
/* 547 */       int x = -targetWidth >> 1;
/* 548 */       while (x < targetWidth * 1.5D) {
/* 549 */         GraphicsUtil.drawString(g, pressText, font, color, new Point(x, y));
/* 550 */         x += dimension.width;
/*     */       } 
/* 552 */       y += intervalHeight;
/*     */     } 
/* 554 */     g.dispose();
/*     */     
/* 556 */     this.targetImage = targetImage;
/* 557 */     return this;
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
/*     */   public Img pressImage(Image pressImg, int x, int y, float alpha) {
/* 570 */     int pressImgWidth = pressImg.getWidth(null);
/* 571 */     int pressImgHeight = pressImg.getHeight(null);
/* 572 */     return pressImage(pressImg, new Rectangle(x, y, pressImgWidth, pressImgHeight), alpha);
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
/*     */   public Img pressImage(Image pressImg, Rectangle rectangle, float alpha) {
/* 585 */     Image targetImg = getValidSrcImg();
/*     */     
/* 587 */     this.targetImage = draw(ImgUtil.toBufferedImage(targetImg, this.targetImageType), pressImg, rectangle, alpha);
/* 588 */     return this;
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
/*     */   public Img rotate(int degree) {
/* 600 */     Image image = getValidSrcImg();
/* 601 */     int width = image.getWidth(null);
/* 602 */     int height = image.getHeight(null);
/* 603 */     Rectangle rectangle = calcRotatedSize(width, height, degree);
/* 604 */     BufferedImage targetImg = new BufferedImage(rectangle.width, rectangle.height, getTypeInt());
/* 605 */     Graphics2D graphics2d = targetImg.createGraphics();
/*     */     
/* 607 */     graphics2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
/*     */     
/* 609 */     graphics2d.translate((rectangle.width - width) / 2.0D, (rectangle.height - height) / 2.0D);
/* 610 */     graphics2d.rotate(Math.toRadians(degree), width / 2.0D, height / 2.0D);
/* 611 */     graphics2d.drawImage(image, 0, 0, (ImageObserver)null);
/* 612 */     graphics2d.dispose();
/* 613 */     this.targetImage = targetImg;
/* 614 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Img flip() {
/* 623 */     Image image = getValidSrcImg();
/* 624 */     int width = image.getWidth(null);
/* 625 */     int height = image.getHeight(null);
/* 626 */     BufferedImage targetImg = new BufferedImage(width, height, getTypeInt());
/* 627 */     Graphics2D graphics2d = targetImg.createGraphics();
/* 628 */     graphics2d.drawImage(image, 0, 0, width, height, width, 0, 0, height, null);
/* 629 */     graphics2d.dispose();
/*     */     
/* 631 */     this.targetImage = targetImg;
/* 632 */     return this;
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
/*     */   public Img stroke(Color color, float width) {
/* 644 */     return stroke(color, new BasicStroke(width));
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
/*     */   public Img stroke(Color color, Stroke stroke) {
/* 656 */     BufferedImage image = ImgUtil.toBufferedImage(getValidSrcImg(), this.targetImageType);
/* 657 */     int width = image.getWidth(null);
/* 658 */     int height = image.getHeight(null);
/* 659 */     Graphics2D g = image.createGraphics();
/*     */     
/* 661 */     g.setColor((Color)ObjectUtil.defaultIfNull(color, Color.BLACK));
/* 662 */     if (null != stroke) {
/* 663 */       g.setStroke(stroke);
/*     */     }
/*     */     
/* 666 */     g.drawRect(0, 0, width - 1, height - 1);
/*     */     
/* 668 */     g.dispose();
/* 669 */     this.targetImage = image;
/*     */     
/* 671 */     return this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Image getImg() {
/* 682 */     return getValidSrcImg();
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
/*     */   public boolean write(OutputStream out) throws IORuntimeException {
/* 694 */     return write(ImgUtil.getImageOutputStream(out));
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
/*     */   public boolean write(ImageOutputStream targetImageStream) throws IORuntimeException {
/* 706 */     Assert.notBlank(this.targetImageType, "Target image type is blank !", new Object[0]);
/* 707 */     Assert.notNull(targetImageStream, "Target output stream is null !", new Object[0]);
/*     */     
/* 709 */     Image targetImage = (null == this.targetImage) ? this.srcImage : this.targetImage;
/* 710 */     Assert.notNull(targetImage, "Target image is null !", new Object[0]);
/*     */     
/* 712 */     return ImgUtil.write(targetImage, this.targetImageType, targetImageStream, this.quality);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean write(File targetFile) throws IORuntimeException {
/* 723 */     String formatName = FileUtil.extName(targetFile);
/* 724 */     if (StrUtil.isNotBlank(formatName)) {
/* 725 */       this.targetImageType = formatName;
/*     */     }
/*     */     
/* 728 */     if (targetFile.exists())
/*     */     {
/* 730 */       targetFile.delete();
/*     */     }
/*     */     
/* 733 */     ImageOutputStream out = null;
/*     */     try {
/* 735 */       out = ImgUtil.getImageOutputStream(targetFile);
/* 736 */       return write(out);
/*     */     } finally {
/* 738 */       IoUtil.close(out);
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
/*     */ 
/*     */   
/*     */   private BufferedImage draw(BufferedImage backgroundImg, Image img, Rectangle rectangle, float alpha) {
/* 754 */     Graphics2D g = backgroundImg.createGraphics();
/* 755 */     GraphicsUtil.setAlpha(g, alpha);
/*     */     
/* 757 */     fixRectangle(rectangle, backgroundImg.getWidth(), backgroundImg.getHeight());
/* 758 */     GraphicsUtil.drawImg(g, img, rectangle);
/*     */     
/* 760 */     g.dispose();
/* 761 */     return backgroundImg;
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
/*     */   private int getTypeInt() {
/* 773 */     switch (this.targetImageType) {
/*     */       case "png":
/* 775 */         return 2;
/*     */     } 
/* 777 */     return 1;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Image getValidSrcImg() {
/* 787 */     return (Image)ObjectUtil.defaultIfNull(this.targetImage, this.srcImage);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private BufferedImage getValidSrcBufferedImg() {
/* 797 */     return ImgUtil.toBufferedImage(getValidSrcImg(), this.targetImageType);
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
/*     */   private Rectangle fixRectangle(Rectangle rectangle, int baseWidth, int baseHeight) {
/* 811 */     if (this.positionBaseCentre) {
/* 812 */       Point pointBaseCentre = ImgUtil.getPointBaseCentre(rectangle, baseWidth, baseHeight);
/*     */       
/* 814 */       rectangle.setLocation(pointBaseCentre.x, pointBaseCentre.y);
/*     */     } 
/* 816 */     return rectangle;
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
/*     */   private static Rectangle calcRotatedSize(int width, int height, int degree) {
/* 829 */     if (degree < 0)
/*     */     {
/* 831 */       degree += 360;
/*     */     }
/* 833 */     if (degree >= 90) {
/* 834 */       if (degree / 90 % 2 == 1) {
/* 835 */         int temp = height;
/*     */         
/* 837 */         height = width;
/* 838 */         width = temp;
/*     */       } 
/* 840 */       degree %= 90;
/*     */     } 
/* 842 */     double r = Math.sqrt((height * height + width * width)) / 2.0D;
/* 843 */     double len = 2.0D * Math.sin(Math.toRadians(degree) / 2.0D) * r;
/* 844 */     double angel_alpha = (Math.PI - Math.toRadians(degree)) / 2.0D;
/* 845 */     double angel_dalta_width = Math.atan(height / width);
/* 846 */     double angel_dalta_height = Math.atan(width / height);
/* 847 */     int len_dalta_width = (int)(len * Math.cos(Math.PI - angel_alpha - angel_dalta_width));
/* 848 */     int len_dalta_height = (int)(len * Math.cos(Math.PI - angel_alpha - angel_dalta_height));
/* 849 */     int des_width = width + len_dalta_width * 2;
/* 850 */     int des_height = height + len_dalta_height * 2;
/*     */     
/* 852 */     return new Rectangle(des_width, des_height);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\Img.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */