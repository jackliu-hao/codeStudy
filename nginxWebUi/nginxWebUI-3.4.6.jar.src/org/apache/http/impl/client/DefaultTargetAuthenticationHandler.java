/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.apache.http.Header;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.auth.MalformedChallengeException;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ import org.apache.http.util.Args;
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
/*    */ public class DefaultTargetAuthenticationHandler
/*    */   extends AbstractAuthenticationHandler
/*    */ {
/*    */   public boolean isAuthenticationRequested(HttpResponse response, HttpContext context) {
/* 64 */     Args.notNull(response, "HTTP response");
/* 65 */     int status = response.getStatusLine().getStatusCode();
/* 66 */     return (status == 401);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, Header> getChallenges(HttpResponse response, HttpContext context) throws MalformedChallengeException {
/* 73 */     Args.notNull(response, "HTTP response");
/* 74 */     Header[] headers = response.getHeaders("WWW-Authenticate");
/* 75 */     return parseChallenges(headers);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected List<String> getAuthPreferences(HttpResponse response, HttpContext context) {
/* 84 */     List<String> authpref = (List<String>)response.getParams().getParameter("http.auth.target-scheme-pref");
/*    */     
/* 86 */     if (authpref != null) {
/* 87 */       return authpref;
/*    */     }
/* 89 */     return super.getAuthPreferences(response, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultTargetAuthenticationHandler.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */