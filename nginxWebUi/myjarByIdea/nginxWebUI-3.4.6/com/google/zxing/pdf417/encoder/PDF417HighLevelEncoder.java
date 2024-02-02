package com.google.zxing.pdf417.encoder;

import com.google.zxing.WriterException;
import com.google.zxing.common.CharacterSetECI;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.Arrays;

final class PDF417HighLevelEncoder {
   private static final int TEXT_COMPACTION = 0;
   private static final int BYTE_COMPACTION = 1;
   private static final int NUMERIC_COMPACTION = 2;
   private static final int SUBMODE_ALPHA = 0;
   private static final int SUBMODE_LOWER = 1;
   private static final int SUBMODE_MIXED = 2;
   private static final int SUBMODE_PUNCTUATION = 3;
   private static final int LATCH_TO_TEXT = 900;
   private static final int LATCH_TO_BYTE_PADDED = 901;
   private static final int LATCH_TO_NUMERIC = 902;
   private static final int SHIFT_TO_BYTE = 913;
   private static final int LATCH_TO_BYTE = 924;
   private static final int ECI_USER_DEFINED = 925;
   private static final int ECI_GENERAL_PURPOSE = 926;
   private static final int ECI_CHARSET = 927;
   private static final byte[] TEXT_MIXED_RAW = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 38, 13, 9, 44, 58, 35, 45, 46, 36, 47, 43, 37, 42, 61, 94, 0, 32, 0, 0, 0};
   private static final byte[] TEXT_PUNCTUATION_RAW = new byte[]{59, 60, 62, 64, 91, 92, 93, 95, 96, 126, 33, 13, 9, 44, 58, 10, 45, 46, 36, 47, 34, 124, 42, 40, 41, 63, 123, 125, 39, 0};
   private static final byte[] MIXED = new byte[128];
   private static final byte[] PUNCTUATION = new byte[128];
   private static final Charset DEFAULT_ENCODING = Charset.forName("ISO-8859-1");

   private PDF417HighLevelEncoder() {
   }

   static String encodeHighLevel(String msg, Compaction compaction, Charset encoding) throws WriterException {
      StringBuilder sb = new StringBuilder(msg.length());
      if (encoding == null) {
         encoding = DEFAULT_ENCODING;
      } else {
         CharacterSetECI eci;
         if (!DEFAULT_ENCODING.equals(encoding) && (eci = CharacterSetECI.getCharacterSetECIByName(encoding.name())) != null) {
            encodingECI(eci.getValue(), sb);
         }
      }

      int len = msg.length();
      int p = 0;
      int textSubMode = 0;
      if (compaction == Compaction.TEXT) {
         encodeText(msg, 0, len, sb, 0);
      } else if (compaction == Compaction.BYTE) {
         byte[] bytes;
         encodeBinary(bytes = msg.getBytes(encoding), 0, bytes.length, 1, sb);
      } else if (compaction == Compaction.NUMERIC) {
         sb.append('Ά');
         encodeNumeric(msg, 0, len, sb);
      } else {
         int encodingMode = 0;

         while(true) {
            while(p < len) {
               int n;
               if ((n = determineConsecutiveDigitCount(msg, p)) >= 13) {
                  sb.append('Ά');
                  encodingMode = 2;
                  textSubMode = 0;
                  encodeNumeric(msg, p, n, sb);
                  p += n;
               } else {
                  int t;
                  if ((t = determineConsecutiveTextCount(msg, p)) < 5 && n != len) {
                     int b;
                     if ((b = determineConsecutiveBinaryCount(msg, p, encoding)) == 0) {
                        b = 1;
                     }

                     byte[] bytes;
                     if ((bytes = msg.substring(p, p + b).getBytes(encoding)).length == 1 && encodingMode == 0) {
                        encodeBinary(bytes, 0, 1, 0, sb);
                     } else {
                        encodeBinary(bytes, 0, bytes.length, encodingMode, sb);
                        encodingMode = 1;
                        textSubMode = 0;
                     }

                     p += b;
                  } else {
                     if (encodingMode != 0) {
                        sb.append('΄');
                        encodingMode = 0;
                        textSubMode = 0;
                     }

                     textSubMode = encodeText(msg, p, t, sb, textSubMode);
                     p += t;
                  }
               }
            }

            return sb.toString();
         }
      }

      return sb.toString();
   }

   private static int encodeText(CharSequence msg, int startpos, int count, StringBuilder sb, int initialSubmode) {
      StringBuilder tmp = new StringBuilder(count);
      int submode = initialSubmode;
      int idx = 0;

      while(true) {
         while(true) {
            char h = msg.charAt(startpos + idx);
            switch (submode) {
               case 0:
                  if (isAlphaUpper(h)) {
                     if (h == ' ') {
                        tmp.append('\u001a');
                     } else {
                        tmp.append((char)(h - 65));
                     }
                  } else {
                     if (isAlphaLower(h)) {
                        submode = 1;
                        tmp.append('\u001b');
                        continue;
                     }

                     if (isMixed(h)) {
                        submode = 2;
                        tmp.append('\u001c');
                        continue;
                     }

                     tmp.append('\u001d');
                     tmp.append((char)PUNCTUATION[h]);
                  }
                  break;
               case 1:
                  if (isAlphaLower(h)) {
                     if (h == ' ') {
                        tmp.append('\u001a');
                     } else {
                        tmp.append((char)(h - 97));
                     }
                  } else if (isAlphaUpper(h)) {
                     tmp.append('\u001b');
                     tmp.append((char)(h - 65));
                  } else {
                     if (isMixed(h)) {
                        submode = 2;
                        tmp.append('\u001c');
                        continue;
                     }

                     tmp.append('\u001d');
                     tmp.append((char)PUNCTUATION[h]);
                  }
                  break;
               case 2:
                  if (isMixed(h)) {
                     tmp.append((char)MIXED[h]);
                  } else {
                     if (isAlphaUpper(h)) {
                        submode = 0;
                        tmp.append('\u001c');
                        continue;
                     }

                     if (isAlphaLower(h)) {
                        submode = 1;
                        tmp.append('\u001b');
                        continue;
                     }

                     if (startpos + idx + 1 < count && isPunctuation(msg.charAt(startpos + idx + 1))) {
                        submode = 3;
                        tmp.append('\u0019');
                        continue;
                     }

                     tmp.append('\u001d');
                     tmp.append((char)PUNCTUATION[h]);
                  }
                  break;
               default:
                  if (!isPunctuation(h)) {
                     submode = 0;
                     tmp.append('\u001d');
                     continue;
                  }

                  tmp.append((char)PUNCTUATION[h]);
            }

            ++idx;
            if (idx >= count) {
               h = 0;
               int len = tmp.length();

               for(int i = 0; i < len; ++i) {
                  if (i % 2 != 0) {
                     h = (char)(h * 30 + tmp.charAt(i));
                     sb.append(h);
                  } else {
                     h = tmp.charAt(i);
                  }
               }

               if (len % 2 != 0) {
                  sb.append((char)(h * 30 + 29));
               }

               return submode;
            }
         }
      }
   }

   private static void encodeBinary(byte[] bytes, int startpos, int count, int startmode, StringBuilder sb) {
      if (count == 1 && startmode == 0) {
         sb.append('Α');
      } else if (count % 6 == 0) {
         sb.append('Μ');
      } else {
         sb.append('΅');
      }

      int idx = startpos;
      if (count >= 6) {
         for(char[] chars = new char[5]; startpos + count - idx >= 6; idx += 6) {
            long t = 0L;

            int i;
            for(i = 0; i < 6; ++i) {
               t = (t << 8) + (long)(bytes[idx + i] & 255);
            }

            for(i = 0; i < 5; ++i) {
               chars[i] = (char)((int)(t % 900L));
               t /= 900L;
            }

            for(i = 4; i >= 0; --i) {
               sb.append(chars[i]);
            }
         }
      }

      for(int i = idx; i < startpos + count; ++i) {
         int ch = bytes[i] & 255;
         sb.append((char)ch);
      }

   }

   private static void encodeNumeric(String msg, int startpos, int count, StringBuilder sb) {
      int idx = 0;
      StringBuilder tmp = new StringBuilder(count / 3 + 1);
      BigInteger num900 = BigInteger.valueOf(900L);

      int len;
      for(BigInteger num0 = BigInteger.valueOf(0L); idx < count; idx += len) {
         tmp.setLength(0);
         len = Math.min(44, count - idx);
         String part = "1" + msg.substring(startpos + idx, startpos + idx + len);
         BigInteger bigint = new BigInteger(part);

         do {
            tmp.append((char)bigint.mod(num900).intValue());
         } while(!(bigint = bigint.divide(num900)).equals(num0));

         for(int i = tmp.length() - 1; i >= 0; --i) {
            sb.append(tmp.charAt(i));
         }
      }

   }

   private static boolean isDigit(char ch) {
      return ch >= '0' && ch <= '9';
   }

   private static boolean isAlphaUpper(char ch) {
      return ch == ' ' || ch >= 'A' && ch <= 'Z';
   }

   private static boolean isAlphaLower(char ch) {
      return ch == ' ' || ch >= 'a' && ch <= 'z';
   }

   private static boolean isMixed(char ch) {
      return MIXED[ch] != -1;
   }

   private static boolean isPunctuation(char ch) {
      return PUNCTUATION[ch] != -1;
   }

   private static boolean isText(char ch) {
      return ch == '\t' || ch == '\n' || ch == '\r' || ch >= ' ' && ch <= '~';
   }

   private static int determineConsecutiveDigitCount(CharSequence msg, int startpos) {
      int count = 0;
      int len = msg.length();
      int idx = startpos;
      if (startpos < len) {
         char ch = msg.charAt(startpos);

         while(isDigit(ch) && idx < len) {
            ++count;
            ++idx;
            if (idx < len) {
               ch = msg.charAt(idx);
            }
         }
      }

      return count;
   }

   private static int determineConsecutiveTextCount(CharSequence msg, int startpos) {
      int len = msg.length();
      int idx = startpos;

      while(idx < len) {
         char ch = msg.charAt(idx);
         int numericCount = 0;

         while(numericCount < 13 && isDigit(ch) && idx < len) {
            ++numericCount;
            ++idx;
            if (idx < len) {
               ch = msg.charAt(idx);
            }
         }

         if (numericCount >= 13) {
            return idx - startpos - numericCount;
         }

         if (numericCount <= 0) {
            if (!isText(msg.charAt(idx))) {
               break;
            }

            ++idx;
         }
      }

      return idx - startpos;
   }

   private static int determineConsecutiveBinaryCount(String msg, int startpos, Charset encoding) throws WriterException {
      CharsetEncoder encoder = encoding.newEncoder();
      int len = msg.length();

      int idx;
      for(idx = startpos; idx < len; ++idx) {
         char ch = msg.charAt(idx);

         int numericCount;
         int i;
         for(numericCount = 0; numericCount < 13 && isDigit(ch); ch = msg.charAt(i)) {
            ++numericCount;
            if ((i = idx + numericCount) >= len) {
               break;
            }
         }

         if (numericCount >= 13) {
            return idx - startpos;
         }

         ch = msg.charAt(idx);
         if (!encoder.canEncode(ch)) {
            throw new WriterException("Non-encodable character detected: " + ch + " (Unicode: " + ch + ')');
         }
      }

      return idx - startpos;
   }

   private static void encodingECI(int eci, StringBuilder sb) throws WriterException {
      if (eci >= 0 && eci < 900) {
         sb.append('Ο');
         sb.append((char)eci);
      } else if (eci < 810900) {
         sb.append('Ξ');
         sb.append((char)(eci / 900 - 1));
         sb.append((char)(eci % 900));
      } else if (eci < 811800) {
         sb.append('Ν');
         sb.append((char)(810900 - eci));
      } else {
         throw new WriterException("ECI number not in valid range from 0..811799, but was " + eci);
      }
   }

   static {
      Arrays.fill(MIXED, (byte)-1);

      int i;
      byte b;
      for(i = 0; i < TEXT_MIXED_RAW.length; ++i) {
         if ((b = TEXT_MIXED_RAW[i]) > 0) {
            MIXED[b] = (byte)i;
         }
      }

      Arrays.fill(PUNCTUATION, (byte)-1);

      for(i = 0; i < TEXT_PUNCTUATION_RAW.length; ++i) {
         if ((b = TEXT_PUNCTUATION_RAW[i]) > 0) {
            PUNCTUATION[b] = (byte)i;
         }
      }

   }
}
