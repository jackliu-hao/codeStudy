/*    */ package org.apache.http.params;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public class HttpConnectionParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public HttpConnectionParamBean(HttpParams params) {
/* 44 */     super(params);
/*    */   }
/*    */   
/*    */   public void setSoTimeout(int soTimeout) {
/* 48 */     HttpConnectionParams.setSoTimeout(this.params, soTimeout);
/*    */   }
/*    */   
/*    */   public void setTcpNoDelay(boolean tcpNoDelay) {
/* 52 */     HttpConnectionParams.setTcpNoDelay(this.params, tcpNoDelay);
/*    */   }
/*    */   
/*    */   public void setSocketBufferSize(int socketBufferSize) {
/* 56 */     HttpConnectionParams.setSocketBufferSize(this.params, socketBufferSize);
/*    */   }
/*    */   
/*    */   public void setLinger(int linger) {
/* 60 */     HttpConnectionParams.setLinger(this.params, linger);
/*    */   }
/*    */   
/*    */   public void setConnectionTimeout(int connectionTimeout) {
/* 64 */     HttpConnectionParams.setConnectionTimeout(this.params, connectionTimeout);
/*    */   }
/*    */   
/*    */   public void setStaleCheckingEnabled(boolean staleCheckingEnabled) {
/* 68 */     HttpConnectionParams.setStaleCheckingEnabled(this.params, staleCheckingEnabled);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpConnectionParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */