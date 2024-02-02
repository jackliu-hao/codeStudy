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
/*    */ public class CompositeExchangeAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final ExchangeAttribute[] attributes;
/*    */   
/*    */   public CompositeExchangeAttribute(ExchangeAttribute[] attributes) {
/* 33 */     ExchangeAttribute[] copy = new ExchangeAttribute[attributes.length];
/* 34 */     System.arraycopy(attributes, 0, copy, 0, attributes.length);
/* 35 */     this.attributes = copy;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 40 */     StringBuilder sb = new StringBuilder();
/* 41 */     for (int i = 0; i < this.attributes.length; i++) {
/* 42 */       String val = this.attributes[i].readAttribute(exchange);
/* 43 */       if (val != null) {
/* 44 */         sb.append(val);
/*    */       }
/*    */     } 
/* 47 */     return sb.toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 52 */     throw new ReadOnlyAttributeException("combined", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     StringBuilder sb = new StringBuilder();
/* 58 */     for (int i = 0; i < this.attributes.length; i++) {
/* 59 */       sb.append(this.attributes[i].toString());
/*    */     }
/* 61 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\CompositeExchangeAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */