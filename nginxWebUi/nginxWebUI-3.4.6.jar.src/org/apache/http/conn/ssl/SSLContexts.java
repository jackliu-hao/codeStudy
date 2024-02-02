/*    */ package org.apache.http.conn.ssl;
/*    */ 
/*    */ import java.security.KeyManagementException;
/*    */ import java.security.NoSuchAlgorithmException;
/*    */ import javax.net.ssl.SSLContext;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class SSLContexts
/*    */ {
/*    */   public static SSLContext createDefault() throws SSLInitializationException {
/*    */     try {
/* 58 */       SSLContext sslcontext = SSLContext.getInstance("TLS");
/* 59 */       sslcontext.init(null, null, null);
/* 60 */       return sslcontext;
/* 61 */     } catch (NoSuchAlgorithmException ex) {
/* 62 */       throw new SSLInitializationException(ex.getMessage(), ex);
/* 63 */     } catch (KeyManagementException ex) {
/* 64 */       throw new SSLInitializationException(ex.getMessage(), ex);
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
/* 79 */       return SSLContext.getDefault();
/* 80 */     } catch (NoSuchAlgorithmException ex) {
/* 81 */       return createDefault();
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SSLContextBuilder custom() {
/* 91 */     return new SSLContextBuilder();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\SSLContexts.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */