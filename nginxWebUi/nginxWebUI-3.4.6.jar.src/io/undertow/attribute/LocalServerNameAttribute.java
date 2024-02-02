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
/*    */ public class LocalServerNameAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String LOCAL_SERVER_NAME_SHORT = "%v";
/*    */   public static final String LOCAL_SERVER_NAME = "%{LOCAL_SERVER_NAME}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new LocalServerNameAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return exchange.getHostName();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     throw new ReadOnlyAttributeException("Local server name", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{LOCAL_SERVER_NAME}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "Local server name";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{LOCAL_SERVER_NAME}") || token.equals("%v")) {
/* 64 */         return LocalServerNameAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\LocalServerNameAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */