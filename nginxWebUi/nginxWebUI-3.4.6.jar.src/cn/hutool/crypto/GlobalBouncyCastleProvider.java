/*    */ package cn.hutool.crypto;
/*    */ 
/*    */ import java.security.Provider;
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum GlobalBouncyCastleProvider
/*    */ {
/*    */   private static boolean useBouncyCastle;
/*    */   private Provider provider;
/* 11 */   INSTANCE;
/*    */   
/*    */   static {
/* 14 */     useBouncyCastle = true;
/*    */   }
/*    */   GlobalBouncyCastleProvider() {
/*    */     try {
/* 18 */       this.provider = ProviderFactory.createBouncyCastleProvider();
/* 19 */     } catch (NoClassDefFoundError noClassDefFoundError) {}
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Provider getProvider() {
/* 30 */     return useBouncyCastle ? this.provider : null;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static void setUseBouncyCastle(boolean isUseBouncyCastle) {
/* 41 */     useBouncyCastle = isUseBouncyCastle;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\GlobalBouncyCastleProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */