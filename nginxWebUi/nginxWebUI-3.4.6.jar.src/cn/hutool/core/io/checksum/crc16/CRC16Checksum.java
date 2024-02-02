/*    */ package cn.hutool.core.io.checksum.crc16;
/*    */ 
/*    */ import cn.hutool.core.util.HexUtil;
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ import java.io.Serializable;
/*    */ import java.util.zip.Checksum;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class CRC16Checksum
/*    */   implements Checksum, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   protected int wCRCin;
/*    */   
/*    */   public CRC16Checksum() {
/* 25 */     reset();
/*    */   }
/*    */ 
/*    */   
/*    */   public long getValue() {
/* 30 */     return this.wCRCin;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHexValue() {
/* 39 */     return getHexValue(false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getHexValue(boolean isPadding) {
/* 48 */     String hex = HexUtil.toHex(getValue());
/* 49 */     if (isPadding) {
/* 50 */       hex = StrUtil.padPre(hex, 4, '0');
/*    */     }
/*    */     
/* 53 */     return hex;
/*    */   }
/*    */ 
/*    */   
/*    */   public void reset() {
/* 58 */     this.wCRCin = 0;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void update(byte[] b) {
/* 66 */     update(b, 0, b.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public void update(byte[] b, int off, int len) {
/* 71 */     for (int i = off; i < off + len; i++)
/* 72 */       update(b[i]); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\checksum\crc16\CRC16Checksum.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */