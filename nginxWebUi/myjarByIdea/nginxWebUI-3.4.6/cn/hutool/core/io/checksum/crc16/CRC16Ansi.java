package cn.hutool.core.io.checksum.crc16;

public class CRC16Ansi extends CRC16Checksum {
   private static final long serialVersionUID = 1L;
   private static final int WC_POLY = 40961;

   public void reset() {
      this.wCRCin = 65535;
   }

   public void update(int b) {
      int hi = this.wCRCin >> 8;
      hi ^= b;
      this.wCRCin = hi;

      for(int i = 0; i < 8; ++i) {
         int flag = this.wCRCin & 1;
         this.wCRCin >>= 1;
         if (flag == 1) {
            this.wCRCin ^= 40961;
         }
      }

   }
}
