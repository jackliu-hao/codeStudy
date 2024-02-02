/*    */ package org.apache.http.conn.ssl;
/*    */ 
/*    */ import java.security.cert.CertificateException;
/*    */ import java.security.cert.X509Certificate;
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
/*    */ public class TrustAllStrategy
/*    */   implements TrustStrategy
/*    */ {
/* 41 */   public static final TrustAllStrategy INSTANCE = new TrustAllStrategy();
/*    */ 
/*    */   
/*    */   public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 45 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\TrustAllStrategy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */