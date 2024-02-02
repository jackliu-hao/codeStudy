/*    */ package org.apache.http.params;
/*    */ 
/*    */ import org.apache.http.HttpVersion;
/*    */ import org.apache.http.ProtocolVersion;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
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
/*    */ public class HttpProtocolParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public HttpProtocolParamBean(HttpParams params) {
/* 46 */     super(params);
/*    */   }
/*    */   
/*    */   public void setHttpElementCharset(String httpElementCharset) {
/* 50 */     HttpProtocolParams.setHttpElementCharset(this.params, httpElementCharset);
/*    */   }
/*    */   
/*    */   public void setContentCharset(String contentCharset) {
/* 54 */     HttpProtocolParams.setContentCharset(this.params, contentCharset);
/*    */   }
/*    */   
/*    */   public void setVersion(HttpVersion version) {
/* 58 */     HttpProtocolParams.setVersion(this.params, (ProtocolVersion)version);
/*    */   }
/*    */   
/*    */   public void setUserAgent(String userAgent) {
/* 62 */     HttpProtocolParams.setUserAgent(this.params, userAgent);
/*    */   }
/*    */   
/*    */   public void setUseExpectContinue(boolean useExpectContinue) {
/* 66 */     HttpProtocolParams.setUseExpectContinue(this.params, useExpectContinue);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpProtocolParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */