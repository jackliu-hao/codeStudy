/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class HttpMethodSecurityInfo
/*    */   extends SecurityInfo<HttpMethodSecurityInfo>
/*    */   implements Cloneable
/*    */ {
/*    */   private volatile String method;
/*    */   
/*    */   public String getMethod() {
/* 29 */     return this.method;
/*    */   }
/*    */   
/*    */   public HttpMethodSecurityInfo setMethod(String method) {
/* 33 */     this.method = method;
/* 34 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   protected HttpMethodSecurityInfo createInstance() {
/* 39 */     return new HttpMethodSecurityInfo();
/*    */   }
/*    */ 
/*    */   
/*    */   public HttpMethodSecurityInfo clone() {
/* 44 */     HttpMethodSecurityInfo info = super.clone();
/* 45 */     info.method = this.method;
/* 46 */     return info;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\HttpMethodSecurityInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */