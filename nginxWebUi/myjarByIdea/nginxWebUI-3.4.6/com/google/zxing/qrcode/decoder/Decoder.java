package com.google.zxing.qrcode.decoder;

import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonDecoder;
import com.google.zxing.common.reedsolomon.ReedSolomonException;
import java.util.Map;

public final class Decoder {
   private final ReedSolomonDecoder rsDecoder;

   public Decoder() {
      this.rsDecoder = new ReedSolomonDecoder(GenericGF.QR_CODE_FIELD_256);
   }

   public DecoderResult decode(boolean[][] image) throws ChecksumException, FormatException {
      return this.decode((boolean[][])image, (Map)null);
   }

   public DecoderResult decode(boolean[][] image, Map<DecodeHintType, ?> hints) throws ChecksumException, FormatException {
      int dimension = image.length;
      BitMatrix bits = new BitMatrix(dimension);

      for(int i = 0; i < dimension; ++i) {
         for(int j = 0; j < dimension; ++j) {
            if (image[i][j]) {
               bits.set(j, i);
            }
         }
      }

      return this.decode(bits, hints);
   }

   public DecoderResult decode(BitMatrix bits) throws ChecksumException, FormatException {
      return this.decode((BitMatrix)bits, (Map)null);
   }

   public DecoderResult decode(BitMatrix bits, Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
      BitMatrixParser parser = new BitMatrixParser(bits);
      FormatException fe = null;
      ChecksumException ce = null;

      try {
         return this.decode(parser, hints);
      } catch (FormatException var7) {
         fe = var7;
      } catch (ChecksumException var8) {
         ce = var8;
      }

      try {
         parser.remask();
         parser.setMirror(true);
         parser.readVersion();
         parser.readFormatInformation();
         parser.mirror();
         DecoderResult result;
         (result = this.decode(parser, hints)).setOther(new QRCodeDecoderMetaData(true));
         return result;
      } catch (ChecksumException | FormatException var9) {
         if (fe != null) {
            throw fe;
         } else if (ce != null) {
            throw ce;
         } else {
            throw var9;
         }
      }
   }

   private DecoderResult decode(BitMatrixParser parser, Map<DecodeHintType, ?> hints) throws FormatException, ChecksumException {
      Version version = parser.readVersion();
      ErrorCorrectionLevel ecLevel = parser.readFormatInformation().getErrorCorrectionLevel();
      DataBlock[] dataBlocks = DataBlock.getDataBlocks(parser.readCodewords(), version, ecLevel);
      int totalBytes = 0;
      DataBlock[] var7 = dataBlocks;
      int resultOffset = dataBlocks.length;

      for(int var9 = 0; var9 < resultOffset; ++var9) {
         DataBlock dataBlock = var7[var9];
         totalBytes += dataBlock.getNumDataCodewords();
      }

      byte[] resultBytes = new byte[totalBytes];
      resultOffset = 0;
      DataBlock[] var17 = dataBlocks;
      int var18 = dataBlocks.length;

      for(int var11 = 0; var11 < var18; ++var11) {
         DataBlock dataBlock;
         byte[] codewordBytes = (dataBlock = var17[var11]).getCodewords();
         int numDataCodewords = dataBlock.getNumDataCodewords();
         this.correctErrors(codewordBytes, numDataCodewords);

         for(int i = 0; i < numDataCodewords; ++i) {
            resultBytes[resultOffset++] = codewordBytes[i];
         }
      }

      return DecodedBitStreamParser.decode(resultBytes, version, ecLevel, hints);
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
