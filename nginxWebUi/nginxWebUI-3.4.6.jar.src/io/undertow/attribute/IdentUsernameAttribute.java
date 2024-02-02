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
/*    */ public class IdentUsernameAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String IDENT_USERNAME = "%l";
/* 32 */   public static final ExchangeAttribute INSTANCE = new IdentUsernameAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 40 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 45 */     throw new ReadOnlyAttributeException("Ident username", newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 50 */     return "%l";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 57 */       return "Ident Username";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 62 */       if (token.equals("%l")) {
/* 63 */         return IdentUsernameAttribute.INSTANCE;
/*    */       }
/* 65 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 70 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\IdentUsernameAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */