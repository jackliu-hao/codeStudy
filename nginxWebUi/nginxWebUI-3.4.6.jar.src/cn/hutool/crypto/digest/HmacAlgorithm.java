/*    */ package cn.hutool.crypto.digest;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum HmacAlgorithm
/*    */ {
/* 10 */   HmacMD5("HmacMD5"),
/* 11 */   HmacSHA1("HmacSHA1"),
/* 12 */   HmacSHA256("HmacSHA256"),
/* 13 */   HmacSHA384("HmacSHA384"),
/* 14 */   HmacSHA512("HmacSHA512"),
/*    */   
/* 16 */   HmacSM3("HmacSM3"),
/*    */   
/* 18 */   SM4CMAC("SM4CMAC");
/*    */   
/*    */   private final String value;
/*    */   
/*    */   HmacAlgorithm(String value) {
/* 23 */     this.value = value;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 27 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\HmacAlgorithm.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */