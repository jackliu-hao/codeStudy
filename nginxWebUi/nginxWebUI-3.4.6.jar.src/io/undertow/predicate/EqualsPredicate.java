/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.attribute.ExchangeAttribute;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
/*    */ import java.util.HashMap;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import java.util.stream.Collectors;
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
/*    */ public class EqualsPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final ExchangeAttribute[] attributes;
/*    */   
/*    */   EqualsPredicate(ExchangeAttribute[] attributes) {
/* 41 */     this.attributes = attributes;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 46 */     if (this.attributes.length < 2) {
/* 47 */       return true;
/*    */     }
/* 49 */     String first = this.attributes[0].readAttribute(value);
/* 50 */     for (int i = 1; i < this.attributes.length; i++) {
/* 51 */       String current = this.attributes[i].readAttribute(value);
/* 52 */       if (first == null) {
/* 53 */         if (current != null)
/* 54 */           return false; 
/*    */       } else {
/* 56 */         if (current == null)
/* 57 */           return false; 
/* 58 */         if (!first.equals(current))
/* 59 */           return false; 
/*    */       } 
/*    */     } 
/* 62 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return "equals( {" + (String)Arrays.<ExchangeAttribute>asList(this.attributes).stream().map(a -> a.toString()).collect(Collectors.joining(", ")) + "} )";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 74 */       return "equals";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 79 */       Map<String, Class<?>> params = new HashMap<>();
/* 80 */       params.put("value", ExchangeAttribute[].class);
/* 81 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 86 */       return Collections.singleton("value");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 91 */       return "value";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 96 */       ExchangeAttribute[] value = (ExchangeAttribute[])config.get("value");
/* 97 */       return new EqualsPredicate(value);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\EqualsPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */