/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.SSLSessionInfo;
/*    */ import io.undertow.util.HexConverter;
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
/*    */ public class SslSessionIdAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/* 30 */   public static final SslSessionIdAttribute INSTANCE = new SslSessionIdAttribute();
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 34 */     SSLSessionInfo ssl = exchange.getConnection().getSslSessionInfo();
/* 35 */     if (ssl == null || ssl.getSessionId() == null) {
/* 36 */       return null;
/*    */     }
/* 38 */     return HexConverter.convertToHexString(ssl.getSessionId());
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 43 */     throw new ReadOnlyAttributeException("SSL Session ID", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return "%{SSL_SESSION_ID}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 55 */       return "SSL Session ID";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 60 */       if (token.equals("%{SSL_SESSION_ID}")) {
/* 61 */         return SslSessionIdAttribute.INSTANCE;
/*    */       }
/* 63 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 68 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\SslSessionIdAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */