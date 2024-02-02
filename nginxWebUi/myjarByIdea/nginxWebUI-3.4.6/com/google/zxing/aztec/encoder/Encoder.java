package com.google.zxing.aztec.encoder;

import com.google.zxing.common.BitArray;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;

public final class Encoder {
   public static final int DEFAULT_EC_PERCENT = 33;
   public static final int DEFAULT_AZTEC_LAYERS = 0;
   private static final int MAX_NB_BITS = 32;
   private static final int MAX_NB_BITS_COMPACT = 4;
   private static final int[] WORD_SIZE = new int[]{4, 6, 6, 8, 8, 8, 8, 8, 8, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 12, 12};

   private Encoder() {
   }

   public static AztecCode encode(byte[] data) {
      return encode(data, 33, 0);
   }

   public static AztecCode encode(byte[] data, int minECCPercent, int userSpecifiedLayers) {
      BitArray bits;
      int eccBits = (bits = (new HighLevelEncoder(data)).encode()).getSize() * minECCPercent / 100 + 11;
      int totalSizeBits = bits.getSize() + eccBits;
      boolean compact;
      int layers;
      int totalBitsInLayer;
      int wordSize;
      BitArray stuffedBits;
      int i;
      int messageSizeInWords;
      if (userSpecifiedLayers != 0) {
         compact = userSpecifiedLayers < 0;
         if ((layers = Math.abs(userSpecifiedLayers)) > (compact ? 4 : 32)) {
            throw new IllegalArgumentException(String.format("Illegal value %s for layers", userSpecifiedLayers));
         }

         totalBitsInLayer = totalBitsInLayer(layers, compact);
         wordSize = WORD_SIZE[layers];
         i = totalBitsInLayer - totalBitsInLayer % wordSize;
         if ((stuffedBits = stuffBits(bits, wordSize)).getSize() + eccBits > i) {
            throw new IllegalArgumentException("Data to large for user specified layer");
         }

         if (compact && stuffedBits.getSize() > wordSize << 6) {
            throw new IllegalArgumentException("Data to large for user specified layer");
         }
      } else {
         wordSize = 0;
         stuffedBits = null;
         i = 0;

         while(true) {
            if (i > 32) {
               throw new IllegalArgumentException("Data too large for an Aztec code");
            }

            totalBitsInLayer = totalBitsInLayer(layers = (compact = i <= 3) ? i + 1 : i, compact);
            if (totalSizeBits <= totalBitsInLayer) {
               if (wordSize != WORD_SIZE[layers]) {
                  wordSize = WORD_SIZE[layers];
                  stuffedBits = stuffBits(bits, wordSize);
               }

               messageSizeInWords = totalBitsInLayer - totalBitsInLayer % wordSize;
               if ((!compact || stuffedBits.getSize() <= wordSize << 6) && stuffedBits.getSize() + eccBits <= messageSizeInWords) {
                  break;
               }
            }

            ++i;
         }
      }

      BitArray messageBits = generateCheckWords(stuffedBits, totalBitsInLayer, wordSize);
      messageSizeInWords = stuffedBits.getSize() / wordSize;
      BitArray modeMessage = generateModeMessage(compact, layers, messageSizeInWords);
      int baseMatrixSize;
      int[] alignmentMap = new int[baseMatrixSize = (compact ? 11 : 14) + (layers << 2)];
      int matrixSize;
      int i;
      int i;
      int rowOffset;
      int rowSize;
      if (compact) {
         matrixSize = baseMatrixSize;

         for(i = 0; i < alignmentMap.length; alignmentMap[i] = i++) {
         }
      } else {
         matrixSize = baseMatrixSize + 1 + 2 * ((baseMatrixSize / 2 - 1) / 15);
         i = baseMatrixSize / 2;
         i = matrixSize / 2;

         for(rowOffset = 0; rowOffset < i; ++rowOffset) {
            rowSize = rowOffset + rowOffset / 15;
            alignmentMap[i - rowOffset - 1] = i - rowSize - 1;
            alignmentMap[i + rowOffset] = i + rowSize + 1;
         }
      }

      BitMatrix matrix = new BitMatrix(matrixSize);
      i = 0;

      for(rowOffset = 0; i < layers; ++i) {
         rowSize = (layers - i << 2) + (compact ? 9 : 12);

         for(int j = 0; j < rowSize; ++j) {
            int columnOffset = j << 1;

            for(int k = 0; k < 2; ++k) {
               if (messageBits.get(rowOffset + columnOffset + k)) {
                  matrix.set(alignmentMap[(i << 1) + k], alignmentMap[(i << 1) + j]);
               }

               if (messageBits.get(rowOffset + (rowSize << 1) + columnOffset + k)) {
                  matrix.set(alignmentMap[(i << 1) + j], alignmentMap[baseMatrixSize - 1 - (i << 1) - k]);
               }

               if (messageBits.get(rowOffset + (rowSize << 2) + columnOffset + k)) {
                  matrix.set(alignmentMap[baseMatrixSize - 1 - (i << 1) - k], alignmentMap[baseMatrixSize - 1 - (i << 1) - j]);
               }

               if (messageBits.get(rowOffset + rowSize * 6 + columnOffset + k)) {
                  matrix.set(alignmentMap[baseMatrixSize - 1 - (i << 1) - j], alignmentMap[(i << 1) + k]);
               }
            }
         }

         rowOffset += rowSize << 3;
      }

      drawModeMessage(matrix, compact, matrixSize, modeMessage);
      if (compact) {
         drawBullsEye(matrix, matrixSize / 2, 5);
      } else {
         drawBullsEye(matrix, matrixSize / 2, 7);
         i = 0;

         for(rowOffset = 0; i < baseMatrixSize / 2 - 1; rowOffset += 16) {
            for(rowSize = matrixSize / 2 & 1; rowSize < matrixSize; rowSize += 2) {
               matrix.set(matrixSize / 2 - rowOffset, rowSize);
               matrix.set(matrixSize / 2 + rowOffset, rowSize);
               matrix.set(rowSize, matrixSize / 2 - rowOffset);
               matrix.set(rowSize, matrixSize / 2 + rowOffset);
            }

            i += 15;
         }
      }

      AztecCode aztec;
      (aztec = new AztecCode()).setCompact(compact);
      aztec.setSize(matrixSize);
      aztec.setLayers(layers);
      aztec.setCodeWords(messageSizeInWords);
      aztec.setMatrix(matrix);
      return aztec;
   }

   private static void drawBullsEye(BitMatrix matrix, int center, int size) {
      for(int i = 0; i < size; i += 2) {
         for(int j = center - i; j <= center + i; ++j) {
            matrix.set(j, center - i);
            matrix.set(j, center + i);
            matrix.set(center - i, j);
            matrix.set(center + i, j);
         }
      }

      matrix.set(center - size, center - size);
      matrix.set(center - size + 1, center - size);
      matrix.set(center - size, center - size + 1);
      matrix.set(center + size, center - size);
      matrix.set(center + size, center - size + 1);
      matrix.set(center + size, center + size - 1);
   }

   static BitArray generateModeMessage(boolean compact, int layers, int messageSizeInWords) {
      BitArray modeMessage = new BitArray();
      if (compact) {
         modeMessage.appendBits(layers - 1, 2);
         modeMessage.appendBits(messageSizeInWords - 1, 6);
         modeMessage = generateCheckWords(modeMessage, 28, 4);
      } else {
         modeMessage.appendBits(layers - 1, 5);
         modeMessage.appendBits(messageSizeInWords - 1, 11);
         modeMessage = generateCheckWords(modeMessage, 40, 4);
      }

      return modeMessage;
   }

   private static void drawModeMessage(BitMatrix matrix, boolean compact, int matrixSize, BitArray modeMessage) {
      int center = matrixSize / 2;
      int i;
      int offset;
      if (compact) {
         for(i = 0; i < 7; ++i) {
            offset = center - 3 + i;
            if (modeMessage.get(i)) {
               matrix.set(offset, center - 5);
            }

            if (modeMessage.get(i + 7)) {
               matrix.set(center + 5, offset);
            }

            if (modeMessage.get(20 - i)) {
               matrix.set(offset, center + 5);
            }

            if (modeMessage.get(27 - i)) {
               matrix.set(center - 5, offset);
            }
         }

      } else {
         for(i = 0; i < 10; ++i) {
            offset = center - 5 + i + i / 5;
            if (modeMessage.get(i)) {
               matrix.set(offset, center - 7);
            }

            if (modeMessage.get(i + 10)) {
               matrix.set(center + 7, offset);
            }

            if (modeMessage.get(29 - i)) {
               matrix.set(offset, center + 7);
            }

            if (modeMessage.get(39 - i)) {
               matrix.set(center - 7, offset);
            }
         }

      }
   }

   private static BitArray generateCheckWords(BitArray bitArray, int totalBits, int wordSize) {
      int messageSizeInWords = bitArray.getSize() / wordSize;
      ReedSolomonEncoder rs = new ReedSolomonEncoder(getGF(wordSize));
      int totalWords = totalBits / wordSize;
      int[] messageWords = bitsToWords(bitArray, wordSize, totalWords);
      rs.encode(messageWords, totalWords - messageSizeInWords);
      int startPad = totalBits % wordSize;
      BitArray messageBits;
      (messageBits = new BitArray()).appendBits(0, startPad);
      int[] var9 = messageWords;
      int var10 = messageWords.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         int messageWord = var9[var11];
         messageBits.appendBits(messageWord, wordSize);
      }

      return messageBits;
   }

   private static int[] bitsToWords(BitArray stuffedBits, int wordSize, int totalWords) {
      int[] message = new int[totalWords];
      int i = 0;

      for(int n = stuffedBits.getSize() / wordSize; i < n; ++i) {
         int value = 0;

         for(int j = 0; j < wordSize; ++j) {
            value |= stuffedBits.get(i * wordSize + j) ? 1 << wordSize - j - 1 : 0;
         }

         message[i] = value;
      }

      return message;
   }

   private static GenericGF getGF(int wordSize) {
      switch (wordSize) {
         case 4:
            return GenericGF.AZTEC_PARAM;
         case 5:
         case 7:
         case 9:
         case 11:
         default:
            throw new IllegalArgumentException("Unsupported word size " + wordSize);
         case 6:
            return GenericGF.AZTEC_DATA_6;
         case 8:
            return GenericGF.AZTEC_DATA_8;
         case 10:
            return GenericGF.AZTEC_DATA_10;
         case 12:
            return GenericGF.AZTEC_DATA_12;
      }
   }

   static BitArray stuffBits(BitArray bits, int wordSize) {
      BitArray out = new BitArray();
      int n = bits.getSize();
      int mask = (1 << wordSize) - 2;

      for(int i = 0; i < n; i += wordSize) {
         int word = 0;

         for(int j = 0; j < wordSize; ++j) {
            if (i + j >= n || bits.get(i + j)) {
               word |= 1 << wordSize - 1 - j;
            }
         }

         if ((word & mask) == mask) {
            out.appendBits(word & mask, wordSize);
            --i;
         } else if ((word & mask) == 0) {
            out.appendBits(word | 1, wordSize);
            --i;
         } else {
            out.appendBits(word, wordSize);
         }
      }

      return out;
   }

   private static int totalBitsInLayer(int layers, boolean compact) {
      return ((compact ? 88 : 112) + (layers << 4)) * layers;
   }
}
