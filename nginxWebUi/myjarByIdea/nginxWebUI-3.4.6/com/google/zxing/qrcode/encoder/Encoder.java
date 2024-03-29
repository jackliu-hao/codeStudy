package com.google.zxing.qrcode.encoder;

import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitArray;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.reedsolomon.GenericGF;
import com.google.zxing.common.reedsolomon.ReedSolomonEncoder;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.google.zxing.qrcode.decoder.Mode;
import com.google.zxing.qrcode.decoder.Version;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

public final class Encoder {
   private static final int[] ALPHANUMERIC_TABLE = new int[]{-1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 36, -1, -1, -1, 37, 38, -1, -1, -1, -1, 39, 40, -1, 41, 42, 43, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 44, -1, -1, -1, -1, -1, -1, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, -1, -1, -1, -1, -1};
   static final String DEFAULT_BYTE_MODE_ENCODING = "ISO-8859-1";

   private Encoder() {
   }

   private static int calculateMaskPenalty(ByteMatrix matrix) {
      return MaskUtil.applyMaskPenaltyRule1(matrix) + MaskUtil.applyMaskPenaltyRule2(matrix) + MaskUtil.applyMaskPenaltyRule3(matrix) + MaskUtil.applyMaskPenaltyRule4(matrix);
   }

   public static QRCode encode(String content, ErrorCorrectionLevel ecLevel) throws WriterException {
      return encode(content, ecLevel, (Map)null);
   }

   public static QRCode encode(String content, ErrorCorrectionLevel ecLevel, Map<EncodeHintType, ?> hints) throws WriterException {
      String encoding = "ISO-8859-1";
      if (hints != null && hints.containsKey(EncodeHintType.CHARACTER_SET)) {
         encoding = hints.get(EncodeHintType.CHARACTER_SET).toString();
      }

      Mode mode = chooseMode(content, encoding);
      BitArray headerBits = new BitArray();
      CharacterSetECI eci;
      if (mode == Mode.BYTE && !"ISO-8859-1".equals(encoding) && (eci = CharacterSetECI.getCharacterSetECIByName(encoding)) != null) {
         appendECI(eci, headerBits);
      }

      appendModeInfo(mode, headerBits);
      BitArray dataBits = new BitArray();
      appendBytes(content, mode, dataBits, encoding);
      Version version;
      if (hints != null && hints.containsKey(EncodeHintType.QR_VERSION)) {
         version = Version.getVersionForNumber(Integer.parseInt(hints.get(EncodeHintType.QR_VERSION).toString()));
         if (!willFit(calculateBitsNeeded(mode, headerBits, dataBits, version), version, ecLevel)) {
            throw new WriterException("Data too big for requested version");
         }
      } else {
         version = recommendVersion(ecLevel, mode, headerBits, dataBits);
      }

      BitArray headerAndDataBits;
      (headerAndDataBits = new BitArray()).appendBitArray(headerBits);
      appendLengthInfo(mode == Mode.BYTE ? dataBits.getSizeInBytes() : content.length(), version, mode, headerAndDataBits);
      headerAndDataBits.appendBitArray(dataBits);
      Version.ECBlocks ecBlocks = version.getECBlocksForLevel(ecLevel);
      int numDataBytes;
      terminateBits(numDataBytes = version.getTotalCodewords() - ecBlocks.getTotalECCodewords(), headerAndDataBits);
      BitArray finalBits = interleaveWithECBytes(headerAndDataBits, version.getTotalCodewords(), numDataBytes, ecBlocks.getNumBlocks());
      QRCode qrCode;
      (qrCode = new QRCode()).setECLevel(ecLevel);
      qrCode.setMode(mode);
      qrCode.setVersion(version);
      int dimension = version.getDimensionForVersion();
      ByteMatrix matrix = new ByteMatrix(dimension, dimension);
      int maskPattern = chooseMaskPattern(finalBits, ecLevel, version, matrix);
      qrCode.setMaskPattern(maskPattern);
      MatrixUtil.buildMatrix(finalBits, ecLevel, version, maskPattern, matrix);
      qrCode.setMatrix(matrix);
      return qrCode;
   }

   private static Version recommendVersion(ErrorCorrectionLevel ecLevel, Mode mode, BitArray headerBits, BitArray dataBits) throws WriterException {
      Version provisionalVersion = chooseVersion(calculateBitsNeeded(mode, headerBits, dataBits, Version.getVersionForNumber(1)), ecLevel);
      return chooseVersion(calculateBitsNeeded(mode, headerBits, dataBits, provisionalVersion), ecLevel);
   }

   private static int calculateBitsNeeded(Mode mode, BitArray headerBits, BitArray dataBits, Version version) {
      return headerBits.getSize() + mode.getCharacterCountBits(version) + dataBits.getSize();
   }

   static int getAlphanumericCode(int code) {
      return code < ALPHANUMERIC_TABLE.length ? ALPHANUMERIC_TABLE[code] : -1;
   }

   public static Mode chooseMode(String content) {
      return chooseMode(content, (String)null);
   }

   private static Mode chooseMode(String content, String encoding) {
      if ("Shift_JIS".equals(encoding) && isOnlyDoubleByteKanji(content)) {
         return Mode.KANJI;
      } else {
         boolean hasNumeric = false;
         boolean hasAlphanumeric = false;

         for(int i = 0; i < content.length(); ++i) {
            char c;
            if ((c = content.charAt(i)) >= '0' && c <= '9') {
               hasNumeric = true;
            } else {
               if (getAlphanumericCode(c) == -1) {
                  return Mode.BYTE;
               }

               hasAlphanumeric = true;
            }
         }

         if (hasAlphanumeric) {
            return Mode.ALPHANUMERIC;
         } else if (hasNumeric) {
            return Mode.NUMERIC;
         } else {
            return Mode.BYTE;
         }
      }
   }

   private static boolean isOnlyDoubleByteKanji(String content) {
      byte[] bytes;
      try {
         bytes = content.getBytes("Shift_JIS");
      } catch (UnsupportedEncodingException var5) {
         return false;
      }

      int length;
      if ((length = bytes.length) % 2 != 0) {
         return false;
      } else {
         for(int i = 0; i < length; i += 2) {
            int byte1;
            if (((byte1 = bytes[i] & 255) < 129 || byte1 > 159) && (byte1 < 224 || byte1 > 235)) {
               return false;
            }
         }

         return true;
      }
   }

   private static int chooseMaskPattern(BitArray bits, ErrorCorrectionLevel ecLevel, Version version, ByteMatrix matrix) throws WriterException {
      int minPenalty = Integer.MAX_VALUE;
      int bestMaskPattern = -1;

      for(int maskPattern = 0; maskPattern < 8; ++maskPattern) {
         MatrixUtil.buildMatrix(bits, ecLevel, version, maskPattern, matrix);
         int penalty;
         if ((penalty = calculateMaskPenalty(matrix)) < minPenalty) {
            minPenalty = penalty;
            bestMaskPattern = maskPattern;
         }
      }

      return bestMaskPattern;
   }

   private static Version chooseVersion(int numInputBits, ErrorCorrectionLevel ecLevel) throws WriterException {
      for(int versionNum = 1; versionNum <= 40; ++versionNum) {
         Version version = Version.getVersionForNumber(versionNum);
         if (willFit(numInputBits, version, ecLevel)) {
            return version;
         }
      }

      throw new WriterException("Data too big");
   }

   private static boolean willFit(int numInputBits, Version version, ErrorCorrectionLevel ecLevel) {
      int numBytes = version.getTotalCodewords();
      int numEcBytes = version.getECBlocksForLevel(ecLevel).getTotalECCodewords();
      int numDataBytes = numBytes - numEcBytes;
      int totalInputBytes = (numInputBits + 7) / 8;
      return numDataBytes >= totalInputBytes;
   }

   static void terminateBits(int numDataBytes, BitArray bits) throws WriterException {
      int capacity = numDataBytes << 3;
      if (bits.getSize() > capacity) {
         throw new WriterException("data bits cannot fit in the QR Code" + bits.getSize() + " > " + capacity);
      } else {
         int numBitsInLastByte;
         for(numBitsInLastByte = 0; numBitsInLastByte < 4 && bits.getSize() < capacity; ++numBitsInLastByte) {
            bits.appendBit(false);
         }

         int numPaddingBytes;
         if ((numBitsInLastByte = bits.getSize() & 7) > 0) {
            for(numPaddingBytes = numBitsInLastByte; numPaddingBytes < 8; ++numPaddingBytes) {
               bits.appendBit(false);
            }
         }

         numPaddingBytes = numDataBytes - bits.getSizeInBytes();

         for(int i = 0; i < numPaddingBytes; ++i) {
            bits.appendBits((i & 1) == 0 ? 236 : 17, 8);
         }

         if (bits.getSize() != capacity) {
            throw new WriterException("Bits size does not equal capacity");
         }
      }
   }

   static void getNumDataBytesAndNumECBytesForBlockID(int numTotalBytes, int numDataBytes, int numRSBlocks, int blockID, int[] numDataBytesInBlock, int[] numECBytesInBlock) throws WriterException {
      if (blockID >= numRSBlocks) {
         throw new WriterException("Block ID too large");
      } else {
         int numRsBlocksInGroup2 = numTotalBytes % numRSBlocks;
         int numRsBlocksInGroup1 = numRSBlocks - numRsBlocksInGroup2;
         int numTotalBytesInGroup1;
         int numTotalBytesInGroup2 = (numTotalBytesInGroup1 = numTotalBytes / numRSBlocks) + 1;
         int numDataBytesInGroup1;
         int numDataBytesInGroup2 = (numDataBytesInGroup1 = numDataBytes / numRSBlocks) + 1;
         int numEcBytesInGroup1 = numTotalBytesInGroup1 - numDataBytesInGroup1;
         int numEcBytesInGroup2 = numTotalBytesInGroup2 - numDataBytesInGroup2;
         if (numEcBytesInGroup1 != numEcBytesInGroup2) {
            throw new WriterException("EC bytes mismatch");
         } else if (numRSBlocks != numRsBlocksInGroup1 + numRsBlocksInGroup2) {
            throw new WriterException("RS blocks mismatch");
         } else if (numTotalBytes != (numDataBytesInGroup1 + numEcBytesInGroup1) * numRsBlocksInGroup1 + (numDataBytesInGroup2 + numEcBytesInGroup2) * numRsBlocksInGroup2) {
            throw new WriterException("Total bytes mismatch");
         } else if (blockID < numRsBlocksInGroup1) {
            numDataBytesInBlock[0] = numDataBytesInGroup1;
            numECBytesInBlock[0] = numEcBytesInGroup1;
         } else {
            numDataBytesInBlock[0] = numDataBytesInGroup2;
            numECBytesInBlock[0] = numEcBytesInGroup2;
         }
      }
   }

   static BitArray interleaveWithECBytes(BitArray bits, int numTotalBytes, int numDataBytes, int numRSBlocks) throws WriterException {
      if (bits.getSizeInBytes() != numDataBytes) {
         throw new WriterException("Number of bits and data bytes does not match");
      } else {
         int dataBytesOffset = 0;
         int maxNumDataBytes = 0;
         int maxNumEcBytes = 0;
         Collection<BlockPair> blocks = new ArrayList(numRSBlocks);

         byte[] ecBytes;
         for(int i = 0; i < numRSBlocks; ++i) {
            int[] numDataBytesInBlock = new int[1];
            int[] numEcBytesInBlock = new int[1];
            getNumDataBytesAndNumECBytesForBlockID(numTotalBytes, numDataBytes, numRSBlocks, i, numDataBytesInBlock, numEcBytesInBlock);
            int size;
            ecBytes = new byte[size = numDataBytesInBlock[0]];
            bits.toBytes(dataBytesOffset << 3, ecBytes, 0, size);
            byte[] ecBytes = generateECBytes(ecBytes, numEcBytesInBlock[0]);
            blocks.add(new BlockPair(ecBytes, ecBytes));
            maxNumDataBytes = Math.max(maxNumDataBytes, size);
            maxNumEcBytes = Math.max(maxNumEcBytes, ecBytes.length);
            dataBytesOffset += numDataBytesInBlock[0];
         }

         if (numDataBytes != dataBytesOffset) {
            throw new WriterException("Data bytes does not match offset");
         } else {
            BitArray result = new BitArray();

            int i;
            Iterator var16;
            for(i = 0; i < maxNumDataBytes; ++i) {
               var16 = blocks.iterator();

               while(var16.hasNext()) {
                  ecBytes = ((BlockPair)var16.next()).getDataBytes();
                  if (i < ecBytes.length) {
                     result.appendBits(ecBytes[i], 8);
                  }
               }
            }

            for(i = 0; i < maxNumEcBytes; ++i) {
               var16 = blocks.iterator();

               while(var16.hasNext()) {
                  ecBytes = ((BlockPair)var16.next()).getErrorCorrectionBytes();
                  if (i < ecBytes.length) {
                     result.appendBits(ecBytes[i], 8);
                  }
               }
            }

            if (numTotalBytes != result.getSizeInBytes()) {
               throw new WriterException("Interleaving error: " + numTotalBytes + " and " + result.getSizeInBytes() + " differ.");
            } else {
               return result;
            }
         }
      }
   }

