package cn.hutool.core.codec;

import cn.hutool.core.util.StrUtil;
import java.util.Arrays;

public class Base58Codec implements Encoder<byte[], String>, Decoder<CharSequence, byte[]> {
   public static Base58Codec INSTANCE = new Base58Codec();

   public String encode(byte[] data) {
      return Base58Codec.Base58Encoder.ENCODER.encode(data);
   }

   public byte[] decode(CharSequence encoded) throws IllegalArgumentException {
      return Base58Codec.Base58Decoder.DECODER.decode(encoded);
   }

   private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
      int remainder = 0;

      for(int i = firstDigit; i < number.length; ++i) {
         int digit = number[i] & 255;
         int temp = remainder * base + digit;
         number[i] = (byte)(temp / divisor);
         remainder = temp % divisor;
      }

      return (byte)remainder;
   }

   public static class Base58Decoder implements Decoder<CharSequence, byte[]> {
      public static Base58Decoder DECODER = new Base58Decoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz");
      private final byte[] lookupTable;

      public Base58Decoder(String alphabet) {
         byte[] lookupTable = new byte[123];
         Arrays.fill(lookupTable, (byte)-1);
         int length = alphabet.length();

         for(int i = 0; i < length; ++i) {
            lookupTable[alphabet.charAt(i)] = (byte)i;
         }

         this.lookupTable = lookupTable;
      }

      public byte[] decode(CharSequence encoded) {
         if (encoded.length() == 0) {
            return new byte[0];
         } else {
            byte[] input58 = new byte[encoded.length()];

            int zeros;
            int outputStart;
            for(zeros = 0; zeros < encoded.length(); ++zeros) {
               char c = encoded.charAt(zeros);
               outputStart = c < 128 ? this.lookupTable[c] : -1;
               if (outputStart < 0) {
                  throw new IllegalArgumentException(StrUtil.format("Invalid char '{}' at [{}]", new Object[]{c, zeros}));
               }

               input58[zeros] = (byte)outputStart;
            }

            for(zeros = 0; zeros < input58.length && input58[zeros] == 0; ++zeros) {
            }

            byte[] decoded = new byte[encoded.length()];
            outputStart = decoded.length;
            int inputStart = zeros;

            while(inputStart < input58.length) {
               --outputStart;
               decoded[outputStart] = Base58Codec.divmod(input58, inputStart, 58, 256);
               if (input58[inputStart] == 0) {
                  ++inputStart;
               }
            }

            while(outputStart < decoded.length && decoded[outputStart] == 0) {
               ++outputStart;
            }

            return Arrays.copyOfRange(decoded, outputStart - zeros, decoded.length);
         }
      }
   }

   public static class Base58Encoder implements Encoder<byte[], String> {
      private static final String DEFAULT_ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz";
      public static final Base58Encoder ENCODER = new Base58Encoder("123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray());
      private final char[] alphabet;
      private final char alphabetZero;

      public Base58Encoder(char[] alphabet) {
         this.alphabet = alphabet;
         this.alphabetZero = alphabet[0];
      }

      public String encode(byte[] data) {
         if (null == data) {
            return null;
         } else if (data.length == 0) {
            return "";
         } else {
            int zeroCount;
            for(zeroCount = 0; zeroCount < data.length && data[zeroCount] == 0; ++zeroCount) {
            }

            data = Arrays.copyOf(data, data.length);
            char[] encoded = new char[data.length * 2];
            int outputStart = encoded.length;
            int inputStart = zeroCount;

            while(inputStart < data.length) {
               --outputStart;
               encoded[outputStart] = this.alphabet[Base58Codec.divmod(data, inputStart, 256, 58)];
               if (data[inputStart] == 0) {
                  ++inputStart;
               }
            }

            while(outputStart < encoded.length && encoded[outputStart] == this.alphabetZero) {
               ++outputStart;
            }

            while(true) {
               --zeroCount;
               if (zeroCount < 0) {
                  return new String(encoded, outputStart, encoded.length - outputStart);
               }

               --outputStart;
               encoded[outputStart] = this.alphabetZero;
            }
         }
      }
   }
}
