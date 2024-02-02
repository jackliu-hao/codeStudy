/*    */ package org.apache.http.params;
/*    */ 
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
/*    */ @Deprecated
/*    */ public abstract class HttpAbstractParamBean
/*    */ {
/*    */   protected final HttpParams params;
/*    */   
/*    */   public HttpAbstractParamBean(HttpParams params) {
/* 45 */     this.params = (HttpParams)Args.notNull(params, "HTTP parameters");
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\params\HttpAbstractParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */