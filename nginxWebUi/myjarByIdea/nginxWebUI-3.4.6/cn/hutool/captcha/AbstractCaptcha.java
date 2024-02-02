package cn.hutool.captcha;

import cn.hutool.captcha.generator.CodeGenerator;
import cn.hutool.captcha.generator.RandomGenerator;
import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.URLUtil;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractCaptcha implements ICaptcha {
   private static final long serialVersionUID = 3180820918087507254L;
   protected int width;
   protected int height;
   protected int interfereCount;
   protected Font font;
   protected String code;
   protected byte[] imageBytes;
   protected CodeGenerator generator;
   protected Color background;
   protected AlphaComposite textAlpha;

   public AbstractCaptcha(int width, int height, int codeCount, int interfereCount) {
      this(width, height, new RandomGenerator(codeCount), interfereCount);
   }

   public AbstractCaptcha(int width, int height, CodeGenerator generator, int interfereCount) {
      this.width = width;
      this.height = height;
      this.generator = generator;
      this.interfereCount = interfereCount;
      this.font = new Font("SansSerif", 0, (int)((double)this.height * 0.75));
   }

   public void createCode() {
      this.generateCode();
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      ImgUtil.writePng(this.createImage(this.code), (OutputStream)out);
      this.imageBytes = out.toByteArray();
   }

   protected void generateCode() {
      this.code = this.generator.generate();
   }

   protected abstract Image createImage(String var1);

   public String getCode() {
      if (null == this.code) {
         this.createCode();
      }

      return this.code;
   }

   public boolean verify(String userInputCode) {
      return this.generator.verify(this.getCode(), userInputCode);
   }

   public void write(String path) throws IORuntimeException {
      this.write(FileUtil.touch(path));
   }

   public void write(File file) throws IORuntimeException {
      try {
         OutputStream out = FileUtil.getOutputStream(file);
         Throwable var3 = null;

         try {
            this.write((OutputStream)out);
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (out != null) {
               if (var3 != null) {
                  try {
                     out.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  out.close();
               }
            }

         }

      } catch (IOException var15) {
         throw new IORuntimeException(var15);
      }
   }

   public void write(OutputStream out) {
      IoUtil.write(out, false, this.getImageBytes());
   }

   public byte[] getImageBytes() {
      if (null == this.imageBytes) {
         this.createCode();
      }

      return this.imageBytes;
   }

   public BufferedImage getImage() {
      return ImgUtil.read((InputStream)IoUtil.toStream(this.getImageBytes()));
   }

   public String getImageBase64() {
      return Base64.encode(this.getImageBytes());
   }

   public String getImageBase64Data() {
      return URLUtil.getDataUriBase64("image/png", this.getImageBase64());
   }

   public void setFont(Font font) {
      this.font = font;
   }

   public CodeGenerator getGenerator() {
      return this.generator;
   }

   public void setGenerator(CodeGenerator generator) {
      this.generator = generator;
   }

   public void setBackground(Color background) {
      this.background = background;
   }

   public void setTextAlpha(float textAlpha) {
      this.textAlpha = AlphaComposite.getInstance(3, textAlpha);
   }
}
