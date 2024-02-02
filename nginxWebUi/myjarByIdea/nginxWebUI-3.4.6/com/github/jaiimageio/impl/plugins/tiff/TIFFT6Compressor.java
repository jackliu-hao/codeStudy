package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.io.IOException;
import javax.imageio.IIOException;

public class TIFFT6Compressor extends TIFFFaxCompressor {
   public TIFFT6Compressor() {
      super("CCITT T.6", 4, true);
   }

   public synchronized int encodeT6(byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
      byte[] refData = null;
      int refAddr = 0;
      int lineAddr = 0;
      int outIndex = 0;
      this.initBitBuf();

      int a0;
      while(height-- != 0) {
         a0 = colOffset;
         int last = colOffset + width;
         int testbit = (data[lineAddr + (colOffset >>> 3)] & 255) >>> 7 - (colOffset & 7) & 1;
         int a1 = testbit != 0 ? colOffset : this.nextState(data, lineAddr, colOffset, last);
         testbit = refData == null ? 0 : (refData[refAddr + (colOffset >>> 3)] & 255) >>> 7 - (colOffset & 7) & 1;
         int b1 = testbit != 0 ? colOffset : this.nextState(refData, refAddr, colOffset, last);
         int color = 0;

         while(true) {
            int b2 = this.nextState(refData, refAddr, b1, last);
            if (b2 < a1) {
               outIndex += this.add2DBits(compData, outIndex, pass, 0);
               a0 = b2;
            } else {
               int tmp = b1 - a1 + 3;
               if (tmp <= 6 && tmp >= 0) {
                  outIndex += this.add2DBits(compData, outIndex, vert, tmp);
                  a0 = a1;
               } else {
                  int a2 = this.nextState(data, lineAddr, a1, last);
                  outIndex += this.add2DBits(compData, outIndex, horz, 0);
                  outIndex += this.add1DBits(compData, outIndex, a1 - a0, color);
                  outIndex += this.add1DBits(compData, outIndex, a2 - a1, color ^ 1);
                  a0 = a2;
               }
            }

            if (a0 >= last) {
               refData = data;
               refAddr = lineAddr;
               lineAddr += lineStride;
               break;
            }

            color = (data[lineAddr + (a0 >>> 3)] & 255) >>> 7 - (a0 & 7) & 1;
            a1 = this.nextState(data, lineAddr, a0, last);
            b1 = this.nextState(refData, refAddr, a0, last);
            testbit = refData == null ? 0 : (refData[refAddr + (b1 >>> 3)] & 255) >>> 7 - (b1 & 7) & 1;
            if (testbit == color) {
               b1 = this.nextState(refData, refAddr, b1, last);
            }
         }
      }

      outIndex += this.addEOFB(compData, outIndex);
      if (this.inverseFill) {
         for(a0 = 0; a0 < outIndex; ++a0) {
            compData[a0] = TIFFFaxDecompressor.flipTable[compData[a0] & 255];
         }
      }

      return outIndex;
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      if (bitsPerSample.length == 1 && bitsPerSample[0] == 1) {
         if (this.metadata instanceof TIFFImageMetadata) {
            TIFFImageMetadata tim = (TIFFImageMetadata)this.metadata;
            long[] options = new long[]{0L};
            BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
            TIFFField T6Options = new TIFFField(base.getTag(293), 4, 1, options);
            tim.rootIFD.addTIFFField(T6Options);
         }

         int maxBits = 9 * ((width + 1) / 2) + 2;
         int bufSize = (maxBits + 7) / 8;
         bufSize = height * (bufSize + 2) + 12;
         byte[] compData = new byte[bufSize];
         int bytes = this.encodeT6(b, scanlineStride, 8 * off, width, height, compData);
         this.stream.write(compData, 0, bytes);
         return bytes;
      } else {
         throw new IIOException("Bits per sample must be 1 for T6 compression!");
      }
   }
}
