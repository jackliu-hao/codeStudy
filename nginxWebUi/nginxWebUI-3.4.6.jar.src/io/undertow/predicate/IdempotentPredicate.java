/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.HttpString;
/*    */ import io.undertow.util.Methods;
/*    */ import java.util.Collections;
/*    */ import java.util.HashSet;
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
/*    */ 
/*    */ public class IdempotentPredicate
/*    */   implements Predicate
/*    */ {
/* 38 */   public static final IdempotentPredicate INSTANCE = new IdempotentPredicate();
/*    */   
/*    */   private static final Set<HttpString> METHODS;
/*    */   
/*    */   static {
/* 43 */     Set<HttpString> methods = new HashSet<>();
/* 44 */     methods.add(Methods.GET);
/* 45 */     methods.add(Methods.DELETE);
/* 46 */     methods.add(Methods.PUT);
/* 47 */     methods.add(Methods.HEAD);
/* 48 */     methods.add(Methods.OPTIONS);
/* 49 */     METHODS = Collections.unmodifiableSet(methods);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 55 */     return METHODS.contains(value.getRequestMethod());
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return "idempotent()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 67 */       return "idempotent";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 72 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 77 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 82 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 87 */       return IdempotentPredicate.INSTANCE;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\IdempotentPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */