/*    */ package cn.hutool.crypto.symmetric;
/*    */ 
/*    */ import cn.hutool.core.util.HexUtil;
/*    */ import cn.hutool.crypto.KeyUtil;
/*    */ import javax.crypto.SecretKey;
/*    */ import javax.crypto.spec.PBEKeySpec;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PBKDF2
/*    */ {
/* 17 */   private String algorithm = "PBKDF2WithHmacSHA1";
/*    */   
/* 19 */   private int keyLength = 512;
/*    */ 
/*    */   
/* 22 */   private int iterationCount = 1000;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PBKDF2() {}
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public PBKDF2(String algorithm, int keyLength, int iterationCount) {
/* 38 */     this.algorithm = algorithm;
/* 39 */     this.keyLength = keyLength;
/* 40 */     this.iterationCount = iterationCount;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public byte[] encrypt(char[] password, byte[] salt) {
/* 51 */     PBEKeySpec pbeKeySpec = new PBEKeySpec(password, salt, this.iterationCount, this.keyLength);
/* 52 */     SecretKey secretKey = KeyUtil.generateKey(this.algorithm, pbeKeySpec);
/* 53 */     return secretKey.getEncoded();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String encryptHex(char[] password, byte[] salt) {
/* 64 */     return HexUtil.encodeHexStr(encrypt(password, salt));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\PBKDF2.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */