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
/*    */ public class RequestProtocolAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_PROTOCOL_SHORT = "%H";
/*    */   public static final String REQUEST_PROTOCOL = "%{PROTOCOL}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new RequestProtocolAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return exchange.getProtocol().toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     throw new ReadOnlyAttributeException("Request protocol", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{PROTOCOL}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "Request protocol";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{PROTOCOL}") || token.equals("%H")) {
/* 64 */         return RequestProtocolAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestProtocolAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */