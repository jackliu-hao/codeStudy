/*    */ package cn.hutool.core.io.checksum;
/*    */ 
/*    */ import cn.hutool.core.io.checksum.crc16.CRC16Checksum;
/*    */ import cn.hutool.core.io.checksum.crc16.CRC16IBM;
/*    */ import java.io.Serializable;
/*    */ import java.util.zip.Checksum;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16
/*    */   implements Checksum, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final CRC16Checksum crc16;
/*    */   
/*    */   public CRC16() {
/* 21 */     this((CRC16Checksum)new CRC16IBM());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CRC16(CRC16Checksum crc16Checksum) {
/* 30 */     this.crc16 = crc16Checksum;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHexValue() {
/* 40 */     return this.crc16.getHexValue();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHexValue(boolean isPadding) {
/* 51 */     return this.crc16.getHexValue(isPadding);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getValue() {
/* 56 */     return this.crc16.getValue();
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 61 */     this.crc16.reset();
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] b, int off, int len) {
/* 66 */     this.crc16.update(b, off, len);
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 71 */     this.crc16.update(b);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\CRC16.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */