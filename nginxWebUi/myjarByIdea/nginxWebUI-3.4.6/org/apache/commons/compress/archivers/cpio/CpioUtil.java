package org.apache.commons.compress.archivers.cpio;

class CpioUtil {
   static long fileType(long mode) {
      return mode & 61440L;
   }

   static long byteArray2long(byte[] number, boolean swapHalfWord) {
      if (number.length % 2 != 0) {
         throw new UnsupportedOperationException();
      } else {
         int pos = false;
         byte[] tmp_number = new byte[number.length];
         System.arraycopy(number, 0, tmp_number, 0, number.length);
         int pos;
         if (!swapHalfWord) {
            byte tmp = false;

            for(pos = 0; pos < tmp_number.length; ++pos) {
               byte tmp = tmp_number[pos];
               tmp_number[pos++] = tmp_number[pos];
               tmp_number[pos] = tmp;
            }
         }

         long ret = (long)(tmp_number[0] & 255);

         for(pos = 1; pos < tmp_number.length; ++pos) {
            ret <<= 8;
            ret |= (long)(tmp_number[pos] & 255);
         }

         return ret;
      }
   }

   static byte[] long2byteArray(long number, int length, boolean swapHalfWord) {
      byte[] ret = new byte[length];
      int pos = false;
      if (length % 2 == 0 && length >= 2) {
         long tmp_number = number;

         int pos;
         for(pos = length - 1; pos >= 0; --pos) {
            ret[pos] = (byte)((int)(tmp_number & 255L));
            tmp_number >>= 8;
         }

         if (!swapHalfWord) {
            byte tmp = false;

            for(pos = 0; pos < length; ++pos) {
               byte tmp = ret[pos];
               ret[pos++] = ret[pos];
               ret[pos] = tmp;
            }
         }

         return ret;
      } else {
         throw new UnsupportedOperationException();
      }
   }
}
