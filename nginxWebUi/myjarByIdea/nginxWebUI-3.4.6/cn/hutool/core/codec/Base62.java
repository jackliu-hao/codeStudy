package cn.hutool.core.codec;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

public class Base62 {
   private static final Charset DEFAULT_CHARSET;

   public static String encode(CharSequence source) {
      return encode(source, DEFAULT_CHARSET);
   }

   public static String encode(CharSequence source, Charset charset) {
      return encode(StrUtil.bytes(source, charset));
   }

   public static String encode(byte[] source) {
      return new String(Base62Codec.INSTANCE.encode(source));
   }

   public static String encode(InputStream in) {
      return encode(IoUtil.readBytes(in));
   }

   public static String encode(File file) {
      return encode(FileUtil.readBytes(file));
   }

   public static String encodeInverted(CharSequence source) {
      return encodeInverted(source, DEFAULT_CHARSET);
   }

   public static String encodeInverted(CharSequence source, Charset charset) {
      return encodeInverted(StrUtil.bytes(source, charset));
   }

   public static String encodeInverted(byte[] source) {
      return new String(Base62Codec.INSTANCE.encode(source, true));
   }

   public static String encodeInverted(InputStream in) {
      return encodeInverted(IoUtil.readBytes(in));
   }

   public static String encodeInverted(File file) {
      return encodeInverted(FileUtil.readBytes(file));
   }

   public static String decodeStrGbk(CharSequence source) {
      return decodeStr(source, CharsetUtil.CHARSET_GBK);
   }

   public static String decodeStr(CharSequence source) {
      return decodeStr(source, DEFAULT_CHARSET);
   }

   public static String decodeStr(CharSequence source, Charset charset) {
      return StrUtil.str(decode(source), charset);
   }

   public static File decodeToFile(CharSequence Base62, File destFile) {
      return FileUtil.writeBytes(decode(Base62), destFile);
   }

   public static void decodeToStream(CharSequence base62Str, OutputStream out, boolean isCloseOut) {
      IoUtil.write(out, isCloseOut, decode(base62Str));
   }

   public static byte[] decode(CharSequence base62Str) {
      return decode(StrUtil.bytes(base62Str, DEFAULT_CHARSET));
   }

   public static byte[] decode(byte[] base62bytes) {
      return Base62Codec.INSTANCE.decode(base62bytes);
   }

   public static String decodeStrInverted(CharSequence source) {
      return decodeStrInverted(source, DEFAULT_CHARSET);
   }

   public static String decodeStrInverted(CharSequence source, Charset charset) {
      return StrUtil.str(decodeInverted(source), charset);
   }

   public static File decodeToFileInverted(CharSequence Base62, File destFile) {
      return FileUtil.writeBytes(decodeInverted(Base62), destFile);
   }

   public static void decodeToStreamInverted(CharSequence base62Str, OutputStream out, boolean isCloseOut) {
      IoUtil.write(out, isCloseOut, decodeInverted(base62Str));
   }

   public static byte[] decodeInverted(CharSequence base62Str) {
      return decodeInverted(StrUtil.bytes(base62Str, DEFAULT_CHARSET));
   }

   public static byte[] decodeInverted(byte[] base62bytes) {
      return Base62Codec.INSTANCE.decode(base62bytes, true);
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
   }
}
