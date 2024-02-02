package cn.hutool.core.codec;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.StrUtil;

public class Base16Codec implements Encoder<byte[], char[]>, Decoder<CharSequence, byte[]> {
   public static final Base16Codec CODEC_LOWER = new Base16Codec(true);
   public static final Base16Codec CODEC_UPPER = new Base16Codec(false);
   private final char[] alphabets;

   public Base16Codec(boolean lowerCase) {
      this.alphabets = (lowerCase ? "0123456789abcdef" : "0123456789ABCDEF").toCharArray();
   }

   public char[] encode(byte[] data) {
      int len = data.length;
      char[] out = new char[len << 1];
      int i = 0;

      for(int j = 0; i < len; ++i) {
         out[j++] = this.alphabets[(240 & data[i]) >>> 4];
         out[j++] = this.alphabets[15 & data[i]];
      }

      return out;
   }

   public byte[] decode(CharSequence encoded) {
      if (StrUtil.isEmpty(encoded)) {
         return null;
      } else {
         CharSequence encoded = StrUtil.cleanBlank(encoded);
         int len = encoded.length();
         if ((len & 1) != 0) {
            encoded = "0" + encoded;
            len = encoded.length();
         }

         byte[] out = new byte[len >> 1];
         int i = 0;

         for(int j = 0; j < len; ++i) {
            int f = toDigit(encoded.charAt(j), j) << 4;
            ++j;
            f |= toDigit(encoded.charAt(j), j);
            ++j;
            out[i] = (byte)(f & 255);
         }

         return out;
      }
   }

   public String toUnicodeHex(char ch) {
      return "\\u" + this.alphabets[ch >> 12 & 15] + this.alphabets[ch >> 8 & 15] + this.alphabets[ch >> 4 & 15] + this.alphabets[ch & 15];
   }

   public void appendHex(StringBuilder builder, byte b) {
      int high = (b & 240) >>> 4;
      int low = b & 15;
      builder.append(this.alphabets[high]);
      builder.append(this.alphabets[low]);
   }

   private static int toDigit(char ch, int index) {
      int digit = Character.digit(ch, 16);
      if (digit < 0) {
         throw new UtilException("Illegal hexadecimal character {} at index {}", new Object[]{ch, index});
      } else {
         return digit;
      }
   }
}
