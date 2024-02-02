/*     */ package javax.servlet;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.util.Enumeration;
/*     */ import java.util.ResourceBundle;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class GenericFilter
/*     */   implements Filter, FilterConfig, Serializable
/*     */ {
/*     */   private static final String LSTRING_FILE = "javax.servlet.LocalStrings";
/*  77 */   private static final ResourceBundle lStrings = ResourceBundle.getBundle("javax.servlet.LocalStrings");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private transient FilterConfig config;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getInitParameter(String name) {
/* 112 */     FilterConfig fc = getFilterConfig();
/* 113 */     if (fc == null) {
/* 114 */       throw new IllegalStateException(lStrings
/* 115 */           .getString("err.filter_config_not_initialized"));
/*     */     }
/*     */     
/* 118 */     return fc.getInitParameter(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Enumeration<String> getInitParameterNames() {
/* 140 */     FilterConfig fc = getFilterConfig();
/* 141 */     if (fc == null) {
/* 142 */       throw new IllegalStateException(lStrings
/* 143 */           .getString("err.filter_config_not_initialized"));
/*     */     }
/*     */     
/* 146 */     return fc.getInitParameterNames();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FilterConfig getFilterConfig() {
/* 159 */     return this.config;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ServletContext getServletContext() {
/* 178 */     FilterConfig sc = getFilterConfig();
/* 179 */     if (sc == null) {
/* 180 */       throw new IllegalStateException(lStrings
/* 181 */           .getString("err.filter_config_not_initialized"));
/*     */     }
/*     */     
/* 184 */     return sc.getServletContext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init(FilterConfig config) throws ServletException {
/* 211 */     this.config = config;
/* 212 */     init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void init() throws ServletException {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFilterName() {
/* 246 */     FilterConfig sc = getFilterConfig();
/* 247 */     if (sc == null) {
/* 248 */       throw new IllegalStateException(lStrings
/* 249 */           .getString("err.servlet_config_not_initialized"));
/*     */     }
/*     */     
/* 252 */     return sc.getFilterName();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\servlet\GenericFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */