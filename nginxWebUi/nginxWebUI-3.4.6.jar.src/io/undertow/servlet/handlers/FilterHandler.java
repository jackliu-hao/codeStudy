/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.UndertowServletMessages;
/*     */ import io.undertow.servlet.core.ManagedFilter;
/*     */ import java.io.IOException;
/*     */ import java.util.EnumMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import javax.servlet.DispatcherType;
/*     */ import javax.servlet.FilterChain;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilterHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final Map<DispatcherType, List<ManagedFilter>> filters;
/*     */   private final Map<DispatcherType, Boolean> asyncSupported;
/*     */   private final boolean allowNonStandardWrappers;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public FilterHandler(Map<DispatcherType, List<ManagedFilter>> filters, boolean allowNonStandardWrappers, HttpHandler next) {
/*  51 */     this.allowNonStandardWrappers = allowNonStandardWrappers;
/*  52 */     this.next = next;
/*  53 */     this.filters = new EnumMap<>(filters);
/*  54 */     Map<DispatcherType, Boolean> asyncSupported = new EnumMap<>(DispatcherType.class);
/*  55 */     for (Map.Entry<DispatcherType, List<ManagedFilter>> entry : filters.entrySet()) {
/*  56 */       boolean supported = true;
/*  57 */       for (ManagedFilter i : entry.getValue()) {
/*  58 */         if (!i.getFilterInfo().isAsyncSupported()) {
/*  59 */           supported = false;
/*     */           break;
/*     */         } 
/*     */       } 
/*  63 */       asyncSupported.put(entry.getKey(), Boolean.valueOf(supported));
/*     */     } 
/*  65 */     this.asyncSupported = asyncSupported;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  70 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  71 */     ServletRequest request = servletRequestContext.getServletRequest();
/*  72 */     ServletResponse response = servletRequestContext.getServletResponse();
/*  73 */     DispatcherType dispatcher = servletRequestContext.getDispatcherType();
/*  74 */     Boolean supported = this.asyncSupported.get(dispatcher);
/*  75 */     if (supported != null && !supported.booleanValue()) {
/*  76 */       servletRequestContext.setAsyncSupported(false);
/*     */     }
/*     */     
/*  79 */     List<ManagedFilter> filters = this.filters.get(dispatcher);
/*  80 */     if (filters == null) {
/*  81 */       this.next.handleRequest(exchange);
/*     */     } else {
/*  83 */       FilterChainImpl filterChain = new FilterChainImpl(exchange, filters, this.next, this.allowNonStandardWrappers);
/*  84 */       filterChain.doFilter(request, response);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static class FilterChainImpl
/*     */     implements FilterChain {
/*  90 */     int location = 0;
/*     */     final HttpServerExchange exchange;
/*     */     final List<ManagedFilter> filters;
/*     */     final HttpHandler next;
/*     */     final boolean allowNonStandardWrappers;
/*     */     
/*     */     private FilterChainImpl(HttpServerExchange exchange, List<ManagedFilter> filters, HttpHandler next, boolean allowNonStandardWrappers) {
/*  97 */       this.exchange = exchange;
/*  98 */       this.filters = filters;
/*  99 */       this.next = next;
/* 100 */       this.allowNonStandardWrappers = allowNonStandardWrappers;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
/* 108 */       ServletRequestContext servletRequestContext = (ServletRequestContext)this.exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 109 */       ServletRequest oldReq = servletRequestContext.getServletRequest();
/* 110 */       ServletResponse oldResp = servletRequestContext.getServletResponse();
/*     */       
/*     */       try {
/* 113 */         if (!this.allowNonStandardWrappers) {
/* 114 */           if (oldReq != request && 
/* 115 */             !(request instanceof javax.servlet.ServletRequestWrapper)) {
/* 116 */             throw UndertowServletMessages.MESSAGES.requestWasNotOriginalOrWrapper(request);
/*     */           }
/*     */           
/* 119 */           if (oldResp != response && 
/* 120 */             !(response instanceof javax.servlet.ServletResponseWrapper)) {
/* 121 */             throw UndertowServletMessages.MESSAGES.responseWasNotOriginalOrWrapper(response);
/*     */           }
/*     */         } 
/*     */         
/* 125 */         servletRequestContext.setServletRequest(request);
/* 126 */         servletRequestContext.setServletResponse(response);
/* 127 */         int index = this.location++;
/* 128 */         if (index >= this.filters.size()) {
/* 129 */           this.next.handleRequest(this.exchange);
/*     */         } else {
/* 131 */           ((ManagedFilter)this.filters.get(index)).doFilter(request, response, this);
/*     */         } 
/* 133 */       } catch (IOException e) {
/* 134 */         throw e;
/* 135 */       } catch (ServletException e) {
/* 136 */         throw e;
/* 137 */       } catch (RuntimeException e) {
/* 138 */         throw e;
/* 139 */       } catch (Exception e) {
/* 140 */         throw new RuntimeException(e);
/*     */       } finally {
/* 142 */         this.location--;
/* 143 */         servletRequestContext.setServletRequest(oldReq);
/* 144 */         servletRequestContext.setServletResponse(oldResp);
/*     */       } 
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\FilterHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */