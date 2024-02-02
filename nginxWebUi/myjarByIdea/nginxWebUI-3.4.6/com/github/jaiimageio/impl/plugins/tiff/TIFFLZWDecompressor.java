package com.github.jaiimageio.impl.plugins.tiff;

import com.github.jaiimageio.plugins.tiff.TIFFDecompressor;
import java.io.IOException;
import javax.imageio.IIOException;

public class TIFFLZWDecompressor extends TIFFDecompressor {
   private static final boolean DEBUG = false;
   private static final int[] andTable = new int[]{511, 1023, 2047, 4095};
   int predictor;
   byte[] srcData;
   byte[] dstData;
   int srcIndex;
   int dstIndex;
   byte[][] stringTable;
   int tableIndex;
   int bitsToGet = 9;
   int nextData = 0;
   int nextBits = 0;

   public TIFFLZWDecompressor(int predictor) throws IIOException {
      if (predictor != 1 && predictor != 2) {
         throw new IIOException("Illegal value for Predictor in TIFF file");
      } else {
         this.predictor = predictor;
      }
   }

   public void decodeRaw(byte[] b, int dstOffset, int bitsPerPixel, int scanlineStride) throws IOException {
      int bytesPerRow;
      if (this.predictor == 2) {
         int len = this.bitsPerSample.length;

         for(bytesPerRow = 0; bytesPerRow < len; ++bytesPerRow) {
            if (this.bitsPerSample[bytesPerRow] != 8) {
               throw new IIOException(this.bitsPerSample[bytesPerRow] + "-bit samples " + "are not supported for Horizontal " + "differencing Predictor");
            }
         }
      }

      this.stream.seek(this.offset);
      byte[] sdata = new byte[this.byteCount];
      this.stream.readFully(sdata);
      bytesPerRow = (this.srcWidth * bitsPerPixel + 7) / 8;
      byte[] buf;
      int bufOffset;
      if (bytesPerRow == scanlineStride) {
         buf = b;
         bufOffset = dstOffset;
      } else {
         buf = new byte[bytesPerRow * this.srcHeight];
         bufOffset = 0;
      }

      this.decode(sdata, 0, buf, bufOffset);
      if (bytesPerRow != scanlineStride) {
         int off = 0;

         for(int y = 0; y < this.srcHeight; ++y) {
            System.arraycopy(buf, off, b, dstOffset, bytesPerRow);
            off += bytesPerRow;
            dstOffset += scanlineStride;
         }
      }

   }

   public int decode(byte[] sdata, int srcOffset, byte[] ddata, int dstOffset) throws IOException {
      if (sdata[0] == 0 && sdata[1] == 1) {
         throw new IIOException("TIFF 5.0-style LZW compression is not supported!");
      } else {
         this.srcData = sdata;
         this.dstData = ddata;
         this.srcIndex = srcOffset;
         this.dstIndex = dstOffset;
         this.nextData = 0;
         this.nextBits = 0;
         this.initializeStringTable();
         int oldCode = 0;

         int code;
         while((code = this.getNextCode()) != 257) {
            if (code == 256) {
               this.initializeStringTable();
               code = this.getNextCode();
               if (code == 257) {
                  break;
               }

               this.writeString(this.stringTable[code]);
               oldCode = code;
            } else {
               byte[] string;
               if (code < this.tableIndex) {
                  string = this.stringTable[code];
                  this.writeString(string);
                  this.addStringToTable(this.stringTable[oldCode], string[0]);
                  oldCode = code;
               } else {
                  string = this.stringTable[oldCode];
                  string = this.composeString(string, string[0]);
                  this.writeString(string);
                  this.addStringToTable(string);
                  oldCode = code;
               }
            }
         }

         if (this.predictor == 2) {
            for(int j = 0; j < this.srcHeight; ++j) {
               int count = dstOffset + this.samplesPerPixel * (j * this.srcWidth + 1);

               for(int i = this.samplesPerPixel; i < this.srcWidth * this.samplesPerPixel; ++i) {
                  byte[] var10000 = this.dstData;
                  var10000[count] += this.dstData[count - this.samplesPerPixel];
                  ++count;
               }
            }
         }

         return this.dstIndex - dstOffset;
      }
   }

   public void initializeStringTable() {
      this.stringTable = new byte[4096][];

      for(int i = 0; i < 256; ++i) {
         this.stringTable[i] = new byte[1];
         this.stringTable[i][0] = (byte)i;
      }

      this.tableIndex = 258;
      this.bitsToGet = 9;
   }

   public void writeString(byte[] string) {
      if (this.dstIndex < this.dstData.length) {
         int maxIndex = Math.min(string.length, this.dstData.length - this.dstIndex);

         for(int i = 0; i < maxIndex; ++i) {
            this.dstData[this.dstIndex++] = string[i];
         }
      }

   }

   public void addStringToTable(byte[] oldString, byte newString) {
      int length = oldString.length;
      byte[] string = new byte[length + 1];
      System.arraycopy(oldString, 0, string, 0, length);
      string[length] = newString;
      this.stringTable[this.tableIndex++] = string;
      if (this.tableIndex == 511) {
         this.bitsToGet = 10;
      } else if (this.tableIndex == 1023) {
         this.bitsToGet = 11;
      } else if (this.tableIndex == 2047) {
         this.bitsToGet = 12;
      }

   }

   public void addStringToTable(byte[] string) {
      this.stringTable[this.tableIndex++] = string;
      if (this.tableIndex == 511) {
         this.bitsToGet = 10;
      } else if (this.tableIndex == 1023) {
         this.bitsToGet = 11;
      } else if (this.tableIndex == 2047) {
         this.bitsToGet = 12;
      }

   }

   public byte[] composeString(byte[] oldString, byte newString) {
      int length = oldString.length;
      byte[] string = new byte[length + 1];
      System.arraycopy(oldString, 0, string, 0, length);
      string[length] = newString;
      return string;
   }

   public int getNextCode() {
      try {
         this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 255;
         this.nextBits += 8;
         if (this.nextBits < this.bitsToGet) {
            this.nextData = this.nextData << 8 | this.srcData[this.srcIndex++] & 255;
            this.nextBits += 8;
         }

         int code = this.nextData >> this.nextBits - this.bitsToGet & andTable[this.bitsToGet - 9];
         this.nextBits -= this.bitsToGet;
         return code;
      } catch (ArrayIndexOutOfBoundsException var2) {
         return 257;
      }
   }
}
