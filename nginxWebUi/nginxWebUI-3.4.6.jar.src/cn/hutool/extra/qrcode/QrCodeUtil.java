/*     */ package cn.hutool.extra.qrcode;
/*     */ 
/*     */ import cn.hutool.core.codec.Base64;
/*     */ import cn.hutool.core.img.Img;
/*     */ import cn.hutool.core.img.ImgUtil;
/*     */ import com.google.zxing.BarcodeFormat;
/*     */ import com.google.zxing.Binarizer;
/*     */ import com.google.zxing.BinaryBitmap;
/*     */ import com.google.zxing.DecodeHintType;
/*     */ import com.google.zxing.LuminanceSource;
/*     */ import com.google.zxing.MultiFormatReader;
/*     */ import com.google.zxing.MultiFormatWriter;
/*     */ import com.google.zxing.NotFoundException;
/*     */ import com.google.zxing.Result;
/*     */ import com.google.zxing.WriterException;
/*     */ import com.google.zxing.common.BitMatrix;
/*     */ import com.google.zxing.common.GlobalHistogramBinarizer;
/*     */ import com.google.zxing.common.HybridBinarizer;
/*     */ import java.awt.Image;
/*     */ import java.awt.Rectangle;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.util.HashMap;
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
/*     */ public class QrCodeUtil
/*     */ {
/*     */   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, String logoBase64) {
/*  53 */     return generateAsBase64(content, qrConfig, imageType, Base64.decode(logoBase64));
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
/*     */   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, byte[] logo) {
/*  66 */     return generateAsBase64(content, qrConfig, imageType, ImgUtil.toImage(logo));
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
/*     */   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, Image logo) {
/*  79 */     qrConfig.setImg(logo);
/*  80 */     return generateAsBase64(content, qrConfig, imageType);
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
/*     */   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType) {
/*  96 */     BufferedImage img = generate(content, qrConfig);
/*  97 */     return ImgUtil.toBase64DataUri(img, imageType);
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
/*     */   public static byte[] generatePng(String content, int width, int height) {
/* 110 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 111 */     generate(content, width, height, "png", out);
/* 112 */     return out.toByteArray();
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
/*     */   public static byte[] generatePng(String content, QrConfig config) {
/* 124 */     ByteArrayOutputStream out = new ByteArrayOutputStream();
/* 125 */     generate(content, config, "png", out);
/* 126 */     return out.toByteArray();
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
/*     */   public static File generate(String content, int width, int height, File targetFile) {
/* 139 */     BufferedImage image = generate(content, width, height);
/* 140 */     ImgUtil.write(image, targetFile);
/* 141 */     return targetFile;
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
/*     */   public static File generate(String content, QrConfig config, File targetFile) {
/* 154 */     BufferedImage image = generate(content, config);
/* 155 */     ImgUtil.write(image, targetFile);
/* 156 */     return targetFile;
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
/*     */   public static void generate(String content, int width, int height, String imageType, OutputStream out) {
/* 169 */     BufferedImage image = generate(content, width, height);
/* 170 */     ImgUtil.write(image, imageType, out);
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
/*     */   public static void generate(String content, QrConfig config, String imageType, OutputStream out) {
/* 183 */     BufferedImage image = generate(content, config);
/* 184 */     ImgUtil.write(image, imageType, out);
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
/*     */   public static BufferedImage generate(String content, int width, int height) {
/* 196 */     return generate(content, new QrConfig(width, height));
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
/*     */   public static BufferedImage generate(String content, BarcodeFormat format, int width, int height) {
/* 209 */     return generate(content, format, new QrConfig(width, height));
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
/*     */   public static BufferedImage generate(String content, QrConfig config) {
/* 221 */     return generate(content, BarcodeFormat.QR_CODE, config);
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
/*     */   public static BufferedImage generate(String content, BarcodeFormat format, QrConfig config) {
/* 235 */     BitMatrix bitMatrix = encode(content, format, config);
/* 236 */     BufferedImage image = toImage(bitMatrix, config.foreColor, config.backColor);
/* 237 */     Image logoImg = config.img;
/* 238 */     if (null != logoImg && BarcodeFormat.QR_CODE == format) {
/*     */       
/* 240 */       int width, height, qrWidth = image.getWidth();
/* 241 */       int qrHeight = image.getHeight();
/*     */ 
/*     */ 
/*     */       
/* 245 */       if (qrWidth < qrHeight) {
/* 246 */         width = qrWidth / config.ratio;
/* 247 */         height = logoImg.getHeight(null) * width / logoImg.getWidth(null);
/*     */       } else {
/* 249 */         height = qrHeight / config.ratio;
/* 250 */         width = logoImg.getWidth(null) * height / logoImg.getHeight(null);
/*     */       } 
/*     */       
/* 253 */       Img.from(image).pressImage(
/* 254 */           Img.from(logoImg).round(0.3D).getImg(), new Rectangle(width, height), 1.0F);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 259 */     return image;
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
/*     */   public static BitMatrix encode(String content, int width, int height) {
/* 273 */     return encode(content, BarcodeFormat.QR_CODE, width, height);
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
/*     */   public static BitMatrix encode(String content, QrConfig config) {
/* 285 */     return encode(content, BarcodeFormat.QR_CODE, config);
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
/*     */   public static BitMatrix encode(String content, BarcodeFormat format, int width, int height) {
/* 298 */     return encode(content, format, new QrConfig(width, height));
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
/*     */   public static BitMatrix encode(String content, BarcodeFormat format, QrConfig config) {
/*     */     BitMatrix bitMatrix;
/* 311 */     MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
/* 312 */     if (null == config)
/*     */     {
/* 314 */       config = new QrConfig();
/*     */     }
/*     */ 
/*     */     
/*     */     try {
/* 319 */       bitMatrix = multiFormatWriter.encode(content, format, config.width, config.height, config.toHints(format));
/* 320 */     } catch (WriterException e) {
/* 321 */       throw new QrCodeException(e);
/*     */     } 
/*     */     
/* 324 */     return bitMatrix;
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
/*     */   public static String decode(InputStream qrCodeInputstream) {
/* 336 */     return decode(ImgUtil.read(qrCodeInputstream));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode(File qrCodeFile) {
/* 346 */     return decode(ImgUtil.read(qrCodeFile));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String decode(Image image) {
/* 356 */     return decode(image, true, false);
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
/*     */   public static String decode(Image image, boolean isTryHarder, boolean isPureBarcode) {
/* 371 */     return decode(image, buildHints(isTryHarder, isPureBarcode));
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
/*     */   public static String decode(Image image, Map<DecodeHintType, Object> hints) {
/* 385 */     MultiFormatReader formatReader = new MultiFormatReader();
/* 386 */     formatReader.setHints(hints);
/*     */     
/* 388 */     LuminanceSource source = new BufferedImageLuminanceSource(ImgUtil.toBufferedImage(image));
/*     */     
/* 390 */     Result result = _decode(formatReader, (Binarizer)new HybridBinarizer(source));
/* 391 */     if (null == result) {
/* 392 */       result = _decode(formatReader, (Binarizer)new GlobalHistogramBinarizer(source));
/*     */     }
/*     */     
/* 395 */     return (null != result) ? result.getText() : null;
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
/*     */   public static BufferedImage toImage(BitMatrix matrix, int foreColor, Integer backColor) {
/* 408 */     int width = matrix.getWidth();
/* 409 */     int height = matrix.getHeight();
/* 410 */     BufferedImage image = new BufferedImage(width, height, (null == backColor) ? 2 : 1);
/* 411 */     for (int x = 0; x < width; x++) {
/* 412 */       for (int y = 0; y < height; y++) {
/* 413 */         if (matrix.get(x, y)) {
/* 414 */           image.setRGB(x, y, foreColor);
/* 415 */         } else if (null != backColor) {
/* 416 */           image.setRGB(x, y, backColor.intValue());
/*     */         } 
/*     */       } 
/*     */     } 
/* 420 */     return image;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<DecodeHintType, Object> buildHints(boolean isTryHarder, boolean isPureBarcode) {
/* 431 */     HashMap<DecodeHintType, Object> hints = new HashMap<>();
/* 432 */     hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
/*     */ 
/*     */     
/* 435 */     if (isTryHarder) {
/* 436 */       hints.put(DecodeHintType.TRY_HARDER, Boolean.valueOf(true));
/*     */     }
/*     */     
/* 439 */     if (isPureBarcode) {
/* 440 */       hints.put(DecodeHintType.PURE_BARCODE, Boolean.valueOf(true));
/*     */     }
/* 442 */     return hints;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Result _decode(MultiFormatReader formatReader, Binarizer binarizer) {
/*     */     try {
/* 454 */       return formatReader.decodeWithState(new BinaryBitmap(binarizer));
/* 455 */     } catch (NotFoundException e) {
/* 456 */       return null;
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\qrcode\QrCodeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */