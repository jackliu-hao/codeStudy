/*    */ package cn.hutool.crypto.symmetric;
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
/*    */ public class Vigenere
/*    */ {
/*    */   public static String encrypt(CharSequence data, CharSequence cipherKey) {
/* 21 */     int dataLen = data.length();
/* 22 */     int cipherKeyLen = cipherKey.length();
/*    */     
/* 24 */     char[] cipherArray = new char[dataLen];
/* 25 */     for (int i = 0; i < dataLen / cipherKeyLen + 1; i++) {
/* 26 */       for (int t = 0; t < cipherKeyLen; t++) {
/* 27 */         if (t + i * cipherKeyLen < dataLen) {
/* 28 */           char dataChar = data.charAt(t + i * cipherKeyLen);
/* 29 */           char cipherKeyChar = cipherKey.charAt(t);
/* 30 */           cipherArray[t + i * cipherKeyLen] = (char)((dataChar + cipherKeyChar - 64) % 95 + 32);
/*    */         } 
/*    */       } 
/*    */     } 
/*    */     
/* 35 */     return String.valueOf(cipherArray);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String decrypt(CharSequence data, CharSequence cipherKey) {
/* 46 */     int dataLen = data.length();
/* 47 */     int cipherKeyLen = cipherKey.length();
/*    */     
/* 49 */     char[] clearArray = new char[dataLen];
/* 50 */     for (int i = 0; i < dataLen; i++) {
/* 51 */       for (int t = 0; t < cipherKeyLen; t++) {
/* 52 */         if (t + i * cipherKeyLen < dataLen) {
/* 53 */           char dataChar = data.charAt(t + i * cipherKeyLen);
/* 54 */           char cipherKeyChar = cipherKey.charAt(t);
/* 55 */           if (dataChar - cipherKeyChar >= 0) {
/* 56 */             clearArray[t + i * cipherKeyLen] = (char)((dataChar - cipherKeyChar) % 95 + 32);
/*    */           } else {
/* 58 */             clearArray[t + i * cipherKeyLen] = (char)((dataChar - cipherKeyChar + 95) % 95 + 32);
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } 
/* 63 */     return String.valueOf(clearArray);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\Vigenere.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */