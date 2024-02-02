/*    */ package cn.hutool.core.lang.id;
/*    */ 
/*    */ import cn.hutool.core.util.RandomUtil;
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
/*    */ public class NanoId
/*    */ {
/* 27 */   private static final SecureRandom DEFAULT_NUMBER_GENERATOR = RandomUtil.getSecureRandom();
/*    */ 
/*    */ 
/*    */ 
/*    */   
/* 32 */   private static final char[] DEFAULT_ALPHABET = "_-0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"
/* 33 */     .toCharArray();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static final int DEFAULT_SIZE = 21;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String randomNanoId() {
/* 46 */     return randomNanoId(21);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static String randomNanoId(int size) {
/* 56 */     return randomNanoId(null, null, size);
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
/*    */   public static String randomNanoId(Random random, char[] alphabet, int size) {
/* 68 */     if (random == null) {
/* 69 */       random = DEFAULT_NUMBER_GENERATOR;
/*    */     }
/*    */     
/* 72 */     if (alphabet == null) {
/* 73 */       alphabet = DEFAULT_ALPHABET;
/*    */     }
/*    */     
/* 76 */     if (alphabet.length == 0 || alphabet.length >= 256) {
/* 77 */       throw new IllegalArgumentException("Alphabet must contain between 1 and 255 symbols.");
/*    */     }
/*    */     
/* 80 */     if (size <= 0) {
/* 81 */       throw new IllegalArgumentException("Size must be greater than zero.");
/*    */     }
/*    */     
/* 84 */     int mask = (2 << (int)Math.floor(Math.log((alphabet.length - 1)) / Math.log(2.0D))) - 1;
/* 85 */     int step = (int)Math.ceil(1.6D * mask * size / alphabet.length);
/*    */     
/* 87 */     StringBuilder idBuilder = new StringBuilder();
/*    */     
/*    */     while (true) {
/* 90 */       byte[] bytes = new byte[step];
/* 91 */       random.nextBytes(bytes);
/* 92 */       for (int i = 0; i < step; i++) {
/* 93 */         int alphabetIndex = bytes[i] & mask;
/* 94 */         if (alphabetIndex < alphabet.length) {
/* 95 */           idBuilder.append(alphabet[alphabetIndex]);
/* 96 */           if (idBuilder.length() == size)
/* 97 */             return idBuilder.toString(); 
/*    */         } 
/*    */       } 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\id\NanoId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */