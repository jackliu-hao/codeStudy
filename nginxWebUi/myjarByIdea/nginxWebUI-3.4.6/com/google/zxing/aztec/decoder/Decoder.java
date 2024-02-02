package com.google.zxing.aztec.decoder;

import com.google.zxing.FormatException;
import com.google.zxing.aztec.AztecDetectorResult;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import java.util.Arrays;
import java.util.List;

public final class Decoder {
   private static final String[] UPPER_TABLE = new String[]{"CTRL_PS", " ", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "CTRL_LL", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
   private static final String[] LOWER_TABLE = new String[]{"CTRL_PS", " ", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "CTRL_US", "CTRL_ML", "CTRL_DL", "CTRL_BS"};
   private static final String[] MIXED_TABLE = new String[]{"CTRL_PS", " ", "\u0001", "\u0002", "\u0003", "\u0004", "\u0005", "\u0006", "\u0007", "\b", "\t", "\n", "\u000b", "\f", "\r", "\u001b", "\u001c", "\u001d", "\u001e", "\u001f", "@", "\\", "^", "_", "`", "|", "~", "\u007f", "CTRL_LL", "CTRL_UL", "CTRL_PL", "CTRL_BS"};
   private static final String[] PUNCT_TABLE = new String[]{"", "\r", "\r\n", ". ", ", ", ": ", "!", "\"", "#", "$", "%", "&", "'", "(", ")", "*", "+", ",", "-", ".", "/", ":", ";", "<", "=", ">", "?", "[", "]", "{", "}", "CTRL_UL"};
   private static final String[] DIGIT_TABLE = new String[]{"CTRL_PS", " ", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ",", ".", "CTRL_UL", "CTRL_US"};
   private AztecDetectorResult ddata;

   public DecoderResult decode(AztecDetectorResult detectorResult) throws FormatException {
      this.ddata = detectorResult;
      BitMatrix matrix = detectorResult.getBits();
      boolean[] rawbits = this.extractBits(matrix);
      boolean[] correctedBits;
      byte[] rawBytes = convertBoolArrayToByteArray(correctedBits = this.correctBits(rawbits));
      String result = getEncodedData(correctedBits);
      DecoderResult decoderResult;
      (decoderResult = new DecoderResult(rawBytes, result, (List)null, (String)null)).setNumBits(correctedBits.length);
      return decoderResult;
   }

   public static String highLevelDecode(boolean[] correctedBits) {
      return getEncodedData(correctedBits);
   }

   private static String getEncodedData(boolean[] correctedBits) {
      int endIndex = correctedBits.length;
      Table latchTable = Decoder.Table.UPPER;
      Table shiftTable = Decoder.Table.UPPER;
      StringBuilder result = new StringBuilder(20);
      int index = 0;

      while(index < endIndex) {
         int length;
         int charCount;
         if (shiftTable != Decoder.Table.BINARY) {
            length = shiftTable == Decoder.Table.DIGIT ? 4 : 5;
            if (endIndex - index < length) {
               break;
            }

            charCount = readCode(correctedBits, index, length);
            index += length;
            String str;
            if ((str = getCharacter(shiftTable, charCount)).startsWith("CTRL_")) {
               latchTable = shiftTable;
               shiftTable = getTable(str.charAt(5));
               if (str.charAt(6) == 'L') {
                  latchTable = shiftTable;
               }
            } else {
               result.append(str);
               shiftTable = latchTable;
            }
         } else {
            if (endIndex - index < 5) {
               break;
            }

            length = readCode(correctedBits, index, 5);
            index += 5;
            if (length == 0) {
               if (endIndex - index < 11) {
                  break;
               }

               length = readCode(correctedBits, index, 11) + 31;
               index += 11;
            }

            for(charCount = 0; charCount < length; ++charCount) {
               if (endIndex - index < 8) {
                  index = endIndex;
                  break;
               }

               int code = readCode(correctedBits, index, 8);
               result.append((char)code);
               index += 8;
            }

            shiftTable = latchTable;
         }
      }

      return result.toString();
   }

   private static Table getTable(char t) {
      switch (t) {
         case 'B':
            return Decoder.Table.BINARY;
         case 'C':
         case 'E':
         case 'F':
         case 'G':
         case 'H':
         case 'I':
         case 'J':
         case 'K':
         case 'N':
         case 'O':
         default:
            return Decoder.Table.UPPER;
         case 'D':
            return Decoder.Table.DIGIT;
         case 'L':
            return Decoder.Table.LOWER;
         case 'M':
            return Decoder.Table.MIXED;
         case 'P':
            return Decoder.Table.PUNCT;
      }
   }

   private static String getCharacter(Table table, int code) {
      switch (table) {
         case UPPER:
            return UPPER_TABLE[code];
         case LOWER:
            return LOWER_TABLE[code];
         case MIXED:
            return MIXED_TABLE[code];
         case PUNCT:
            return PUNCT_TABLE[code];
         case DIGIT:
            return DIGIT_TABLE[code];
         default:
            throw new IllegalStateException("Bad table");
      }
   }

   private boolean[] correctBits(boolean[] rawbits) throws FormatException {
      GenericGF gf;
      byte codewordSize;
      if (this.ddata.getNbLayers() <= 2) {
         codewordSize = 6;
         gf = GenericGF.AZTEC_DATA_6;
      } else if (this.ddata.getNbLayers() <= 8) {
         codewordSize = 8;
         gf = GenericGF.AZTEC_DATA_8;
      } else if (this.ddata.getNbLayers() <= 22) {
         codewordSize = 10;
         gf = GenericGF.AZTEC_DATA_10;
      } else {
         codewordSize = 12;
         gf = GenericGF.AZTEC_DATA_12;
      }

      int numDataCodewords = this.ddata.getNbDatablocks();
      int numCodewords;
      if ((numCodewords = rawbits.length / codewordSize) < numDataCodewords) {
         throw FormatException.getFormatInstance();
      } else {
         int offset = rawbits.length % codewordSize;
         int[] dataWords = new int[numCodewords];

         int mask;
         for(mask = 0; mask < numCodewords; offset += codewordSize) {
            dataWords[mask] = readCode(rawbits, offset, codewordSize);
            ++mask;
         }

         try {
            (new ReedSolomonDecoder(gf)).decode(dataWords, numCodewords - numDataCodewords);
         } catch (ReedSolomonException var15) {
            throw FormatException.getFormatInstance(var15);
         }

         mask = (1 << codewordSize) - 1;
         int stuffedBits = 0;

         int index;
         for(int i = 0; i < numDataCodewords; ++i) {
            if ((index = dataWords[i]) == 0 || index == mask) {
               throw FormatException.getFormatInstance();
            }

            if (index == 1 || index == mask - 1) {
               ++stuffedBits;
            }
         }

         boolean[] correctedBits = new boolean[numDataCodewords * codewordSize - stuffedBits];
         index = 0;

         for(int i = 0; i < numDataCodewords; ++i) {
            int dataWord;
            if ((dataWord = dataWords[i]) != 1 && dataWord != mask - 1) {
               for(int bit = codewordSize - 1; bit >= 0; --bit) {
                  correctedBits[index++] = (dataWord & 1 << bit) != 0;
               }
            } else {
               Arrays.fill(correctedBits, index, index + codewordSize - 1, dataWord > 1);
               index += codewordSize - 1;
            }
         }

         return correctedBits;
      }
   }

   private boolean[] extractBits(BitMatrix matrix) {
      boolean compact = this.ddata.isCompact();
      int layers = this.ddata.getNbLayers();
      int baseMatrixSize;
      int[] alignmentMap = new int[baseMatrixSize = (compact ? 11 : 14) + (layers << 2)];
      boolean[] rawbits = new boolean[totalBitsInLayer(layers, compact)];
      int i;
      int rowOffset;
      int rowSize;
      int low;
      int high;
      if (compact) {
         for(i = 0; i < alignmentMap.length; alignmentMap[i] = i++) {
         }
      } else {
         i = baseMatrixSize + 1 + 2 * ((baseMatrixSize / 2 - 1) / 15);
         rowOffset = baseMatrixSize / 2;
         rowSize = i / 2;

         for(low = 0; low < rowOffset; ++low) {
            high = low + low / 15;
            alignmentMap[rowOffset - low - 1] = rowSize - high - 1;
            alignmentMap[rowOffset + low] = rowSize + high + 1;
         }
      }

      i = 0;

      for(rowOffset = 0; i < layers; ++i) {
         rowSize = (layers - i << 2) + (compact ? 9 : 12);
         low = i << 1;
         high = baseMatrixSize - 1 - low;

         for(int j = 0; j < rowSize; ++j) {
            int columnOffset = j << 1;

            for(int k = 0; k < 2; ++k) {
               rawbits[rowOffset + columnOffset + k] = matrix.get(alignmentMap[low + k], alignmentMap[low + j]);
               rawbits[rowOffset + 2 * rowSize + columnOffset + k] = matrix.get(alignmentMap[low + j], alignmentMap[high - k]);
               rawbits[rowOffset + 4 * rowSize + columnOffset + k] = matrix.get(alignmentMap[high - k], alignmentMap[high - j]);
               rawbits[rowOffset + rowSize * 6 + columnOffset + k] = matrix.get(alignmentMap[high - j], alignmentMap[low + k]);
            }
         }

         rowOffset += rowSize << 3;
      }

      return rawbits;
   }

   private static int readCode(boolean[] rawbits, int startIndex, int length) {
      int res = 0;

      for(int i = startIndex; i < startIndex + length; ++i) {
         res <<= 1;
         if (rawbits[i]) {
            res |= 1;
         }
      }

      return res;
   }

   private static byte readByte(boolean[] rawbits, int startIndex) {
      int n;
      return (n = rawbits.length - startIndex) >= 8 ? (byte)readCode(rawbits, startIndex, 8) : (byte)(readCode(rawbits, startIndex, n) << 8 - n);
   }

   static byte[] convertBoolArrayToByteArray(boolean[] boolArr) {
      byte[] byteArr = new byte[(boolArr.length + 7) / 8];

      for(int i = 0; i < byteArr.length; ++i) {
         byteArr[i] = readByte(boolArr, i << 3);
      }

      return byteArr;
   }

   private static int totalBitsInLayer(int layers, boolean compact) {
      return ((compact ? 88 : 112) + (layers << 4)) * layers;
   }

   private static enum Table {
      UPPER,
      LOWER,
      MIXED,
      DIGIT,
      PUNCT,
      BINARY;
   }
}
