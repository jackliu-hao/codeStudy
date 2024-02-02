/*    */ package org.wildfly.common.iteration;
/*    */ 
/*    */ import org.wildfly.common._private.CommonMessages;
/*    */ import org.wildfly.common.codec.Base32Alphabet;
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
/*    */ final class BigEndianBase32DecodingByteIterator
/*    */   extends Base32DecodingByteIterator
/*    */ {
/*    */   private final Base32Alphabet alphabet;
/*    */   
/*    */   BigEndianBase32DecodingByteIterator(CodePointIterator iter, boolean requirePadding, Base32Alphabet alphabet) {
/* 31 */     super(iter, requirePadding);
/* 32 */     this.alphabet = alphabet;
/*    */   }
/*    */   
/*    */   int calc0(int b0, int b1) {
/* 36 */     int d0 = this.alphabet.decode(b0);
/* 37 */     int d1 = this.alphabet.decode(b1);
/*    */ 
/*    */     
/* 40 */     if (d0 == -1 || d1 == -1) throw CommonMessages.msg.invalidBase32Character(); 
/* 41 */     return (d0 << 3 | d1 >> 2) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc1(int b1, int b2, int b3) {
/* 45 */     int d1 = this.alphabet.decode(b1);
/* 46 */     int d2 = this.alphabet.decode(b2);
/* 47 */     int d3 = this.alphabet.decode(b3);
/*    */ 
/*    */ 
/*    */     
/* 51 */     if (d1 == -1 || d2 == -1 || d3 == -1) throw CommonMessages.msg.invalidBase32Character(); 
/* 52 */     return (d1 << 6 | d2 << 1 | d3 >> 4) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc2(int b3, int b4) {
/* 56 */     int d3 = this.alphabet.decode(b3);
/* 57 */     int d4 = this.alphabet.decode(b4);
/*    */ 
/*    */     
/* 60 */     if (d3 == -1 || d4 == -1) throw CommonMessages.msg.invalidBase32Character(); 
/* 61 */     return (d3 << 4 | d4 >> 1) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc3(int b4, int b5, int b6) {
/* 65 */     int d4 = this.alphabet.decode(b4);
/* 66 */     int d5 = this.alphabet.decode(b5);
/* 67 */     int d6 = this.alphabet.decode(b6);
/*    */ 
/*    */ 
/*    */     
/* 71 */     if (d4 == -1 || d5 == -1 || d6 == -1) throw CommonMessages.msg.invalidBase32Character(); 
/* 72 */     return (d4 << 7 | d5 << 2 | d6 >> 3) & 0xFF;
/*    */   }
/*    */   
/*    */   int calc4(int b6, int b7) {
/* 76 */     int d6 = this.alphabet.decode(b6);
/* 77 */     int d7 = this.alphabet.decode(b7);
/*    */ 
/*    */     
/* 80 */     if (d6 == -1 || d7 == -1) throw CommonMessages.msg.invalidBase32Character(); 
/* 81 */     return (d6 << 5 | d7) & 0xFF;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\BigEndianBase32DecodingByteIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */