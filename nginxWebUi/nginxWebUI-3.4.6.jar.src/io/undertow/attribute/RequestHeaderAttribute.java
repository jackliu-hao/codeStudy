/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.HeaderValues;
/*    */ import io.undertow.util.HttpString;
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
/*    */ public class RequestHeaderAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final HttpString requestHeader;
/*    */   
/*    */   public RequestHeaderAttribute(HttpString requestHeader) {
/* 36 */     this.requestHeader = requestHeader;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     HeaderValues header = exchange.getRequestHeaders().get(this.requestHeader);
/* 42 */     if (header == null)
/* 43 */       return null; 
/* 44 */     if (header.size() == 1) {
/* 45 */       return header.getFirst();
/*    */     }
/* 47 */     StringBuilder sb = new StringBuilder();
/* 48 */     sb.append("[");
/* 49 */     for (int i = 0; i < header.size(); i++) {
/* 50 */       if (i != 0) {
/* 51 */         sb.append(", ");
/*    */       }
/* 53 */       sb.append(header.get(i));
/*    */     } 
/* 55 */     sb.append("]");
/* 56 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 61 */     exchange.getRequestHeaders().put(this.requestHeader, newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "%{i," + this.requestHeader + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 73 */       return "Request header";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 78 */       if (token.startsWith("%{i,") && token.endsWith("}")) {
/* 79 */         HttpString headerName = HttpString.tryFromString(token.substring(4, token.length() - 1));
/* 80 */         return new RequestHeaderAttribute(headerName);
/*    */       } 
/* 82 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 87 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\RequestHeaderAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */