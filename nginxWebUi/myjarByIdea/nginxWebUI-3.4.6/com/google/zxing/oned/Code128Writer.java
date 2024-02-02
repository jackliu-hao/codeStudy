package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public final class Code128Writer extends OneDimensionalCodeWriter {
   private static final int CODE_START_B = 104;
   private static final int CODE_START_C = 105;
   private static final int CODE_CODE_B = 100;
   private static final int CODE_CODE_C = 99;
   private static final int CODE_STOP = 106;
   private static final char ESCAPE_FNC_1 = 'ñ';
   private static final char ESCAPE_FNC_2 = 'ò';
   private static final char ESCAPE_FNC_3 = 'ó';
   private static final char ESCAPE_FNC_4 = 'ô';
   private static final int CODE_FNC_1 = 102;
   private static final int CODE_FNC_2 = 97;
   private static final int CODE_FNC_3 = 96;
   private static final int CODE_FNC_4_B = 100;

   public BitMatrix encode(String contents, BarcodeFormat format, int width, int height, Map<EncodeHintType, ?> hints) throws WriterException {
      if (format != BarcodeFormat.CODE_128) {
         throw new IllegalArgumentException("Can only encode CODE_128, but got " + format);
      } else {
         return super.encode(contents, format, width, height, hints);
      }
   }

   public boolean[] encode(String contents) {
      int length;
      if ((length = contents.length()) > 0 && length <= 80) {
         int checkSum;
         for(int i = 0; i < length; ++i) {
            if ((checkSum = contents.charAt(i)) < 32 || checkSum > 126) {
               switch (checkSum) {
                  case 241:
                  case 242:
                  case 243:
                  case 244:
                     break;
                  default:
                     throw new IllegalArgumentException("Bad character in input: " + checkSum);
               }
            }
         }

         Collection<int[]> patterns = new ArrayList();
         checkSum = 0;
         int checkWeight = 1;
         int codeSet = 0;
         int position = 0;

         int codeWidth;
         while(position < length) {
            int patternIndex;
            if ((codeWidth = chooseCode(contents, position, codeSet)) == codeSet) {
               switch (contents.charAt(position)) {
                  case 'ñ':
                     patternIndex = 102;
                     break;
                  case 'ò':
                     patternIndex = 97;
                     break;
                  case 'ó':
                     patternIndex = 96;
                     break;
                  case 'ô':
                     patternIndex = 100;
                     break;
                  default:
                     if (codeSet == 100) {
                        patternIndex = contents.charAt(position) - 32;
                     } else {
                        patternIndex = Integer.parseInt(contents.substring(position, position + 2));
                        ++position;
                     }
               }

               ++position;
            } else {
               if (codeSet == 0) {
                  if (codeWidth == 100) {
                     patternIndex = 104;
                  } else {
                     patternIndex = 105;
                  }
               } else {
                  patternIndex = codeWidth;
               }

               codeSet = codeWidth;
            }

            patterns.add(Code128Reader.CODE_PATTERNS[patternIndex]);
            checkSum += patternIndex * checkWeight;
            if (position != 0) {
               ++checkWeight;
            }
         }

         checkSum %= 103;
         patterns.add(Code128Reader.CODE_PATTERNS[checkSum]);
         patterns.add(Code128Reader.CODE_PATTERNS[106]);
         codeWidth = 0;
         Iterator var17 = patterns.iterator();

         while(var17.hasNext()) {
            int[] var11;
            int var12 = (var11 = (int[])var17.next()).length;

            for(int var13 = 0; var13 < var12; ++var13) {
               int width = var11[var13];
               codeWidth += width;
            }
         }

         boolean[] result = new boolean[codeWidth];
         int pos = 0;

         int[] pattern;
         for(Iterator var16 = patterns.iterator(); var16.hasNext(); pos += appendPattern(result, pos, pattern, true)) {
            pattern = (int[])var16.next();
         }

         return result;
      } else {
         throw new IllegalArgumentException("Contents length should be between 1 and 80 characters, but got " + length);
      }
   }

   private static CType findCType(CharSequence value, int start) {
      int last = value.length();
      if (start >= last) {
         return Code128Writer.CType.UNCODABLE;
      } else {
         char c;
         if ((c = value.charAt(start)) == 241) {
            return Code128Writer.CType.FNC_1;
         } else if (c >= '0' && c <= '9') {
            if (start + 1 >= last) {
               return Code128Writer.CType.ONE_DIGIT;
            } else {
               return (c = value.charAt(start + 1)) >= '0' && c <= '9' ? Code128Writer.CType.TWO_DIGITS : Code128Writer.CType.ONE_DIGIT;
            }
         } else {
            return Code128Writer.CType.UNCODABLE;
         }
      }
   }

   private static int chooseCode(CharSequence value, int start, int oldCode) {
      CType lookahead;
      if ((lookahead = findCType(value, start)) != Code128Writer.CType.UNCODABLE && lookahead != Code128Writer.CType.ONE_DIGIT) {
         if (oldCode == 99) {
            return oldCode;
         } else if (oldCode == 100) {
            if (lookahead == Code128Writer.CType.FNC_1) {
               return oldCode;
            } else if ((lookahead = findCType(value, start + 2)) != Code128Writer.CType.UNCODABLE && lookahead != Code128Writer.CType.ONE_DIGIT) {
               if (lookahead == Code128Writer.CType.FNC_1) {
                  return findCType(value, start + 3) == Code128Writer.CType.TWO_DIGITS ? 99 : 100;
               } else {
                  for(int index = start + 4; (lookahead = findCType(value, index)) == Code128Writer.CType.TWO_DIGITS; index += 2) {
                  }

                  return lookahead == Code128Writer.CType.ONE_DIGIT ? 100 : 99;
               }
            } else {
               return oldCode;
            }
         } else {
            if (lookahead == Code128Writer.CType.FNC_1) {
               lookahead = findCType(value, start + 1);
            }

            return lookahead == Code128Writer.CType.TWO_DIGITS ? 99 : 100;
         }
      } else {
         return 100;
      }
   }

   private static enum CType {
      UNCODABLE,
      ONE_DIGIT,
      TWO_DIGITS,
      FNC_1;
   }
}
