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
/*    */ public class RequestSmallerThanPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final long size;
/*    */   
/*    */   RequestSmallerThanPredicate(long size) {
/* 39 */     this.size = size;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange exchange) {
/* 44 */     String length = exchange.getResponseHeaders().getFirst(Headers.CONTENT_LENGTH);
/* 45 */     if (length == null) {
/* 46 */       return false;
/*    */     }
/* 48 */     return (Long.parseLong(length) < this.size);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 52 */     return "request-smaller-than( '" + this.size + "' )";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 59 */       return "request-smaller-than";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 64 */       return Collections.singletonMap("size", Long.class);
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 69 */       return Collections.singleton("size");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 74 */       return "size";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 79 */       Long size = (Long)config.get("size");
/* 80 */       return new RequestSmallerThanPredicate(size.longValue());
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\RequestSmallerThanPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */