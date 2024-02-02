/*    */ package cn.hutool.crypto.digest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum DigestAlgorithm
/*    */ {
/* 10 */   MD2("MD2"),
/* 11 */   MD5("MD5"),
/* 12 */   SHA1("SHA-1"),
/* 13 */   SHA256("SHA-256"),
/* 14 */   SHA384("SHA-384"),
/* 15 */   SHA512("SHA-512");
/*    */ 
/*    */ 
/*    */   
/*    */   private final String value;
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   DigestAlgorithm(String value) {
/* 25 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 33 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\DigestAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */