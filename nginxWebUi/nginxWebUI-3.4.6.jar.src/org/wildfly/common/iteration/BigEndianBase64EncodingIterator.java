/*    */ package org.wildfly.common.iteration;
/*    */ 
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
/*    */ final class BigEndianBase64EncodingIterator
/*    */   extends Base64EncodingIterator
/*    */ {
/*    */   private final Base64Alphabet alphabet;
/*    */   
/*    */   BigEndianBase64EncodingIterator(ByteIterator iter, boolean addPadding, Base64Alphabet alphabet) {
/* 29 */     super(iter, addPadding);
/* 30 */     this.alphabet = alphabet;
/*    */   }
/*    */ 
/*    */   
/*    */   int calc0(int b0) {
/* 35 */     return this.alphabet.encode(b0 >> 2 & 0x3F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc1(int b0, int b1) {
/* 40 */     return this.alphabet.encode((b0 << 4 | b1 >> 4) & 0x3F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc2(int b1, int b2) {
/* 45 */     return this.alphabet.encode((b1 << 2 | b2 >> 6) & 0x3F);
/*    */   }
/*    */ 
/*    */   
/*    */   int calc3(int b2) {
/* 50 */     return this.alphabet.encode(b2 & 0x3F);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\iteration\BigEndianBase64EncodingIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */