/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16IBM
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 40961;
/*    */   
/*    */   public void update(int b) {
/* 17 */     this.wCRCin ^= b & 0xFF;
/* 18 */     for (int j = 0; j < 8; j++) {
/* 19 */       if ((this.wCRCin & 0x1) != 0) {
/* 20 */         this.wCRCin >>= 1;
/* 21 */         this.wCRCin ^= 0xA001;
/*    */       } else {
/* 23 */         this.wCRCin >>= 1;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16IBM.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */