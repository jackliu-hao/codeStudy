/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.NetworkUtils;
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
/*    */ public class RemoteObfuscatedIPAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REMOTE_OBFUSCATED_IP_SHORT = "%o";
/*    */   public static final String REMOTE_OBFUSCATED_IP = "%{REMOTE_OBFUSCATED_IP}";
/* 37 */   public static final ExchangeAttribute INSTANCE = new RemoteObfuscatedIPAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 45 */     InetSocketAddress sourceAddress = exchange.getSourceAddress();
/* 46 */     InetAddress address = sourceAddress.getAddress();
/* 47 */     if (address == null)
/*    */     {
/*    */       
/* 50 */       address = ((InetSocketAddress)exchange.getConnection().getPeerAddress()).getAddress();
/*    */     }
/* 52 */     if (address == null) {
/* 53 */       return null;
/*    */     }
/* 55 */     return NetworkUtils.toObfuscatedString(address);
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 60 */     throw new ReadOnlyAttributeException("Remote Obfuscated IP", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 65 */     return "%{REMOTE_OBFUSCATED_IP}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 72 */       return "Remote Obfuscated IP";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 77 */       if (token.equals("%{REMOTE_OBFUSCATED_IP}") || token.equals("%o")) {
/* 78 */         return RemoteObfuscatedIPAttribute.INSTANCE;
/*    */       }
/* 80 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 85 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RemoteObfuscatedIPAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */