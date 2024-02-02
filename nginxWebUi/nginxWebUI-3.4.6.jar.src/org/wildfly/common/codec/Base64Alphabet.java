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
/*    */ public abstract class Base64Alphabet
/*    */   extends Alphabet
/*    */ {
/*    */   protected Base64Alphabet(boolean littleEndian) {
/* 38 */     super(littleEndian);
/*    */   }
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
/* 60 */   public static final Base64Alphabet STANDARD = new Base64Alphabet(false) {
/*    */       public int encode(int val) {
/* 62 */         if (val <= 25)
/* 63 */           return 65 + val; 
/* 64 */         if (val <= 51)
/* 65 */           return 97 + val - 26; 
/* 66 */         if (val <= 61)
/* 67 */           return 48 + val - 52; 
/* 68 */         if (val == 62) {
/* 69 */           return 43;
/*    */         }
/* 71 */         assert val == 63;
/* 72 */         return 47;
/*    */       }
/*    */ 
/*    */       
/*    */       public int decode(int codePoint) throws IllegalArgumentException {
/* 77 */         if (65 <= codePoint && codePoint <= 90)
/* 78 */           return codePoint - 65; 
/* 79 */         if (97 <= codePoint && codePoint <= 122)
/* 80 */           return codePoint - 97 + 26; 
/* 81 */         if (48 <= codePoint && codePoint <= 57)
/* 82 */           return codePoint - 48 + 52; 
/* 83 */         if (codePoint == 43)
/* 84 */           return 62; 
/* 85 */         if (codePoint == 47) {
/* 86 */           return 63;
/*    */         }
/* 88 */         return -1;
/*    */       }
/*    */     };
/*    */   
/*    */   public abstract int encode(int paramInt);
/*    */   
/*    */   public abstract int decode(int paramInt);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\codec\Base64Alphabet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */