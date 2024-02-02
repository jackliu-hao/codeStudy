package cn.hutool.core.io.checksum;

import java.io.Serializable;
import java.util.zip.Checksum;

public class CRC8 implements Checksum, Serializable {
   private static final long serialVersionUID = 1L;
   private final short init;
   private final short[] crcTable = new short[256];
   private short value;

   public CRC8(int polynomial, short init) {
      this.value = this.init = init;

      for(int dividend = 0; dividend < 256; ++dividend) {
         int remainder = dividend;

         for(int bit = 0; bit < 8; ++bit) {
            if ((remainder & 1) != 0) {
               remainder = remainder >>> 1 ^ polynomial;
            } else {
               remainder >>>= 1;
            }
         }

         this.crcTable[dividend] = (short)remainder;
      }

   }

   public void update(byte[] buffer, int offset, int len) {
      for(int i = 0; i < len; ++i) {
         int data = buffer[offset + i] ^ this.value;
         this.value = (short)(this.crcTable[data & 255] ^ this.value << 8);
      }

   }

   public void update(byte[] buffer) {
      this.update(buffer, 0, buffer.length);
   }

   public void update(int b) {
      this.update(new byte[]{(byte)b}, 0, 1);
   }

   public long getValue() {
      return (long)(this.value & 255);
   }

   public void reset() {
      this.value = this.init;
   }
}
