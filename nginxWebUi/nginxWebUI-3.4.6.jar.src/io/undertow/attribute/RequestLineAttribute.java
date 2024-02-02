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
/*    */ public class RequestLineAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_LINE_SHORT = "%r";
/*    */   public static final String REQUEST_LINE = "%{REQUEST_LINE}";
/* 33 */   public static final ExchangeAttribute INSTANCE = new RequestLineAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 44 */     StringBuilder sb = (new StringBuilder()).append(exchange.getRequestMethod().toString()).append(' ').append(exchange.getRequestURI());
/* 45 */     if (!exchange.getQueryString().isEmpty()) {
/* 46 */       sb.append('?');
/* 47 */       sb.append(exchange.getQueryString());
/*    */     } 
/* 49 */     sb.append(' ')
/* 50 */       .append(exchange.getProtocol().toString()).toString();
/* 51 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 56 */     throw new ReadOnlyAttributeException("Request line", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return "%{REQUEST_LINE}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 68 */       return "Request line";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 73 */       if (token.equals("%{REQUEST_LINE}") || token.equals("%r")) {
/* 74 */         return RequestLineAttribute.INSTANCE;
/*    */       }
/* 76 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 81 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestLineAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */