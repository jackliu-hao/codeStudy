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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class StrictHostnameVerifier
/*    */   extends AbstractVerifier
/*    */ {
/* 61 */   public static final StrictHostnameVerifier INSTANCE = new StrictHostnameVerifier();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final void verify(String host, String[] cns, String[] subjectAlts) throws SSLException {
/* 68 */     verify(host, cns, subjectAlts, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public final String toString() {
/* 73 */     return "STRICT";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\ssl\StrictHostnameVerifier.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */