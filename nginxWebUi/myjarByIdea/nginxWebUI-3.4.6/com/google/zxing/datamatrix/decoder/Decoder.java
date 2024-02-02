package com.google.zxing.datamatrix.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;

public final class Decoder {
   private final ReedSolomonDecoder rsDecoder;

   public Decoder() {
      this.rsDecoder = new ReedSolomonDecoder(GenericGF.DATA_MATRIX_FIELD_256);
   }

   public DecoderResult decode(boolean[][] image) throws FormatException, ChecksumException {
      int dimension = image.length;
      BitMatrix bits = new BitMatrix(dimension);

      for(int i = 0; i < dimension; ++i) {
         for(int j = 0; j < dimension; ++j) {
            if (image[i][j]) {
               bits.set(j, i);
            }
         }
      }

      return this.decode(bits);
   }

   public DecoderResult decode(BitMatrix bits) throws FormatException, ChecksumException {
      BitMatrixParser parser;
      Version version = (parser = new BitMatrixParser(bits)).getVersion();
      DataBlock[] dataBlocks = DataBlock.getDataBlocks(parser.readCodewords(), version);
      int totalBytes = 0;
      DataBlock[] var6 = dataBlocks;
      int dataBlocksCount = dataBlocks.length;

      int j;
      DataBlock dataBlock;
      for(j = 0; j < dataBlocksCount; ++j) {
         dataBlock = var6[j];
         totalBytes += dataBlock.getNumDataCodewords();
      }

      byte[] resultBytes = new byte[totalBytes];
      dataBlocksCount = dataBlocks.length;

      for(j = 0; j < dataBlocksCount; ++j) {
         byte[] codewordBytes = (dataBlock = dataBlocks[j]).getCodewords();
         int numDataCodewords = dataBlock.getNumDataCodewords();
         this.correctErrors(codewordBytes, numDataCodewords);

         for(int i = 0; i < numDataCodewords; ++i) {
            resultBytes[i * dataBlocksCount + j] = codewordBytes[i];
         }
      }

      return DecodedBitStreamParser.decode(resultBytes);
   }

   private void correctErrors(byte[] codewordBytes, int numDataCodewords) throws ChecksumException {
      int numCodewords;
      int[] codewordsInts = new int[numCodewords = codewordBytes.length];

      int i;
      for(i = 0; i < numCodewords; ++i) {
         codewordsInts[i] = codewordBytes[i] & 255;
      }

      try {
         this.rsDecoder.decode(codewordsInts, codewordBytes.length - numDataCodewords);
      } catch (ReedSolomonException var6) {
         throw ChecksumException.getChecksumInstance();
      }

      for(i = 0; i < numDataCodewords; ++i) {
         codewordBytes[i] = (byte)codewordsInts[i];
      }

   }
}
