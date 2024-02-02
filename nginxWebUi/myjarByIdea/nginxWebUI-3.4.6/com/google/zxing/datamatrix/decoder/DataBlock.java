package com.google.zxing.datamatrix.decoder;

final class DataBlock {
   private final int numDataCodewords;
   private final byte[] codewords;

   private DataBlock(int numDataCodewords, byte[] codewords) {
      this.numDataCodewords = numDataCodewords;
      this.codewords = codewords;
   }

   static DataBlock[] getDataBlocks(byte[] rawCodewords, Version version) {
      Version.ECBlocks ecBlocks = version.getECBlocks();
      int totalBlocks = 0;
      Version.ECB[] ecBlockArray;
      Version.ECB[] var5;
      int numResultBlocks = (var5 = ecBlockArray = ecBlocks.getECBlocks()).length;

      for(int var7 = 0; var7 < numResultBlocks; ++var7) {
         Version.ECB ecBlock = var5[var7];
         totalBlocks += ecBlock.getCount();
      }

      DataBlock[] result = new DataBlock[totalBlocks];
      numResultBlocks = 0;
      Version.ECB[] var19 = ecBlockArray;
      int longerBlocksNumDataCodewords = ecBlockArray.length;

      int shorterBlocksNumDataCodewords;
      int i;
      int numLongerBlocks;
      int max;
      for(shorterBlocksNumDataCodewords = 0; shorterBlocksNumDataCodewords < longerBlocksNumDataCodewords; ++shorterBlocksNumDataCodewords) {
         Version.ECB ecBlock = var19[shorterBlocksNumDataCodewords];

         for(i = 0; i < ecBlock.getCount(); ++i) {
            numLongerBlocks = ecBlock.getDataCodewords();
            max = ecBlocks.getECCodewords() + numLongerBlocks;
            result[numResultBlocks++] = new DataBlock(numLongerBlocks, new byte[max]);
         }
      }

      shorterBlocksNumDataCodewords = (longerBlocksNumDataCodewords = result[0].codewords.length - ecBlocks.getECCodewords()) - 1;
      int rawCodewordsOffset = 0;

      for(i = 0; i < shorterBlocksNumDataCodewords; ++i) {
         for(numLongerBlocks = 0; numLongerBlocks < numResultBlocks; ++numLongerBlocks) {
            result[numLongerBlocks].codewords[i] = rawCodewords[rawCodewordsOffset++];
         }
      }

      boolean specialVersion;
      numLongerBlocks = (specialVersion = version.getVersionNumber() == 24) ? 8 : numResultBlocks;

      for(max = 0; max < numLongerBlocks; ++max) {
         result[max].codewords[longerBlocksNumDataCodewords - 1] = rawCodewords[rawCodewordsOffset++];
      }

      max = result[0].codewords.length;

      for(int i = longerBlocksNumDataCodewords; i < max; ++i) {
         for(int j = 0; j < numResultBlocks; ++j) {
            int jOffset = specialVersion ? (j + 8) % numResultBlocks : j;
            int iOffset = specialVersion && jOffset > 7 ? i - 1 : i;
            result[jOffset].codewords[iOffset] = rawCodewords[rawCodewordsOffset++];
         }
      }

      if (rawCodewordsOffset != rawCodewords.length) {
         throw new IllegalArgumentException();
      } else {
         return result;
      }
   }

   int getNumDataCodewords() {
      return this.numDataCodewords;
   }

   byte[] getCodewords() {
      return this.codewords;
   }
}
