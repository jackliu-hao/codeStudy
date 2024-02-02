package com.google.zxing.oned;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.ResultPoint;
import com.google.zxing.common.BitArray;
import java.util.ArrayList;
import java.util.Map;

public final class Code128Reader extends OneDReader {
   static final int[][] CODE_PATTERNS = new int[][]{{2, 1, 2, 2, 2, 2}, {2, 2, 2, 1, 2, 2}, {2, 2, 2, 2, 2, 1}, {1, 2, 1, 2, 2, 3}, {1, 2, 1, 3, 2, 2}, {1, 3, 1, 2, 2, 2}, {1, 2, 2, 2, 1, 3}, {1, 2, 2, 3, 1, 2}, {1, 3, 2, 2, 1, 2}, {2, 2, 1, 2, 1, 3}, {2, 2, 1, 3, 1, 2}, {2, 3, 1, 2, 1, 2}, {1, 1, 2, 2, 3, 2}, {1, 2, 2, 1, 3, 2}, {1, 2, 2, 2, 3, 1}, {1, 1, 3, 2, 2, 2}, {1, 2, 3, 1, 2, 2}, {1, 2, 3, 2, 2, 1}, {2, 2, 3, 2, 1, 1}, {2, 2, 1, 1, 3, 2}, {2, 2, 1, 2, 3, 1}, {2, 1, 3, 2, 1, 2}, {2, 2, 3, 1, 1, 2}, {3, 1, 2, 1, 3, 1}, {3, 1, 1, 2, 2, 2}, {3, 2, 1, 1, 2, 2}, {3, 2, 1, 2, 2, 1}, {3, 1, 2, 2, 1, 2}, {3, 2, 2, 1, 1, 2}, {3, 2, 2, 2, 1, 1}, {2, 1, 2, 1, 2, 3}, {2, 1, 2, 3, 2, 1}, {2, 3, 2, 1, 2, 1}, {1, 1, 1, 3, 2, 3}, {1, 3, 1, 1, 2, 3}, {1, 3, 1, 3, 2, 1}, {1, 1, 2, 3, 1, 3}, {1, 3, 2, 1, 1, 3}, {1, 3, 2, 3, 1, 1}, {2, 1, 1, 3, 1, 3}, {2, 3, 1, 1, 1, 3}, {2, 3, 1, 3, 1, 1}, {1, 1, 2, 1, 3, 3}, {1, 1, 2, 3, 3, 1}, {1, 3, 2, 1, 3, 1}, {1, 1, 3, 1, 2, 3}, {1, 1, 3, 3, 2, 1}, {1, 3, 3, 1, 2, 1}, {3, 1, 3, 1, 2, 1}, {2, 1, 1, 3, 3, 1}, {2, 3, 1, 1, 3, 1}, {2, 1, 3, 1, 1, 3}, {2, 1, 3, 3, 1, 1}, {2, 1, 3, 1, 3, 1}, {3, 1, 1, 1, 2, 3}, {3, 1, 1, 3, 2, 1}, {3, 3, 1, 1, 2, 1}, {3, 1, 2, 1, 1, 3}, {3, 1, 2, 3, 1, 1}, {3, 3, 2, 1, 1, 1}, {3, 1, 4, 1, 1, 1}, {2, 2, 1, 4, 1, 1}, {4, 3, 1, 1, 1, 1}, {1, 1, 1, 2, 2, 4}, {1, 1, 1, 4, 2, 2}, {1, 2, 1, 1, 2, 4}, {1, 2, 1, 4, 2, 1}, {1, 4, 1, 1, 2, 2}, {1, 4, 1, 2, 2, 1}, {1, 1, 2, 2, 1, 4}, {1, 1, 2, 4, 1, 2}, {1, 2, 2, 1, 1, 4}, {1, 2, 2, 4, 1, 1}, {1, 4, 2, 1, 1, 2}, {1, 4, 2, 2, 1, 1}, {2, 4, 1, 2, 1, 1}, {2, 2, 1, 1, 1, 4}, {4, 1, 3, 1, 1, 1}, {2, 4, 1, 1, 1, 2}, {1, 3, 4, 1, 1, 1}, {1, 1, 1, 2, 4, 2}, {1, 2, 1, 1, 4, 2}, {1, 2, 1, 2, 4, 1}, {1, 1, 4, 2, 1, 2}, {1, 2, 4, 1, 1, 2}, {1, 2, 4, 2, 1, 1}, {4, 1, 1, 2, 1, 2}, {4, 2, 1, 1, 1, 2}, {4, 2, 1, 2, 1, 1}, {2, 1, 2, 1, 4, 1}, {2, 1, 4, 1, 2, 1}, {4, 1, 2, 1, 2, 1}, {1, 1, 1, 1, 4, 3}, {1, 1, 1, 3, 4, 1}, {1, 3, 1, 1, 4, 1}, {1, 1, 4, 1, 1, 3}, {1, 1, 4, 3, 1, 1}, {4, 1, 1, 1, 1, 3}, {4, 1, 1, 3, 1, 1}, {1, 1, 3, 1, 4, 1}, {1, 1, 4, 1, 3, 1}, {3, 1, 1, 1, 4, 1}, {4, 1, 1, 1, 3, 1}, {2, 1, 1, 4, 1, 2}, {2, 1, 1, 2, 1, 4}, {2, 1, 1, 2, 3, 2}, {2, 3, 3, 1, 1, 1, 2}};
   private static final float MAX_AVG_VARIANCE = 0.25F;
   private static final float MAX_INDIVIDUAL_VARIANCE = 0.7F;
   private static final int CODE_SHIFT = 98;
   private static final int CODE_CODE_C = 99;
   private static final int CODE_CODE_B = 100;
   private static final int CODE_CODE_A = 101;
   private static final int CODE_FNC_1 = 102;
   private static final int CODE_FNC_2 = 97;
   private static final int CODE_FNC_3 = 96;
   private static final int CODE_FNC_4_A = 101;
   private static final int CODE_FNC_4_B = 100;
   private static final int CODE_START_A = 103;
   private static final int CODE_START_B = 104;
   private static final int CODE_START_C = 105;
   private static final int CODE_STOP = 106;

   private static int[] findStartPattern(BitArray row) throws NotFoundException {
      int width = row.getSize();
      int rowOffset = row.getNextSet(0);
      int counterPosition = 0;
      int[] counters = new int[6];
      int patternStart = rowOffset;
      boolean isWhite = false;

      for(int i = rowOffset; i < width; ++i) {
         if (row.get(i) ^ isWhite) {
            int var10002 = counters[counterPosition]++;
         } else {
            if (counterPosition == 5) {
               float bestVariance = 0.25F;
               int bestMatch = -1;

               for(int startCode = 103; startCode <= 105; ++startCode) {
                  float variance;
                  if ((variance = patternMatchVariance(counters, CODE_PATTERNS[startCode], 0.7F)) < bestVariance) {
                     bestVariance = variance;
                     bestMatch = startCode;
                  }
               }

               if (bestMatch >= 0 && row.isRange(Math.max(0, patternStart - (i - patternStart) / 2), patternStart, false)) {
                  return new int[]{patternStart, i, bestMatch};
               }

               patternStart += counters[0] + counters[1];
               System.arraycopy(counters, 2, counters, 0, 4);
               counters[4] = 0;
               counters[5] = 0;
               --counterPosition;
            } else {
               ++counterPosition;
            }

            counters[counterPosition] = 1;
            isWhite = !isWhite;
         }
      }

      throw NotFoundException.getNotFoundInstance();
   }

   private static int decodeCode(BitArray row, int[] counters, int rowOffset) throws NotFoundException {
      recordPattern(row, rowOffset, counters);
      float bestVariance = 0.25F;
      int bestMatch = -1;

      for(int d = 0; d < CODE_PATTERNS.length; ++d) {
         int[] pattern = CODE_PATTERNS[d];
         float variance;
         if ((variance = patternMatchVariance(counters, pattern, 0.7F)) < bestVariance) {
            bestVariance = variance;
            bestMatch = d;
         }
      }

      if (bestMatch >= 0) {
         return bestMatch;
      } else {
         throw NotFoundException.getNotFoundInstance();
      }
   }

   public Result decodeRow(int rowNumber, BitArray row, Map<DecodeHintType, ?> hints) throws NotFoundException, FormatException, ChecksumException {
      boolean convertFNC1 = hints != null && hints.containsKey(DecodeHintType.ASSUME_GS1);
      int[] startPatternInfo;
      int startCode = (startPatternInfo = findStartPattern(row))[2];
      ArrayList rawCodes;
      (rawCodes = new ArrayList(20)).add((byte)startCode);
      int codeSet;
      switch (startCode) {
         case 103:
            codeSet = 101;
            break;
         case 104:
            codeSet = 100;
            break;
         case 105:
            codeSet = 99;
            break;
         default:
            throw FormatException.getFormatInstance();
      }

      boolean done = false;
      boolean isNextShifted = false;
      StringBuilder result = new StringBuilder(20);
      int lastStart = startPatternInfo[0];
      int nextStart = startPatternInfo[1];
      int[] counters = new int[6];
      int lastCode = 0;
      int code = 0;
      int checksumTotal = startCode;
      int multiplier = 0;
      boolean lastCharacterWasPrintable = true;
      boolean upperMode = false;
      boolean shiftUpperMode = false;

      int rawCodesSize;
      while(!done) {
         boolean unshift = isNextShifted;
         isNextShifted = false;
         lastCode = code;
         code = decodeCode(row, counters, nextStart);
         rawCodes.add((byte)code);
         if (code != 106) {
            lastCharacterWasPrintable = true;
         }

         if (code != 106) {
            ++multiplier;
            checksumTotal += multiplier * code;
         }

         lastStart = nextStart;
         int[] var23 = counters;

         for(int var25 = 0; var25 < 6; ++var25) {
            rawCodesSize = var23[var25];
            nextStart += rawCodesSize;
         }

         switch (code) {
            case 103:
            case 104:
            case 105:
               throw FormatException.getFormatInstance();
         }

         label200:
         switch (codeSet) {
            case 99:
               if (code < 100) {
                  if (code < 10) {
                     result.append('0');
                  }

                  result.append(code);
               } else {
                  if (code != 106) {
                     lastCharacterWasPrintable = false;
                  }

                  switch (code) {
                     case 100:
                        codeSet = 100;
                        break label200;
                     case 101:
                        codeSet = 101;
                        break label200;
                     case 102:
                        if (convertFNC1) {
                           if (result.length() == 0) {
                              result.append("]C1");
                           } else {
                              result.append('\u001d');
                           }
                        }
                     case 103:
                     case 104:
                     case 105:
                     default:
                        break label200;
                     case 106:
                        done = true;
                  }
               }
               break;
            case 100:
               if (code < 96) {
                  if (shiftUpperMode == upperMode) {
                     result.append((char)(code + 32));
                  } else {
                     result.append((char)(code + 32 + 128));
                  }

                  shiftUpperMode = false;
               } else {
                  if (code != 106) {
                     lastCharacterWasPrintable = false;
                  }

                  switch (code) {
                     case 96:
                     case 97:
                     case 103:
                     case 104:
                     case 105:
                     default:
                        break label200;
                     case 98:
                        isNextShifted = true;
                        codeSet = 101;
                        break label200;
                     case 99:
                        codeSet = 99;
                        break label200;
                     case 100:
                        if (!upperMode && shiftUpperMode) {
                           upperMode = true;
                           shiftUpperMode = false;
                        } else {
                           if (upperMode && shiftUpperMode) {
                              upperMode = false;
                              shiftUpperMode = false;
                              break label200;
                           }

                           shiftUpperMode = true;
                        }
                        break label200;
                     case 101:
                        codeSet = 101;
                        break label200;
                     case 102:
                        if (convertFNC1) {
                           if (result.length() == 0) {
                              result.append("]C1");
                           } else {
                              result.append('\u001d');
                           }
                        }
                        break label200;
                     case 106:
                        done = true;
                  }
               }
               break;
            case 101:
               if (code < 64) {
                  if (shiftUpperMode == upperMode) {
                     result.append((char)(code + 32));
                  } else {
                     result.append((char)(code + 32 + 128));
                  }

                  shiftUpperMode = false;
               } else if (code < 96) {
                  if (shiftUpperMode == upperMode) {
                     result.append((char)(code - 64));
                  } else {
                     result.append((char)(code + 64));
                  }

                  shiftUpperMode = false;
               } else {
                  if (code != 106) {
                     lastCharacterWasPrintable = false;
                  }

                  switch (code) {
                     case 96:
                     case 97:
                     case 103:
                     case 104:
                     case 105:
                     default:
                        break;
                     case 98:
                        isNextShifted = true;
                        codeSet = 100;
                        break;
                     case 99:
                        codeSet = 99;
                        break;
                     case 100:
                        codeSet = 100;
                        break;
                     case 101:
                        if (!upperMode && shiftUpperMode) {
                           upperMode = true;
                           shiftUpperMode = false;
                        } else {
                           if (upperMode && shiftUpperMode) {
                              upperMode = false;
                              shiftUpperMode = false;
                              break;
                           }

                           shiftUpperMode = true;
                        }
                        break;
                     case 102:
                        if (convertFNC1) {
                           if (result.length() == 0) {
                              result.append("]C1");
                           } else {
                              result.append('\u001d');
                           }
                        }
                        break;
                     case 106:
                        done = true;
                  }
               }
         }

         if (unshift) {
            codeSet = codeSet == 101 ? 100 : 101;
         }
      }

      int lastPatternSize = nextStart - lastStart;
      nextStart = row.getNextUnset(nextStart);
      if (!row.isRange(nextStart, Math.min(row.getSize(), nextStart + (nextStart - lastStart) / 2), false)) {
         throw NotFoundException.getNotFoundInstance();
      } else if ((checksumTotal - multiplier * lastCode) % 103 != lastCode) {
         throw ChecksumException.getChecksumInstance();
      } else {
         int resultLength;
         if ((resultLength = result.length()) == 0) {
            throw NotFoundException.getNotFoundInstance();
         } else {
            if (resultLength > 0 && lastCharacterWasPrintable) {
               if (codeSet == 99) {
                  result.delete(resultLength - 2, resultLength);
               } else {
                  result.delete(resultLength - 1, resultLength);
               }
            }

            float left = (float)(startPatternInfo[1] + startPatternInfo[0]) / 2.0F;
            float right = (float)lastStart + (float)lastPatternSize / 2.0F;
            byte[] rawBytes = new byte[rawCodesSize = rawCodes.size()];

            for(int i = 0; i < rawCodesSize; ++i) {
               rawBytes[i] = (Byte)rawCodes.get(i);
            }

            return new Result(result.toString(), rawBytes, new ResultPoint[]{new ResultPoint(left, (float)rowNumber), new ResultPoint(right, (float)rowNumber)}, BarcodeFormat.CODE_128);
         }
      }
   }
}
