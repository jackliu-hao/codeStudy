package cn.hutool.captcha;

import cn.hutool.core.img.gif.AnimatedGifEncoder;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

public class GifCaptcha extends AbstractCaptcha {
   private static final long serialVersionUID = 7091627304326538464L;
   private int quality;
   private int repeat;
   private int minColor;
   private int maxColor;

   public GifCaptcha(int width, int height) {
      this(width, height, 5);
   }

   public GifCaptcha(int width, int height, int codeCount) {
      super(width, height, codeCount, 10);
      this.quality = 10;
      this.repeat = 0;
      this.minColor = 0;
      this.maxColor = 255;
   }

   public GifCaptcha setQuality(int quality) {
      if (quality < 1) {
         quality = 1;
      }

      this.quality = quality;
      return this;
   }

   public GifCaptcha setRepeat(int repeat) {
      if (repeat >= 0) {
         this.repeat = repeat;
      }

      return this;
   }

   public GifCaptcha setMaxColor(int maxColor) {
      this.maxColor = maxColor;
      return this;
   }

   public GifCaptcha setMinColor(int minColor) {
      this.minColor = minColor;
      return this;
   }

   public void createCode() {
      this.generateCode();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      AnimatedGifEncoder gifEncoder = new AnimatedGifEncoder();
      gifEncoder.start((OutputStream)out);
      gifEncoder.setQuality(this.quality);
      int delay = 100;
      gifEncoder.setDelay(delay);
      gifEncoder.setRepeat(this.repeat);
      char[] chars = this.code.toCharArray();
      Color[] fontColor = new Color[chars.length];

      for(int i = 0; i < chars.length; ++i) {
         fontColor[i] = this.getRandomColor(this.minColor, this.maxColor);
         BufferedImage frame = this.graphicsImage(chars, fontColor, chars, i);
         gifEncoder.addFrame(frame);
         frame.flush();
      }

      gifEncoder.finish();
      this.imageBytes = out.toByteArray();
   }

   protected Image createImage(String code) {
      return null;
   }

   private BufferedImage graphicsImage(char[] chars, Color[] fontColor, char[] words, int flag) {
      BufferedImage image = new BufferedImage(this.width, this.height, 1);
      Graphics2D g2d = image.createGraphics();
      g2d.setColor((Color)ObjectUtil.defaultIfNull(this.background, (Object)Color.WHITE));
      g2d.fillRect(0, 0, this.width, this.height);
      float y = (float)((this.height >> 1) + (this.font.getSize() >> 1));
      float m = 1.0F * (float)(this.width - chars.length * this.font.getSize()) / (float)chars.length;
      float x = Math.max(m / 2.0F, 2.0F);
      g2d.setFont(this.font);
      if (null != this.textAlpha) {
         g2d.setComposite(this.textAlpha);
      }

      for(int i = 0; i < chars.length; ++i) {
         AlphaComposite ac = AlphaComposite.getInstance(3, this.getAlpha(chars.length, flag, i));
         g2d.setComposite(ac);
         g2d.setColor(fontColor[i]);
         g2d.drawOval(RandomUtil.randomInt(this.width), RandomUtil.randomInt(this.height), RandomUtil.randomInt(5, 30), 5 + RandomUtil.randomInt(5, 30));
         g2d.drawString(words[i] + "", x + ((float)this.font.getSize() + m) * (float)i, y);
      }

      g2d.dispose();
      return image;
   }

   private float getAlpha(int v, int i, int j) {
      int num = i + j;
      float r = 1.0F / (float)v;
      float s = (float)(v + 1) * r;
      return num > v ? (float)num * r - s : (float)num * r;
   }

   private Color getRandomColor(int min, int max) {
      if (min > 255) {
         min = 255;
      }

      if (max > 255) {
         max = 255;
      }

      if (min < 0) {
         min = 0;
      }

      if (max < 0) {
         max = 0;
      }

      if (min > max) {
         min = 0;
         max = 255;
      }

      return new Color(RandomUtil.randomInt(min, max), RandomUtil.randomInt(min, max), RandomUtil.randomInt(min, max));
   }
}
