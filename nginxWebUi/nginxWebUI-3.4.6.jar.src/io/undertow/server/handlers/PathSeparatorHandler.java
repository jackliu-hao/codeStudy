/*    */ package io.undertow.server.handlers;
/*    */ 
/*    */ import io.undertow.server.HandlerWrapper;
/*    */ import io.undertow.server.HttpHandler;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*    */ import io.undertow.util.CanonicalPathUtils;
/*    */ import java.io.File;
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
/*    */ public class PathSeparatorHandler
/*    */   implements HttpHandler
/*    */ {
/*    */   private final HttpHandler next;
/*    */   
/*    */   public PathSeparatorHandler(HttpHandler next) {
/* 45 */     this.next = next;
/*    */   }
/*    */ 
/*    */   
/*    */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/* 50 */     boolean handlingRequired = (File.separatorChar != '/');
/* 51 */     if (handlingRequired) {
/* 52 */       exchange.setRequestPath(CanonicalPathUtils.canonicalize(exchange.getRequestPath().replace(File.separatorChar, '/')));
/* 53 */       exchange.setRelativePath(CanonicalPathUtils.canonicalize(exchange.getRelativePath().replace(File.separatorChar, '/')));
/* 54 */       exchange.setResolvedPath(CanonicalPathUtils.canonicalize(exchange.getResolvedPath().replace(File.separatorChar, '/')));
/*    */     } 
/* 56 */     this.next.handleRequest(exchange);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 61 */     return "path-separator()";
/*    */   }
/*    */ 
/*    */   
/*    */   public static class Builder
/*    */     implements HandlerBuilder
/*    */   {
/*    */     public String name() {
/* 69 */       return "path-separator";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 74 */       return Collections.emptyMap();
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 79 */       return Collections.emptySet();
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 84 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public HandlerWrapper build(Map<String, Object> config) {
/* 89 */       return new PathSeparatorHandler.Wrapper();
/*    */     }
/*    */   }
/*    */   
/*    */   private static class Wrapper implements HandlerWrapper {
/*    */     private Wrapper() {}
/*    */     
/*    */     public HttpHandler wrap(HttpHandler handler) {
/* 97 */       return new PathSeparatorHandler(handler);
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\PathSeparatorHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */