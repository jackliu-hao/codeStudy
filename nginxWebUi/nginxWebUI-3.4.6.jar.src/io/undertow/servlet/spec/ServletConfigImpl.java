/*    */ package io.undertow.servlet.spec;
/*    */ 
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.api.ServletInfo;
/*    */ import io.undertow.servlet.util.IteratorEnumeration;
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.ServletConfig;
/*    */ import javax.servlet.ServletContext;
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
/*    */ public class ServletConfigImpl
/*    */   implements ServletConfig
/*    */ {
/*    */   private final ServletInfo servletInfo;
/*    */   private final ServletContext servletContext;
/*    */   
/*    */   public ServletConfigImpl(ServletInfo servletInfo, ServletContext servletContext) {
/* 39 */     this.servletInfo = servletInfo;
/* 40 */     this.servletContext = servletContext;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getServletName() {
/* 45 */     return this.servletInfo.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public ServletContext getServletContext() {
/* 50 */     return this.servletContext;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInitParameter(String name) {
/* 55 */     if (name == null) {
/* 56 */       throw UndertowServletMessages.MESSAGES.nullName();
/*    */     }
/* 58 */     return (String)this.servletInfo.getInitParams().get(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Enumeration<String> getInitParameterNames() {
/* 63 */     return (Enumeration<String>)new IteratorEnumeration(this.servletInfo.getInitParams().keySet().iterator());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\ServletConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */