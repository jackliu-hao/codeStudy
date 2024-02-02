/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.Collections;
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
/*    */ public class SecurePredicate
/*    */   implements Predicate
/*    */ {
/* 32 */   public static final SecurePredicate INSTANCE = new SecurePredicate();
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 36 */     return value.getRequestScheme().equals("https");
/*    */   }
/*    */   
/*    */   public String toString() {
/* 40 */     return "secure()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 47 */       return "secure";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 52 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 57 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 62 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 67 */       return SecurePredicate.INSTANCE;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\SecurePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */