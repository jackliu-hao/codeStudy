/*    */ package cn.hutool.crypto;
/*    */ 
/*    */ import java.security.Provider;
/*    */ import org.bouncycastle.jce.provider.BouncyCastleProvider;
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
/*    */ public class ProviderFactory
/*    */ {
/*    */   public static Provider createBouncyCastleProvider() {
/* 24 */     return (Provider)new BouncyCastleProvider();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\ProviderFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */