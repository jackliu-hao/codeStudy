/*    */ package cn.hutool.crypto.digest;
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
/*    */ public class SM3
/*    */   extends Digester
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   public static final String ALGORITHM_NAME = "SM3";
/*    */   
/*    */   public static SM3 create() {
/* 29 */     return new SM3();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SM3() {
/* 36 */     super("SM3");
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SM3(byte[] salt) {
/* 45 */     this(salt, 0, 1);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SM3(byte[] salt, int digestCount) {
/* 55 */     this(salt, 0, digestCount);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SM3(byte[] salt, int saltPosition, int digestCount) {
/* 66 */     this();
/* 67 */     this.salt = salt;
/* 68 */     this.saltPosition = saltPosition;
/* 69 */     this.digestCount = digestCount;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\digest\SM3.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */