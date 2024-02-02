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
/*    */ 
/*    */ @Deprecated
/*    */ public class ConnConnectionParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public ConnConnectionParamBean(HttpParams params) {
/* 47 */     super(params);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   @Deprecated
/*    */   public void setMaxStatusLineGarbage(int maxStatusLineGarbage) {
/* 56 */     this.params.setIntParameter("http.connection.max-status-line-garbage", maxStatusLineGarbage);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\params\ConnConnectionParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */