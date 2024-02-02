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
/*    */ public class TransportProtocolAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String TRANSPORT_PROTOCOL = "%{TRANSPORT_PROTOCOL}";
/* 32 */   public static final ExchangeAttribute INSTANCE = new TransportProtocolAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 40 */     return exchange.getConnection().getTransportProtocol();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 45 */     throw new ReadOnlyAttributeException("transport protocol", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     return "%{TRANSPORT_PROTOCOL}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 57 */       return "Transport Protocol";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 62 */       if (token.equals("%{TRANSPORT_PROTOCOL}")) {
/* 63 */         return TransportProtocolAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\TransportProtocolAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */