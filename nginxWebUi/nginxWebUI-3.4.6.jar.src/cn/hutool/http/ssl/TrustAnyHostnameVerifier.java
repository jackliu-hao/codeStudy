/*    */ package cn.hutool.http.ssl;
/*    */ 
/*    */ import javax.net.ssl.HostnameVerifier;
/*    */ import javax.net.ssl.SSLSession;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class TrustAnyHostnameVerifier
/*    */   implements HostnameVerifier
/*    */ {
/*    */   public boolean verify(String hostname, SSLSession session) {
/* 15 */     return true;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\ssl\TrustAnyHostnameVerifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */