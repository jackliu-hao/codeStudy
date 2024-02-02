package cn.hutool.core.img;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

public class BackgroundRemoval {
   public static String[] IMAGES_TYPE = new String[]{"jpg", "png"};

   public static boolean backgroundRemoval(String inputPath, String outputPath, int tolerance) {
      return backgroundRemoval(new File(inputPath), new File(outputPath), tolerance);
   }

   public static boolean backgroundRemoval(File input, File output, int tolerance) {
      return backgroundRemoval(input, output, (Color)null, tolerance);
   }

   public static boolean backgroundRemoval(File input, File output, Color override, int tolerance) {
      if (fileTypeValidation(input, IMAGES_TYPE)) {
         return false;
      } else {
         try {
            BufferedImage bufferedImage = ImageIO.read(input);
            return ImageIO.write(backgroundRemoval(bufferedImage, override, tolerance), "png", output);
         } catch (IOException var5) {
            var5.printStackTrace();
            return false;
         }
      }
   }

   public static BufferedImage backgroundRemoval(BufferedImage bufferedImage, Color override, int tolerance) {
      tolerance = Math.min(255, Math.max(tolerance, 0));
      ImageIcon imageIcon = new ImageIcon(bufferedImage);
      BufferedImage image = new BufferedImage(imageIcon.getIconWidth(), imageIcon.getIconHeight(), 6);
      Graphics graphics = image.getGraphics();
      graphics.drawImage(imageIcon.getImage(), 0, 0, imageIcon.getImageObserver());
      String[] removeRgb = getRemoveRgb(bufferedImage);
      String mainColor = getMainColor(bufferedImage);
      int alpha = 0;

      for(int y = image.getMinY(); y < image.getHeight(); ++y) {
         for(int x = image.getMinX(); x < image.getWidth(); ++x) {
            int rgb = image.getRGB(x, y);
            String hex = ImgUtil.toHex((rgb & 16711680) >> 16, (rgb & '\uff00') >> 8, rgb & 255);
            boolean isTrue = ArrayUtil.contains(removeRgb, hex) || areColorsWithinTolerance(hexToRgb(mainColor), new Color(Integer.parseInt(hex.substring(1), 16)), tolerance);
            if (isTrue) {
               rgb = override == null ? alpha + 1 << 24 | rgb & 16777215 : override.getRGB();
            }

            image.setRGB(x, y, rgb);
         }
      }

      graphics.drawImage(image, 0, 0, imageIcon.getImageObserver());
      return image;
   }

   public static BufferedImage backgroundRemoval(ByteArrayOutputStream outputStream, Color override, int tolerance) {
      try {
         return backgroundRemoval(ImageIO.read(new ByteArrayInputStream(outputStream.toByteArray())), override, tolerance);
      } catch (IOException var4) {
         var4.printStackTrace();
         return null;
      }
   }

   private static String[] getRemoveRgb(BufferedImage image) {
      int width = image.getWidth() - 1;
      int height = image.getHeight() - 1;
      int leftUpPixel = image.getRGB(1, 1);
      String leftUp = ImgUtil.toHex((leftUpPixel & 16711680) >> 16, (leftUpPixel & '\uff00') >> 8, leftUpPixel & 255);
      int upMiddlePixel = image.getRGB(width / 2, 1);
      String upMiddle = ImgUtil.toHex((upMiddlePixel & 16711680) >> 16, (upMiddlePixel & '\uff00') >> 8, upMiddlePixel & 255);
      int rightUpPixel = image.getRGB(width, 1);
      String rightUp = ImgUtil.toHex((rightUpPixel & 16711680) >> 16, (rightUpPixel & '\uff00') >> 8, rightUpPixel & 255);
      int rightMiddlePixel = image.getRGB(width, height / 2);
      String rightMiddle = ImgUtil.toHex((rightMiddlePixel & 16711680) >> 16, (rightMiddlePixel & '\uff00') >> 8, rightMiddlePixel & 255);
      int lowerRightPixel = image.getRGB(width, height);
      String lowerRight = ImgUtil.toHex((lowerRightPixel & 16711680) >> 16, (lowerRightPixel & '\uff00') >> 8, lowerRightPixel & 255);
      int lowerMiddlePixel = image.getRGB(width / 2, height);
      String lowerMiddle = ImgUtil.toHex((lowerMiddlePixel & 16711680) >> 16, (lowerMiddlePixel & '\uff00') >> 8, lowerMiddlePixel & 255);
      int leftLowerPixel = image.getRGB(1, height);
      String leftLower = ImgUtil.toHex((leftLowerPixel & 16711680) >> 16, (leftLowerPixel & '\uff00') >> 8, leftLowerPixel & 255);
      int leftMiddlePixel = image.getRGB(1, height / 2);
      String leftMiddle = ImgUtil.toHex((leftMiddlePixel & 16711680) >> 16, (leftMiddlePixel & '\uff00') >> 8, leftMiddlePixel & 255);
      return new String[]{leftUp, upMiddle, rightUp, rightMiddle, lowerRight, lowerMiddle, leftLower, leftMiddle};
   }

   public static Color hexToRgb(String hex) {
      return new Color(Integer.parseInt(hex.substring(1), 16));
   }

   public static boolean areColorsWithinTolerance(Color color1, Color color2, int tolerance) {
      return areColorsWithinTolerance(color1, color2, new Color(tolerance, tolerance, tolerance));
   }

   public static boolean areColorsWithinTolerance(Color color1, Color color2, Color tolerance) {
      return color1.getRed() - color2.getRed() < tolerance.getRed() && color1.getRed() - color2.getRed() > -tolerance.getRed() && color1.getBlue() - color2.getBlue() < tolerance.getBlue() && color1.getBlue() - color2.getBlue() > -tolerance.getBlue() && color1.getGreen() - color2.getGreen() < tolerance.getGreen() && color1.getGreen() - color2.getGreen() > -tolerance.getGreen();
   }

   public static String getMainColor(String input) {
      return getMainColor(new File(input));
   }

   public static String getMainColor(File input) {
      try {
         return getMainColor(ImageIO.read(input));
      } catch (IOException var2) {
         var2.printStackTrace();
         return "";
      }
   }

   public static String getMainColor(BufferedImage bufferedImage) {
      if (bufferedImage == null) {
         throw new IllegalArgumentException("图片流是空的");
      } else {
         List<String> list = new ArrayList();

         for(int y = bufferedImage.getMinY(); y < bufferedImage.getHeight(); ++y) {
            for(int x = bufferedImage.getMinX(); x < bufferedImage.getWidth(); ++x) {
               int pixel = bufferedImage.getRGB(x, y);
               list.add(((pixel & 16711680) >> 16) + "-" + ((pixel & '\uff00') >> 8) + "-" + (pixel & 255));
            }
         }

         Map<String, Integer> map = new HashMap(list.size());

         Integer integer;
         String string;
         for(Iterator var11 = list.iterator(); var11.hasNext(); map.put(string, integer)) {
            string = (String)var11.next();
            integer = (Integer)map.get(string);
            if (integer == null) {
               integer = 1;
            } else {
               integer = integer + 1;
            }
         }

         String max = "";
         long num = 0L;
         Iterator var6 = map.entrySet().iterator();

         while(true) {
            String key;
            Integer temp;
            do {
               if (!var6.hasNext()) {
                  String[] strings = max.split("-");
                  int rgbLength = 3;
                  if (strings.length == rgbLength) {
                     return ImgUtil.toHex(Integer.parseInt(strings[0]), Integer.parseInt(strings[1]), Integer.parseInt(strings[2]));
                  }

                  return "";
               }

               Map.Entry<String, Integer> entry = (Map.Entry)var6.next();
               key = (String)entry.getKey();
               temp = (Integer)entry.getValue();
            } while(!StrUtil.isBlank(max) && (long)temp <= num);

            max = key;
            num = (long)temp;
         }
      }
   }

   private static boolean fileTypeValidation(File input, String[] imagesType) {
      if (!input.exists()) {
         throw new IllegalArgumentException("给定文件为空");
      } else {
         String type = FileTypeUtil.getType(input);
         if (!ArrayUtil.contains(imagesType, type)) {
            throw new IllegalArgumentException(StrUtil.format("文件类型{}不支持", new Object[]{type}));
         } else {
            return false;
         }
      }
   }
}
