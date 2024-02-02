/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.Map;
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
/*    */ public class PredicateContextAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String name;
/*    */   
/*    */   public PredicateContextAttribute(String name) {
/* 34 */     this.name = name;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 39 */     Map<String, Object> context = (Map<String, Object>)exchange.getAttachment(Predicate.PREDICATE_CONTEXT);
/* 40 */     if (context != null) {
/* 41 */       Object object = context.get(this.name);
/* 42 */       return (object == null) ? null : object.toString();
/*    */     } 
/* 44 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 49 */     Map<String, Object> context = (Map<String, Object>)exchange.getAttachment(Predicate.PREDICATE_CONTEXT);
/* 50 */     if (context != null) {
/* 51 */       context.put(this.name, newValue);
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 57 */     return "${" + this.name + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 64 */       return "Predicate context variable";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 69 */       if (token.startsWith("${") && token.endsWith("}") && token.length() > 3)
/* 70 */         return new PredicateContextAttribute(token.substring(2, token.length() - 1)); 
/* 71 */       if (token.startsWith("$")) {
/* 72 */         return new PredicateContextAttribute(token.substring(1, token.length()));
/*    */       }
/* 74 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 79 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\PredicateContextAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */