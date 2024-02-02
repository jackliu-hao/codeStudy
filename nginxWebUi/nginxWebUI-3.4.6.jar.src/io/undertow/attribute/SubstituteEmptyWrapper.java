/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SubstituteEmptyWrapper
/*    */   implements ExchangeAttributeWrapper
/*    */ {
/*    */   private final String substitute;
/*    */   
/*    */   public SubstituteEmptyWrapper(String substitute) {
/* 13 */     this.substitute = substitute;
/*    */   }
/*    */ 
/*    */   
/*    */   public ExchangeAttribute wrap(ExchangeAttribute attribute) {
/* 18 */     return new SubstituteEmptyAttribute(attribute, this.substitute);
/*    */   }
/*    */   
/*    */   public static class SubstituteEmptyAttribute implements ExchangeAttribute {
/*    */     private final ExchangeAttribute attribute;
/*    */     private final String substitute;
/*    */     
/*    */     public SubstituteEmptyAttribute(ExchangeAttribute attribute, String substitute) {
/* 26 */       this.attribute = attribute;
/* 27 */       this.substitute = substitute;
/*    */     }
/*    */ 
/*    */     
/*    */     public String readAttribute(HttpServerExchange exchange) {
/* 32 */       String val = this.attribute.readAttribute(exchange);
/* 33 */       if (val == null || val.isEmpty()) {
/* 34 */         return this.substitute;
/*    */       }
/* 36 */       return val;
/*    */     }
/*    */ 
/*    */     
/*    */     public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 41 */       this.attribute.writeAttribute(exchange, newValue);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\SubstituteEmptyWrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */