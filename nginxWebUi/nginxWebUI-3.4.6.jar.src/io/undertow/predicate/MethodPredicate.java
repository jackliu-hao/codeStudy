/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.HttpString;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
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
/*    */ public class MethodPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final HttpString[] methods;
/*    */   
/*    */   MethodPredicate(String[] methods) {
/* 38 */     HttpString[] values = new HttpString[methods.length];
/* 39 */     for (int i = 0; i < methods.length; i++) {
/* 40 */       values[i] = HttpString.tryFromString(methods[i]);
/*    */     }
/* 42 */     this.methods = values;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 48 */     for (int i = 0; i < this.methods.length; i++) {
/* 49 */       if (value.getRequestMethod().equals(this.methods[i])) {
/* 50 */         return true;
/*    */       }
/*    */     } 
/* 53 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 58 */     if (this.methods.length == 1) {
/* 59 */       return "method( '" + this.methods[0] + "' )";
/*    */     }
/* 61 */     return "method( {" + (String)Arrays.<HttpString>asList(this.methods).stream().map(s -> s.toString()).collect(Collectors.joining(", ")) + "} )";
/*    */   }
/*    */ 
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 69 */       return "method";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 74 */       return Collections.singletonMap("value", String[].class);
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 79 */       return Collections.singleton("value");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 84 */       return "value";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 89 */       String[] methods = (String[])config.get("value");
/* 90 */       return new MethodPredicate(methods);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\MethodPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */