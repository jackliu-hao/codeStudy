package cn.hutool.extra.qrcode;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.CharsetUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.nio.charset.Charset;
import java.util.HashMap;

public class QrConfig {
   private static final int BLACK = -16777216;
   private static final int WHITE = -1;
   protected int width;
   protected int height;
   protected int foreColor;
   protected Integer backColor;
   protected Integer margin;
   protected Integer qrVersion;
   protected ErrorCorrectionLevel errorCorrection;
   protected Charset charset;
   protected Image img;
   protected int ratio;

   public static QrConfig create() {
      return new QrConfig();
   }

   public QrConfig() {
      this(300, 300);
   }

   public QrConfig(int width, int height) {
      this.foreColor = -16777216;
      this.backColor = -1;
      this.margin = 2;
      this.errorCorrection = ErrorCorrectionLevel.M;
      this.charset = CharsetUtil.CHARSET_UTF_8;
      this.ratio = 6;
      this.width = width;
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public QrConfig setWidth(int width) {
      this.width = width;
      return this;
   }

   public int getHeight() {
      return this.height;
   }

   public QrConfig setHeight(int height) {
      this.height = height;
      return this;
   }

   public int getForeColor() {
      return this.foreColor;
   }

   /** @deprecated */
   @Deprecated
   public QrConfig setForeColor(int foreColor) {
      this.foreColor = foreColor;
      return this;
   }

   public QrConfig setForeColor(Color foreColor) {
      if (null != foreColor) {
         this.foreColor = foreColor.getRGB();
      }

      return this;
   }

   public int getBackColor() {
      return this.backColor;
   }

   /** @deprecated */
   @Deprecated
   public QrConfig setBackColor(int backColor) {
      this.backColor = backColor;
      return this;
   }

   public QrConfig setBackColor(Color backColor) {
      if (null == backColor) {
         this.backColor = null;
      } else {
         this.backColor = backColor.getRGB();
      }

      return this;
   }

   public Integer getMargin() {
      return this.margin;
   }

   public QrConfig setMargin(Integer margin) {
      this.margin = margin;
      return this;
   }

   public Integer getQrVersion() {
      return this.qrVersion;
   }

   public QrConfig setQrVersion(Integer qrVersion) {
      this.qrVersion = qrVersion;
      return this;
   }

   public ErrorCorrectionLevel getErrorCorrection() {
      return this.errorCorrection;
   }

   public QrConfig setErrorCorrection(ErrorCorrectionLevel errorCorrection) {
      this.errorCorrection = errorCorrection;
      return this;
   }

   public Charset getCharset() {
      return this.charset;
   }

   public QrConfig setCharset(Charset charset) {
      this.charset = charset;
      return this;
   }

   public Image getImg() {
      return this.img;
   }

   public QrConfig setImg(String imgPath) {
      return this.setImg(FileUtil.file(imgPath));
   }

   public QrConfig setImg(File imgFile) {
      return this.setImg((Image)ImgUtil.read(imgFile));
   }

   public QrConfig setImg(Image img) {
      this.img = img;
      return this;
   }

   public int getRatio() {
      return this.ratio;
   }

   public QrConfig setRatio(int ratio) {
      this.ratio = ratio;
      return this;
   }

   public HashMap<EncodeHintType, Object> toHints() {
      return this.toHints(BarcodeFormat.QR_CODE);
   }

   public HashMap<EncodeHintType, Object> toHints(BarcodeFormat format) {
      HashMap<EncodeHintType, Object> hints = new HashMap();
      if (null != this.charset) {
         hints.put(EncodeHintType.CHARACTER_SET, this.charset.toString().toLowerCase());
      }

      if (null != this.errorCorrection) {
         Object value;
         if (BarcodeFormat.AZTEC != format && BarcodeFormat.PDF_417 != format) {
            value = this.errorCorrection;
         } else {
            value = this.errorCorrection.getBits();
         }

         hints.put(EncodeHintType.ERROR_CORRECTION, value);
      }

      if (null != this.margin) {
         hints.put(EncodeHintType.MARGIN, this.margin);
      }

      if (null != this.qrVersion) {
         hints.put(EncodeHintType.QR_VERSION, this.qrVersion);
      }

      return hints;
   }
}
