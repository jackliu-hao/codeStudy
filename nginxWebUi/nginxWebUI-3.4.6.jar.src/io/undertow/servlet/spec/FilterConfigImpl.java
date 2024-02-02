/*    */ package io.undertow.servlet.spec;
/*    */ 
/*    */ import io.undertow.servlet.api.FilterInfo;
/*    */ import io.undertow.servlet.util.IteratorEnumeration;
/*    */ import java.util.Enumeration;
/*    */ import javax.servlet.FilterConfig;
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
/*    */ public class FilterConfigImpl
/*    */   implements FilterConfig
/*    */ {
/*    */   private final FilterInfo filterInfo;
/*    */   private final ServletContext servletContext;
/*    */   
/*    */   public FilterConfigImpl(FilterInfo filterInfo, ServletContext servletContext) {
/* 38 */     this.filterInfo = filterInfo;
/* 39 */     this.servletContext = servletContext;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getFilterName() {
/* 44 */     return this.filterInfo.getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public ServletContext getServletContext() {
/* 49 */     return this.servletContext;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getInitParameter(String name) {
/* 54 */     return (String)this.filterInfo.getInitParams().get(name);
/*    */   }
/*    */ 
/*    */   
/*    */   public Enumeration<String> getInitParameterNames() {
/* 59 */     return (Enumeration<String>)new IteratorEnumeration(this.filterInfo.getInitParams().keySet().iterator());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\spec\FilterConfigImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */