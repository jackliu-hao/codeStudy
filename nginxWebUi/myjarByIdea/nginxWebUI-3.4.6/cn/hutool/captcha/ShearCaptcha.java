package cn.hutool.captcha;

import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

public class ShearCaptcha extends AbstractCaptcha {
   private static final long serialVersionUID = -7096627300356535494L;

   public ShearCaptcha(int width, int height) {
      this(width, height, 5);
   }

   public ShearCaptcha(int width, int height, int codeCount) {
      this(width, height, codeCount, 4);
   }

   public ShearCaptcha(int width, int height, int codeCount, int thickness) {
      super(width, height, codeCount, thickness);
   }

   public Image createImage(String code) {
      BufferedImage image = new BufferedImage(this.width, this.height, 1);
      Graphics2D g = GraphicsUtil.createGraphics(image, (Color)ObjectUtil.defaultIfNull(this.background, (Object)Color.WHITE));
      this.drawString(g, code);
      this.shear(g, this.width, this.height, (Color)ObjectUtil.defaultIfNull(this.background, (Object)Color.WHITE));
      this.drawInterfere(g, 0, RandomUtil.randomInt(this.height) + 1, this.width, RandomUtil.randomInt(this.height) + 1, this.interfereCount, ImgUtil.randomColor());
      return image;
   }

   private void drawString(Graphics2D g, String code) {
      if (null != this.textAlpha) {
         g.setComposite(this.textAlpha);
      }

      GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
   }

   private void shear(Graphics g, int w1, int h1, Color color) {
      this.shearX(g, w1, h1, color);
      this.shearY(g, w1, h1, color);
   }

   private void shearX(Graphics g, int w1, int h1, Color color) {
      int period = RandomUtil.randomInt(this.width);
      int frames = 1;
      int phase = RandomUtil.randomInt(2);

      for(int i = 0; i < h1; ++i) {
         double d = (double)(period >> 1) * Math.sin((double)i / (double)period + 6.283185307179586 * (double)phase / (double)frames);
         g.copyArea(0, i, w1, 1, (int)d, 0);
         g.setColor(color);
         g.drawLine((int)d, i, 0, i);
         g.drawLine((int)d + w1, i, w1, i);
      }

   }

   private void shearY(Graphics g, int w1, int h1, Color color) {
      int period = RandomUtil.randomInt(this.height >> 1);
      int frames = 20;
      int phase = 7;

      for(int i = 0; i < w1; ++i) {
         double d = (double)(period >> 1) * Math.sin((double)i / (double)period + 6.283185307179586 * (double)phase / (double)frames);
         g.copyArea(i, 0, 1, h1, 0, (int)d);
         g.setColor(color);
         g.drawLine(i, (int)d, i, 0);
         g.drawLine(i, (int)d + h1, i, h1);
      }

   }

   private void drawInterfere(Graphics g, int x1, int y1, int x2, int y2, int thickness, Color c) {
      g.setColor(c);
      int dX = x2 - x1;
      int dY = y2 - y1;
      double lineLength = Math.sqrt((double)(dX * dX + dY * dY));
      double scale = (double)thickness / (2.0 * lineLength);
      double ddx = -scale * (double)dY;
      double ddy = scale * (double)dX;
      ddx += ddx > 0.0 ? 0.5 : -0.5;
      ddy += ddy > 0.0 ? 0.5 : -0.5;
      int dx = (int)ddx;
      int dy = (int)ddy;
      int[] xPoints = new int[4];
      int[] yPoints = new int[4];
      xPoints[0] = x1 + dx;
      yPoints[0] = y1 + dy;
      xPoints[1] = x1 - dx;
      yPoints[1] = y1 - dy;
      xPoints[2] = x2 - dx;
      yPoints[2] = y2 - dy;
      xPoints[3] = x2 + dx;
      yPoints[3] = y2 + dy;
      g.fillPolygon(xPoints, yPoints, 4);
   }
}
