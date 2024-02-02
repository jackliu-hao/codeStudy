package cn.hutool.core.io.checksum.crc16;

public class CRC16Modbus extends CRC16Checksum {
   private static final long serialVersionUID = 1L;
   private static final int WC_POLY = 40961;

   public void reset() {
      this.wCRCin = 65535;
   }

   public void update(int b) {
      this.wCRCin ^= b & 255;

      for(int j = 0; j < 8; ++j) {
         if ((this.wCRCin & 1) != 0) {
            this.wCRCin >>= 1;
            this.wCRCin ^= 40961;
         } else {
            this.wCRCin >>= 1;
         }
      }

   }
}
