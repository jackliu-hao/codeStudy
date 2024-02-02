/*    */ package org.apache.http.conn.ssl;
/*    */ 
/*    */ import javax.net.ssl.HostnameVerifier;
/*    */ import javax.net.ssl.SSLSession;
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
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class NoopHostnameVerifier
/*    */   implements HostnameVerifier
/*    */ {
/* 45 */   public static final NoopHostnameVerifier INSTANCE = new NoopHostnameVerifier();
/*    */ 
/*    */   
/*    */   public boolean verify(String s, SSLSession sslSession) {
/* 49 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 54 */     return "NO_OP";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\NoopHostnameVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */