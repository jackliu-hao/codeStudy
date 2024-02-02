/*    */ package org.wildfly.common.iteration;
/*    */ 
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
/*    */ final class LittleEndianBase32EncodingIterator
/*    */   extends Base32EncodingCodePointIterator
/*    */ {
/*    */   private final Base32Alphabet alphabet;
/*    */   
/*    */   LittleEndianBase32EncodingIterator(ByteIterator iter, boolean addPadding, Base32Alphabet alphabet) {
/* 29 */     super(iter, addPadding);
/* 30 */     this.alphabet = alphabet;
/*    */   }
/*    */ 
/*    */   
/*    */   int calc0(int b0) {
/* 35 */     return this.alphabet.encode(b0 & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc1(int b0, int b1) {
/* 40 */     return this.alphabet.encode((b1 << 3 | b0 >> 5) & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc2(int b1) {
/* 45 */     return this.alphabet.encode(b1 >> 2 & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc3(int b1, int b2) {
/* 50 */     return this.alphabet.encode((b2 << 1 | b1 >> 7) & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc4(int b2, int b3) {
/* 55 */     return this.alphabet.encode((b3 << 4 | b2 >> 4) & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc5(int b3) {
/* 60 */     return this.alphabet.encode(b3 >> 1 & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc6(int b3, int b4) {
/* 65 */     return this.alphabet.encode((b4 << 2 | b3 >> 6) & 0x1F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc7(int b4) {
/* 70 */     return this.alphabet.encode(b4 >> 3 & 0x1F);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\LittleEndianBase32EncodingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */