/*    */ package cn.hutool.crypto.asymmetric;
/*    */ 
/*    */ import java.security.PrivateKey;
/*    */ import java.security.PublicKey;
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
/*    */ public abstract class AbstractAsymmetricCrypto<T extends AbstractAsymmetricCrypto<T>>
/*    */   extends BaseAsymmetric<T>
/*    */   implements AsymmetricEncryptor, AsymmetricDecryptor
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public AbstractAsymmetricCrypto(String algorithm, PrivateKey privateKey, PublicKey publicKey) {
/* 30 */     super(algorithm, privateKey, publicKey);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\crypto\asymmetric\AbstractAsymmetricCrypto.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */