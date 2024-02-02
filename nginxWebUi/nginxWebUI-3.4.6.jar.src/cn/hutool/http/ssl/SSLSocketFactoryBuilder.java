/*    */ package cn.hutool.http.ssl;
/*    */ 
/*    */ import cn.hutool.core.net.SSLContextBuilder;
/*    */ import cn.hutool.core.net.SSLProtocols;
/*    */ import java.security.KeyManagementException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import java.security.SecureRandom;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.SSLSocketFactory;
/*    */ import javax.net.ssl.TrustManager;
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
/*    */ @Deprecated
/*    */ public class SSLSocketFactoryBuilder
/*    */   implements SSLProtocols
/*    */ {
/* 29 */   SSLContextBuilder sslContextBuilder = SSLContextBuilder.create();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SSLSocketFactoryBuilder create() {
/* 38 */     return new SSLSocketFactoryBuilder();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLSocketFactoryBuilder setProtocol(String protocol) {
/* 48 */     this.sslContextBuilder.setProtocol(protocol);
/* 49 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLSocketFactoryBuilder setTrustManagers(TrustManager... trustManagers) {
/* 59 */     this.sslContextBuilder.setTrustManagers(trustManagers);
/* 60 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLSocketFactoryBuilder setKeyManagers(KeyManager... keyManagers) {
/* 70 */     this.sslContextBuilder.setKeyManagers(keyManagers);
/* 71 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLSocketFactoryBuilder setSecureRandom(SecureRandom secureRandom) {
/* 81 */     this.sslContextBuilder.setSecureRandom(secureRandom);
/* 82 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public SSLSocketFactory build() throws NoSuchAlgorithmException, KeyManagementException {
/* 93 */     return this.sslContextBuilder.buildChecked().getSocketFactory();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ssl\SSLSocketFactoryBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */