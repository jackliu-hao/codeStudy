/*    */ package org.wildfly.common.codec;
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
/*    */ 
/*    */ public abstract class Alphabet
/*    */ {
/*    */   private final boolean littleEndian;
/*    */   
/*    */   Alphabet(boolean littleEndian) {
/* 28 */     this.littleEndian = littleEndian;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isLittleEndian() {
/* 37 */     return this.littleEndian;
/*    */   }
/*    */   
/*    */   public abstract int encode(int paramInt);
/*    */   
/*    */   public abstract int decode(int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\codec\Alphabet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */