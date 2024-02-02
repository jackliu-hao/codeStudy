package cn.hutool.core.codec;

import java.util.Arrays;

public class Base32Codec implements Encoder<byte[], String>, Decoder<CharSequence, byte[]> {
   public static Base32Codec INSTANCE = new Base32Codec();

   public String encode(byte[] data) {
      return this.encode(data, false);
   }

   public String encode(byte[] data, boolean useHex) {
      Base32Encoder encoder = useHex ? Base32Codec.Base32Encoder.HEX_ENCODER : Base32Codec.Base32Encoder.ENCODER;
      return encoder.encode(data);
   }

   public byte[] decode(CharSequence encoded) {
      return this.decode(encoded, false);
   }

   public byte[] decode(CharSequence encoded, boolean useHex) {
      Base32Decoder decoder = useHex ? Base32Codec.Base32Decoder.HEX_DECODER : Base32Codec.Base32Decoder.DECODER;
      return decoder.decode(encoded);
   }

   public static class Base32Decoder implements Decoder<CharSequence, byte[]> {
      private static final char BASE_CHAR = '0';
      public static final Base32Decoder DECODER = new Base32Decoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567");
      public static final Base32Decoder HEX_DECODER = new Base32Decoder("0123456789ABCDEFGHIJKLMNOPQRSTUV");
      private final byte[] lookupTable = new byte[128];

      public Base32Decoder(String alphabet) {
         Arrays.fill(this.lookupTable, (byte)-1);
         int length = alphabet.length();

         for(int i = 0; i < length; ++i) {
            char c = alphabet.charAt(i);
            this.lookupTable[c - 48] = (byte)i;
            if (c >= 'A' && c <= 'Z') {
               this.lookupTable[Character.toLowerCase(c) - 48] = (byte)i;
            }
         }

      }

      public byte[] decode(CharSequence encoded) {
         String base32 = encoded.toString();
         int len = base32.endsWith("=") ? base32.indexOf("=") * 5 / 8 : base32.length() * 5 / 8;
         byte[] bytes = new byte[len];
         int i = 0;
         int index = 0;

         for(int offset = 0; i < base32.length(); ++i) {
            int lookup = base32.charAt(i) - 48;
            if (lookup >= 0 && lookup < this.lookupTable.length) {
               int digit = this.lookupTable[lookup];
               if (digit >= 0) {
                  if (index <= 3) {
                     index = (index + 5) % 8;
                     if (index == 0) {
                        bytes[offset] |= digit;
                        ++offset;
                        if (offset >= bytes.length) {
                           break;
                        }
                     } else {
                        bytes[offset] = (byte)(bytes[offset] | digit << 8 - index);
                     }
                  } else {
                     index = (index + 5) % 8;
                     bytes[offset] = (byte)(bytes[offset] | digit >>> index);
                     ++offset;
                     if (offset >= bytes.length) {
                        break;
                     }

                     bytes[offset] = (byte)(bytes[offset] | digit << 8 - index);
                  }
               }
            }
         }

         return bytes;
      }
   }

   public static class Base32Encoder implements Encoder<byte[], String> {
      private static final String DEFAULT_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ234567";
      private static final String HEX_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUV";
      private static final Character DEFAULT_PAD = '=';
      private static final int[] BASE32_FILL = new int[]{-1, 4, 1, 6, 3};
      public static final Base32Encoder ENCODER;
      public static final Base32Encoder HEX_ENCODER;
      private final char[] alphabet;
      private final Character pad;

      public Base32Encoder(String alphabet, Character pad) {
         this.alphabet = alphabet.toCharArray();
         this.pad = pad;
      }

      public String encode(byte[] data) {
         int i = 0;
         int index = 0;
         int encodeLen = data.length * 8 / 5;
         if (encodeLen != 0) {
            encodeLen = encodeLen + 1 + BASE32_FILL[data.length * 8 % 5];
         }

         int digit;
         StringBuilder base32;
         for(base32 = new StringBuilder(encodeLen); i < data.length; base32.append(this.alphabet[digit])) {
            int currByte = data[i] >= 0 ? data[i] : data[i] + 256;
            if (index > 3) {
               int nextByte;
               if (i + 1 < data.length) {
                  nextByte = data[i + 1] >= 0 ? data[i + 1] : data[i + 1] + 256;
               } else {
                  nextByte = 0;
               }

               digit = currByte & 255 >> index;
               index = (index + 5) % 8;
               digit <<= index;
               digit |= nextByte >> 8 - index;
               ++i;
            } else {
               digit = currByte >> 8 - (index + 5) & 31;
               index = (index + 5) % 8;
               if (index == 0) {
                  ++i;
               }
            }
         }

         if (null != this.pad) {
            while(base32.length() < encodeLen) {
               base32.append(this.pad);
            }
         }

         return base32.toString();
      }

      static {
         ENCODER = new Base32Encoder("ABCDEFGHIJKLMNOPQRSTUVWXYZ234567", DEFAULT_PAD);
         HEX_ENCODER = new Base32Encoder("0123456789ABCDEFGHIJKLMNOPQRSTUV", DEFAULT_PAD);
      }
   }
}
