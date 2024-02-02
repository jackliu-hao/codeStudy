/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.core.ManagedFilter;
/*     */ import io.undertow.servlet.core.ManagedServlet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.Executor;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.http.MappingMatch;
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
/*     */ public class ServletChain
/*     */ {
/*     */   private final HttpHandler handler;
/*     */   private final ManagedServlet managedServlet;
/*     */   private final String servletPath;
/*     */   private final Executor executor;
/*     */   private final boolean defaultServletMapping;
/*     */   private final MappingMatch mappingMatch;
/*     */   private final String pattern;
/*     */   private final Map<DispatcherType, List<ManagedFilter>> filters;
/*     */   
/*     */   public ServletChain(HttpHandler handler, ManagedServlet managedServlet, String servletPath, boolean defaultServletMapping, MappingMatch mappingMatch, String pattern, Map<DispatcherType, List<ManagedFilter>> filters) {
/*  48 */     this(handler, managedServlet, servletPath, defaultServletMapping, mappingMatch, pattern, filters, true);
/*     */   }
/*     */   
/*     */   private ServletChain(final HttpHandler originalHandler, ManagedServlet managedServlet, String servletPath, boolean defaultServletMapping, MappingMatch mappingMatch, String pattern, Map<DispatcherType, List<ManagedFilter>> filters, boolean wrapHandler) {
/*  52 */     if (wrapHandler) {
/*  53 */       this.handler = new HttpHandler()
/*     */         {
/*     */           private volatile boolean initDone = false;
/*     */ 
/*     */           
/*     */           public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  59 */             if (!this.initDone) {
/*  60 */               synchronized (this) {
/*  61 */                 if (!this.initDone) {
/*  62 */                   ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  63 */                   ServletChain.this.forceInit(src.getDispatcherType());
/*  64 */                   this.initDone = true;
/*     */                 } 
/*     */               } 
/*     */             }
/*  68 */             originalHandler.handleRequest(exchange);
/*     */           }
/*     */         };
/*     */     } else {
/*  72 */       this.handler = originalHandler;
/*     */     } 
/*  74 */     this.managedServlet = managedServlet;
/*  75 */     this.servletPath = servletPath;
/*  76 */     this.defaultServletMapping = defaultServletMapping;
/*  77 */     this.mappingMatch = mappingMatch;
/*  78 */     this.pattern = pattern;
/*  79 */     this.executor = managedServlet.getServletInfo().getExecutor();
/*  80 */     this.filters = filters;
/*     */   }
/*     */   
/*     */   public ServletChain(ServletChain other, String pattern, MappingMatch mappingMatch) {
/*  84 */     this(other.getHandler(), other.getManagedServlet(), other.getServletPath(), other.isDefaultServletMapping(), mappingMatch, pattern, other.filters, false);
/*     */   }
/*     */   
/*     */   public HttpHandler getHandler() {
/*  88 */     return this.handler;
/*     */   }
/*     */   
/*     */   public ManagedServlet getManagedServlet() {
/*  92 */     return this.managedServlet;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getServletPath() {
/* 100 */     return this.servletPath;
/*     */   }
/*     */   
/*     */   public Executor getExecutor() {
/* 104 */     return this.executor;
/*     */   }
/*     */   
/*     */   public boolean isDefaultServletMapping() {
/* 108 */     return this.defaultServletMapping;
/*     */   }
/*     */   
/*     */   public MappingMatch getMappingMatch() {
/* 112 */     return this.mappingMatch;
/*     */   }
/*     */   
/*     */   public String getPattern() {
/* 116 */     return this.pattern;
/*     */   }
/*     */ 
/*     */   
/*     */   void forceInit(DispatcherType dispatcherType) throws ServletException {
/* 121 */     if (this.filters != null) {
/* 122 */       List<ManagedFilter> list = this.filters.get(dispatcherType);
/* 123 */       if (list != null && !list.isEmpty()) {
/* 124 */         for (int i = 0; i < list.size(); i++) {
/* 125 */           ManagedFilter filter = list.get(i);
/* 126 */           filter.forceInit();
/*     */         } 
/*     */       }
/*     */     } 
/* 130 */     this.managedServlet.forceInit();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */