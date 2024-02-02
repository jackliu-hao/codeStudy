/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.servlet.api.FilterInfo;
/*     */ import io.undertow.servlet.api.LifecycleInterceptor;
/*     */ import io.undertow.servlet.api.ServletInfo;
/*     */ import java.util.List;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterConfig;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletConfig;
/*     */ import javax.servlet.ServletException;
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
/*     */ class LifecyleInterceptorInvocation
/*     */   implements LifecycleInterceptor.LifecycleContext
/*     */ {
/*     */   private final List<LifecycleInterceptor> list;
/*     */   private final ServletInfo servletInfo;
/*     */   private final FilterInfo filterInfo;
/*     */   private final Servlet servlet;
/*     */   private final Filter filter;
/*     */   private int i;
/*     */   private final ServletConfig servletConfig;
/*     */   private final FilterConfig filterConfig;
/*     */   
/*     */   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, ServletInfo servletInfo, Servlet servlet, ServletConfig servletConfig) {
/*  46 */     this.list = list;
/*  47 */     this.servletInfo = servletInfo;
/*  48 */     this.servlet = servlet;
/*  49 */     this.servletConfig = servletConfig;
/*  50 */     this.filter = null;
/*  51 */     this.filterConfig = null;
/*  52 */     this.filterInfo = null;
/*  53 */     this.i = list.size();
/*     */   }
/*     */   
/*     */   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, ServletInfo servletInfo, Servlet servlet) {
/*  57 */     this.list = list;
/*  58 */     this.servlet = servlet;
/*  59 */     this.servletInfo = servletInfo;
/*  60 */     this.filterInfo = null;
/*  61 */     this.servletConfig = null;
/*  62 */     this.filter = null;
/*  63 */     this.filterConfig = null;
/*  64 */     this.i = list.size();
/*     */   }
/*     */   
/*     */   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, FilterInfo filterInfo, Filter filter, FilterConfig filterConfig) {
/*  68 */     this.list = list;
/*  69 */     this.servlet = null;
/*  70 */     this.servletConfig = null;
/*  71 */     this.filter = filter;
/*  72 */     this.filterConfig = filterConfig;
/*  73 */     this.filterInfo = filterInfo;
/*  74 */     this.servletInfo = null;
/*  75 */     this.i = list.size();
/*     */   }
/*     */   
/*     */   LifecyleInterceptorInvocation(List<LifecycleInterceptor> list, FilterInfo filterInfo, Filter filter) {
/*  79 */     this.list = list;
/*  80 */     this.servlet = null;
/*  81 */     this.servletConfig = null;
/*  82 */     this.filter = filter;
/*  83 */     this.filterConfig = null;
/*  84 */     this.filterInfo = filterInfo;
/*  85 */     this.servletInfo = null;
/*  86 */     this.i = list.size();
/*     */   }
/*     */ 
/*     */   
/*     */   public void proceed() throws ServletException {
/*  91 */     if (--this.i >= 0) {
/*  92 */       LifecycleInterceptor next = this.list.get(this.i);
/*  93 */       if (this.filter != null) {
/*  94 */         if (this.filterConfig == null) {
/*  95 */           next.destroy(this.filterInfo, this.filter, this);
/*     */         } else {
/*  97 */           next.init(this.filterInfo, this.filter, this);
/*     */         }
/*     */       
/* 100 */       } else if (this.servletConfig == null) {
/* 101 */         next.destroy(this.servletInfo, this.servlet, this);
/*     */       } else {
/* 103 */         next.init(this.servletInfo, this.servlet, this);
/*     */       }
/*     */     
/* 106 */     } else if (this.i == -1) {
/* 107 */       if (this.filter != null) {
/* 108 */         if (this.filterConfig == null) {
/* 109 */           this.filter.destroy();
/*     */         } else {
/* 111 */           this.filter.init(this.filterConfig);
/*     */         }
/*     */       
/* 114 */       } else if (this.servletConfig == null) {
/* 115 */         this.servlet.destroy();
/*     */       } else {
/* 117 */         this.servlet.init(this.servletConfig);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\LifecyleInterceptorInvocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */