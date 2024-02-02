/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16Modbus
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 40961;
/*    */   
/*    */   public void reset() {
/* 18 */     this.wCRCin = 65535;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 23 */     this.wCRCin ^= b & 0xFF;
/* 24 */     for (int j = 0; j < 8; j++) {
/* 25 */       if ((this.wCRCin & 0x1) != 0) {
/* 26 */         this.wCRCin >>= 1;
/* 27 */         this.wCRCin ^= 0xA001;
/*    */       } else {
/* 29 */         this.wCRCin >>= 1;
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16Modbus.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */