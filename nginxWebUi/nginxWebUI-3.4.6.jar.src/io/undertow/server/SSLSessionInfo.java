/*    */ package io.undertow.server;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.security.cert.Certificate;
/*    */ import javax.net.ssl.SSLPeerUnverifiedException;
/*    */ import javax.net.ssl.SSLSession;
/*    */ import javax.security.cert.X509Certificate;
/*    */ import org.xnio.SslClientAuthMode;
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
/*    */ 
/*    */ public interface SSLSessionInfo
/*    */ {
/*    */   static int calculateKeySize(String cipherSuite) {
/* 46 */     if (cipherSuite == null)
/* 47 */       return 0; 
/* 48 */     if (cipherSuite.contains("WITH_AES_256_"))
/* 49 */       return 256; 
/* 50 */     if (cipherSuite.contains("WITH_RC4_128_"))
/* 51 */       return 128; 
/* 52 */     if (cipherSuite.contains("WITH_AES_128_"))
/* 53 */       return 128; 
/* 54 */     if (cipherSuite.contains("WITH_RC4_40_"))
/* 55 */       return 40; 
/* 56 */     if (cipherSuite.contains("WITH_3DES_EDE_CBC_"))
/* 57 */       return 168; 
/* 58 */     if (cipherSuite.contains("WITH_IDEA_CBC_"))
/* 59 */       return 128; 
/* 60 */     if (cipherSuite.contains("WITH_RC2_CBC_40_"))
/* 61 */       return 40; 
/* 62 */     if (cipherSuite.contains("WITH_DES40_CBC_"))
/* 63 */       return 40; 
/* 64 */     if (cipherSuite.contains("WITH_DES_CBC_")) {
/* 65 */       return 56;
/*    */     }
/* 67 */     return 0;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   byte[] getSessionId();
/*    */ 
/*    */ 
/*    */   
/*    */   String getCipherSuite();
/*    */ 
/*    */   
/*    */   default int getKeySize() {
/* 80 */     return calculateKeySize(getCipherSuite());
/*    */   }
/*    */   
/*    */   Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException, RenegotiationRequiredException;
/*    */   
/*    */   @Deprecated
/*    */   X509Certificate[] getPeerCertificateChain() throws SSLPeerUnverifiedException, RenegotiationRequiredException;
/*    */   
/*    */   void renegotiate(HttpServerExchange paramHttpServerExchange, SslClientAuthMode paramSslClientAuthMode) throws IOException;
/*    */   
/*    */   SSLSession getSSLSession();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\SSLSessionInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */