/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.HttpString;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ public class DisallowedMethodsHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final Set<HttpString> disallowedMethods;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public DisallowedMethodsHandler(HttpHandler next, Set<HttpString> disallowedMethods) {
/*  46 */     this.disallowedMethods = new HashSet<>(disallowedMethods);
/*  47 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public DisallowedMethodsHandler(HttpHandler next, HttpString... disallowedMethods) {
/*  52 */     this.disallowedMethods = new HashSet<>(Arrays.asList(disallowedMethods));
/*  53 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  58 */     if (this.disallowedMethods.contains(exchange.getRequestMethod())) {
/*  59 */       exchange.setStatusCode(405);
/*  60 */       exchange.endExchange();
/*     */     } else {
/*  62 */       this.next.handleRequest(exchange);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  68 */     if (this.disallowedMethods.size() == 1) {
/*  69 */       return "disallowed-methods( " + this.disallowedMethods.toArray()[0] + " )";
/*     */     }
/*  71 */     return "disallowed-methods( {" + (String)this.disallowedMethods.stream().map(s -> s.toString()).collect(Collectors.joining(", ")) + "} )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  79 */       return "disallowed-methods";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  84 */       return Collections.singletonMap("methods", String[].class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  89 */       return Collections.singleton("methods");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  94 */       return "methods";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/*  99 */       return new DisallowedMethodsHandler.Wrapper((String[])config.get("methods"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String[] methods;
/*     */     
/*     */     private Wrapper(String[] methods) {
/* 109 */       this.methods = methods;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 114 */       HttpString[] strings = new HttpString[this.methods.length];
/* 115 */       for (int i = 0; i < this.methods.length; i++) {
/* 116 */         strings[i] = new HttpString(this.methods[i]);
/*     */       }
/*     */       
/* 119 */       return new DisallowedMethodsHandler(handler, strings);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\DisallowedMethodsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */