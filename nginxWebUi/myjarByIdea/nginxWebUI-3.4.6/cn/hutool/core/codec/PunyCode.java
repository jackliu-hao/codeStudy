package cn.hutool.core.codec;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;

public class PunyCode {
   private static final int TMIN = 1;
   private static final int TMAX = 26;
   private static final int BASE = 36;
   private static final int INITIAL_N = 128;
   private static final int INITIAL_BIAS = 72;
   private static final int DAMP = 700;
   private static final int SKEW = 38;
   private static final char DELIMITER = '-';
   public static final String PUNY_CODE_PREFIX = "xn--";

   public static String encode(CharSequence input) throws UtilException {
      return encode(input, false);
   }

   public static String encode(CharSequence input, boolean withPrefix) throws UtilException {
      int n = 128;
      int delta = 0;
      int bias = 72;
      StringBuilder output = new StringBuilder();
      int length = input.length();
      int b = 0;

      int h;
      int m;
      for(h = 0; h < length; ++h) {
         m = input.charAt(h);
         if (isBasic((char)m)) {
            output.append((char)m);
            ++b;
         }
      }

      if (b > 0) {
         output.append('-');
      }

      for(h = b; h < length; ++n) {
         m = Integer.MAX_VALUE;

         int j;
         char c;
         for(j = 0; j < length; ++j) {
            c = input.charAt(j);
            if (c >= n && c < m) {
               m = c;
            }
         }

         if (m - n > (Integer.MAX_VALUE - delta) / (h + 1)) {
            throw new UtilException("OVERFLOW");
         }

         delta += (m - n) * (h + 1);
         n = m;

         for(j = 0; j < length; ++j) {
            c = input.charAt(j);
            if (c < n) {
               ++delta;
               if (0 == delta) {
                  throw new UtilException("OVERFLOW");
               }
            }

            if (c == n) {
               int q = delta;
               int k = 36;

               while(true) {
                  int t;
                  if (k <= bias) {
                     t = 1;
                  } else if (k >= bias + 26) {
                     t = 26;
                  } else {
                     t = k - bias;
                  }

                  if (q < t) {
                     output.append((char)digit2codepoint(q));
                     bias = adapt(delta, h + 1, h == b);
                     delta = 0;
                     ++h;
                     break;
                  }

                  output.append((char)digit2codepoint(t + (q - t) % (36 - t)));
                  q = (q - t) / (36 - t);
                  k += 36;
               }
            }
         }

         ++delta;
      }

      if (withPrefix) {
         output.insert(0, "xn--");
      }

      return output.toString();
   }

   public static String decode(String input) throws UtilException {
      input = StrUtil.removePrefixIgnoreCase(input, "xn--");
      int n = 128;
      int i = 0;
      int bias = 72;
      StringBuilder output = new StringBuilder();
      int d = input.lastIndexOf(45);
      int length;
      int oldi;
      if (d > 0) {
         for(length = 0; length < d; ++length) {
            oldi = input.charAt(length);
            if (isBasic((char)oldi)) {
               output.append((char)oldi);
            }
         }

         ++d;
      } else {
         d = 0;
      }

      length = input.length();

      label54:
      while(d < length) {
         oldi = i;
         int w = 1;

         for(int k = 36; d != length; k += 36) {
            int c = input.charAt(d++);
            int digit = codepoint2digit(c);
            if (digit > (Integer.MAX_VALUE - i) / w) {
               throw new UtilException("OVERFLOW");
            }

            i += digit * w;
            int t;
            if (k <= bias) {
               t = 1;
            } else if (k >= bias + 26) {
               t = 26;
            } else {
               t = k - bias;
            }

            if (digit < t) {
               bias = adapt(i - oldi, output.length() + 1, oldi == 0);
               if (i / (output.length() + 1) > Integer.MAX_VALUE - n) {
                  throw new UtilException("OVERFLOW");
               }

               n += i / (output.length() + 1);
               i %= output.length() + 1;
               output.insert(i, (char)n);
               ++i;
               continue label54;
            }

            w *= 36 - t;
         }

         throw new UtilException("BAD_INPUT");
      }

      return output.toString();
   }

   private static int adapt(int delta, int numpoints, boolean first) {
      if (first) {
         delta /= 700;
      } else {
         delta /= 2;
      }

      delta += delta / numpoints;

      int k;
      for(k = 0; delta > 455; k += 36) {
         delta /= 35;
      }

      return k + 36 * delta / (delta + 38);
   }

   private static boolean isBasic(char c) {
      return c < 128;
   }

   private static int digit2codepoint(int d) throws UtilException {
      Assert.checkBetween(d, 0, 35);
      if (d < 26) {
         return d + 97;
      } else if (d < 36) {
         return d - 26 + 48;
      } else {
         throw new UtilException("BAD_INPUT");
      }
   }

   private static int codepoint2digit(int c) throws UtilException {
      if (c - 48 < 10) {
         return c - 48 + 26;
      } else if (c - 97 < 26) {
         return c - 97;
      } else {
         throw new UtilException("BAD_INPUT");
      }
   }
}
