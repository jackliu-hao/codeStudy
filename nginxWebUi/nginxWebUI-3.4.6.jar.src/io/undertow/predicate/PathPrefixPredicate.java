/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.PathMatcher;
/*     */ import java.util.Collections;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathPrefixPredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final PathMatcher<Boolean> pathMatcher;
/*  40 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*     */ 
/*     */   
/*     */   PathPrefixPredicate(String... paths) {
/*  44 */     PathMatcher<Boolean> matcher = new PathMatcher();
/*  45 */     for (String path : paths) {
/*  46 */       if (!path.startsWith("/")) {
/*  47 */         matcher.addPrefixPath("/" + path, Boolean.TRUE);
/*     */       } else {
/*  49 */         matcher.addPrefixPath(path, Boolean.TRUE);
/*     */       } 
/*     */     } 
/*  52 */     this.pathMatcher = matcher;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean resolve(HttpServerExchange value) {
/*  57 */     String relativePath = value.getRelativePath();
/*  58 */     PathMatcher.PathMatch<Boolean> result = this.pathMatcher.match(relativePath);
/*     */     
/*  60 */     boolean matches = Boolean.TRUE.equals(result.getValue());
/*     */     
/*  62 */     if (traceEnabled) {
/*  63 */       UndertowLogger.PREDICATE_LOGGER.tracef("Path prefix(s) [%s] %s input [%s] for %s.", new Object[] { this.pathMatcher.getPathMatchesSet().stream().collect(Collectors.joining(", ")), matches ? "MATCH" : "DO NOT MATCH", relativePath, value });
/*     */     }
/*  65 */     if (matches) {
/*  66 */       Map<String, Object> context = (Map<String, Object>)value.getAttachment(PREDICATE_CONTEXT);
/*  67 */       if (context == null) {
/*  68 */         value.putAttachment(PREDICATE_CONTEXT, context = new TreeMap<>());
/*     */       }
/*  70 */       if (traceEnabled && result.getRemaining().length() > 0) {
/*  71 */         UndertowLogger.PREDICATE_LOGGER.tracef("Storing \"remaining\" string of [%s] for %s.", result.getRemaining(), value);
/*     */       }
/*  73 */       context.put("remaining", result.getRemaining());
/*     */     } 
/*  75 */     return matches;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  79 */     Set<String> matches = this.pathMatcher.getPathMatchesSet();
/*  80 */     if (matches.size() == 1) {
/*  81 */       return "path-prefix( '" + matches.toArray()[0] + "' )";
/*     */     }
/*  83 */     return "path-prefix( { '" + (String)matches.stream().collect(Collectors.joining("', '")) + "' } )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/*  91 */       return "path-prefix";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  96 */       return Collections.singletonMap("path", String[].class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 101 */       return Collections.singleton("path");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 106 */       return "path";
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 111 */       String[] path = (String[])config.get("path");
/* 112 */       return new PathPrefixPredicate(path);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PathPrefixPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */