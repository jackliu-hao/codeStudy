/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16X25
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 33800;
/*    */   
/*    */   public void reset() {
/* 17 */     this.wCRCin = 65535;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] b, int off, int len) {
/* 22 */     super.update(b, off, len);
/* 23 */     this.wCRCin ^= 0xFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 28 */     this.wCRCin ^= b & 0xFF;
/* 29 */     for (int j = 0; j < 8; j++) {
/* 30 */       if ((this.wCRCin & 0x1) != 0) {
/* 31 */         this.wCRCin >>= 1;
/* 32 */         this.wCRCin ^= 0x8408;
/*    */       } else {
/* 34 */         this.wCRCin >>= 1;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16X25.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */