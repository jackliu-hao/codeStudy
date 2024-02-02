/*    */ package org.apache.http.conn.params;
/*    */ 
/*    */ import java.net.InetAddress;
/*    */ import org.apache.http.HttpHost;
/*    */ import org.apache.http.conn.routing.HttpRoute;
/*    */ import org.apache.http.params.HttpAbstractParamBean;
/*    */ import org.apache.http.params.HttpParams;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public class ConnRouteParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public ConnRouteParamBean(HttpParams params) {
/* 50 */     super(params);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setDefaultProxy(HttpHost defaultProxy) {
/* 55 */     this.params.setParameter("http.route.default-proxy", defaultProxy);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLocalAddress(InetAddress address) {
/* 60 */     this.params.setParameter("http.route.local-address", address);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setForcedRoute(HttpRoute route) {
/* 65 */     this.params.setParameter("http.route.forced-route", route);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnRouteParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */