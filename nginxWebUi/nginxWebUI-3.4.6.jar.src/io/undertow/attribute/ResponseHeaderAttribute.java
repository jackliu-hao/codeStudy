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
/*    */ public class ResponseHeaderAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final HttpString responseHeader;
/*    */   
/*    */   public ResponseHeaderAttribute(HttpString responseHeader) {
/* 36 */     this.responseHeader = responseHeader;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     HeaderValues header = exchange.getResponseHeaders().get(this.responseHeader);
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
/* 61 */     exchange.getResponseHeaders().put(this.responseHeader, newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 66 */     return "%{o," + this.responseHeader + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 73 */       return "Response header";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 78 */       if (token.startsWith("%{o,") && token.endsWith("}")) {
/* 79 */         HttpString headerName = HttpString.tryFromString(token.substring(4, token.length() - 1));
/* 80 */         return new ResponseHeaderAttribute(headerName);
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


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ResponseHeaderAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */