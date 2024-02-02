package cn.hutool.core.io.checksum;

import cn.hutool.core.io.checksum.crc16.CRC16Checksum;
import cn.hutool.core.io.checksum.crc16.CRC16IBM;
import java.io.Serializable;
import java.util.zip.Checksum;

public class CRC16 implements Checksum, Serializable {
   private static final long serialVersionUID = 1L;
   private final CRC16Checksum crc16;

   public CRC16() {
      this(new CRC16IBM());
   }

   public CRC16(CRC16Checksum crc16Checksum) {
      this.crc16 = crc16Checksum;
   }

   public String getHexValue() {
      return this.crc16.getHexValue();
   }

   public String getHexValue(boolean isPadding) {
      return this.crc16.getHexValue(isPadding);
   }

   public long getValue() {
      return this.crc16.getValue();
   }

   public void reset() {
      this.crc16.reset();
   }

   public void update(byte[] b, int off, int len) {
      this.crc16.update(b, off, len);
   }

   public void update(int b) {
      this.crc16.update(b);
   }
}
