/*    */ package io.undertow.attribute;
/*    */ 
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
/*    */ public class SecureExchangeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String TOKEN = "%{SECURE}";
/*    */   public static final String LEGACY_INCORRECT_TOKEN = "${SECURE}";
/* 31 */   public static final ExchangeAttribute INSTANCE = new SecureExchangeAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 35 */     return Boolean.toString(exchange.isSecure());
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 40 */     exchange.putAttachment(HttpServerExchange.SECURE_REQUEST, Boolean.valueOf(Boolean.parseBoolean(newValue)));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 45 */     return "%{SECURE}";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 52 */       return "Secure";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 57 */       if (token.equals("%{SECURE}") || token.equals("${SECURE}")) {
/* 58 */         return SecureExchangeAttribute.INSTANCE;
/*    */       }
/* 60 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 65 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\SecureExchangeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */