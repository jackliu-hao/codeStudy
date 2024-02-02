/*     */ package cn.hutool.core.img;
/*     */ 
/*     */ import cn.hutool.core.io.FileTypeUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.awt.Color;
/*     */ import java.awt.Graphics;
/*     */ import java.awt.image.BufferedImage;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.imageio.ImageIO;
/*     */ import javax.swing.ImageIcon;
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
/*     */ public class BackgroundRemoval
/*     */ {
/*  34 */   public static String[] IMAGES_TYPE = new String[] { "jpg", "png" };
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
/*     */   public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) {
/*  51 */     return backgroundRemoval(new File(inputPath), new File(outputPath), tolerance);
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
/*     */ 
/*     */   
/*     */   public static boolean backgroundRemoval(File input, File output, int tolerance) {
/*  69 */     return backgroundRemoval(input, output, null, tolerance);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) {
/*  88 */     if (fileTypeValidation(input, IMAGES_TYPE)) {
/*  89 */       return false;
/*     */     }
/*     */     
/*     */     try {
/*  93 */       BufferedImage bufferedImage = ImageIO.read(input);
/*     */       
/*  95 */       return ImageIO.write(backgroundRemoval(bufferedImage, override, tolerance), "png", output);
/*  96 */     } catch (IOException e) {
/*  97 */       e.printStackTrace();
/*  98 */       return false;
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
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
/* 118 */     tolerance = Math.min(255, Math.max(tolerance, 0));
/*     */     
/* 120 */     ImageIcon imageIcon = new ImageIcon(bufferedImage);
/* 121 */     BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), 6);
/*     */ 
/*     */     
/* 124 */     Graphics graphics = image.getGraphics();
/* 125 */     graphics.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
/*     */     
/* 127 */     String[] removeRgb = getRemoveRgb(bufferedImage);
/*     */     
/* 129 */     String mainColor = getMainColor(bufferedImage);
/* 130 */     int alpha = 0;
/* 131 */     for (int y = image.getMinY(); y < image.getHeight(); y++) {
/* 132 */       for (int x = image.getMinX(); x < image.getWidth(); x++) {
/*     */         
/* 134 */         int rgb = image.getRGB(x, y);
/* 135 */         String hex = ImgUtil.toHex((rgb & 0xFF0000) >> 16, (rgb & 0xFF00) >> 8, rgb & 0xFF);
/*     */         
/* 137 */         boolean isTrue = (ArrayUtil.contains((Object[])removeRgb, hex) || areColorsWithinTolerance(hexToRgb(mainColor), new Color(Integer.parseInt(hex.substring(1), 16)), tolerance));
/* 138 */         if (isTrue) {
/* 139 */           rgb = (override == null) ? (alpha + 1 << 24 | rgb & 0xFFFFFF) : override.getRGB();
/*     */         }
/* 141 */         image.setRGB(x, y, rgb);
/*     */       } 
/*     */     } 
/* 144 */     graphics.drawImage(image, 0, 0, imageIcon.getImageObserver());
/* 145 */     return image;
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
/*     */ 
/*     */   
/*     */   public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
/*     */     try {
/* 164 */       return backgroundRemoval(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())), override, tolerance);
/* 165 */     } catch (IOException e) {
/* 166 */       e.printStackTrace();
/* 167 */       return null;
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
/*     */   private static String[] getRemoveRgb(BufferedImage image) {
/* 180 */     int width = image.getWidth() - 1;
/* 181 */     int height = image.getHeight() - 1;
/*     */     
/* 183 */     int leftUpPixel = image.getRGB(1, 1);
/* 184 */     String leftUp = ImgUtil.toHex((leftUpPixel & 0xFF0000) >> 16, (leftUpPixel & 0xFF00) >> 8, leftUpPixel & 0xFF);
/*     */     
/* 186 */     int upMiddlePixel = image.getRGB(width / 2, 1);
/* 187 */     String upMiddle = ImgUtil.toHex((upMiddlePixel & 0xFF0000) >> 16, (upMiddlePixel & 0xFF00) >> 8, upMiddlePixel & 0xFF);
/*     */     
/* 189 */     int rightUpPixel = image.getRGB(width, 1);
/* 190 */     String rightUp = ImgUtil.toHex((rightUpPixel & 0xFF0000) >> 16, (rightUpPixel & 0xFF00) >> 8, rightUpPixel & 0xFF);
/*     */     
/* 192 */     int rightMiddlePixel = image.getRGB(width, height / 2);
/* 193 */     String rightMiddle = ImgUtil.toHex((rightMiddlePixel & 0xFF0000) >> 16, (rightMiddlePixel & 0xFF00) >> 8, rightMiddlePixel & 0xFF);
/*     */     
/* 195 */     int lowerRightPixel = image.getRGB(width, height);
/* 196 */     String lowerRight = ImgUtil.toHex((lowerRightPixel & 0xFF0000) >> 16, (lowerRightPixel & 0xFF00) >> 8, lowerRightPixel & 0xFF);
/*     */     
/* 198 */     int lowerMiddlePixel = image.getRGB(width / 2, height);
/* 199 */     String lowerMiddle = ImgUtil.toHex((lowerMiddlePixel & 0xFF0000) >> 16, (lowerMiddlePixel & 0xFF00) >> 8, lowerMiddlePixel & 0xFF);
/*     */     
/* 201 */     int leftLowerPixel = image.getRGB(1, height);
/* 202 */     String leftLower = ImgUtil.toHex((leftLowerPixel & 0xFF0000) >> 16, (leftLowerPixel & 0xFF00) >> 8, leftLowerPixel & 0xFF);
/*     */     
/* 204 */     int leftMiddlePixel = image.getRGB(1, height / 2);
/* 205 */     String leftMiddle = ImgUtil.toHex((leftMiddlePixel & 0xFF0000) >> 16, (leftMiddlePixel & 0xFF00) >> 8, leftMiddlePixel & 0xFF);
/*     */     
/* 207 */     return new String[] { leftUp, upMiddle, rightUp, rightMiddle, lowerRight, lowerMiddle, leftLower, leftMiddle };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Color hexToRgb(String hex) {
/* 217 */     return new Color(Integer.parseInt(hex.substring(1), 16));
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
/*     */   public static boolean areColorsWithinTolerance(Color color1, Color color2, int tolerance) {
/* 231 */     return areColorsWithinTolerance(color1, color2, new Color(tolerance, tolerance, tolerance));
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
/*     */   public static boolean areColorsWithinTolerance(Color color1, Color color2, Color tolerance) {
/* 244 */     return (color1.getRed() - color2.getRed() < tolerance.getRed() && color1
/* 245 */       .getRed() - color2.getRed() > -tolerance.getRed() && color1
/* 246 */       .getBlue() - color2.getBlue() < tolerance
/* 247 */       .getBlue() && color1.getBlue() - color2.getBlue() > 
/* 248 */       -tolerance.getBlue() && color1
/* 249 */       .getGreen() - color2.getGreen() < tolerance
/* 250 */       .getGreen() && color1.getGreen() - color2
/* 251 */       .getGreen() > -tolerance.getGreen());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMainColor(String input) {
/* 262 */     return getMainColor(new File(input));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMainColor(File input) {
/*     */     try {
/* 274 */       return getMainColor(ImageIO.read(input));
/* 275 */     } catch (IOException e) {
/* 276 */       e.printStackTrace();
/*     */       
/* 278 */       return "";
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getMainColor(BufferedImage bufferedImage) {
/* 289 */     if (bufferedImage == null) {
/* 290 */       throw new IllegalArgumentException("图片流是空的");
/*     */     }
/*     */ 
/*     */     
/* 294 */     List<String> list = new ArrayList<>();
/* 295 */     for (int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); y++) {
/* 296 */       for (int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); x++) {
/* 297 */         int pixel = bufferedImage.getRGB(x, y);
/* 298 */         list.add(((pixel & 0xFF0000) >> 16) + "-" + ((pixel & 0xFF00) >> 8) + "-" + (pixel & 0xFF));
/*     */       } 
/*     */     } 
/*     */     
/* 302 */     Map<String, Integer> map = new HashMap<>(list.size());
/* 303 */     for (String string : list) {
/* 304 */       Integer integer = map.get(string);
/* 305 */       if (integer == null) {
/* 306 */         integer = Integer.valueOf(1);
/*     */       } else {
/* 308 */         Integer integer1 = integer, integer2 = integer = Integer.valueOf(integer.intValue() + 1);
/*     */       } 
/* 310 */       map.put(string, integer);
/*     */     } 
/* 312 */     String max = "";
/* 313 */     long num = 0L;
/* 314 */     for (Map.Entry<String, Integer> entry : map.entrySet()) {
/* 315 */       String key = entry.getKey();
/* 316 */       Integer temp = entry.getValue();
/* 317 */       if (StrUtil.isBlank(max) || temp.intValue() > num) {
/* 318 */         max = key;
/* 319 */         num = temp.intValue();
/*     */       } 
/*     */     } 
/* 322 */     String[] strings = max.split("-");
/*     */     
/* 324 */     int rgbLength = 3;
/* 325 */     if (strings.length == rgbLength) {
/* 326 */       return ImgUtil.toHex(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), 
/* 327 */           Integer.parseInt(strings[2]));
/*     */     }
/* 329 */     return "";
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
/*     */   private static boolean fileTypeValidation(File input, String[] imagesType) {
/* 343 */     if (!input.exists()) {
/* 344 */       throw new IllegalArgumentException("给定文件为空");
/*     */     }
/*     */     
/* 347 */     String type = FileTypeUtil.getType(input);
/*     */     
/* 349 */     if (!ArrayUtil.contains((Object[])imagesType, type)) {
/* 350 */       throw new IllegalArgumentException(StrUtil.format("文件类型{}不支持", new Object[] { type }));
/*     */     }
/* 352 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\img\BackgroundRemoval.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */