package cn.hutool.core.util;

import cn.hutool.core.io.CharsetDetector;
import cn.hutool.core.io.FileUtil;
import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;

public class CharsetUtil {
   public static final String ISO_8859_1 = "ISO-8859-1";
   public static final String UTF_8 = "UTF-8";
   public static final String GBK = "GBK";
   public static final Charset CHARSET_ISO_8859_1;
   public static final Charset CHARSET_UTF_8;
   public static final Charset CHARSET_GBK;

   public static Charset charset(String charsetName) throws UnsupportedCharsetException {
      return StrUtil.isBlank(charsetName) ? Charset.defaultCharset() : Charset.forName(charsetName);
   }

   public static Charset parse(String charsetName) {
      return parse(charsetName, Charset.defaultCharset());
   }

   public static Charset parse(String charsetName, Charset defaultCharset) {
      if (StrUtil.isBlank(charsetName)) {
         return defaultCharset;
      } else {
         Charset result;
         try {
            result = Charset.forName(charsetName);
         } catch (UnsupportedCharsetException var4) {
            result = defaultCharset;
         }

         return result;
      }
   }

   public static String convert(String source, String srcCharset, String destCharset) {
      return convert(source, Charset.forName(srcCharset), Charset.forName(destCharset));
   }

   public static String convert(String source, Charset srcCharset, Charset destCharset) {
      if (null == srcCharset) {
         srcCharset = StandardCharsets.ISO_8859_1;
      }

      if (null == destCharset) {
         destCharset = StandardCharsets.UTF_8;
      }

      return !StrUtil.isBlank(source) && !srcCharset.equals(destCharset) ? new String(source.getBytes(srcCharset), destCharset) : source;
   }

   public static File convert(File file, Charset srcCharset, Charset destCharset) {
      String str = FileUtil.readString(file, srcCharset);
      return FileUtil.writeString(str, file, destCharset);
   }

   public static String systemCharsetName() {
      return systemCharset().name();
   }

   public static Charset systemCharset() {
      return FileUtil.isWindows() ? CHARSET_GBK : defaultCharset();
   }

   public static String defaultCharsetName() {
      return defaultCharset().name();
   }

   public static Charset defaultCharset() {
      return Charset.defaultCharset();
   }

   public static Charset defaultCharset(InputStream in, Charset... charsets) {
      return CharsetDetector.detect(in, charsets);
   }

   public static Charset defaultCharset(int bufferSize, InputStream in, Charset... charsets) {
      return CharsetDetector.detect(bufferSize, in, charsets);
   }

   static {
      CHARSET_ISO_8859_1 = StandardCharsets.ISO_8859_1;
      CHARSET_UTF_8 = StandardCharsets.UTF_8;
      Charset _CHARSET_GBK = null;

      try {
         _CHARSET_GBK = Charset.forName("GBK");
      } catch (UnsupportedCharsetException var2) {
      }

      CHARSET_GBK = _CHARSET_GBK;
   }
}