   static byte[] generateECBytes(byte[] dataBytes, int numEcBytesInBlock) {
      int numDataBytes;
      int[] toEncode = new int[(numDataBytes = dataBytes.length) + numEcBytesInBlock];

      for(int i = 0; i < numDataBytes; ++i) {
         toEncode[i] = dataBytes[i] & 255;
      }

      (new ReedSolomonEncoder(GenericGF.QR_CODE_FIELD_256)).encode(toEncode, numEcBytesInBlock);
      byte[] ecBytes = new byte[numEcBytesInBlock];

      for(int i = 0; i < numEcBytesInBlock; ++i) {
         ecBytes[i] = (byte)toEncode[numDataBytes + i];
      }

      return ecBytes;
   }

   static void appendModeInfo(Mode mode, BitArray bits) {
      bits.appendBits(mode.getBits(), 4);
   }

   static void appendLengthInfo(int numLetters, Version version, Mode mode, BitArray bits) throws WriterException {
      int numBits = mode.getCharacterCountBits(version);
      if (numLetters >= 1 << numBits) {
         throw new WriterException(numLetters + " is bigger than " + ((1 << numBits) - 1));
      } else {
         bits.appendBits(numLetters, numBits);
      }
   }

   static void appendBytes(String content, Mode mode, BitArray bits, String encoding) throws WriterException {
      switch (mode) {
         case NUMERIC:
            appendNumericBytes(content, bits);
            return;
         case ALPHANUMERIC:
            appendAlphanumericBytes(content, bits);
            return;
         case BYTE:
            append8BitBytes(content, bits, encoding);
            return;
         case KANJI:
            appendKanjiBytes(content, bits);
            return;
         default:
            throw new WriterException("Invalid mode: " + mode);
      }
   }

   static void appendNumericBytes(CharSequence content, BitArray bits) {
      int length = content.length();
      int i = 0;

      while(i < length) {
         int num1 = content.charAt(i) - 48;
         int num2;
         if (i + 2 < length) {
            num2 = content.charAt(i + 1) - 48;
            int num3 = content.charAt(i + 2) - 48;
            bits.appendBits(num1 * 100 + num2 * 10 + num3, 10);
            i += 3;
         } else if (i + 1 < length) {
            num2 = content.charAt(i + 1) - 48;
            bits.appendBits(num1 * 10 + num2, 7);
            i += 2;
         } else {
            bits.appendBits(num1, 4);
            ++i;
         }
      }

   }

   static void appendAlphanumericBytes(CharSequence content, BitArray bits) throws WriterException {
      int length = content.length();
      int i = 0;

      while(i < length) {
         int code1;
         if ((code1 = getAlphanumericCode(content.charAt(i))) == -1) {
            throw new WriterException();
         }

         if (i + 1 < length) {
            int code2;
            if ((code2 = getAlphanumericCode(content.charAt(i + 1))) == -1) {
               throw new WriterException();
            }

            bits.appendBits(code1 * 45 + code2, 11);
            i += 2;
         } else {
            bits.appendBits(code1, 6);
            ++i;
         }
      }

   }

   static void append8BitBytes(String content, BitArray bits, String encoding) throws WriterException {
      byte[] bytes;
      try {
         bytes = content.getBytes(encoding);
      } catch (UnsupportedEncodingException var8) {
         throw new WriterException(var8);
      }

      byte[] var4 = bytes;
      int var5 = bytes.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         byte b = var4[var6];
         bits.appendBits(b, 8);
      }

   }

   static void appendKanjiBytes(String content, BitArray bits) throws WriterException {
      byte[] bytes;
      try {
         bytes = content.getBytes("Shift_JIS");
      } catch (UnsupportedEncodingException var10) {
         throw new WriterException(var10);
      }

      int length = bytes.length;

      for(int i = 0; i < length; i += 2) {
         int byte1 = bytes[i] & 255;
         int byte2 = bytes[i + 1] & 255;
         int code = byte1 << 8 | byte2;
         int subtracted = -1;
         if (code >= 33088 && code <= 40956) {
            subtracted = code - '腀';
         } else if (code >= 57408 && code <= 60351) {
            subtracted = code - '셀';
         }

         if (subtracted == -1) {
            throw new WriterException("Invalid byte sequence");
         }

         int encoded = (subtracted >> 8) * 192 + (subtracted & 255);
         bits.appendBits(encoded, 13);
      }

   }

   private static void appendECI(CharacterSetECI eci, BitArray bits) {
      bits.appendBits(Mode.ECI.getBits(), 4);
      bits.appendBits(eci.getValue(), 8);
   }
}
