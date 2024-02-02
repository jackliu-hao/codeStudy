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
/*    */ public class HostAndPortAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String HOST_AND_PORT = "%{HOST_AND_PORT}";
/* 32 */   public static final ExchangeAttribute INSTANCE = new HostAndPortAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 40 */     return exchange.getHostAndPort();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 45 */     throw new ReadOnlyAttributeException("Host and Port", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     return "%{HOST_AND_PORT}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 57 */       return "Host and Port";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 62 */       if (token.equals("%{HOST_AND_PORT}")) {
/* 63 */         return HostAndPortAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\HostAndPortAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */