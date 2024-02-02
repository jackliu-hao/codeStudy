/*     */ package io.undertow.servlet.handlers;
/*     */ 
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.api.InstanceHandle;
/*     */ import io.undertow.servlet.core.ManagedServlet;
/*     */ import java.io.IOException;
/*     */ import javax.servlet.Servlet;
/*     */ import javax.servlet.ServletException;
/*     */ import javax.servlet.ServletRequest;
/*     */ import javax.servlet.ServletResponse;
/*     */ import javax.servlet.UnavailableException;
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
/*     */ public class ServletHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final ManagedServlet managedServlet;
/*     */   
/*     */   public ServletHandler(ManagedServlet managedServlet) {
/*  49 */     this.managedServlet = managedServlet;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws IOException, ServletException {
/*  54 */     if (this.managedServlet.isPermanentlyUnavailable()) {
/*  55 */       UndertowServletLogger.REQUEST_LOGGER.debugf("Returning 404 for servlet %s due to permanent unavailability", this.managedServlet.getServletInfo().getName());
/*  56 */       exchange.setStatusCode(404);
/*     */       
/*     */       return;
/*     */     } 
/*  60 */     if (this.managedServlet.isTemporarilyUnavailable()) {
/*  61 */       UndertowServletLogger.REQUEST_LOGGER.debugf("Returning 503 for servlet %s due to temporary unavailability", this.managedServlet.getServletInfo().getName());
/*  62 */       exchange.setStatusCode(503);
/*     */       return;
/*     */     } 
/*  65 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  66 */     if (!this.managedServlet.getServletInfo().isAsyncSupported()) {
/*  67 */       servletRequestContext.setAsyncSupported(false);
/*     */     }
/*  69 */     ServletRequest request = servletRequestContext.getServletRequest();
/*  70 */     ServletResponse response = servletRequestContext.getServletResponse();
/*  71 */     InstanceHandle<? extends Servlet> servlet = null;
/*     */     try {
/*  73 */       servlet = this.managedServlet.getServlet();
/*  74 */       ((Servlet)servlet.getInstance()).service(request, response);
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
/*     */     }
/*  86 */     catch (UnavailableException e) {
/*  87 */       this.managedServlet.handleUnavailableException(e);
/*  88 */       if (e.isPermanent()) {
/*  89 */         exchange.setStatusCode(404);
/*     */       } else {
/*  91 */         exchange.setStatusCode(503);
/*     */       } 
/*     */     } finally {
/*  94 */       if (servlet != null) {
/*  95 */         servlet.release();
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public ManagedServlet getManagedServlet() {
/* 101 */     return this.managedServlet;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\ServletHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */