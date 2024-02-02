/*     */ package org.wildfly.common.codec;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Base32Alphabet
/*     */   extends Alphabet
/*     */ {
/*     */   protected Base32Alphabet(boolean littleEndian) {
/*  38 */     super(littleEndian);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  60 */   public static final Base32Alphabet STANDARD = new Base32Alphabet(false) {
/*     */       public int encode(int val) {
/*  62 */         if (val <= 25) {
/*  63 */           return 65 + val;
/*     */         }
/*  65 */         assert val < 32;
/*  66 */         return 50 + val - 26;
/*     */       }
/*     */ 
/*     */       
/*     */       public int decode(int codePoint) {
/*  71 */         if (65 <= codePoint && codePoint <= 90)
/*  72 */           return codePoint - 65; 
/*  73 */         if (50 <= codePoint && codePoint <= 55) {
/*  74 */           return codePoint - 50 + 26;
/*     */         }
/*  76 */         return -1;
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  84 */   public static final Base32Alphabet LOWERCASE = new Base32Alphabet(false) {
/*     */       public int encode(int val) {
/*  86 */         if (val <= 25) {
/*  87 */           return 97 + val;
/*     */         }
/*  89 */         assert val < 32;
/*  90 */         return 50 + val - 26;
/*     */       }
/*     */ 
/*     */       
/*     */       public int decode(int codePoint) {
/*  95 */         if (97 <= codePoint && codePoint <= 122)
/*  96 */           return codePoint - 97; 
/*  97 */         if (50 <= codePoint && codePoint <= 55) {
/*  98 */           return codePoint - 50 + 26;
/*     */         }
/* 100 */         return -1;
/*     */       }
/*     */     };
/*     */   
/*     */   public abstract int encode(int paramInt);
/*     */   
/*     */   public abstract int decode(int paramInt);
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\codec\Base32Alphabet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */