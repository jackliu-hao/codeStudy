/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.SSLSessionInfo;
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
/*    */ public class SslCipherAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/* 29 */   public static final SslCipherAttribute INSTANCE = new SslCipherAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 33 */     SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
/* 34 */     if (ssl == null) {
/* 35 */       return null;
/*    */     }
/* 37 */     return ssl.getCipherSuite();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 42 */     throw new ReadOnlyAttributeException("SSL Cipher", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 47 */     return "%{SSL_CIPHER}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 54 */       return "SSL Cipher";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 59 */       if (token.equals("%{SSL_CIPHER}")) {
/* 60 */         return SslCipherAttribute.INSTANCE;
/*    */       }
/* 62 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 67 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\SslCipherAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */