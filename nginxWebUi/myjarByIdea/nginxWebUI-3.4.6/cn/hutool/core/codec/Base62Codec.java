package cn.hutool.core.codec;

import cn.hutool.core.util.ArrayUtil;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class Base62Codec implements Encoder<byte[], byte[]>, Decoder<byte[], byte[]>, Serializable {
   private static final long serialVersionUID = 1L;
   private static final int STANDARD_BASE = 256;
   private static final int TARGET_BASE = 62;
   public static Base62Codec INSTANCE = new Base62Codec();

   public byte[] encode(byte[] data) {
      return this.encode(data, false);
   }

   public byte[] encode(byte[] data, boolean useInverted) {
      Base62Encoder encoder = useInverted ? Base62Codec.Base62Encoder.INVERTED_ENCODER : Base62Codec.Base62Encoder.GMP_ENCODER;
      return encoder.encode(data);
   }

   public byte[] decode(byte[] encoded) {
      return this.decode(encoded, false);
   }

   public byte[] decode(byte[] encoded, boolean useInverted) {
      Base62Decoder decoder = useInverted ? Base62Codec.Base62Decoder.INVERTED_DECODER : Base62Codec.Base62Decoder.GMP_DECODER;
      return decoder.decode(encoded);
   }

   private static byte[] translate(byte[] indices, byte[] dictionary) {
      byte[] translation = new byte[indices.length];

      for(int i = 0; i < indices.length; ++i) {
         translation[i] = dictionary[indices[i]];
      }

      return translation;
   }

   private static byte[] convert(byte[] message, int sourceBase, int targetBase) {
      int estimatedLength = estimateOutputLength(message.length, sourceBase, targetBase);
      ByteArrayOutputStream out = new ByteArrayOutputStream(estimatedLength);

      ByteArrayOutputStream quotient;
      for(byte[] source = message; source.length > 0; source = quotient.toByteArray()) {
         quotient = new ByteArrayOutputStream(source.length);
         int remainder = 0;
         byte[] var8 = source;
         int var9 = source.length;

         for(int var10 = 0; var10 < var9; ++var10) {
            byte b = var8[var10];
            int accumulator = (b & 255) + remainder * sourceBase;
            int digit = (accumulator - accumulator % targetBase) / targetBase;
            remainder = accumulator % targetBase;
            if (quotient.size() > 0 || digit > 0) {
               quotient.write(digit);
            }
         }

         out.write(remainder);
      }

      for(int i = 0; i < message.length - 1 && message[i] == 0; ++i) {
         out.write(0);
      }

      return ArrayUtil.reverse(out.toByteArray());
   }

   private static int estimateOutputLength(int inputLength, int sourceBase, int targetBase) {
      return (int)Math.ceil(Math.log((double)sourceBase) / Math.log((double)targetBase) * (double)inputLength);
   }

   public static class Base62Decoder implements Decoder<byte[], byte[]> {
      public static Base62Decoder GMP_DECODER;
      public static Base62Decoder INVERTED_DECODER;
      private final byte[] lookupTable = new byte[123];

      public Base62Decoder(byte[] alphabet) {
         for(int i = 0; i < alphabet.length; ++i) {
            this.lookupTable[alphabet[i]] = (byte)i;
         }

      }

      public byte[] decode(byte[] encoded) {
         byte[] prepared = Base62Codec.translate(encoded, this.lookupTable);
         return Base62Codec.convert(prepared, 62, 256);
      }

      static {
         GMP_DECODER = new Base62Decoder(Base62Codec.Base62Encoder.GMP);
         INVERTED_DECODER = new Base62Decoder(Base62Codec.Base62Encoder.INVERTED);
      }
   }

   public static class Base62Encoder implements Encoder<byte[], byte[]> {
      private static final byte[] GMP = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122};
      private static final byte[] INVERTED = new byte[]{48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109, 110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90};
      public static Base62Encoder GMP_ENCODER;
      public static Base62Encoder INVERTED_ENCODER;
      private final byte[] alphabet;

      public Base62Encoder(byte[] alphabet) {
         this.alphabet = alphabet;
      }

      public byte[] encode(byte[] data) {
         byte[] indices = Base62Codec.convert(data, 256, 62);
         return Base62Codec.translate(indices, this.alphabet);
      }

      static {
         GMP_ENCODER = new Base62Encoder(GMP);
         INVERTED_ENCODER = new Base62Encoder(INVERTED);
      }
   }
}
