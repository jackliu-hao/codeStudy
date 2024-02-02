/*     */ package io.undertow.servlet.core;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.api.DeploymentManager;
/*     */ import io.undertow.servlet.api.FilterInfo;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.spec.FilterConfigImpl;
/*     */ import io.undertow.servlet.spec.ServletContextImpl;
/*     */ import java.io.IOException;
/*     */ import javax.servlet.Filter;
/*     */ import javax.servlet.FilterChain;
/*     */ import javax.servlet.FilterConfig;
/*     */ import javax.servlet.ServletContext;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
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
/*     */ public class ManagedFilter
/*     */   implements Lifecycle
/*     */ {
/*     */   private final FilterInfo filterInfo;
/*     */   private final ServletContextImpl servletContext;
/*     */   private volatile boolean started = false;
/*     */   private volatile Filter filter;
/*     */   private volatile InstanceHandle<? extends Filter> handle;
/*     */   
/*     */   public ManagedFilter(FilterInfo filterInfo, ServletContextImpl servletContext) {
/*  50 */     this.filterInfo = filterInfo;
/*  51 */     this.servletContext = servletContext;
/*     */   }
/*     */   
/*     */   public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
/*  55 */     if (this.servletContext.getDeployment().getDeploymentState() != DeploymentManager.State.STARTED) {
/*  56 */       throw UndertowServletMessages.MESSAGES.deploymentStopped(this.servletContext.getDeployment().getDeploymentInfo().getDeploymentName());
/*     */     }
/*  58 */     if (!this.started) {
/*  59 */       start();
/*     */     }
/*  61 */     getFilter().doFilter(request, response, chain);
/*     */   }
/*     */   
/*     */   private Filter getFilter() throws ServletException {
/*  65 */     if (this.filter == null) {
/*  66 */       createFilter();
/*     */     }
/*  68 */     return this.filter;
/*     */   }
/*     */   
/*     */   public void createFilter() throws ServletException {
/*  72 */     synchronized (this) {
/*  73 */       if (this.filter == null) {
/*     */         try {
/*  75 */           this.handle = this.filterInfo.getInstanceFactory().createInstance();
/*  76 */         } catch (Exception e) {
/*  77 */           throw UndertowServletMessages.MESSAGES.couldNotInstantiateComponent(this.filterInfo.getName(), e);
/*     */         } 
/*  79 */         Filter filter = (Filter)this.handle.getInstance();
/*  80 */         (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.filterInfo, filter, (FilterConfig)new FilterConfigImpl(this.filterInfo, (ServletContext)this.servletContext))).proceed();
/*  81 */         this.filter = filter;
/*     */       } 
/*     */     } 
/*     */   }
/*     */   
/*     */   public synchronized void start() throws ServletException {
/*  87 */     if (!this.started)
/*     */     {
/*  89 */       this.started = true;
/*     */     }
/*     */   }
/*     */   
/*     */   public synchronized void stop() {
/*  94 */     this.started = false;
/*  95 */     if (this.handle != null) {
/*     */       try {
/*  97 */         (new LifecyleInterceptorInvocation(this.servletContext.getDeployment().getDeploymentInfo().getLifecycleInterceptors(), this.filterInfo, this.filter)).proceed();
/*  98 */       } catch (Exception e) {
/*  99 */         UndertowServletLogger.ROOT_LOGGER.failedToDestroy(this.filterInfo, e);
/*     */       } 
/* 101 */       this.handle.release();
/*     */     } 
/* 103 */     this.filter = null;
/* 104 */     this.handle = null;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isStarted() {
/* 109 */     return this.started;
/*     */   }
/*     */   
/*     */   public FilterInfo getFilterInfo() {
/* 113 */     return this.filterInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 118 */     return "ManagedFilter{filterInfo=" + this.filterInfo + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void forceInit() throws ServletException {
/* 124 */     if (this.filter == null)
/* 125 */       createFilter(); 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\core\ManagedFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */