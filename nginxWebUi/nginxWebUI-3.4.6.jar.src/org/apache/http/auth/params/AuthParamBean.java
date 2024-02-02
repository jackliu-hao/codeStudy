/*    */ package org.apache.http.auth.params;
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
/*    */ 
/*    */ @Deprecated
/*    */ public class AuthParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public AuthParamBean(HttpParams params) {
/* 48 */     super(params);
/*    */   }
/*    */   
/*    */   public void setCredentialCharset(String charset) {
/* 52 */     AuthParams.setCredentialCharset(this.params, charset);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\auth\params\AuthParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */