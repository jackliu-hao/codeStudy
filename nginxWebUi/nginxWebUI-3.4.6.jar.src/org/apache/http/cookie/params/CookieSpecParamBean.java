/*    */ package org.apache.http.cookie.params;
/*    */ 
/*    */ import java.util.Collection;
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
/*    */ public class CookieSpecParamBean
/*    */   extends HttpAbstractParamBean
/*    */ {
/*    */   public CookieSpecParamBean(HttpParams params) {
/* 49 */     super(params);
/*    */   }
/*    */   
/*    */   public void setDatePatterns(Collection<String> patterns) {
/* 53 */     this.params.setParameter("http.protocol.cookie-datepatterns", patterns);
/*    */   }
/*    */   
/*    */   public void setSingleHeader(boolean singleHeader) {
/* 57 */     this.params.setBooleanParameter("http.protocol.single-cookie-header", singleHeader);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\cookie\params\CookieSpecParamBean.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */