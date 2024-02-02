/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.NoSuchProviderException;
/*    */ import javax.net.ssl.KeyManagerFactory;
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
/*    */ public class KeyManagerFactoryFactoryBean
/*    */ {
/*    */   private String algorithm;
/*    */   private String provider;
/*    */   
/*    */   public KeyManagerFactory createKeyManagerFactory() throws NoSuchProviderException, NoSuchAlgorithmException {
/* 45 */     return (getProvider() != null) ? KeyManagerFactory.getInstance(getAlgorithm(), getProvider()) : KeyManagerFactory.getInstance(getAlgorithm());
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getAlgorithm() {
/* 55 */     if (this.algorithm == null) {
/* 56 */       return KeyManagerFactory.getDefaultAlgorithm();
/*    */     }
/* 58 */     return this.algorithm;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAlgorithm(String algorithm) {
/* 68 */     this.algorithm = algorithm;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getProvider() {
/* 76 */     return this.provider;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setProvider(String provider) {
/* 85 */     this.provider = provider;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\KeyManagerFactoryFactoryBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */