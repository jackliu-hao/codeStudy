/*    */ package cn.hutool.crypto.asymmetric;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum SignAlgorithm
/*    */ {
/* 11 */   NONEwithRSA("NONEwithRSA"),
/*    */ 
/*    */   
/* 14 */   MD2withRSA("MD2withRSA"),
/* 15 */   MD5withRSA("MD5withRSA"),
/*    */ 
/*    */   
/* 18 */   SHA1withRSA("SHA1withRSA"),
/* 19 */   SHA256withRSA("SHA256withRSA"),
/* 20 */   SHA384withRSA("SHA384withRSA"),
/* 21 */   SHA512withRSA("SHA512withRSA"),
/*    */ 
/*    */   
/* 24 */   NONEwithDSA("NONEwithDSA"),
/*    */   
/* 26 */   SHA1withDSA("SHA1withDSA"),
/*    */ 
/*    */   
/* 29 */   NONEwithECDSA("NONEwithECDSA"),
/* 30 */   SHA1withECDSA("SHA1withECDSA"),
/* 31 */   SHA256withECDSA("SHA256withECDSA"),
/* 32 */   SHA384withECDSA("SHA384withECDSA"),
/* 33 */   SHA512withECDSA("SHA512withECDSA"),
/*    */ 
/*    */   
/* 36 */   SHA256withRSA_PSS("SHA256WithRSA/PSS"),
/* 37 */   SHA384withRSA_PSS("SHA384WithRSA/PSS"),
/* 38 */   SHA512withRSA_PSS("SHA512WithRSA/PSS");
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   SignAlgorithm(String value) {
/* 48 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 57 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\SignAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */