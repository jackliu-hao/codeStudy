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
/*    */ public class LocalIPAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String LOCAL_IP = "%{LOCAL_IP}";
/*    */   public static final String LOCAL_IP_SHORT = "%A";
/* 35 */   public static final ExchangeAttribute INSTANCE = new LocalIPAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     InetSocketAddress localAddress = (InetSocketAddress)exchange.getConnection().getLocalAddress();
/* 44 */     return localAddress.getAddress().getHostAddress();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 49 */     throw new ReadOnlyAttributeException("Local IP", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "%{LOCAL_IP}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 61 */       return "Local IP";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 66 */       if (token.equals("%{LOCAL_IP}") || token.equals("%A")) {
/* 67 */         return LocalIPAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\LocalIPAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */