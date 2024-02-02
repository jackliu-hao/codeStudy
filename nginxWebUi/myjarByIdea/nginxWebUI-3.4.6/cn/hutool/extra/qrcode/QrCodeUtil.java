package cn.hutool.extra.qrcode;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.img.Img;
import cn.hutool.core.img.ImgUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Binarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.GlobalHistogramBinarizer;
import com.google.zxing.common.HybridBinarizer;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class QrCodeUtil {
   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, String logoBase64) {
      return generateAsBase64(content, qrConfig, imageType, Base64.decode((CharSequence)logoBase64));
   }

   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, byte[] logo) {
      return generateAsBase64(content, qrConfig, imageType, (Image)ImgUtil.toImage(logo));
   }

   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType, Image logo) {
      qrConfig.setImg(logo);
      return generateAsBase64(content, qrConfig, imageType);
   }

   public static String generateAsBase64(String content, QrConfig qrConfig, String imageType) {
      BufferedImage img = generate(content, qrConfig);
      return ImgUtil.toBase64DataUri(img, imageType);
   }

   public static byte[] generatePng(String content, int width, int height) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      generate(content, width, height, "png", out);
      return out.toByteArray();
   }

   public static byte[] generatePng(String content, QrConfig config) {
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      generate(content, config, "png", out);
      return out.toByteArray();
   }

   public static File generate(String content, int width, int height, File targetFile) {
      BufferedImage image = generate(content, width, height);
      ImgUtil.write(image, targetFile);
      return targetFile;
   }

   public static File generate(String content, QrConfig config, File targetFile) {
      BufferedImage image = generate(content, config);
      ImgUtil.write(image, targetFile);
      return targetFile;
   }

   public static void generate(String content, int width, int height, String imageType, OutputStream out) {
      BufferedImage image = generate(content, width, height);
      ImgUtil.write((Image)image, imageType, (OutputStream)out);
   }

   public static void generate(String content, QrConfig config, String imageType, OutputStream out) {
      BufferedImage image = generate(content, config);
      ImgUtil.write((Image)image, imageType, (OutputStream)out);
   }

   public static BufferedImage generate(String content, int width, int height) {
      return generate(content, new QrConfig(width, height));
   }

   public static BufferedImage generate(String content, BarcodeFormat format, int width, int height) {
      return generate(content, format, new QrConfig(width, height));
   }

   public static BufferedImage generate(String content, QrConfig config) {
      return generate(content, BarcodeFormat.QR_CODE, config);
   }

   public static BufferedImage generate(String content, BarcodeFormat format, QrConfig config) {
      BitMatrix bitMatrix = encode(content, format, config);
      BufferedImage image = toImage(bitMatrix, config.foreColor, config.backColor);
      Image logoImg = config.img;
      if (null != logoImg && BarcodeFormat.QR_CODE == format) {
         int qrWidth = image.getWidth();
         int qrHeight = image.getHeight();
         int width;
         int height;
         if (qrWidth < qrHeight) {
            width = qrWidth / config.ratio;
            height = logoImg.getHeight((ImageObserver)null) * width / logoImg.getWidth((ImageObserver)null);
         } else {
            height = qrHeight / config.ratio;
            width = logoImg.getWidth((ImageObserver)null) * height / logoImg.getHeight((ImageObserver)null);
         }

         Img.from((Image)image).pressImage(Img.from(logoImg).round(0.3).getImg(), new Rectangle(width, height), 1.0F);
      }

      return image;
   }

   public static BitMatrix encode(String content, int width, int height) {
      return encode(content, BarcodeFormat.QR_CODE, width, height);
   }

   public static BitMatrix encode(String content, QrConfig config) {
      return encode(content, BarcodeFormat.QR_CODE, config);
   }

   public static BitMatrix encode(String content, BarcodeFormat format, int width, int height) {
      return encode(content, format, new QrConfig(width, height));
   }

   public static BitMatrix encode(String content, BarcodeFormat format, QrConfig config) {
      MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
      if (null == config) {
         config = new QrConfig();
      }

      try {
         BitMatrix bitMatrix = multiFormatWriter.encode(content, format, config.width, config.height, config.toHints(format));
         return bitMatrix;
      } catch (WriterException var6) {
         throw new QrCodeException(var6);
      }
   }

   public static String decode(InputStream qrCodeInputstream) {
      return decode((Image)ImgUtil.read(qrCodeInputstream));
   }

   public static String decode(File qrCodeFile) {
      return decode((Image)ImgUtil.read(qrCodeFile));
   }

   public static String decode(Image image) {
      return decode(image, true, false);
   }

   public static String decode(Image image, boolean isTryHarder, boolean isPureBarcode) {
      return decode(image, buildHints(isTryHarder, isPureBarcode));
   }

   public static String decode(Image image, Map<DecodeHintType, Object> hints) {
      MultiFormatReader formatReader = new MultiFormatReader();
      formatReader.setHints(hints);
      LuminanceSource source = new BufferedImageLuminanceSource(ImgUtil.toBufferedImage(image));
      Result result = _decode(formatReader, new HybridBinarizer(source));
      if (null == result) {
         result = _decode(formatReader, new GlobalHistogramBinarizer(source));
      }

      return null != result ? result.getText() : null;
   }

   public static BufferedImage toImage(BitMatrix matrix, int foreColor, Integer backColor) {
      int width = matrix.getWidth();
      int height = matrix.getHeight();
      BufferedImage image = new BufferedImage(width, height, null == backColor ? 2 : 1);

      for(int x = 0; x < width; ++x) {
         for(int y = 0; y < height; ++y) {
            if (matrix.get(x, y)) {
               image.setRGB(x, y, foreColor);
            } else if (null != backColor) {
               image.setRGB(x, y, backColor);
            }
         }
      }

      return image;
   }

   private static Map<DecodeHintType, Object> buildHints(boolean isTryHarder, boolean isPureBarcode) {
      HashMap<DecodeHintType, Object> hints = new HashMap();
      hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
      if (isTryHarder) {
         hints.put(DecodeHintType.TRY_HARDER, true);
      }

      if (isPureBarcode) {
         hints.put(DecodeHintType.PURE_BARCODE, true);
      }

      return hints;
   }

   private static Result _decode(MultiFormatReader formatReader, Binarizer binarizer) {
      try {
         return formatReader.decodeWithState(new BinaryBitmap(binarizer));
      } catch (NotFoundException var3) {
         return null;
      }
   }
}
