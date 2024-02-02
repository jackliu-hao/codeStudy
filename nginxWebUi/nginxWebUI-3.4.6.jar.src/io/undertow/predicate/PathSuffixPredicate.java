/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PathSuffixPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final String suffix;
/* 37 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*    */ 
/*    */   
/*    */   PathSuffixPredicate(String suffix) {
/* 41 */     this.suffix = suffix;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 46 */     boolean matches = value.getRelativePath().endsWith(this.suffix);
/* 47 */     if (traceEnabled) {
/* 48 */       UndertowLogger.PREDICATE_LOGGER.tracef("Path suffix [%s] %s input [%s] for %s.", new Object[] { this.suffix, matches ? "MATCHES" : "DOES NOT MATCH", value.getRelativePath(), value });
/*    */     }
/* 50 */     return matches;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 54 */     return "path-suffix( '" + this.suffix + "' )";
/*    */   }
/*    */ 
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 62 */       return "path-suffix";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 67 */       return Collections.singletonMap("path", String[].class);
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 72 */       return Collections.singleton("path");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 77 */       return "path";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 82 */       String[] path = (String[])config.get("path");
/* 83 */       return Predicates.suffixes(path);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PathSuffixPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */