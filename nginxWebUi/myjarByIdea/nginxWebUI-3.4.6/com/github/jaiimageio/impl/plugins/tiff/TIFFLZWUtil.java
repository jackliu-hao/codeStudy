package com.github.jaiimageio.impl.plugins.tiff;

import java.io.IOException;
import javax.imageio.IIOException;

public class TIFFLZWUtil {
   private static final boolean debug = false;
   byte[] srcData;
   int srcIndex;
   byte[] dstData;
   int dstIndex = 0;
   byte[][] stringTable;
   int tableIndex;
   int bitsToGet = 9;
   int predictor;
   int samplesPerPixel;
   int nextData = 0;
   int nextBits = 0;
   private static final int[] andTable = new int[]{511, 1023, 2047, 4095};

   public byte[] decode(byte[] data, int predictor, int samplesPerPixel, int width, int height) throws IOException {
      if (data[0] == 0 && data[1] == 1) {
         throw new IIOException("TIFF 5.0-style LZW compression is not supported!");
      } else {
         this.srcData = data;
         this.srcIndex = 0;
         this.nextData = 0;
         this.nextBits = 0;
         this.dstData = new byte[8192];
         this.dstIndex = 0;
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

         if (predictor == 2) {
            for(int j = 0; j < height; ++j) {
               int count = samplesPerPixel * (j * width + 1);

               for(int i = samplesPerPixel; i < width * samplesPerPixel; ++i) {
                  byte[] var10000 = this.dstData;
                  var10000[count] += this.dstData[count - samplesPerPixel];
                  ++count;
               }
            }
         }

         byte[] newDstData = new byte[this.dstIndex];
         System.arraycopy(this.dstData, 0, newDstData, 0, this.dstIndex);
         return newDstData;
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

   private void ensureCapacity(int bytesToAdd) {
      if (this.dstIndex + bytesToAdd > this.dstData.length) {
         byte[] newDstData = new byte[Math.max((int)((float)this.dstData.length * 1.2F), this.dstIndex + bytesToAdd)];
         System.arraycopy(this.dstData, 0, newDstData, 0, this.dstData.length);
         this.dstData = newDstData;
      }

   }

   public void writeString(byte[] string) {
      this.ensureCapacity(string.length);

      for(int i = 0; i < string.length; ++i) {
         this.dstData[this.dstIndex++] = string[i];
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
