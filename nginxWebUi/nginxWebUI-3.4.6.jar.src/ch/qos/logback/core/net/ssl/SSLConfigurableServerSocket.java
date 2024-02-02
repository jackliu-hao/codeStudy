/*    */ package ch.qos.logback.core.net.ssl;
/*    */ 
/*    */ import javax.net.ssl.SSLServerSocket;
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
/*    */ public class SSLConfigurableServerSocket
/*    */   implements SSLConfigurable
/*    */ {
/*    */   private final SSLServerSocket delegate;
/*    */   
/*    */   public SSLConfigurableServerSocket(SSLServerSocket delegate) {
/* 29 */     this.delegate = delegate;
/*    */   }
/*    */   
/*    */   public String[] getDefaultProtocols() {
/* 33 */     return this.delegate.getEnabledProtocols();
/*    */   }
/*    */   
/*    */   public String[] getSupportedProtocols() {
/* 37 */     return this.delegate.getSupportedProtocols();
/*    */   }
/*    */   
/*    */   public void setEnabledProtocols(String[] protocols) {
/* 41 */     this.delegate.setEnabledProtocols(protocols);
/*    */   }
/*    */   
/*    */   public String[] getDefaultCipherSuites() {
/* 45 */     return this.delegate.getEnabledCipherSuites();
/*    */   }
/*    */   
/*    */   public String[] getSupportedCipherSuites() {
/* 49 */     return this.delegate.getSupportedCipherSuites();
/*    */   }
/*    */   
/*    */   public void setEnabledCipherSuites(String[] suites) {
/* 53 */     this.delegate.setEnabledCipherSuites(suites);
/*    */   }
/*    */   
/*    */   public void setNeedClientAuth(boolean state) {
/* 57 */     this.delegate.setNeedClientAuth(state);
/*    */   }
/*    */   
/*    */   public void setWantClientAuth(boolean state) {
/* 61 */     this.delegate.setWantClientAuth(state);
/*    */   }
/*    */   
/*    */   public void setHostnameVerification(boolean verifyHostname) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\net\ssl\SSLConfigurableServerSocket.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */