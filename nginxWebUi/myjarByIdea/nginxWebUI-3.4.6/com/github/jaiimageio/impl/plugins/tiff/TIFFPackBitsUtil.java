package com.github.jaiimageio.impl.plugins.tiff;

import java.io.IOException;

public class TIFFPackBitsUtil {
   private static final boolean debug = false;
   byte[] dstData = new byte[8192];
   int dstIndex = 0;

   private void ensureCapacity(int bytesToAdd) {
      if (this.dstIndex + bytesToAdd > this.dstData.length) {
         byte[] newDstData = new byte[Math.max((int)((float)this.dstData.length * 1.2F), this.dstIndex + bytesToAdd)];
         System.arraycopy(this.dstData, 0, newDstData, 0, this.dstData.length);
         this.dstData = newDstData;
      }

   }

   public byte[] decode(byte[] srcData) throws IOException {
      int inIndex = 0;

      while(true) {
         while(inIndex < srcData.length) {
            byte b = srcData[inIndex++];
            int repeat;
            if (b >= 0 && b <= 127) {
               this.ensureCapacity(b + 1);

               for(repeat = 0; repeat < b + 1; ++repeat) {
                  this.dstData[this.dstIndex++] = srcData[inIndex++];
               }
            } else if (b <= -1 && b >= -127) {
               repeat = srcData[inIndex++];
               this.ensureCapacity(-b + 1);

               for(int i = 0; i < -b + 1; ++i) {
                  this.dstData[this.dstIndex++] = (byte)repeat;
               }
            } else {
               ++inIndex;
            }
         }

         byte[] newDstData = new byte[this.dstIndex];
         System.arraycopy(this.dstData, 0, newDstData, 0, this.dstIndex);
         return newDstData;
      }
   }
}
