/*    */ package org.apache.http.conn.ssl;
/*    */ 
/*    */ import javax.net.ssl.SSLException;
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
/*    */ public class BrowserCompatHostnameVerifier
/*    */   extends AbstractVerifier
/*    */ {
/* 55 */   public static final BrowserCompatHostnameVerifier INSTANCE = new BrowserCompatHostnameVerifier();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
/* 62 */     verify(host, cns, subjectAlts, false);
/*    */   }
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 67 */     return "BROWSER_COMPATIBLE";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\BrowserCompatHostnameVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */