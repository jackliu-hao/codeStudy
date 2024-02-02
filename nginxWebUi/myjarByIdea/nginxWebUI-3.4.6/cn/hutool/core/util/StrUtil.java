package cn.hutool.core.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.text.StrFormatter;
import cn.hutool.core.text.StrPool;
import cn.hutool.core.text.TextSimilarity;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.Map;

public class StrUtil extends CharSequenceUtil implements StrPool {
   public static boolean isBlankIfStr(Object obj) {
      if (null == obj) {
         return true;
      } else {
         return obj instanceof CharSequence ? isBlank((CharSequence)obj) : false;
      }
   }

   public static boolean isEmptyIfStr(Object obj) {
      if (null == obj) {
         return true;
      } else if (obj instanceof CharSequence) {
         return 0 == ((CharSequence)obj).length();
      } else {
         return false;
      }
   }

   public static void trim(String[] strs) {
      if (null != strs) {
         for(int i = 0; i < strs.length; ++i) {
            String str = strs[i];
            if (null != str) {
               strs[i] = trim(str);
            }
         }

      }
   }

   public static String utf8Str(Object obj) {
      return str(obj, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static String str(Object obj, String charsetName) {
      return str(obj, Charset.forName(charsetName));
   }

   public static String str(Object obj, Charset charset) {
      if (null == obj) {
         return null;
      } else if (obj instanceof String) {
         return (String)obj;
      } else if (obj instanceof byte[]) {
         return str((byte[])((byte[])obj), charset);
      } else if (obj instanceof Byte[]) {
         return str((Byte[])((Byte[])obj), charset);
      } else if (obj instanceof ByteBuffer) {
         return str((ByteBuffer)obj, charset);
      } else {
         return ArrayUtil.isArray(obj) ? ArrayUtil.toString(obj) : obj.toString();
      }
   }

   public static String str(byte[] bytes, String charset) {
      return str(bytes, CharsetUtil.charset(charset));
   }

   public static String str(byte[] data, Charset charset) {
      if (data == null) {
         return null;
      } else {
         return null == charset ? new String(data) : new String(data, charset);
      }
   }

   public static String str(Byte[] bytes, String charset) {
      return str(bytes, CharsetUtil.charset(charset));
   }

   public static String str(Byte[] data, Charset charset) {
      if (data == null) {
         return null;
      } else {
         byte[] bytes = new byte[data.length];

         for(int i = 0; i < data.length; ++i) {
            Byte dataByte = data[i];
            bytes[i] = null == dataByte ? -1 : dataByte;
         }

         return str(bytes, charset);
      }
   }

   public static String str(ByteBuffer data, String charset) {
      return data == null ? null : str(data, CharsetUtil.charset(charset));
   }

   public static String str(ByteBuffer data, Charset charset) {
      if (null == charset) {
         charset = Charset.defaultCharset();
      }

      return charset.decode(data).toString();
   }

   public static String toString(Object obj) {
      return String.valueOf(obj);
   }

   public static String toStringOrNull(Object obj) {
      return null == obj ? null : obj.toString();
   }

   public static StringBuilder builder() {
      return new StringBuilder();
   }

   public static StrBuilder strBuilder() {
      return StrBuilder.create();
   }

   public static StringBuilder builder(int capacity) {
      return new StringBuilder(capacity);
   }

   public static StrBuilder strBuilder(int capacity) {
      return StrBuilder.create(capacity);
   }

   public static StringReader getReader(CharSequence str) {
      return null == str ? null : new StringReader(str.toString());
   }

   public static StringWriter getWriter() {
      return new StringWriter();
   }

   public static String reverse(String str) {
      return new String(ArrayUtil.reverse(str.toCharArray()));
   }

   public static String fillBefore(String str, char filledChar, int len) {
      return fill(str, filledChar, len, true);
   }

   public static String fillAfter(String str, char filledChar, int len) {
      return fill(str, filledChar, len, false);
   }

   public static String fill(String str, char filledChar, int len, boolean isPre) {
      int strLen = str.length();
      if (strLen > len) {
         return str;
      } else {
         String filledStr = repeat(filledChar, len - strLen);
         return isPre ? filledStr.concat(str) : str.concat(filledStr);
      }
   }

   public static double similar(String str1, String str2) {
      return TextSimilarity.similar(str1, str2);
   }

   public static String similar(String str1, String str2, int scale) {
      return TextSimilarity.similar(str1, str2, scale);
   }

   public static String uuid() {
      return IdUtil.randomUUID();
   }

   public static String format(CharSequence template, Map<?, ?> map) {
      return format(template, map, true);
   }

   public static String format(CharSequence template, Map<?, ?> map, boolean ignoreNull) {
      return StrFormatter.format(template, map, ignoreNull);
   }
}
