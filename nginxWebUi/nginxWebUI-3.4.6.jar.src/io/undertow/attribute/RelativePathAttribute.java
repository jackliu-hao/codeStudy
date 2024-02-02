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
/*    */ public class RelativePathAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String RELATIVE_PATH_SHORT = "%R";
/*    */   public static final String RELATIVE_PATH = "%{RELATIVE_PATH}";
/* 34 */   public static final ExchangeAttribute INSTANCE = new RelativePathAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     return exchange.getRelativePath();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 47 */     int pos = newValue.indexOf('?');
/* 48 */     if (pos == -1) {
/* 49 */       exchange.setRelativePath(newValue);
/* 50 */       String requestURI = exchange.getResolvedPath() + newValue;
/* 51 */       if (requestURI.contains("%")) {
/*    */ 
/*    */ 
/*    */ 
/*    */         
/* 56 */         exchange.setRequestURI(requestURI.replaceAll("%", "%25"));
/*    */       } else {
/* 58 */         exchange.setRequestURI(requestURI);
/*    */       } 
/* 60 */       exchange.setRequestPath(requestURI);
/*    */     } else {
/* 62 */       String path = newValue.substring(0, pos);
/* 63 */       exchange.setRelativePath(path);
/* 64 */       String requestURI = exchange.getResolvedPath() + path;
/* 65 */       if (requestURI.contains("%")) {
/* 66 */         exchange.setRequestURI(requestURI.replaceAll("%", "%25"));
/*    */       } else {
/* 68 */         exchange.setRequestURI(requestURI);
/*    */       } 
/* 70 */       exchange.setRequestPath(requestURI);
/*    */       
/* 72 */       String newQueryString = newValue.substring(pos);
/* 73 */       exchange.setQueryString(newQueryString);
/* 74 */       exchange.getQueryParameters().putAll(QueryParameterUtils.parseQueryString(newQueryString.substring(1), QueryParameterUtils.getQueryParamEncoding(exchange)));
/*    */     } 
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 80 */     return "%{RELATIVE_PATH}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 87 */       return "Relative Path";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 92 */       return (token.equals("%{RELATIVE_PATH}") || token.equals("%R")) ? RelativePathAttribute.INSTANCE : null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 97 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RelativePathAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */