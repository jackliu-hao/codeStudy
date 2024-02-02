/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.QueryParameterUtils;
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
/*    */ public class RequestURLAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_URL_SHORT = "%U";
/*    */   public static final String REQUEST_URL = "%{REQUEST_URL}";
/* 34 */   public static final ExchangeAttribute INSTANCE = new RequestURLAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     return exchange.getRequestURI();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 47 */     int pos = newValue.indexOf('?');
/* 48 */     if (pos == -1) {
/* 49 */       exchange.setRelativePath(newValue);
/* 50 */       exchange.setRequestURI(newValue);
/* 51 */       exchange.setRequestPath(newValue);
/* 52 */       exchange.setResolvedPath("");
/*    */     } else {
/* 54 */       String path = newValue.substring(0, pos);
/* 55 */       exchange.setRelativePath(path);
/* 56 */       exchange.setRequestURI(path);
/* 57 */       exchange.setRequestPath(path);
/* 58 */       exchange.setResolvedPath("");
/* 59 */       String newQueryString = newValue.substring(pos);
/* 60 */       exchange.setQueryString(newQueryString);
/* 61 */       exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 68 */     return "%{REQUEST_URL}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 75 */       return "Request URL";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 80 */       if (token.equals("%{REQUEST_URL}") || token.equals("%U")) {
/* 81 */         return RequestURLAttribute.INSTANCE;
/*    */       }
/* 83 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 88 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestURLAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */