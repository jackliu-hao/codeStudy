package cn.hutool.core.img;

import cn.hutool.core.util.ObjectUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;

public class GraphicsUtil {
   public static Graphics2D createGraphics(BufferedImage image, Color color) {
      Graphics2D g = image.createGraphics();
      if (null != color) {
         g.setColor(color);
         g.fillRect(0, 0, image.getWidth(), image.getHeight());
      }

      return g;
   }

   public static int getCenterY(Graphics g, int backgroundHeight) {
      FontMetrics metrics = null;

      try {
         metrics = g.getFontMetrics();
      } catch (Exception var4) {
      }

      int y;
      if (null != metrics) {
         y = (backgroundHeight - metrics.getHeight()) / 2 + metrics.getAscent();
      } else {
         y = backgroundHeight / 3;
      }

      return y;
   }

   public static Graphics drawStringColourful(Graphics g, String str, Font font, int width, int height) {
      return drawString(g, str, font, (Color)null, width, height);
   }

   public static Graphics drawString(Graphics g, String str, Font font, Color color, int width, int height) {
      if (g instanceof Graphics2D) {
         ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }

      g.setFont(font);
      int midY = getCenterY(g, height);
      if (null != color) {
         g.setColor(color);
      }

      int len = str.length();
      int charWidth = width / len;

      for(int i = 0; i < len; ++i) {
         if (null == color) {
            g.setColor(ImgUtil.randomColor());
         }

         g.drawString(String.valueOf(str.charAt(i)), i * charWidth, midY);
      }

      return g;
   }

   public static Graphics drawString(Graphics g, String str, Font font, Color color, Rectangle rectangle) {
      int backgroundWidth = rectangle.width;
      int backgroundHeight = rectangle.height;

      Dimension dimension;
      try {
         dimension = FontUtil.getDimension(g.getFontMetrics(font), str);
      } catch (Exception var9) {
         dimension = new Dimension(backgroundWidth / 3, backgroundHeight / 3);
      }

      rectangle.setSize(dimension.width, dimension.height);
      Point point = ImgUtil.getPointBaseCentre(rectangle, backgroundWidth, backgroundHeight);
      return drawString(g, str, font, color, point);
   }

   public static Graphics drawString(Graphics g, String str, Font font, Color color, Point point) {
      if (g instanceof Graphics2D) {
         ((Graphics2D)g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      }

      g.setFont(font);
      g.setColor((Color)ObjectUtil.defaultIfNull(color, (Object)Color.BLACK));
      g.drawString(str, point.x, point.y);
      return g;
   }

   public static Graphics drawImg(Graphics g, Image img, Point point) {
      return drawImg(g, img, new Rectangle(point.x, point.y, img.getWidth((ImageObserver)null), img.getHeight((ImageObserver)null)));
   }

   public static Graphics drawImg(Graphics g, Image img, Rectangle rectangle) {
      g.drawImage(img, rectangle.x, rectangle.y, rectangle.width, rectangle.height, (ImageObserver)null);
      return g;
   }

   public static Graphics2D setAlpha(Graphics2D g, float alpha) {
      g.setComposite(AlphaComposite.getInstance(10, alpha));
      return g;
   }
}
