package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFCompressor;
import java.io.IOException;

public class TIFFNullCompressor extends TIFFCompressor {
   public TIFFNullCompressor() {
      super("", 1, true);
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      int bitsPerPixel = 0;

      int bytesPerRow;
      for(bytesPerRow = 0; bytesPerRow < bitsPerSample.length; ++bytesPerRow) {
         bitsPerPixel += bitsPerSample[bytesPerRow];
      }

      bytesPerRow = (bitsPerPixel * width + 7) / 8;
      int numBytes = height * bytesPerRow;
      if (bytesPerRow == scanlineStride) {
         this.stream.write(b, off, numBytes);
      } else {
         for(int row = 0; row < height; ++row) {
            this.stream.write(b, off, bytesPerRow);
            off += scanlineStride;
         }
      }

      return numBytes;
   }
}
