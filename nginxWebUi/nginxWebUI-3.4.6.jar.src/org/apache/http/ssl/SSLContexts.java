/*    */ package org.apache.http.ssl;
/*    */ 
/*    */ import java.security.KeyManagementException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import javax.net.ssl.SSLContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SSLContexts
/*    */ {
/*    */   public static SSLContext createDefault() throws SSLInitializationException {
/*    */     try {
/* 51 */       SSLContext sslContext = SSLContext.getInstance("TLS");
/* 52 */       sslContext.init(null, null, null);
/* 53 */       return sslContext;
/* 54 */     } catch (NoSuchAlgorithmException ex) {
/* 55 */       throw new SSLInitializationException(ex.getMessage(), ex);
/* 56 */     } catch (KeyManagementException ex) {
/* 57 */       throw new SSLInitializationException(ex.getMessage(), ex);
/*    */     } 
/*    */   }
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
/*    */   public static SSLContext createSystemDefault() throws SSLInitializationException {
/*    */     try {
/* 72 */       return SSLContext.getDefault();
/* 73 */     } catch (NoSuchAlgorithmException ex) {
/* 74 */       return createDefault();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SSLContextBuilder custom() {
/* 84 */     return SSLContextBuilder.create();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\ssl\SSLContexts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */