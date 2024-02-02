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
/*    */ public class RequestPathAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String REQUEST_PATH = "%{REQUEST_PATH}";
/* 31 */   public static final ExchangeAttribute INSTANCE = new RequestPathAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 39 */     return exchange.getRelativePath();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 44 */     int pos = newValue.indexOf('?');
/* 45 */     exchange.setResolvedPath("");
/* 46 */     if (pos == -1) {
/* 47 */       exchange.setRelativePath(newValue);
/* 48 */       exchange.setRequestURI(newValue);
/* 49 */       exchange.setRequestPath(newValue);
/*    */     } else {
/* 51 */       String path = newValue.substring(0, pos);
/* 52 */       exchange.setRequestPath(path);
/* 53 */       exchange.setRelativePath(path);
/* 54 */       exchange.setRequestURI(newValue);
/*    */       
/* 56 */       String newQueryString = newValue.substring(pos);
/* 57 */       exchange.setQueryString(newQueryString);
/* 58 */       exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 64 */     return "%{REQUEST_PATH}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 71 */       return "Request Path";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 76 */       return token.equals("%{REQUEST_PATH}") ? RequestPathAttribute.INSTANCE : null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 81 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestPathAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */