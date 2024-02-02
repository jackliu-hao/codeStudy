/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import java.net.URI;
/*    */ import org.apache.http.HttpRequest;
/*    */ import org.apache.http.HttpResponse;
/*    */ import org.apache.http.ProtocolException;
/*    */ import org.apache.http.annotation.Contract;
/*    */ import org.apache.http.annotation.ThreadingBehavior;
/*    */ import org.apache.http.client.RedirectHandler;
/*    */ import org.apache.http.client.RedirectStrategy;
/*    */ import org.apache.http.client.methods.HttpGet;
/*    */ import org.apache.http.client.methods.HttpHead;
/*    */ import org.apache.http.client.methods.HttpUriRequest;
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
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ @Contract(threading = ThreadingBehavior.IMMUTABLE)
/*    */ class DefaultRedirectStrategyAdaptor
/*    */   implements RedirectStrategy
/*    */ {
/*    */   private final RedirectHandler handler;
/*    */   
/*    */   public DefaultRedirectStrategyAdaptor(RedirectHandler handler) {
/* 55 */     this.handler = handler;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 63 */     return this.handler.isRedirectRequested(response, context);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public HttpUriRequest getRedirect(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
/* 71 */     URI uri = this.handler.getLocationURI(response, context);
/* 72 */     String method = request.getRequestLine().getMethod();
/* 73 */     if (method.equalsIgnoreCase("HEAD")) {
/* 74 */       return (HttpUriRequest)new HttpHead(uri);
/*    */     }
/* 76 */     return (HttpUriRequest)new HttpGet(uri);
/*    */   }
/*    */ 
/*    */   
/*    */   public RedirectHandler getHandler() {
/* 81 */     return this.handler;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\DefaultRedirectStrategyAdaptor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */