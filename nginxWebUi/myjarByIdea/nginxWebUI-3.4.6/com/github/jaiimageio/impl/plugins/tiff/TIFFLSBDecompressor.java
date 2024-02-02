package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import java.io.IOException;

public class TIFFLSBDecompressor extends TIFFDecompressor {
   private static byte[] flipTable;

   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      this.stream.seek(this.offset);
      int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
      int y;
      int xMax;
      int x;
      if (bytesPerRow == scanlineStride) {
         y = bytesPerRow * this.srcHeight;
         this.stream.readFully(b, dstOffset, y);
         xMax = dstOffset + y;

         for(x = dstOffset; x < xMax; ++x) {
            b[x] = flipTable[b[x] & 255];
         }
      } else {
         for(y = 0; y < this.srcHeight; ++y) {
            this.stream.readFully(b, dstOffset, bytesPerRow);
            xMax = dstOffset + bytesPerRow;

            for(x = dstOffset; x < xMax; ++x) {
               b[x] = flipTable[b[x] & 255];
            }

            dstOffset += scanlineStride;
         }
      }

   }

   static {
      flipTable = TIFFFaxDecompressor.flipTable;
   }
}
