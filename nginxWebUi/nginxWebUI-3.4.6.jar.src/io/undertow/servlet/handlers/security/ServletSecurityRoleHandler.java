/*    */ package io.undertow.servlet.handlers.security;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.api.AuthorizationManager;
/*    */ import io.undertow.servlet.api.SingleConstraintMatch;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.util.List;
/*    */ import javax.servlet.DispatcherType;
/*    */ import javax.servlet.ServletRequest;
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
/*    */ public class ServletSecurityRoleHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   private final AuthorizationManager authorizationManager;
/*    */   
/*    */   public ServletSecurityRoleHandler(HttpHandler next, AuthorizationManager authorizationManager) {
/* 44 */     this.next = next;
/* 45 */     this.authorizationManager = authorizationManager;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 50 */     ServletRequestContext servletRequestContext = (ServletRequestContext)exchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/* 51 */     ServletRequest request = servletRequestContext.getServletRequest();
/* 52 */     if (request.getDispatcherType() == DispatcherType.REQUEST) {
/* 53 */       List<SingleConstraintMatch> constraints = servletRequestContext.getRequiredConstrains();
/* 54 */       SecurityContext sc = exchange.getSecurityContext();
/* 55 */       if (!this.authorizationManager.canAccessResource(constraints, sc.getAuthenticatedAccount(), servletRequestContext.getCurrentServlet().getManagedServlet().getServletInfo(), (HttpServletRequest)servletRequestContext.getOriginalRequest(), servletRequestContext.getDeployment())) {
/*    */         
/* 57 */         HttpServletResponse response = (HttpServletResponse)servletRequestContext.getServletResponse();
/* 58 */         response.sendError(403);
/*    */         return;
/*    */       } 
/*    */     } 
/* 62 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\handlers\security\ServletSecurityRoleHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */