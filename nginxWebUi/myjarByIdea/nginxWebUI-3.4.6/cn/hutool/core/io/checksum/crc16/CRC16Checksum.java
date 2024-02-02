package cn.hutool.core.io.checksum.crc16;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.util.zip.Checksum;

public abstract class CRC16Checksum implements Checksum, Serializable {
   private static final long serialVersionUID = 1L;
   protected int wCRCin;

   public CRC16Checksum() {
      this.reset();
   }

   public long getValue() {
      return (long)this.wCRCin;
   }

   public String getHexValue() {
      return this.getHexValue(false);
   }

   public String getHexValue(boolean isPadding) {
      String hex = HexUtil.toHex(this.getValue());
      if (isPadding) {
         hex = StrUtil.padPre(hex, 4, '0');
      }

      return hex;
   }

   public void reset() {
      this.wCRCin = 0;
   }

   public void update(byte[] b) {
      this.update(b, 0, b.length);
   }

   public void update(byte[] b, int off, int len) {
      for(int i = off; i < off + len; ++i) {
         this.update(b[i]);
      }

   }
}
