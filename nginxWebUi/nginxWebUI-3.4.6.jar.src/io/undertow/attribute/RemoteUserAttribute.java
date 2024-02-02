/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpServerExchange;
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
/*    */ public class RemoteUserAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REMOTE_USER_SHORT = "%u";
/*    */   public static final String REMOTE_USER = "%{REMOTE_USER}";
/* 34 */   public static final ExchangeAttribute INSTANCE = new RemoteUserAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     SecurityContext sc = exchange.getSecurityContext();
/* 43 */     if (sc == null || !sc.isAuthenticated()) {
/* 44 */       return null;
/*    */     }
/* 46 */     return sc.getAuthenticatedAccount().getPrincipal().getName();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 51 */     throw new ReadOnlyAttributeException("Remote user", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "%{REMOTE_USER}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 63 */       return "Remote user";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 68 */       if (token.equals("%{REMOTE_USER}") || token.equals("%u")) {
/* 69 */         return RemoteUserAttribute.INSTANCE;
/*    */       }
/* 71 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 76 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RemoteUserAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */