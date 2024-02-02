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
/*    */ public class NullAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String NAME = "%{NULL}";
/* 32 */   public static final NullAttribute INSTANCE = new NullAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 41 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 46 */     throw new ReadOnlyAttributeException("%{NULL}", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 51 */     return "%{NULL}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 58 */       return "null";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 63 */       if (token.equals("%{NULL}")) {
/* 64 */         return NullAttribute.INSTANCE;
/*    */       }
/* 66 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 71 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\NullAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */