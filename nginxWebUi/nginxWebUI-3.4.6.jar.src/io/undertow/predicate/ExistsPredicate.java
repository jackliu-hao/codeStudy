/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ public class ExistsPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final ExchangeAttribute attribute;
/*    */   
/*    */   ExistsPredicate(ExchangeAttribute attribute) {
/* 39 */     this.attribute = attribute;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 44 */     String att = this.attribute.readAttribute(value);
/* 45 */     if (att == null) {
/* 46 */       return false;
/*    */     }
/* 48 */     return !att.isEmpty();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 53 */     return "exists( '" + this.attribute.toString() + "' )";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 60 */       return "exists";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 65 */       Map<String, Class<?>> params = new HashMap<>();
/* 66 */       params.put("value", ExchangeAttribute.class);
/* 67 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 72 */       return Collections.singleton("value");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 77 */       return "value";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 82 */       ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 83 */       return new ExistsPredicate(value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\ExistsPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */