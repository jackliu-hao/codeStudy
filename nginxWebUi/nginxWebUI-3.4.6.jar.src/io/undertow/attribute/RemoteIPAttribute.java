/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.net.InetAddress;
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
/*    */ public class RemoteIPAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REMOTE_IP_SHORT = "%a";
/*    */   public static final String REMOTE_IP = "%{REMOTE_IP}";
/* 36 */   public static final ExchangeAttribute INSTANCE = new RemoteIPAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 44 */     InetSocketAddress sourceAddress = exchange.getSourceAddress();
/* 45 */     InetAddress address = sourceAddress.getAddress();
/* 46 */     if (address == null)
/*    */     {
/*    */       
/* 49 */       address = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress();
/*    */     }
/* 51 */     if (address == null) {
/* 52 */       return null;
/*    */     }
/* 54 */     return address.getHostAddress();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 59 */     throw new ReadOnlyAttributeException("Remote IP", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "%{REMOTE_IP}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 71 */       return "Remote IP";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 76 */       if (token.equals("%{REMOTE_IP}") || token.equals("%a")) {
/* 77 */         return RemoteIPAttribute.INSTANCE;
/*    */       }
/* 79 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 84 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RemoteIPAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */