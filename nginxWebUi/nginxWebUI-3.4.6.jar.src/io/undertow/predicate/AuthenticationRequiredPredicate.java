/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.security.api.SecurityContext;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AuthenticationRequiredPredicate
/*    */   implements Predicate
/*    */ {
/* 18 */   public static final AuthenticationRequiredPredicate INSTANCE = new AuthenticationRequiredPredicate();
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 22 */     SecurityContext sc = value.getSecurityContext();
/* 23 */     if (sc == null) {
/* 24 */       return false;
/*    */     }
/* 26 */     return sc.isAuthenticationRequired();
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 31 */     return "auth-required()";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 38 */       return "auth-required";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 43 */       Map<String, Class<?>> params = new HashMap<>();
/* 44 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 49 */       Set<String> params = new HashSet<>();
/* 50 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 55 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 60 */       return AuthenticationRequiredPredicate.INSTANCE;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\AuthenticationRequiredPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */