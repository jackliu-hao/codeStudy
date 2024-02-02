/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.Headers;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @Deprecated
/*    */ public class MinContentSizePredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final long minSize;
/*    */   
/*    */   MinContentSizePredicate(long minSize) {
/* 42 */     this.minSize = minSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 47 */     String length = value.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 48 */     if (length == null) {
/* 49 */       return false;
/*    */     }
/* 51 */     return (Long.parseLong(length) < this.minSize);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 56 */     return "max-content-size( " + this.minSize + " )";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 63 */       return "min-content-size";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 68 */       return Collections.singletonMap("value", Long.class);
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 73 */       return Collections.singleton("value");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 78 */       return "value";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 83 */       Long max = (Long)config.get("value");
/* 84 */       return new MinContentSizePredicate(max.longValue());
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\MinContentSizePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */