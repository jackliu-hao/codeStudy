package com.google.zxing.qrcode.decoder;

import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.common.BitSource;
import com.google.zxing.common.CharacterSetECI;
import com.google.zxing.common.DecoderResult;
import com.google.zxing.common.StringUtils;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

final class DecodedBitStreamParser {
   private static final char[] ALPHANUMERIC_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ $%*+-./:".toCharArray();
   private static final int GB2312_SUBSET = 1;

   private DecodedBitStreamParser() {
   }

   static DecoderResult decode(byte[] bytes, Version version, ErrorCorrectionLevel ecLevel, Map<DecodeHintType, ?> hints) throws FormatException {
      BitSource bits = new BitSource(bytes);
      StringBuilder result = new StringBuilder(50);
      List<byte[]> byteSegments = new ArrayList(1);
      int symbolSequence = -1;
      int parityData = -1;

      try {
         CharacterSetECI currentCharacterSetECI = null;
         boolean fc1InEffect = false;

         Mode mode;
         do {
            if (bits.available() < 4) {
               mode = Mode.TERMINATOR;
            } else {
               mode = Mode.forBits(bits.readBits(4));
            }

            if (mode != Mode.TERMINATOR) {
               if (mode != Mode.FNC1_FIRST_POSITION && mode != Mode.FNC1_SECOND_POSITION) {
                  if (mode == Mode.STRUCTURED_APPEND) {
                     if (bits.available() < 16) {
                        throw FormatException.getFormatInstance();
                     }

                     symbolSequence = bits.readBits(8);
                     parityData = bits.readBits(8);
                  } else if (mode == Mode.ECI) {
                     if ((currentCharacterSetECI = CharacterSetECI.getCharacterSetECIByValue(parseECIValue(bits))) == null) {
                        throw FormatException.getFormatInstance();
                     }
                  } else {
                     int count;
                     if (mode == Mode.HANZI) {
                        count = bits.readBits(4);
                        int countHanzi = bits.readBits(mode.getCharacterCountBits(version));
                        if (count == 1) {
                           decodeHanziSegment(bits, result, countHanzi);
                        }
                     } else {
                        count = bits.readBits(mode.getCharacterCountBits(version));
                        if (mode == Mode.NUMERIC) {
                           decodeNumericSegment(bits, result, count);
                        } else if (mode == Mode.ALPHANUMERIC) {
                           decodeAlphanumericSegment(bits, result, count, fc1InEffect);
                        } else if (mode == Mode.BYTE) {
                           decodeByteSegment(bits, result, count, currentCharacterSetECI, byteSegments, hints);
                        } else {
                           if (mode != Mode.KANJI) {
                              throw FormatException.getFormatInstance();
                           }

                           decodeKanjiSegment(bits, result, count);
                        }
                     }
                  }
               } else {
                  fc1InEffect = true;
               }
            }
         } while(mode != Mode.TERMINATOR);
      } catch (IllegalArgumentException var14) {
         throw FormatException.getFormatInstance();
      }

      return new DecoderResult(bytes, result.toString(), byteSegments.isEmpty() ? null : byteSegments, ecLevel == null ? null : ecLevel.toString(), symbolSequence, parityData);
   }

   private static void decodeHanziSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
      if (count * 13 > bits.available()) {
         throw FormatException.getFormatInstance();
      } else {
         byte[] buffer = new byte[2 * count];

         for(int offset = 0; count > 0; --count) {
            int twoBytes;
            int assembledTwoBytes;
            if ((assembledTwoBytes = (twoBytes = bits.readBits(13)) / 96 << 8 | twoBytes % 96) < 959) {
               assembledTwoBytes += 41377;
            } else {
               assembledTwoBytes += 42657;
            }

            buffer[offset] = (byte)(assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte)assembledTwoBytes;
            offset += 2;
         }

         try {
            result.append(new String(buffer, "GB2312"));
         } catch (UnsupportedEncodingException var7) {
            throw FormatException.getFormatInstance();
         }
      }
   }

   private static void decodeKanjiSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
      if (count * 13 > bits.available()) {
         throw FormatException.getFormatInstance();
      } else {
         byte[] buffer = new byte[2 * count];

         for(int offset = 0; count > 0; --count) {
            int twoBytes;
            int assembledTwoBytes;
            if ((assembledTwoBytes = (twoBytes = bits.readBits(13)) / 192 << 8 | twoBytes % 192) < 7936) {
               assembledTwoBytes += 33088;
            } else {
               assembledTwoBytes += 49472;
            }

            buffer[offset] = (byte)(assembledTwoBytes >> 8);
            buffer[offset + 1] = (byte)assembledTwoBytes;
            offset += 2;
         }

         try {
            result.append(new String(buffer, "SJIS"));
         } catch (UnsupportedEncodingException var7) {
            throw FormatException.getFormatInstance();
         }
      }
   }

   private static void decodeByteSegment(BitSource bits, StringBuilder result, int count, CharacterSetECI currentCharacterSetECI, Collection<byte[]> byteSegments, Map<DecodeHintType, ?> hints) throws FormatException {
      if (count << 3 > bits.available()) {
         throw FormatException.getFormatInstance();
      } else {
         byte[] readBytes = new byte[count];

         for(int i = 0; i < count; ++i) {
            readBytes[i] = (byte)bits.readBits(8);
         }

         String encoding;
         if (currentCharacterSetECI == null) {
            encoding = StringUtils.guessEncoding(readBytes, hints);
         } else {
            encoding = currentCharacterSetECI.name();
         }

         try {
            result.append(new String(readBytes, encoding));
         } catch (UnsupportedEncodingException var8) {
            throw FormatException.getFormatInstance();
         }

         byteSegments.add(readBytes);
      }
   }

   private static char toAlphaNumericChar(int value) throws FormatException {
      if (value >= ALPHANUMERIC_CHARS.length) {
         throw FormatException.getFormatInstance();
      } else {
         return ALPHANUMERIC_CHARS[value];
      }
   }

   private static void decodeAlphanumericSegment(BitSource bits, StringBuilder result, int count, boolean fc1InEffect) throws FormatException {
      int start;
      int i;
      for(start = result.length(); count > 1; count -= 2) {
         if (bits.available() < 11) {
            throw FormatException.getFormatInstance();
         }

         i = bits.readBits(11);
         result.append(toAlphaNumericChar(i / 45));
         result.append(toAlphaNumericChar(i % 45));
      }

      if (count == 1) {
         if (bits.available() < 6) {
            throw FormatException.getFormatInstance();
         }

         result.append(toAlphaNumericChar(bits.readBits(6)));
      }

      if (fc1InEffect) {
         for(i = start; i < result.length(); ++i) {
            if (result.charAt(i) == '%') {
               if (i < result.length() - 1 && result.charAt(i + 1) == '%') {
                  result.deleteCharAt(i + 1);
               } else {
                  result.setCharAt(i, '\u001d');
               }
            }
         }
      }

   }

   private static void decodeNumericSegment(BitSource bits, StringBuilder result, int count) throws FormatException {
      int digitBits;
      while(count >= 3) {
         if (bits.available() < 10) {
            throw FormatException.getFormatInstance();
         }

         if ((digitBits = bits.readBits(10)) >= 1000) {
            throw FormatException.getFormatInstance();
         }

         result.append(toAlphaNumericChar(digitBits / 100));
         result.append(toAlphaNumericChar(digitBits / 10 % 10));
         result.append(toAlphaNumericChar(digitBits % 10));
         count -= 3;
      }

      if (count == 2) {
         if (bits.available() < 7) {
            throw FormatException.getFormatInstance();
         } else if ((digitBits = bits.readBits(7)) >= 100) {
            throw FormatException.getFormatInstance();
         } else {
            result.append(toAlphaNumericChar(digitBits / 10));
            result.append(toAlphaNumericChar(digitBits % 10));
         }
      } else {
         if (count == 1) {
            if (bits.available() < 4) {
               throw FormatException.getFormatInstance();
            }

            if ((digitBits = bits.readBits(4)) >= 10) {
               throw FormatException.getFormatInstance();
            }

            result.append(toAlphaNumericChar(digitBits));
         }

      }
   }

   private static int parseECIValue(BitSource bits) throws FormatException {
      int firstByte;
      if (((firstByte = bits.readBits(8)) & 128) == 0) {
         return firstByte & 127;
      } else {
         int secondThirdBytes;
         if ((firstByte & 192) == 128) {
            secondThirdBytes = bits.readBits(8);
            return (firstByte & 63) << 8 | secondThirdBytes;
         } else if ((firstByte & 224) == 192) {
            secondThirdBytes = bits.readBits(16);
            return (firstByte & 31) << 16 | secondThirdBytes;
         } else {
            throw FormatException.getFormatInstance();
         }
      }
   }
}
