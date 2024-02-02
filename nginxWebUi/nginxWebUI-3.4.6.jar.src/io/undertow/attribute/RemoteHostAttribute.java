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
/*    */ public class RemoteHostAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REMOTE_HOST_NAME_SHORT = "%h";
/*    */   public static final String REMOTE_HOST = "%{REMOTE_HOST}";
/* 35 */   public static final ExchangeAttribute INSTANCE = new RemoteHostAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 43 */     InetSocketAddress sourceAddress = exchange.getSourceAddress();
/* 44 */     return sourceAddress.getHostString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 49 */     throw new ReadOnlyAttributeException("Remote host", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 54 */     return "%{REMOTE_HOST}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 61 */       return "Remote host";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 66 */       if (token.equals("%{REMOTE_HOST}") || token.equals("%h")) {
/* 67 */         return RemoteHostAttribute.INSTANCE;
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RemoteHostAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */