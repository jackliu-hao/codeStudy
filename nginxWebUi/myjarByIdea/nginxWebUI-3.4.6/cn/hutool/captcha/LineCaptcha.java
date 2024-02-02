package cn.hutool.captcha;

import cn.hutool.core.img.GraphicsUtil;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.concurrent.ThreadLocalRandom;

public class LineCaptcha extends AbstractCaptcha {
   private static final long serialVersionUID = 8691294460763091089L;

   public LineCaptcha(int width, int height) {
      this(width, height, 5, 150);
   }

   public LineCaptcha(int width, int height, int codeCount, int lineCount) {
      super(width, height, codeCount, lineCount);
   }

   public Image createImage(String code) {
      BufferedImage image = new BufferedImage(this.width, this.height, 1);
      Graphics2D g = GraphicsUtil.createGraphics(image, (Color)ObjectUtil.defaultIfNull(this.background, (Object)Color.WHITE));
      this.drawInterfere(g);
      this.drawString(g, code);
      return image;
   }

   private void drawString(Graphics2D g, String code) {
      if (null != this.textAlpha) {
         g.setComposite(this.textAlpha);
      }

      GraphicsUtil.drawStringColourful(g, code, this.font, this.width, this.height);
   }

   private void drawInterfere(Graphics2D g) {
      ThreadLocalRandom random = RandomUtil.getRandom();

      for(int i = 0; i < this.interfereCount; ++i) {
         int xs = random.nextInt(this.width);
         int ys = random.nextInt(this.height);
         int xe = xs + random.nextInt(this.width / 8);
         int ye = ys + random.nextInt(this.height / 8);
         g.setColor(ImgUtil.randomColor(random));
         g.drawLine(xs, ys, xe, ye);
      }

   }
}
