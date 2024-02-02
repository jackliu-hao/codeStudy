package cn.hutool.core.codec;

import cn.hutool.core.lang.mutable.MutableInt;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

public class Base64Decoder {
   private static final Charset DEFAULT_CHARSET;
   private static final byte PADDING = -2;
   private static final byte[] DECODE_TABLE;

   public static String decodeStr(CharSequence source) {
      return decodeStr(source, DEFAULT_CHARSET);
   }

   public static String decodeStr(CharSequence source, Charset charset) {
      return StrUtil.str(decode(source), charset);
   }

   public static byte[] decode(CharSequence source) {
      return decode(StrUtil.bytes(source, DEFAULT_CHARSET));
   }

   public static byte[] decode(byte[] in) {
      return ArrayUtil.isEmpty((byte[])in) ? in : decode(in, 0, in.length);
   }

   public static byte[] decode(byte[] in, int pos, int length) {
      if (ArrayUtil.isEmpty((byte[])in)) {
         return in;
      } else {
         MutableInt offset = new MutableInt(pos);
         int maxPos = pos + length - 1;
         int octetId = 0;
         byte[] octet = new byte[length * 3 / 4];

         while(offset.intValue() <= maxPos) {
            byte sestet0 = getNextValidDecodeByte(in, offset, maxPos);
            byte sestet1 = getNextValidDecodeByte(in, offset, maxPos);
            byte sestet2 = getNextValidDecodeByte(in, offset, maxPos);
            byte sestet3 = getNextValidDecodeByte(in, offset, maxPos);
            if (-2 != sestet1) {
               octet[octetId++] = (byte)(sestet0 << 2 | sestet1 >>> 4);
            }

            if (-2 != sestet2) {
               octet[octetId++] = (byte)((sestet1 & 15) << 4 | sestet2 >>> 2);
            }

            if (-2 != sestet3) {
               octet[octetId++] = (byte)((sestet2 & 3) << 6 | sestet3);
            }
         }

         if (octetId == octet.length) {
            return octet;
         } else {
            return (byte[])((byte[])ArrayUtil.copy(octet, new byte[octetId], octetId));
         }
      }
   }

   public static boolean isBase64Code(byte octet) {
      return octet == 61 || octet >= 0 && octet < DECODE_TABLE.length && DECODE_TABLE[octet] != -1;
   }

   private static byte getNextValidDecodeByte(byte[] in, MutableInt pos, int maxPos) {
      while(true) {
         if (pos.intValue() <= maxPos) {
            byte base64Byte = in[pos.intValue()];
            pos.increment();
            if (base64Byte <= -1) {
               continue;
            }

            byte decodeByte = DECODE_TABLE[base64Byte];
            if (decodeByte <= -1) {
               continue;
            }

            return decodeByte;
         }

         return -2;
      }
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
      DECODE_TABLE = new byte[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 62, -1, 62, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, -2, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1, -1, -1, -1, 63, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51};
   }
}
