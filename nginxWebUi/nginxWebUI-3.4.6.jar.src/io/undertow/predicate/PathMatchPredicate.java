/*    */ package io.undertow.predicate;
/*    */ 
/*    */ import io.undertow.UndertowLogger;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.util.PathMatcher;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class PathMatchPredicate
/*    */   implements Predicate
/*    */ {
/*    */   private final PathMatcher<Boolean> pathMatcher;
/* 39 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*    */ 
/*    */   
/*    */   PathMatchPredicate(String... paths) {
/* 43 */     PathMatcher<Boolean> matcher = new PathMatcher();
/* 44 */     for (String path : paths) {
/* 45 */       if (!path.startsWith("/")) {
/* 46 */         matcher.addExactPath("/" + path, Boolean.TRUE);
/*    */       } else {
/* 48 */         matcher.addExactPath(path, Boolean.TRUE);
/*    */       } 
/*    */     } 
/* 51 */     this.pathMatcher = matcher;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 55 */     Set<String> matches = this.pathMatcher.getExactPathMatchesSet();
/* 56 */     if (matches.size() == 1) {
/* 57 */       return "path( '" + matches.toArray()[0] + "' )";
/*    */     }
/* 59 */     return "path( { '" + (String)matches.stream().collect(Collectors.joining("', '")) + "' } )";
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 65 */     String relativePath = value.getRelativePath();
/* 66 */     PathMatcher.PathMatch<Boolean> result = this.pathMatcher.match(relativePath);
/* 67 */     boolean matches = Boolean.TRUE.equals(result.getValue());
/* 68 */     if (traceEnabled) {
/* 69 */       UndertowLogger.PREDICATE_LOGGER.tracef("Path(s) [%s] %s input [%s] for %s.", new Object[] { this.pathMatcher.getExactPathMatchesSet().stream().collect(Collectors.joining(", ")), matches ? "MATCH" : "DO NOT MATCH", relativePath, value });
/*    */     }
/* 71 */     return matches;
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 78 */       return "path";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 83 */       return Collections.singletonMap("path", String[].class);
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 88 */       return Collections.singleton("path");
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 93 */       return "path";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 98 */       String[] path = (String[])config.get("path");
/* 99 */       return new PathMatchPredicate(path);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PathMatchPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */