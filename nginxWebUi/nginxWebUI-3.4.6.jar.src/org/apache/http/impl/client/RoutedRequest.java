/*    */ package org.apache.http.impl.client;
/*    */ 
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public class RoutedRequest
/*    */ {
/*    */   protected final RequestWrapper request;
/*    */   protected final HttpRoute route;
/*    */   
/*    */   public RoutedRequest(RequestWrapper req, HttpRoute route) {
/* 53 */     this.request = req;
/* 54 */     this.route = route;
/*    */   }
/*    */   
/*    */   public final RequestWrapper getRequest() {
/* 58 */     return this.request;
/*    */   }
/*    */   
/*    */   public final HttpRoute getRoute() {
/* 62 */     return this.route;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\client\RoutedRequest.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */