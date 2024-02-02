/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16DNP
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 42684;
/*    */   
/*    */   public void update(byte[] b, int off, int len) {
/* 17 */     super.update(b, off, len);
/* 18 */     this.wCRCin ^= 0xFFFF;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 23 */     this.wCRCin ^= b & 0xFF;
/* 24 */     for (int j = 0; j < 8; j++) {
/* 25 */       if ((this.wCRCin & 0x1) != 0) {
/* 26 */         this.wCRCin >>= 1;
/* 27 */         this.wCRCin ^= 0xA6BC;
/*    */       } else {
/* 29 */         this.wCRCin >>= 1;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16DNP.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */