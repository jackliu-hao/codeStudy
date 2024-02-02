package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
import java.io.IOException;

public class TIFFPackBitsCompressor extends TIFFCompressor {
   public TIFFPackBitsCompressor() {
      super("PackBits", 32773, true);
   }

   private static int packBits(byte[] input, int inOffset, int inCount, byte[] output, int outOffset) {
      int inMax = inOffset + inCount - 1;
      int inMaxMinus1 = inMax - 1;

      while(true) {
         while(true) {
            int run;
            do {
               if (inOffset > inMax) {
                  return outOffset;
               }

               run = 1;

               byte replicate;
               for(replicate = input[inOffset]; run < 127 && inOffset < inMax && input[inOffset] == input[inOffset + 1]; ++inOffset) {
                  ++run;
               }

               if (run > 1) {
                  ++inOffset;
                  output[outOffset++] = (byte)(-(run - 1));
                  output[outOffset++] = replicate;
               }

               for(run = 0; run < 128 && (inOffset < inMax && input[inOffset] != input[inOffset + 1] || inOffset < inMaxMinus1 && input[inOffset] != input[inOffset + 2]); output[outOffset] = input[inOffset++]) {
                  ++run;
                  ++outOffset;
               }

               if (run > 0) {
                  output[outOffset] = (byte)(run - 1);
                  ++outOffset;
               }
            } while(inOffset != inMax);

            if (run > 0 && run < 128) {
               ++output[outOffset];
               output[outOffset++] = input[inOffset++];
            } else {
               output[outOffset++] = 0;
               output[outOffset++] = input[inOffset++];
            }
         }
      }
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      int bitsPerPixel = 0;

      int bytesPerRow;
      for(bytesPerRow = 0; bytesPerRow < bitsPerSample.length; ++bytesPerRow) {
         bitsPerPixel += bitsPerSample[bytesPerRow];
      }

      bytesPerRow = (bitsPerPixel * width + 7) / 8;
      int bufSize = bytesPerRow + (bytesPerRow + 127) / 128;
      byte[] compData = new byte[bufSize];
      int bytesWritten = 0;

      for(int i = 0; i < height; ++i) {
         int bytes = packBits(b, off, scanlineStride, compData, 0);
         off += scanlineStride;
         bytesWritten += bytes;
         this.stream.write(compData, 0, bytes);
      }

      return bytesWritten;
   }
}
