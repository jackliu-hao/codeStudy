/*    */ package org.apache.http.conn.params;
/*    */ 
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
/*    */ @Deprecated
/*    */ public class ConnManagerParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public ConnManagerParamBean(HttpParams params) {
/* 46 */     super(params);
/*    */   }
/*    */   
/*    */   public void setTimeout(long timeout) {
/* 50 */     this.params.setLongParameter("http.conn-manager.timeout", timeout);
/*    */   }
/*    */   
/*    */   public void setMaxTotalConnections(int maxConnections) {
/* 54 */     this.params.setIntParameter("http.conn-manager.max-total", maxConnections);
/*    */   }
/*    */   
/*    */   public void setConnectionsPerRoute(ConnPerRouteBean connPerRoute) {
/* 58 */     this.params.setParameter("http.conn-manager.max-per-route", connPerRoute);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnManagerParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */