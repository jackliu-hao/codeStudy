/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import javax.net.ssl.SSLParameters;
/*    */ import javax.net.ssl.SSLSocket;
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
/*    */ public class SSLConfigurableSocket
/*    */   implements SSLConfigurable
/*    */ {
/*    */   private final SSLSocket delegate;
/*    */   
/*    */   public SSLConfigurableSocket(SSLSocket delegate) {
/* 30 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public String[] getDefaultProtocols() {
/* 34 */     return this.delegate.getEnabledProtocols();
/*    */   }
/*    */   
/*    */   public String[] getSupportedProtocols() {
/* 38 */     return this.delegate.getSupportedProtocols();
/*    */   }
/*    */   
/*    */   public void setEnabledProtocols(String[] protocols) {
/* 42 */     this.delegate.setEnabledProtocols(protocols);
/*    */   }
/*    */   
/*    */   public String[] getDefaultCipherSuites() {
/* 46 */     return this.delegate.getEnabledCipherSuites();
/*    */   }
/*    */   
/*    */   public String[] getSupportedCipherSuites() {
/* 50 */     return this.delegate.getSupportedCipherSuites();
/*    */   }
/*    */   
/*    */   public void setEnabledCipherSuites(String[] suites) {
/* 54 */     this.delegate.setEnabledCipherSuites(suites);
/*    */   }
/*    */   
/*    */   public void setNeedClientAuth(boolean state) {
/* 58 */     this.delegate.setNeedClientAuth(state);
/*    */   }
/*    */   
/*    */   public void setWantClientAuth(boolean state) {
/* 62 */     this.delegate.setWantClientAuth(state);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setHostnameVerification(boolean hostnameVerification) {
/* 67 */     if (!hostnameVerification) {
/*    */       return;
/*    */     }
/* 70 */     SSLParameters sslParameters = this.delegate.getSSLParameters();
/* 71 */     sslParameters.setEndpointIdentificationAlgorithm("HTTPS");
/* 72 */     this.delegate.setSSLParameters(sslParameters);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\SSLConfigurableSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */