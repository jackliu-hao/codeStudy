/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.commons.logging.Log;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.auth.AuthState;
/*    */ import org.apache.http.client.AuthenticationStrategy;
/*    */ import org.apache.http.impl.auth.HttpAuthenticator;
/*    */ import org.apache.http.protocol.HttpContext;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public class HttpAuthenticator
/*    */   extends HttpAuthenticator
/*    */ {
/*    */   public HttpAuthenticator(Log log) {
/* 45 */     super(log);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpAuthenticator() {}
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean authenticate(HttpHost host, HttpResponse response, AuthenticationStrategy authStrategy, AuthState authState, HttpContext context) {
/* 58 */     return handleAuthChallenge(host, response, authStrategy, authState, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\HttpAuthenticator.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */