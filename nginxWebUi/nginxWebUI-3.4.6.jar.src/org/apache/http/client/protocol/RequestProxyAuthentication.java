/*    */ package org.apache.http.client.protocol;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.apache.http.HttpException;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.auth.AuthState;
/*    */ import org.apache.http.conn.HttpRoutedConnection;
/*    */ import org.apache.http.conn.routing.HttpRoute;
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
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ public class RequestProxyAuthentication
/*    */   extends RequestAuthenticationBase
/*    */ {
/*    */   public void process(HttpRequest request, HttpContext context) throws HttpException, IOException {
/* 63 */     Args.notNull(request, "HTTP request");
/* 64 */     Args.notNull(context, "HTTP context");
/*    */     
/* 66 */     if (request.containsHeader("Proxy-Authorization")) {
/*    */       return;
/*    */     }
/*    */     
/* 70 */     HttpRoutedConnection conn = (HttpRoutedConnection)context.getAttribute("http.connection");
/*    */     
/* 72 */     if (conn == null) {
/* 73 */       this.log.debug("HTTP connection not set in the context");
/*    */       return;
/*    */     } 
/* 76 */     HttpRoute route = conn.getRoute();
/* 77 */     if (route.isTunnelled()) {
/*    */       return;
/*    */     }
/*    */ 
/*    */     
/* 82 */     AuthState authState = (AuthState)context.getAttribute("http.auth.proxy-scope");
/*    */     
/* 84 */     if (authState == null) {
/* 85 */       this.log.debug("Proxy auth state not set in the context");
/*    */       return;
/*    */     } 
/* 88 */     if (this.log.isDebugEnabled()) {
/* 89 */       this.log.debug("Proxy auth state: " + authState.getState());
/*    */     }
/* 91 */     process(authState, request, context);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\client\protocol\RequestProxyAuthentication.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */