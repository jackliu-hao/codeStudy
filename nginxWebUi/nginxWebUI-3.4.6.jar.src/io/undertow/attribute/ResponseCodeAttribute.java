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
/*    */ public class ResponseCodeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String RESPONSE_CODE_SHORT = "%s";
/*    */   public static final String RESPONSE_CODE = "%{RESPONSE_CODE}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new ResponseCodeAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return Integer.toString(exchange.getStatusCode());
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     exchange.setStatusCode(Integer.parseInt(newValue));
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{RESPONSE_CODE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "Response code";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{RESPONSE_CODE}") || token.equals("%s")) {
/* 64 */         return ResponseCodeAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ResponseCodeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */