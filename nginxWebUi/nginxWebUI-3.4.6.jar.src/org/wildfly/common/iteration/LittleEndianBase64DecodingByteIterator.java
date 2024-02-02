/*    */ package org.wildfly.common.iteration;
/*    */ 
/*    */ import org.wildfly.common._private.CommonMessages;
/*    */ import org.wildfly.common.codec.Base64Alphabet;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ final class LittleEndianBase64DecodingByteIterator
/*    */   extends Base64DecodingByteIterator
/*    */ {
/*    */   private final Base64Alphabet alphabet;
/*    */   
/*    */   LittleEndianBase64DecodingByteIterator(CodePointIterator iter, boolean requirePadding, Base64Alphabet alphabet) {
/* 31 */     super(iter, requirePadding);
/* 32 */     this.alphabet = alphabet;
/*    */   }
/*    */   
/*    */   int calc0(int b0, int b1) {
/* 36 */     int d0 = this.alphabet.decode(b0);
/* 37 */     int d1 = this.alphabet.decode(b1);
/*    */ 
/*    */     
/* 40 */     if (d0 == -1 || d1 == -1) throw CommonMessages.msg.invalidBase64Character(); 
/* 41 */     return (d0 | d1 << 6) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc1(int b1, int b2) {
/* 45 */     int d1 = this.alphabet.decode(b1);
/* 46 */     int d2 = this.alphabet.decode(b2);
/*    */ 
/*    */     
/* 49 */     if (d1 == -1 || d2 == -1) throw CommonMessages.msg.invalidBase64Character(); 
/* 50 */     return (d1 >> 2 | d2 << 4) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc2(int b2, int b3) {
/* 54 */     int d2 = this.alphabet.decode(b2);
/* 55 */     int d3 = this.alphabet.decode(b3);
/*    */ 
/*    */     
/* 58 */     if (d2 == -1 || d3 == -1) throw CommonMessages.msg.invalidBase64Character(); 
/* 59 */     return (d2 >> 4 | d3 << 2) & 0xFF;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\LittleEndianBase64DecodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */