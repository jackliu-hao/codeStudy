package com.google.zxing.datamatrix.encoder;

public final class ErrorCorrection {
   private static final int[] FACTOR_SETS = new int[]{5, 7, 10, 11, 12, 14, 18, 20, 24, 28, 36, 42, 48, 56, 62, 68};
   private static final int[][] FACTORS = new int[][]{{228, 48, 15, 111, 62}, {23, 68, 144, 134, 240, 92, 254}, {28, 24, 185, 166, 223, 248, 116, 255, 110, 61}, {175, 138, 205, 12, 194, 168, 39, 245, 60, 97, 120}, {41, 153, 158, 91, 61, 42, 142, 213, 97, 178, 100, 242}, {156, 97, 192, 252, 95, 9, 157, 119, 138, 45, 18, 186, 83, 185}, {83, 195, 100, 39, 188, 75, 66, 61, 241, 213, 109, 129, 94, 254, 225, 48, 90, 188}, {15, 195, 244, 9, 233, 71, 168, 2, 188, 160, 153, 145, 253, 79, 108, 82, 27, 174, 186, 172}, {52, 190, 88, 205, 109, 39, 176, 21, 155, 197, 251, 223, 155, 21, 5, 172, 254, 124, 12, 181, 184, 96, 50, 193}, {211, 231, 43, 97, 71, 96, 103, 174, 37, 151, 170, 53, 75, 34, 249, 121, 17, 138, 110, 213, 141, 136, 120, 151, 233, 168, 93, 255}, {245, 127, 242, 218, 130, 250, 162, 181, 102, 120, 84, 179, 220, 251, 80, 182, 229, 18, 2, 4, 68, 33, 101, 137, 95, 119, 115, 44, 175, 184, 59, 25, 225, 98, 81, 112}, {77, 193, 137, 31, 19, 38, 22, 153, 247, 105, 122, 2, 245, 133, 242, 8, 175, 95, 100, 9, 167, 105, 214, 111, 57, 121, 21, 1, 253, 57, 54, 101, 248, 202, 69, 50, 150, 177, 226, 5, 9, 5}, {245, 132, 172, 223, 96, 32, 117, 22, 238, 133, 238, 231, 205, 188, 237, 87, 191, 106, 16, 147, 118, 23, 37, 90, 170, 205, 131, 88, 120, 100, 66, 138, 186, 240, 82, 44, 176, 87, 187, 147, 160, 175, 69, 213, 92, 253, 225, 19}, {175, 9, 223, 238, 12, 17, 220, 208, 100, 29, 175, 170, 230, 192, 215, 235, 150, 159, 36, 223, 38, 200, 132, 54, 228, 146, 218, 234, 117, 203, 29, 232, 144, 238, 22, 150, 201, 117, 62, 207, 164, 13, 137, 245, 127, 67, 247, 28, 155, 43, 203, 107, 233, 53, 143, 46}, {242, 93, 169, 50, 144, 210, 39, 118, 202, 188, 201, 189, 143, 108, 196, 37, 185, 112, 134, 230, 245, 63, 197, 190, 250, 106, 185, 221, 175, 64, 114, 71, 161, 44, 147, 6, 27, 218, 51, 63, 87, 10, 40, 130, 188, 17, 163, 31, 176, 170, 4, 107, 232, 7, 94, 166, 224, 124, 86, 47, 11, 204}, {220, 228, 173, 89, 251, 149, 159, 56, 89, 33, 147, 244, 154, 36, 73, 127, 213, 136, 248, 180, 234, 197, 158, 177, 68, 122, 93, 213, 15, 160, 227, 236, 66, 139, 153, 185, 202, 167, 179, 25, 220, 232, 96, 210, 231, 136, 223, 239, 181, 241, 59, 52, 172, 25, 49, 232, 211, 189, 64, 54, 108, 153, 132, 63, 96, 103, 82, 186}};
   private static final int MODULO_VALUE = 301;
   private static final int[] LOG = new int[256];
   private static final int[] ALOG = new int[255];

   private ErrorCorrection() {
   }

   public static String encodeECC200(String codewords, SymbolInfo symbolInfo) {
      if (codewords.length() != symbolInfo.getDataCapacity()) {
         throw new IllegalArgumentException("The number of codewords does not match the selected symbol");
      } else {
         StringBuilder sb;
         (sb = new StringBuilder(symbolInfo.getDataCapacity() + symbolInfo.getErrorCodewords())).append(codewords);
         int blockCount;
         if ((blockCount = symbolInfo.getInterleavedBlockCount()) == 1) {
            String ecc = createECCBlock(codewords, symbolInfo.getErrorCodewords());
            sb.append(ecc);
         } else {
            sb.setLength(sb.capacity());
            int[] dataSizes = new int[blockCount];
            int[] errorSizes = new int[blockCount];
            int[] startPos = new int[blockCount];

            int block;
            for(block = 0; block < blockCount; ++block) {
               dataSizes[block] = symbolInfo.getDataLengthForInterleavedBlock(block + 1);
               errorSizes[block] = symbolInfo.getErrorLengthForInterleavedBlock(block + 1);
               startPos[block] = 0;
               if (block > 0) {
                  startPos[block] = startPos[block - 1] + dataSizes[block];
               }
            }

            for(block = 0; block < blockCount; ++block) {
               StringBuilder temp = new StringBuilder(dataSizes[block]);

               for(int d = block; d < symbolInfo.getDataCapacity(); d += blockCount) {
                  temp.append(codewords.charAt(d));
               }

               String ecc = createECCBlock(temp.toString(), errorSizes[block]);
               int pos = 0;

               for(int e = block; e < errorSizes[block] * blockCount; e += blockCount) {
                  sb.setCharAt(symbolInfo.getDataCapacity() + e, ecc.charAt(pos++));
               }
            }
         }

         return sb.toString();
      }
   }

   private static String createECCBlock(CharSequence codewords, int numECWords) {
      return createECCBlock(codewords, 0, codewords.length(), numECWords);
   }

   private static String createECCBlock(CharSequence codewords, int start, int len, int numECWords) {
      int table = -1;

      for(int i = 0; i < FACTOR_SETS.length; ++i) {
         if (FACTOR_SETS[i] == numECWords) {
            table = i;
            break;
         }
      }

      if (table < 0) {
         throw new IllegalArgumentException("Illegal number of error correction codewords specified: " + numECWords);
      } else {
         int[] poly = FACTORS[table];
         char[] ecc = new char[numECWords];

         int i;
         for(i = 0; i < numECWords; ++i) {
            ecc[i] = 0;
         }

         int m;
         for(i = start; i < start + len; ++i) {
            m = ecc[numECWords - 1] ^ codewords.charAt(i);

            for(int k = numECWords - 1; k > 0; --k) {
               if (m != 0 && poly[k] != 0) {
                  ecc[k] = (char)(ecc[k - 1] ^ ALOG[(LOG[m] + LOG[poly[k]]) % 255]);
               } else {
                  ecc[k] = ecc[k - 1];
               }
            }

            if (m != 0 && poly[0] != 0) {
               ecc[0] = (char)ALOG[(LOG[m] + LOG[poly[0]]) % 255];
            } else {
               ecc[0] = 0;
            }
         }

         char[] eccReversed = new char[numECWords];

         for(m = 0; m < numECWords; ++m) {
            eccReversed[m] = ecc[numECWords - m - 1];
         }

         return String.valueOf(eccReversed);
      }
   }

   static {
      int p = 1;

      for(int i = 0; i < 255; ++i) {
         ALOG[i] = p;
         LOG[p] = i;
         if ((p <<= 1) >= 256) {
            p ^= 301;
         }
      }

   }
}
