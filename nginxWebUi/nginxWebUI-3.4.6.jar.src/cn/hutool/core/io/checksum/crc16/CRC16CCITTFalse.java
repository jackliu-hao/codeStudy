/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16CCITTFalse
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 4129;
/*    */   
/*    */   public void reset() {
/* 16 */     this.wCRCin = 65535;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] b, int off, int len) {
/* 21 */     super.update(b, off, len);
/* 22 */     this.wCRCin &= 0xFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 27 */     for (int i = 0; i < 8; i++) {
/* 28 */       boolean bit = ((b >> 7 - i & 0x1) == 1);
/* 29 */       boolean c15 = ((this.wCRCin >> 15 & 0x1) == 1);
/* 30 */       this.wCRCin <<= 1;
/* 31 */       if (c15 ^ bit)
/* 32 */         this.wCRCin ^= 0x1021; 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16CCITTFalse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */