/*    */ package org.apache.commons.codec.digest;
/*    */ 
/*    */ import java.security.SecureRandom;
/*    */ import java.util.Random;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ class B64
/*    */ {
/*    */   static final String B64T_STRING = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
/* 43 */   static final char[] B64T_ARRAY = "./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
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
/*    */   static void b64from24bit(byte b2, byte b1, byte b0, int outLen, StringBuilder buffer) {
/* 62 */     int w = b2 << 16 & 0xFFFFFF | b1 << 8 & 0xFFFF | b0 & 0xFF;
/*    */     
/* 64 */     int n = outLen;
/* 65 */     while (n-- > 0) {
/* 66 */       buffer.append(B64T_ARRAY[w & 0x3F]);
/* 67 */       w >>= 6;
/*    */     } 
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
/*    */   static String getRandomSalt(int num) {
/* 81 */     return getRandomSalt(num, new SecureRandom());
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
/*    */   static String getRandomSalt(int num, Random random) {
/* 95 */     StringBuilder saltString = new StringBuilder(num);
/* 96 */     for (int i = 1; i <= num; i++) {
/* 97 */       saltString.append("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".charAt(random.nextInt("./0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".length())));
/*    */     }
/* 99 */     return saltString.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\digest\B64.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */