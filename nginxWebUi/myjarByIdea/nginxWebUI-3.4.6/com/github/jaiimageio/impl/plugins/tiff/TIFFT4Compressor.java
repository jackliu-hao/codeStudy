package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.BaselineTIFFTagSet;
import com.github.jaiimageio.plugins.tiff.TIFFField;
import java.io.IOException;
import javax.imageio.IIOException;
import javax.imageio.metadata.IIOMetadata;

public class TIFFT4Compressor extends TIFFFaxCompressor {
   private boolean is1DMode = false;
   private boolean isEOLAligned = false;

   public TIFFT4Compressor() {
      super("CCITT T.4", 3, true);
   }

   public void setMetadata(IIOMetadata metadata) {
      super.setMetadata(metadata);
      if (metadata instanceof TIFFImageMetadata) {
         TIFFImageMetadata tim = (TIFFImageMetadata)metadata;
         TIFFField f = tim.getTIFFField(292);
         if (f != null) {
            int options = f.getAsInt(0);
            this.is1DMode = (options & 1) == 0;
            this.isEOLAligned = (options & 4) == 4;
         } else {
            long[] oarray = new long[]{(long)((this.isEOLAligned ? 4 : 0) | (this.is1DMode ? 0 : 1))};
            BaselineTIFFTagSet base = BaselineTIFFTagSet.getInstance();
            TIFFField T4Options = new TIFFField(base.getTag(292), 4, 1, oarray);
            tim.rootIFD.addTIFFField(T4Options);
         }
      }

   }

   public int encodeT4(boolean is1DMode, boolean isEOLAligned, byte[] data, int lineStride, int colOffset, int width, int height, byte[] compData) {
      byte[] refData = data;
      int lineAddr = 0;
      int outIndex = 0;
      this.initBitBuf();
      int KParameter = 2;

      int i;
      for(i = 0; i < height; ++i) {
         if (!is1DMode && i % KParameter != 0) {
            outIndex += this.addEOL(is1DMode, isEOLAligned, false, compData, outIndex);
            int refAddr = lineAddr - lineStride;
            int a0 = colOffset;
            int last = colOffset + width;
            int testbit = (data[lineAddr + (colOffset >>> 3)] & 255) >>> 7 - (colOffset & 7) & 1;
            int a1 = testbit != 0 ? colOffset : this.nextState(data, lineAddr, colOffset, last);
            testbit = (refData[refAddr + (colOffset >>> 3)] & 255) >>> 7 - (colOffset & 7) & 1;
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
                  break;
               }

               color = (data[lineAddr + (a0 >>> 3)] & 255) >>> 7 - (a0 & 7) & 1;
               a1 = this.nextState(data, lineAddr, a0, last);
               b1 = this.nextState(refData, refAddr, a0, last);
               testbit = (refData[refAddr + (b1 >>> 3)] & 255) >>> 7 - (b1 & 7) & 1;
               if (testbit == color) {
                  b1 = this.nextState(refData, refAddr, b1, last);
               }
            }
         } else {
            outIndex += this.addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
            outIndex += this.encode1D(data, lineAddr, colOffset, width, compData, outIndex);
         }

         lineAddr += lineStride;
      }

      for(i = 0; i < 6; ++i) {
         outIndex += this.addEOL(is1DMode, isEOLAligned, true, compData, outIndex);
      }

      while(this.ndex > 0) {
         compData[outIndex++] = (byte)(this.bits >>> 24);
         this.bits <<= 8;
         this.ndex -= 8;
      }

      if (this.inverseFill) {
         for(i = 0; i < outIndex; ++i) {
            compData[i] = TIFFFaxDecompressor.flipTable[compData[i] & 255];
         }
      }

      return outIndex;
   }

   public int encode(byte[] b, int off, int width, int height, int[] bitsPerSample, int scanlineStride) throws IOException {
      if (bitsPerSample.length == 1 && bitsPerSample[0] == 1) {
         int maxBits = 9 * ((width + 1) / 2) + 2;
         int bufSize = (maxBits + 7) / 8;
         bufSize = height * (bufSize + 2) + 12;
         byte[] compData = new byte[bufSize];
         int bytes = this.encodeT4(this.is1DMode, this.isEOLAligned, b, scanlineStride, 8 * off, width, height, compData);
         this.stream.write(compData, 0, bytes);
         return bytes;
      } else {
         throw new IIOException("Bits per sample must be 1 for T4 compression!");
      }
   }
}
