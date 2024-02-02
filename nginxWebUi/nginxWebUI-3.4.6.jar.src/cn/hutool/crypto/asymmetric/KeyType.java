/*    */ package cn.hutool.crypto.asymmetric;
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
/*    */ public enum KeyType
/*    */ {
/* 15 */   PublicKey(1),
/*    */ 
/*    */ 
/*    */   
/* 19 */   PrivateKey(2),
/*    */ 
/*    */ 
/*    */   
/* 23 */   SecretKey(3);
/*    */ 
/*    */ 
/*    */   
/*    */   private final int value;
/*    */ 
/*    */ 
/*    */   
/*    */   KeyType(int value) {
/* 32 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getValue() {
/* 43 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\KeyType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */