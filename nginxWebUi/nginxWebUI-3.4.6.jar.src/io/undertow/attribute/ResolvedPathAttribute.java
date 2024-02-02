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
/*    */ public class ResolvedPathAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   public static final String RESOLVED_PATH = "%{RESOLVED_PATH}";
/* 30 */   public static final ExchangeAttribute INSTANCE = new ResolvedPathAttribute();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 38 */     return exchange.getResolvedPath();
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 43 */     exchange.setResolvedPath(newValue);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 48 */     return "%{RESOLVED_PATH}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 55 */       return "Resolved Path";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 60 */       return token.equals("%{RESOLVED_PATH}") ? ResolvedPathAttribute.INSTANCE : null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 65 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\ResolvedPathAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */