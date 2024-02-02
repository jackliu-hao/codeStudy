/*    */ package cn.hutool.crypto.symmetric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SymmetricAlgorithm
/*    */ {
/* 12 */   AES("AES"),
/* 13 */   ARCFOUR("ARCFOUR"),
/* 14 */   Blowfish("Blowfish"),
/*    */   
/* 16 */   DES("DES"),
/*    */   
/* 18 */   DESede("DESede"),
/* 19 */   RC2("RC2"),
/*    */   
/* 21 */   PBEWithMD5AndDES("PBEWithMD5AndDES"),
/* 22 */   PBEWithSHA1AndDESede("PBEWithSHA1AndDESede"),
/* 23 */   PBEWithSHA1AndRC2_40("PBEWithSHA1AndRC2_40");
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */   
/*    */   SymmetricAlgorithm(String value) {
/* 32 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\symmetric\SymmetricAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */