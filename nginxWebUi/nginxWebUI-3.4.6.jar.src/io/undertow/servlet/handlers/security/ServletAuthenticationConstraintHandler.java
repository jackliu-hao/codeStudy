/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.security.handlers.AuthenticationConstraintHandler;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.api.SecurityInfo;
/*    */ import io.undertow.servlet.api.SingleConstraintMatch;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServletAuthenticationConstraintHandler
/*    */   extends AuthenticationConstraintHandler
/*    */ {
/*    */   public ServletAuthenticationConstraintHandler(HttpHandler next) {
/* 40 */     super(next);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected boolean isAuthenticationRequired(HttpServerExchange exchange) {
/* 46 */     if (exchange.getRelativePath().endsWith("/j_security_check")) {
/* 47 */       return true;
/*    */     }
/* 49 */     List<SingleConstraintMatch> constraints = ((ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getRequiredConstrains();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/* 55 */     boolean authenticationRequired = false;
/* 56 */     for (SingleConstraintMatch constraint : constraints) {
/* 57 */       if (constraint.getRequiredRoles().isEmpty()) {
/* 58 */         if (constraint.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.DENY)
/*    */         {
/*    */ 
/*    */           
/* 62 */           return false; } 
/* 63 */         if (constraint.getEmptyRoleSemantic() == SecurityInfo.EmptyRoleSemantic.AUTHENTICATE)
/* 64 */           authenticationRequired = true; 
/*    */         continue;
/*    */       } 
/* 67 */       authenticationRequired = true;
/*    */     } 
/*    */     
/* 70 */     if (authenticationRequired) {
/* 71 */       UndertowLogger.SECURITY_LOGGER.debugf("Authenticating required for request %s", exchange);
/*    */     }
/* 73 */     return authenticationRequired;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletAuthenticationConstraintHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */