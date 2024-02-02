package cn.hutool.core.codec;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

public class Base32 {
   public static String encode(byte[] bytes) {
      return Base32Codec.INSTANCE.encode(bytes);
   }

   public static String encode(String source) {
      return encode(source, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encode(String source, Charset charset) {
      return encode(StrUtil.bytes(source, charset));
   }

   public static String encodeHex(byte[] bytes) {
      return Base32Codec.INSTANCE.encode(bytes, true);
   }

   public static String encodeHex(String source) {
      return encodeHex(source, CharsetUtil.CHARSET_UTF_8);
   }

   public static String encodeHex(String source, Charset charset) {
      return encodeHex(StrUtil.bytes(source, charset));
   }

   public static byte[] decode(String base32) {
      return Base32Codec.INSTANCE.decode((CharSequence)base32);
   }

   public static String decodeStr(String source) {
      return decodeStr(source, CharsetUtil.CHARSET_UTF_8);
   }

   public static String decodeStr(String source, Charset charset) {
      return StrUtil.str(decode(source), charset);
   }

   public static byte[] decodeHex(String base32) {
      return Base32Codec.INSTANCE.decode(base32, true);
   }

   public static String decodeStrHex(String source) {
      return decodeStrHex(source, CharsetUtil.CHARSET_UTF_8);
   }

   public static String decodeStrHex(String source, Charset charset) {
      return StrUtil.str(decodeHex(source), charset);
   }
}
