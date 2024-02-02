/*    */ package cn.hutool.core.io.checksum;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.zip.Checksum;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CRC8
/*    */   implements Checksum, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final short init;
/* 17 */   private final short[] crcTable = new short[256];
/*    */ 
/*    */ 
/*    */   
/*    */   private short value;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CRC8(int polynomial, short init) {
/* 27 */     this.value = this.init = init;
/* 28 */     for (int dividend = 0; dividend < 256; dividend++) {
/* 29 */       int remainder = dividend;
/* 30 */       for (int bit = 0; bit < 8; bit++) {
/* 31 */         if ((remainder & 0x1) != 0) {
/* 32 */           remainder = remainder >>> 1 ^ polynomial;
/*    */         } else {
/* 34 */           remainder >>>= 1;
/*    */         } 
/*    */       } 
/* 37 */       this.crcTable[dividend] = (short)remainder;
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] buffer, int offset, int len) {
/* 43 */     for (int i = 0; i < len; i++) {
/* 44 */       int data = buffer[offset + i] ^ this.value;
/* 45 */       this.value = (short)(this.crcTable[data & 0xFF] ^ this.value << 8);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(byte[] buffer) {
/* 55 */     update(buffer, 0, buffer.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(int b) {
/* 60 */     update(new byte[] { (byte)b }, 0, 1);
/*    */   }
/*    */ 
/*    */   
/*    */   public long getValue() {
/* 65 */     return (this.value & 0xFF);
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 70 */     this.value = this.init;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\CRC8.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */