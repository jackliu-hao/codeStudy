/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC16Ansi
/*    */   extends CRC16Checksum
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private static final int WC_POLY = 40961;
/*    */   
/*    */   public void reset() {
/* 16 */     this.wCRCin = 65535;
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 21 */     int hi = this.wCRCin >> 8;
/* 22 */     hi ^= b;
/* 23 */     this.wCRCin = hi;
/*    */     
/* 25 */     for (int i = 0; i < 8; i++) {
/* 26 */       int flag = this.wCRCin & 0x1;
/* 27 */       this.wCRCin >>= 1;
/* 28 */       if (flag == 1)
/* 29 */         this.wCRCin ^= 0xA001; 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16Ansi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */