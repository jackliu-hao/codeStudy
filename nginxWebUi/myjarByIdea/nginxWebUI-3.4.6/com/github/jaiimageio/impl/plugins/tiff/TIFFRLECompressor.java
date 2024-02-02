package com.github.jaiimageio.impl.plugins.tiff;

import java.io.IOException;
import javax.imageio.IIOException;

public class TIFFRLECompressor extends TIFFFaxCompressor {
   public TIFFRLECompressor() {
      super("CCITT RLE", 2, true);
   }

   public int encodeRLE(byte[] data, int rowOffset, int colOffset, int rowLength, byte[] compData) {
      this.initBitBuf();

      int outIndex;
      for(outIndex = this.encode1D(data, rowOffset, colOffset, rowLength, compData, 0); this.ndex > 0; this.ndex -= 8) {
         compData[outIndex++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
      }

      if (this.inverseFill) {
         byte[] flipTable = TIFFFaxDecompressor.flipTable;

         for(int i = 0; i < outIndex; ++i) {
            compData[i] = flipTable[compData[i] & 255];
         }
      }

      return outIndex;
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      if (bitsPerSample.length == 1 && bitsPerSample[0] == 1) {
         int maxBits = 9 * ((width + 1) / 2) + 2;
         byte[] compData = new byte[(maxBits + 7) / 8];
         int bytes = 0;
         int rowOffset = off;

         for(int i = 0; i < height; ++i) {
            int rowBytes = this.encodeRLE(b, rowOffset, 0, width, compData);
            this.stream.write(compData, 0, rowBytes);
            rowOffset += scanlineStride;
            bytes += rowBytes;
         }

         return bytes;
      } else {
         throw new IIOException("Bits per sample must be 1 for RLE compression!");
      }
   }
}
