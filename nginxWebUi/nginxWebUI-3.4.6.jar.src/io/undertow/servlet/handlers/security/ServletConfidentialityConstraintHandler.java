/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.security.handlers.SinglePortConfidentialityHandler;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.UndertowServletMessages;
/*    */ import io.undertow.servlet.api.AuthorizationManager;
/*    */ import io.undertow.servlet.api.ConfidentialPortManager;
/*    */ import io.undertow.servlet.api.TransportGuaranteeType;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.net.URI;
/*    */ import java.net.URISyntaxException;
/*    */ import javax.servlet.http.HttpServletRequest;
/*    */ import javax.servlet.http.HttpServletResponse;
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
/*    */ 
/*    */ public class ServletConfidentialityConstraintHandler
/*    */   extends SinglePortConfidentialityHandler
/*    */ {
/*    */   private final ConfidentialPortManager portManager;
/*    */   
/*    */   public ServletConfidentialityConstraintHandler(ConfidentialPortManager portManager, HttpHandler next) {
/* 45 */     super(next, -1);
/* 46 */     this.portManager = portManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 51 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 52 */     AuthorizationManager authorizationManager = servletRequestContext.getDeployment().getDeploymentInfo().getAuthorizationManager();
/*    */     
/* 54 */     TransportGuaranteeType connectionGuarantee = servletRequestContext.getOriginalRequest().isSecure() ? TransportGuaranteeType.CONFIDENTIAL : TransportGuaranteeType.NONE;
/* 55 */     TransportGuaranteeType transportGuarantee = authorizationManager.transportGuarantee(connectionGuarantee, servletRequestContext
/* 56 */         .getTransportGuarenteeType(), (HttpServletRequest)servletRequestContext.getOriginalRequest());
/* 57 */     servletRequestContext.setTransportGuarenteeType(transportGuarantee);
/*    */     
/* 59 */     if (TransportGuaranteeType.REJECTED == transportGuarantee) {
/* 60 */       HttpServletResponse response = (HttpServletResponse)servletRequestContext.getServletResponse();
/* 61 */       response.sendError(403);
/*    */       return;
/*    */     } 
/* 64 */     super.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   protected boolean confidentialityRequired(HttpServerExchange exchange) {
/* 69 */     TransportGuaranteeType transportGuarantee = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getTransportGuarenteeType();
/*    */ 
/*    */ 
/*    */     
/* 73 */     return (TransportGuaranteeType.CONFIDENTIAL == transportGuarantee || TransportGuaranteeType.INTEGRAL == transportGuarantee);
/*    */   }
/*    */ 
/*    */   
/*    */   protected URI getRedirectURI(HttpServerExchange exchange) throws URISyntaxException {
/* 78 */     int port = this.portManager.getConfidentialPort(exchange);
/* 79 */     if (port < 0) {
/* 80 */       throw UndertowServletMessages.MESSAGES.noConfidentialPortAvailable();
/*    */     }
/*    */     
/* 83 */     return getRedirectURI(exchange, port);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean isConfidential(HttpServerExchange exchange) {
/* 95 */     ServletRequestContext src = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 96 */     if (src != null) {
/* 97 */       return src.getOriginalRequest().isSecure();
/*    */     }
/* 99 */     return super.isConfidential(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletConfidentialityConstraintHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */