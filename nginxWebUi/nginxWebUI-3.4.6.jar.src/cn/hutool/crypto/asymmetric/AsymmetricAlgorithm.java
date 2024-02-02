/*    */ package cn.hutool.crypto.asymmetric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum AsymmetricAlgorithm
/*    */ {
/* 12 */   RSA("RSA"),
/*    */   
/* 14 */   RSA_ECB_PKCS1("RSA/ECB/PKCS1Padding"),
/*    */   
/* 16 */   RSA_ECB("RSA/ECB/NoPadding"),
/*    */   
/* 18 */   RSA_None("RSA/None/NoPadding");
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */   
/*    */   AsymmetricAlgorithm(String value) {
/* 27 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 35 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\AsymmetricAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */