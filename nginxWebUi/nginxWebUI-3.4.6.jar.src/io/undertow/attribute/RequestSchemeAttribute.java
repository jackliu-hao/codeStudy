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
/*    */ public class RequestSchemeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_SCHEME = "%{SCHEME}";
/* 32 */   public static final ExchangeAttribute INSTANCE = new RequestSchemeAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 40 */     return exchange.getRequestScheme();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 45 */     exchange.setRequestScheme(newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     return "%{SCHEME}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 57 */       return "Request scheme";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 62 */       if (token.equals("%{SCHEME}")) {
/* 63 */         return RequestSchemeAttribute.INSTANCE;
/*    */       }
/* 65 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 70 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestSchemeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */