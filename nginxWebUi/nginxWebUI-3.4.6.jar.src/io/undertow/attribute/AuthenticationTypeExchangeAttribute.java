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
/*    */ public class AuthenticationTypeExchangeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String TOKEN = "%{AUTHENTICATION_TYPE}";
/* 30 */   public static final ExchangeAttribute INSTANCE = new AuthenticationTypeExchangeAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 34 */     SecurityContext sc = exchange.getSecurityContext();
/* 35 */     if (sc == null) {
/* 36 */       return null;
/*    */     }
/* 38 */     return sc.getMechanismName();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 43 */     throw new ReadOnlyAttributeException("Authentication Type", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return "%{AUTHENTICATION_TYPE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 55 */       return "Authentication Type";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 60 */       if (token.equals("%{AUTHENTICATION_TYPE}")) {
/* 61 */         return AuthenticationTypeExchangeAttribute.INSTANCE;
/*    */       }
/* 63 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 68 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\AuthenticationTypeExchangeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */