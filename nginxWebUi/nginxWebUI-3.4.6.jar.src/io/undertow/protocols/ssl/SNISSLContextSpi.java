/*    */ package io.undertow.protocols.ssl;
/*    */ 
/*    */ import java.security.KeyManagementException;
/*    */ import java.security.SecureRandom;
/*    */ import javax.net.ssl.KeyManager;
/*    */ import javax.net.ssl.SSLContextSpi;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import javax.net.ssl.SSLServerSocketFactory;
/*    */ import javax.net.ssl.SSLSessionContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class SNISSLContextSpi
/*    */   extends SSLContextSpi
/*    */ {
/*    */   private final SNIContextMatcher matcher;
/*    */   
/*    */   SNISSLContextSpi(SNIContextMatcher matcher) {
/* 39 */     this.matcher = matcher;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected void engineInit(KeyManager[] keyManagers, TrustManager[] trustManagers, SecureRandom secureRandom) throws KeyManagementException {}
/*    */ 
/*    */ 
/*    */   
/*    */   protected SSLSocketFactory engineGetSocketFactory() {
/* 49 */     return this.matcher.getDefaultContext().getSocketFactory();
/*    */   }
/*    */ 
/*    */   
/*    */   protected SSLServerSocketFactory engineGetServerSocketFactory() {
/* 54 */     return this.matcher.getDefaultContext().getServerSocketFactory();
/*    */   }
/*    */ 
/*    */   
/*    */   protected SSLEngine engineCreateSSLEngine() {
/* 59 */     return new SNISSLEngine(this.matcher);
/*    */   }
/*    */ 
/*    */   
/*    */   protected SSLEngine engineCreateSSLEngine(String s, int i) {
/* 64 */     return new SNISSLEngine(this.matcher, s, i);
/*    */   }
/*    */ 
/*    */   
/*    */   protected SSLSessionContext engineGetServerSessionContext() {
/* 69 */     return this.matcher.getDefaultContext().getServerSessionContext();
/*    */   }
/*    */ 
/*    */   
/*    */   protected SSLSessionContext engineGetClientSessionContext() {
/* 74 */     return this.matcher.getDefaultContext().getClientSessionContext();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\protocols\ssl\SNISSLContextSpi.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */