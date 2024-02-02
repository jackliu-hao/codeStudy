package cn.hutool.core.io.checksum.crc16;

public class CRC16DNP extends CRC16Checksum {
   private static final long serialVersionUID = 1L;
   private static final int WC_POLY = 42684;

   public void update(byte[] b, int off, int len) {
      super.update(b, off, len);
      this.wCRCin ^= 65535;
   }

   public void update(int b) {
      this.wCRCin ^= b & 255;

      for(int j = 0; j < 8; ++j) {
         if ((this.wCRCin & 1) != 0) {
            this.wCRCin >>= 1;
            this.wCRCin ^= 42684;
         } else {
            this.wCRCin >>= 1;
         }
      }

   }
}
