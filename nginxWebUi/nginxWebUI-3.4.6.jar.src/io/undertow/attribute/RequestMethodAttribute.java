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
/*    */ 
/*    */ 
/*    */ public class RequestMethodAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_METHOD_SHORT = "%m";
/*    */   public static final String REQUEST_METHOD = "%{METHOD}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new RequestMethodAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return exchange.getRequestMethod().toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     throw new ReadOnlyAttributeException("Request method", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{METHOD}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "Request method";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{METHOD}") || token.equals("%m")) {
/* 64 */         return RequestMethodAttribute.INSTANCE;
/*    */       }
/* 66 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 71 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestMethodAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */