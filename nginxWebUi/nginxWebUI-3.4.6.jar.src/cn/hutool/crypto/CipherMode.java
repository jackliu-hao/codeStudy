/*    */ package cn.hutool.crypto;
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
/*    */ public enum CipherMode
/*    */ {
/* 15 */   encrypt(1),
/*    */ 
/*    */ 
/*    */   
/* 19 */   decrypt(2),
/*    */ 
/*    */ 
/*    */   
/* 23 */   wrap(3),
/*    */ 
/*    */ 
/*    */   
/* 27 */   unwrap(4);
/*    */ 
/*    */ 
/*    */   
/*    */   private final int value;
/*    */ 
/*    */ 
/*    */   
/*    */   CipherMode(int value) {
/* 36 */     this.value = value;
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
/* 47 */     return this.value;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\CipherMode.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */