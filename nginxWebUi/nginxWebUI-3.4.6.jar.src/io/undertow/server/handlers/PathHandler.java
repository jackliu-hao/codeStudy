/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.Handlers;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.cache.LRUCache;
/*     */ import io.undertow.util.PathMatcher;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PathHandler
/*     */   implements HttpHandler
/*     */ {
/*  42 */   private final PathMatcher<HttpHandler> pathMatcher = new PathMatcher();
/*     */   
/*     */   private final LRUCache<String, PathMatcher.PathMatch<HttpHandler>> cache;
/*     */   
/*     */   public PathHandler(HttpHandler defaultHandler) {
/*  47 */     this(0);
/*  48 */     this.pathMatcher.addPrefixPath("/", defaultHandler);
/*     */   }
/*     */   
/*     */   public PathHandler(HttpHandler defaultHandler, int cacheSize) {
/*  52 */     this(cacheSize);
/*  53 */     this.pathMatcher.addPrefixPath("/", defaultHandler);
/*     */   }
/*     */   
/*     */   public PathHandler() {
/*  57 */     this(0);
/*     */   }
/*     */   
/*     */   public PathHandler(int cacheSize) {
/*  61 */     if (cacheSize > 0) {
/*  62 */       this.cache = new LRUCache(cacheSize, -1, true);
/*     */     } else {
/*  64 */       this.cache = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  70 */     Set<Map.Entry<String, HttpHandler>> paths = this.pathMatcher.getPaths().entrySet();
/*  71 */     if (paths.size() == 1) {
/*  72 */       return "path( " + paths.toArray()[0] + " )";
/*     */     }
/*  74 */     return "path( {" + (String)paths.stream().map(s -> ((HttpHandler)s.getValue()).toString()).collect(Collectors.joining(", ")) + "} )";
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  80 */     PathMatcher.PathMatch<HttpHandler> match = null;
/*  81 */     boolean hit = false;
/*  82 */     if (this.cache != null) {
/*  83 */       match = (PathMatcher.PathMatch<HttpHandler>)this.cache.get(exchange.getRelativePath());
/*  84 */       hit = true;
/*     */     } 
/*  86 */     if (match == null) {
/*  87 */       match = this.pathMatcher.match(exchange.getRelativePath());
/*     */     }
/*  89 */     if (match.getValue() == null) {
/*  90 */       ResponseCodeHandler.HANDLE_404.handleRequest(exchange);
/*     */       return;
/*     */     } 
/*  93 */     if (hit) {
/*  94 */       this.cache.add(exchange.getRelativePath(), match);
/*     */     }
/*  96 */     exchange.setRelativePath(match.getRemaining());
/*  97 */     if (exchange.getResolvedPath().isEmpty()) {
/*     */       
/*  99 */       exchange.setResolvedPath(match.getMatched());
/*     */     } else {
/*     */       
/* 102 */       exchange.setResolvedPath(exchange.getResolvedPath() + match.getMatched());
/*     */     } 
/* 104 */     ((HttpHandler)match.getValue()).handleRequest(exchange);
/*     */   }
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
/*     */   @Deprecated
/*     */   public synchronized PathHandler addPath(String path, HttpHandler handler) {
/* 123 */     return addPrefixPath(path, handler);
/*     */   }
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
/*     */   public synchronized PathHandler addPrefixPath(String path, HttpHandler handler) {
/* 141 */     Handlers.handlerNotNull(handler);
/* 142 */     this.pathMatcher.addPrefixPath(path, handler);
/* 143 */     return this;
/*     */   }
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
/*     */   public synchronized PathHandler addExactPath(String path, HttpHandler handler) {
/* 156 */     Handlers.handlerNotNull(handler);
/* 157 */     this.pathMatcher.addExactPath(path, handler);
/* 158 */     return this;
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public synchronized PathHandler removePath(String path) {
/* 163 */     return removePrefixPath(path);
/*     */   }
/*     */   
/*     */   public synchronized PathHandler removePrefixPath(String path) {
/* 167 */     this.pathMatcher.removePrefixPath(path);
/* 168 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized PathHandler removeExactPath(String path) {
/* 172 */     this.pathMatcher.removeExactPath(path);
/* 173 */     return this;
/*     */   }
/*     */   
/*     */   public synchronized PathHandler clearPaths() {
/* 177 */     this.pathMatcher.clearPaths();
/* 178 */     return this;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PathHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */