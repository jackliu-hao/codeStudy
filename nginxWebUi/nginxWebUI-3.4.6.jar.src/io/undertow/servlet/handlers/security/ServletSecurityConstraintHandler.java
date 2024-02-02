/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.api.SingleConstraintMatch;
/*    */ import io.undertow.servlet.api.TransportGuaranteeType;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
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
/*    */ 
/*    */ public class ServletSecurityConstraintHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final SecurityPathMatches securityPathMatches;
/*    */   private final HttpHandler next;
/*    */   
/*    */   public ServletSecurityConstraintHandler(SecurityPathMatches securityPathMatches, HttpHandler next) {
/* 39 */     this.securityPathMatches = securityPathMatches;
/* 40 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 45 */     String path = exchange.getRelativePath();
/* 46 */     SecurityPathMatch securityMatch = this.securityPathMatches.getSecurityInfo(path, exchange.getRequestMethod().toString());
/* 47 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 48 */     List<SingleConstraintMatch> list = servletRequestContext.getRequiredConstrains();
/* 49 */     if (list == null) {
/* 50 */       servletRequestContext.setRequiredConstrains(list = new ArrayList<>());
/*    */     }
/* 52 */     list.add(securityMatch.getMergedConstraint());
/* 53 */     TransportGuaranteeType type = servletRequestContext.getTransportGuarenteeType();
/* 54 */     if (type == null || type.ordinal() < securityMatch.getTransportGuaranteeType().ordinal()) {
/* 55 */       servletRequestContext.setTransportGuarenteeType(securityMatch.getTransportGuaranteeType());
/*    */     }
/*    */     
/* 58 */     UndertowLogger.SECURITY_LOGGER.debugf("Security constraints for request %s are %s", exchange.getRequestURI(), list);
/* 59 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletSecurityConstraintHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */