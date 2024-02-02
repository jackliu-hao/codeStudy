package com.wf.captcha;

import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.GifEncoder;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.CubicCurve2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class GifCaptcha extends Captcha {
   public GifCaptcha() {
   }

   public GifCaptcha(int width, int height) {
      this.setWidth(width);
      this.setHeight(height);
   }

   public GifCaptcha(int width, int height, int len) {
      this(width, height);
      this.setLen(len);
   }

   public GifCaptcha(int width, int height, int len, Font font) {
      this(width, height, len);
      this.setFont(font);
   }

   public boolean out(OutputStream os) {
      try {
         char[] strs = this.textChar();
         Color[] fontColor = new Color[this.len];

         for(int i = 0; i < this.len; ++i) {
            fontColor[i] = this.color();
         }

         int x1 = 5;
         int y1 = num(5, this.height / 2);
         int x2 = this.width - 5;
         int y2 = num(this.height / 2, this.height - 5);
         int ctrlx = num(this.width / 4, this.width / 4 * 3);
         int ctrly = num(5, this.height - 5);
         int ctrlx1;
         if (num(2) == 0) {
            ctrlx1 = y1;
            y1 = y2;
            y2 = ctrlx1;
         }

         ctrlx1 = num(this.width / 4, this.width / 4 * 3);
         int ctrly1 = num(5, this.height - 5);
         int[][] besselXY = new int[][]{{x1, y1}, {ctrlx, ctrly}, {ctrlx1, ctrly1}, {x2, y2}};
         GifEncoder gifEncoder = new GifEncoder();
         gifEncoder.setQuality(180);
         gifEncoder.setDelay(100);
         gifEncoder.setRepeat(0);
         gifEncoder.start(os);

         for(int i = 0; i < this.len; ++i) {
            BufferedImage frame = this.graphicsImage(fontColor, strs, i, besselXY);
            gifEncoder.addFrame(frame);
            frame.flush();
         }

         gifEncoder.finish();
         boolean var27 = true;
         return var27;
      } catch (Exception var24) {
         var24.printStackTrace();
      } finally {
         try {
            os.close();
         } catch (IOException var23) {
            var23.printStackTrace();
         }

      }

      return false;
   }

   public String toBase64() {
      return this.toBase64("data:image/gif;base64,");
   }

   private BufferedImage graphicsImage(Color[] fontColor, char[] strs, int flag, int[][] besselXY) {
      BufferedImage image = new BufferedImage(this.width, this.height, 1);
      Graphics2D g2d = (Graphics2D)image.getGraphics();
      g2d.setColor(Color.WHITE);
      g2d.fillRect(0, 0, this.width, this.height);
      g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      g2d.setComposite(AlphaComposite.getInstance(3, 0.1F * (float)num(10)));
      this.drawOval(2, g2d);
      g2d.setComposite(AlphaComposite.getInstance(3, 0.7F));
      g2d.setStroke(new BasicStroke(1.2F, 0, 2));
      g2d.setColor(fontColor[0]);
      CubicCurve2D shape = new CubicCurve2D.Double((double)besselXY[0][0], (double)besselXY[0][1], (double)besselXY[1][0], (double)besselXY[1][1], (double)besselXY[2][0], (double)besselXY[2][1], (double)besselXY[3][0], (double)besselXY[3][1]);
      g2d.draw(shape);
      g2d.setFont(this.getFont());
      FontMetrics fontMetrics = g2d.getFontMetrics();
      int fW = this.width / strs.length;
      int fSp = (fW - (int)fontMetrics.getStringBounds("W", g2d).getWidth()) / 2;

      for(int i = 0; i < strs.length; ++i) {
         AlphaComposite ac3 = AlphaComposite.getInstance(3, this.getAlpha(flag, i));
         g2d.setComposite(ac3);
         g2d.setColor(fontColor[i]);
         int fY = this.height - (this.height - (int)fontMetrics.getStringBounds(String.valueOf(strs[i]), g2d).getHeight() >> 1);
         g2d.drawString(String.valueOf(strs[i]), i * fW + fSp + 3, fY - 3);
      }

      g2d.dispose();
      return image;
   }

   private float getAlpha(int i, int j) {
      int num = i + j;
      float r = 1.0F / (float)(this.len - 1);
      float s = (float)this.len * r;
      return num >= this.len ? (float)num * r - s : (float)num * r;
   }
}
