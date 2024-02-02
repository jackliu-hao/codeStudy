/*    */ package cn.hutool.core.net;
/*    */ 
/*    */ import java.net.Socket;
/*    */ import java.security.cert.X509Certificate;
/*    */ import javax.net.ssl.SSLEngine;
/*    */ import javax.net.ssl.X509ExtendedTrustManager;
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
/*    */ public class DefaultTrustManager
/*    */   extends X509ExtendedTrustManager
/*    */ {
/* 21 */   public static DefaultTrustManager INSTANCE = new DefaultTrustManager();
/*    */ 
/*    */   
/*    */   public X509Certificate[] getAcceptedIssuers() {
/* 25 */     return null;
/*    */   }
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] chain, String authType) {}
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] chain, String authType) {}
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {}
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] x509Certificates, String s, Socket socket) {}
/*    */   
/*    */   public void checkClientTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {}
/*    */   
/*    */   public void checkServerTrusted(X509Certificate[] x509Certificates, String s, SSLEngine sslEngine) {}
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\net\DefaultTrustManager.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */