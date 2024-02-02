/*    */ package io.undertow.servlet.api;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServletSecurityInfo
/*    */   extends SecurityInfo<ServletSecurityInfo>
/*    */   implements Cloneable
/*    */ {
/* 29 */   private final List<HttpMethodSecurityInfo> httpMethodSecurityInfo = new ArrayList<>();
/*    */ 
/*    */   
/*    */   protected ServletSecurityInfo createInstance() {
/* 33 */     return new ServletSecurityInfo();
/*    */   }
/*    */   
/*    */   public ServletSecurityInfo addHttpMethodSecurityInfo(HttpMethodSecurityInfo info) {
/* 37 */     this.httpMethodSecurityInfo.add(info);
/* 38 */     return this;
/*    */   }
/*    */   
/*    */   public List<HttpMethodSecurityInfo> getHttpMethodSecurityInfo() {
/* 42 */     return new ArrayList<>(this.httpMethodSecurityInfo);
/*    */   }
/*    */ 
/*    */   
/*    */   public ServletSecurityInfo clone() {
/* 47 */     ServletSecurityInfo info = super.clone();
/* 48 */     for (HttpMethodSecurityInfo method : this.httpMethodSecurityInfo) {
/* 49 */       info.httpMethodSecurityInfo.add(method.clone());
/*    */     }
/* 51 */     return info;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\api\ServletSecurityInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */