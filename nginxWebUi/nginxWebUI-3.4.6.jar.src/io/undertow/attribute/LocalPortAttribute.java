/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.net.InetSocketAddress;
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
/*    */ 
/*    */ public class LocalPortAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String LOCAL_PORT_SHORT = "%p";
/*    */   public static final String LOCAL_PORT = "%{LOCAL_PORT}";
/* 35 */   public static final ExchangeAttribute INSTANCE = new LocalPortAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     InetSocketAddress localAddress = (InetSocketAddress)exchange.getConnection().getLocalAddress();
/* 44 */     return Integer.toString(localAddress.getPort());
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 49 */     throw new ReadOnlyAttributeException("Local port", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "%{LOCAL_PORT}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 61 */       return "Local Port";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 66 */       if (token.equals("%{LOCAL_PORT}") || token.equals("%p")) {
/* 67 */         return LocalPortAttribute.INSTANCE;
/*    */       }
/* 69 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 74 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\LocalPortAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */