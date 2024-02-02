package com.wf.captcha.base;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics2D;
import java.awt.geom.CubicCurve2D;
import java.awt.geom.QuadCurve2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public abstract class Captcha extends Randoms {
   public static final int[][] COLOR = new int[][]{{0, 135, 255}, {51, 153, 51}, {255, 102, 102}, {255, 153, 0}, {153, 102, 0}, {153, 102, 153}, {51, 153, 153}, {102, 102, 255}, {0, 102, 204}, {204, 51, 51}, {0, 153, 204}, {0, 51, 102}};
   public static final int TYPE_DEFAULT = 1;
   public static final int TYPE_ONLY_NUMBER = 2;
   public static final int TYPE_ONLY_CHAR = 3;
   public static final int TYPE_ONLY_UPPER = 4;
   public static final int TYPE_ONLY_LOWER = 5;
   public static final int TYPE_NUM_AND_UPPER = 6;
   public static final int FONT_1 = 0;
   public static final int FONT_2 = 1;
   public static final int FONT_3 = 2;
   public static final int FONT_4 = 3;
   public static final int FONT_5 = 4;
   public static final int FONT_6 = 5;
   public static final int FONT_7 = 6;
   public static final int FONT_8 = 7;
   public static final int FONT_9 = 8;
   public static final int FONT_10 = 9;
   private static final String[] FONT_NAMES = new String[]{"actionj.ttf", "epilog.ttf", "fresnel.ttf", "headache.ttf", "lexo.ttf", "prefix.ttf", "progbot.ttf", "ransom.ttf", "robot.ttf", "scandal.ttf"};
   private Font font = null;
   protected int len = 5;
   protected int width = 130;
   protected int height = 48;
   protected int charType = 1;
   protected String chars = null;

   protected char[] alphas() {
      char[] cs = new char[this.len];

      for(int i = 0; i < this.len; ++i) {
         switch (this.charType) {
            case 2:
               cs[i] = alpha(8);
               break;
            case 3:
               cs[i] = alpha(8, charMaxIndex);
               break;
            case 4:
               cs[i] = alpha(8, 31);
               break;
            case 5:
               cs[i] = alpha(31, lowerMaxIndex);
               break;
            case 6:
               cs[i] = alpha(31);
               break;
            default:
               cs[i] = alpha();
         }
      }

      this.chars = new String(cs);
      return cs;
   }

   protected Color color(int fc, int bc) {
      if (fc > 255) {
         fc = 255;
      }

      if (bc > 255) {
         bc = 255;
      }

      int r = fc + num(bc - fc);
      int g = fc + num(bc - fc);
      int b = fc + num(bc - fc);
      return new Color(r, g, b);
   }

   protected Color color() {
      int[] color = COLOR[num(COLOR.length)];
      return new Color(color[0], color[1], color[2]);
   }

   public abstract boolean out(OutputStream var1);

   public abstract String toBase64();

   public String toBase64(String type) {
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      this.out(outputStream);
      return type + Base64.getEncoder().encodeToString(outputStream.toByteArray());
   }

   public String text() {
      this.checkAlpha();
      return this.chars;
   }

   public char[] textChar() {
      this.checkAlpha();
      return this.chars.toCharArray();
   }

   public void checkAlpha() {
      if (this.chars == null) {
         this.alphas();
      }

   }

   public void drawLine(int num, Graphics2D g) {
      this.drawLine(num, (Color)null, g);
   }

   public void drawLine(int num, Color color, Graphics2D g) {
      for(int i = 0; i < num; ++i) {
         g.setColor(color == null ? this.color() : color);
         int x1 = num(-10, this.width - 10);
         int y1 = num(5, this.height - 5);
         int x2 = num(10, this.width + 10);
         int y2 = num(2, this.height - 2);
         g.drawLine(x1, y1, x2, y2);
      }

   }

   public void drawOval(int num, Graphics2D g) {
      this.drawOval(num, (Color)null, g);
   }

   public void drawOval(int num, Color color, Graphics2D g) {
      for(int i = 0; i < num; ++i) {
         g.setColor(color == null ? this.color() : color);
         int w = 5 + num(10);
         g.drawOval(num(this.width - 25), num(this.height - 15), w, w);
      }

   }

   public void drawBesselLine(int num, Graphics2D g) {
      this.drawBesselLine(num, (Color)null, g);
   }

   public void drawBesselLine(int num, Color color, Graphics2D g) {
      for(int i = 0; i < num; ++i) {
         g.setColor(color == null ? this.color() : color);
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

         if (num(2) == 0) {
            QuadCurve2D shape = new QuadCurve2D.Double();
            shape.setCurve((double)x1, (double)y1, (double)ctrlx, (double)ctrly, (double)x2, (double)y2);
            g.draw(shape);
         } else {
            ctrlx1 = num(this.width / 4, this.width / 4 * 3);
            int ctrly1 = num(5, this.height - 5);
            CubicCurve2D shape = new CubicCurve2D.Double((double)x1, (double)y1, (double)ctrlx, (double)ctrly, (double)ctrlx1, (double)ctrly1, (double)x2, (double)y2);
            g.draw(shape);
         }
      }

   }

   public Font getFont() {
      if (this.font == null) {
         try {
            this.setFont(0);
         } catch (Exception var2) {
            this.setFont(new Font("Arial", 1, 32));
         }
      }

      return this.font;
   }

   public void setFont(Font font) {
      this.font = font;
   }

   public void setFont(int font) throws IOException, FontFormatException {
      this.setFont(font, 32.0F);
   }

   public void setFont(int font, float size) throws IOException, FontFormatException {
      this.setFont(font, 1, size);
   }

   public void setFont(int font, int style, float size) throws IOException, FontFormatException {
      this.font = Font.createFont(0, this.getClass().getResourceAsStream("/" + FONT_NAMES[font])).deriveFont(style, size);
   }

   public int getLen() {
      return this.len;
   }

   public void setLen(int len) {
      this.len = len;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getCharType() {
      return this.charType;
   }

   public void setCharType(int charType) {
      this.charType = charType;
   }
}
