package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import java.io.IOException;

public class TIFFPackBitsDecompressor extends TIFFDecompressor {
   private static final boolean DEBUG = false;

   public int decode(byte[] srcData, int srcOffset, byte[] dstData, int dstOffset) throws IOException {
      int srcIndex = srcOffset;
      int dstIndex = dstOffset;
      int dstArraySize = dstData.length;
      int srcArraySize = srcData.length;

      try {
         while(dstIndex < dstArraySize && srcIndex < srcArraySize) {
            byte b = srcData[srcIndex++];
            int repeat;
            if (b >= 0 && b <= 127) {
               for(repeat = 0; repeat < b + 1; ++repeat) {
                  dstData[dstIndex++] = srcData[srcIndex++];
               }
            } else if (b <= -1 && b >= -127) {
               repeat = srcData[srcIndex++];

               for(int i = 0; i < -b + 1; ++i) {
                  dstData[dstIndex++] = (byte)repeat;
               }
            } else {
               ++srcIndex;
            }
         }
      } catch (ArrayIndexOutOfBoundsException var12) {
         if (this.reader instanceof TIFFImageReader) {
            ((TIFFImageReader)this.reader).forwardWarningMessage("ArrayIndexOutOfBoundsException ignored in TIFFPackBitsDecompressor.decode()");
         }
      }

      return dstIndex - dstOffset;
   }

   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      this.stream.seek(this.offset);
      byte[] srcData = new byte[this.byteCount];
      this.stream.readFully(srcData);
      int bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
      byte[] buf;
      int bufOffset;
      if (bytesPerRow == scanlineStride) {
         buf = b;
         bufOffset = dstOffset;
      } else {
         buf = new byte[bytesPerRow * this.srcHeight];
         bufOffset = 0;
      }

      this.decode(srcData, 0, buf, bufOffset);
      if (bytesPerRow != scanlineStride) {
         int off = 0;

         for(int y = 0; y < this.srcHeight; ++y) {
            System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
            off += bytesPerRow;
            dstOffset += scanlineStride;
         }
      }

   }
}
