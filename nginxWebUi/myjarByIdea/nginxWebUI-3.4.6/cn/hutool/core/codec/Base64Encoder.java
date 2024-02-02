package cn.hutool.core.codec;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import java.nio.charset.Charset;

public class Base64Encoder {
   private static final Charset DEFAULT_CHARSET;
   private static final byte[] STANDARD_ENCODE_TABLE;
   private static final byte[] URL_SAFE_ENCODE_TABLE;

   public static byte[] encode(byte[] arr, boolean lineSep) {
      return encode(arr, lineSep, false);
   }

   public static byte[] encodeUrlSafe(byte[] arr, boolean lineSep) {
      return encode(arr, lineSep, true);
   }

   public static String encode(CharSequence source) {
      return encode(source, DEFAULT_CHARSET);
   }

   public static String encodeUrlSafe(CharSequence source) {
      return encodeUrlSafe(source, DEFAULT_CHARSET);
   }

   public static String encode(CharSequence source, Charset charset) {
      return encode(StrUtil.bytes(source, charset));
   }

   public static String encodeUrlSafe(CharSequence source, Charset charset) {
      return encodeUrlSafe(StrUtil.bytes(source, charset));
   }

   public static String encode(byte[] source) {
      return StrUtil.str(encode(source, false), DEFAULT_CHARSET);
   }

   public static String encodeUrlSafe(byte[] source) {
      return StrUtil.str(encodeUrlSafe(source, false), DEFAULT_CHARSET);
   }

   public static String encodeStr(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
      return StrUtil.str(encode(arr, isMultiLine, isUrlSafe), DEFAULT_CHARSET);
   }

   public static byte[] encode(byte[] arr, boolean isMultiLine, boolean isUrlSafe) {
      if (null == arr) {
         return null;
      } else {
         int len = arr.length;
         if (len == 0) {
            return new byte[0];
         } else {
            int evenlen = len / 3 * 3;
            int cnt = (len - 1) / 3 + 1 << 2;
            int destlen = cnt + (isMultiLine ? (cnt - 1) / 76 << 1 : 0);
            byte[] dest = new byte[destlen];
            byte[] encodeTable = isUrlSafe ? URL_SAFE_ENCODE_TABLE : STANDARD_ENCODE_TABLE;
            int left = 0;
            int i = 0;
            int urlSafeLen = 0;

            while(left < evenlen) {
               int i = (arr[left++] & 255) << 16 | (arr[left++] & 255) << 8 | arr[left++] & 255;
               dest[i++] = encodeTable[i >>> 18 & 63];
               dest[i++] = encodeTable[i >>> 12 & 63];
               dest[i++] = encodeTable[i >>> 6 & 63];
               dest[i++] = encodeTable[i & 63];
               if (isMultiLine) {
                  ++urlSafeLen;
                  if (urlSafeLen == 19 && i < destlen - 2) {
                     dest[i++] = 13;
                     dest[i++] = 10;
                     urlSafeLen = 0;
                  }
               }
            }

            left = len - evenlen;
            if (left > 0) {
               i = (arr[evenlen] & 255) << 10 | (left == 2 ? (arr[len - 1] & 255) << 2 : 0);
               dest[destlen - 4] = encodeTable[i >> 12];
               dest[destlen - 3] = encodeTable[i >>> 6 & 63];
               if (isUrlSafe) {
                  urlSafeLen = destlen - 2;
                  if (2 == left) {
                     dest[destlen - 2] = encodeTable[i & 63];
                     ++urlSafeLen;
                  }

                  byte[] urlSafeDest = new byte[urlSafeLen];
                  System.arraycopy(dest, 0, urlSafeDest, 0, urlSafeLen);
                  return urlSafeDest;
               }

               dest[destlen - 2] = left == 2 ? encodeTable[i & 63] : 61;
               dest[destlen - 1] = 61;
            }

            return dest;
         }
      }
   }

   static {
      DEFAULT_CHARSET = CharsetUtil.CHARSET_UTF_8;
      STANDARD_ENCODE_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47};
      URL_SAFE_ENCODE_TABLE = new byte[]{65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 45, 95};
   }
}
