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
/*    */ 
/*    */ 
/*    */ public class QuotingExchangeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final ExchangeAttribute exchangeAttribute;
/* 34 */   public static final ExchangeAttributeWrapper WRAPPER = new Wrapper();
/*    */   
/*    */   public QuotingExchangeAttribute(ExchangeAttribute exchangeAttribute) {
/* 37 */     this.exchangeAttribute = exchangeAttribute;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     String svalue = this.exchangeAttribute.readAttribute(exchange);
/*    */     
/* 44 */     if (svalue == null || "-".equals(svalue) || svalue.isEmpty()) {
/* 45 */       return "-";
/*    */     }
/*    */ 
/*    */     
/* 49 */     StringBuilder buffer = new StringBuilder(svalue.length() + 2);
/* 50 */     buffer.append('\'');
/* 51 */     int i = 0;
/* 52 */     while (i < svalue.length()) {
/* 53 */       int j = svalue.indexOf('\'', i);
/* 54 */       if (j == -1) {
/* 55 */         buffer.append(svalue.substring(i));
/* 56 */         i = svalue.length(); continue;
/*    */       } 
/* 58 */       buffer.append(svalue.substring(i, j + 1));
/* 59 */       buffer.append('"');
/* 60 */       i = j + 2;
/*    */     } 
/*    */ 
/*    */     
/* 64 */     buffer.append('\'');
/* 65 */     return buffer.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 70 */     throw new ReadOnlyAttributeException();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 75 */     return "\"" + this.exchangeAttribute.toString() + "\"";
/*    */   }
/*    */   
/*    */   public static class Wrapper
/*    */     implements ExchangeAttributeWrapper
/*    */   {
/*    */     public ExchangeAttribute wrap(ExchangeAttribute attribute) {
/* 82 */       return new QuotingExchangeAttribute(attribute);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\QuotingExchangeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */