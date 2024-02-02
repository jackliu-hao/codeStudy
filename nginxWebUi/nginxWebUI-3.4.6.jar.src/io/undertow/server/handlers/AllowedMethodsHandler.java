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
/*     */ public class AllowedMethodsHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final Set<HttpString> allowedMethods;
/*     */   private final HttpHandler next;
/*     */   
/*     */   public AllowedMethodsHandler(HttpHandler next, Set<HttpString> allowedMethods) {
/*  46 */     this.allowedMethods = new HashSet<>(allowedMethods);
/*  47 */     this.next = next;
/*     */   }
/*     */   
/*     */   public AllowedMethodsHandler(HttpHandler next, HttpString... allowedMethods) {
/*  51 */     this.allowedMethods = new HashSet<>(Arrays.asList(allowedMethods));
/*  52 */     this.next = next;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  57 */     if (this.allowedMethods.contains(exchange.getRequestMethod())) {
/*  58 */       this.next.handleRequest(exchange);
/*     */     } else {
/*  60 */       exchange.setStatusCode(405);
/*  61 */       exchange.endExchange();
/*     */     } 
/*     */   }
/*     */   
/*     */   public Set<HttpString> getAllowedMethods() {
/*  66 */     return Collections.unmodifiableSet(this.allowedMethods);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/*  72 */     if (this.allowedMethods.size() == 1) {
/*  73 */       return "allowed-methods( " + this.allowedMethods.toArray()[0] + " )";
/*     */     }
/*  75 */     return "allowed-methods( {" + (String)this.allowedMethods.stream().map(s -> s.toString()).collect(Collectors.joining(", ")) + "} )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  83 */       return "allowed-methods";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  88 */       return Collections.singletonMap("methods", String[].class);
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  93 */       return Collections.singleton("methods");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  98 */       return "methods";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 103 */       return new AllowedMethodsHandler.Wrapper((String[])config.get("methods"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final String[] methods;
/*     */     
/*     */     private Wrapper(String[] methods) {
/* 113 */       this.methods = methods;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 118 */       HttpString[] strings = new HttpString[this.methods.length];
/* 119 */       for (int i = 0; i < this.methods.length; i++) {
/* 120 */         strings[i] = new HttpString(this.methods[i]);
/*     */       }
/*     */       
/* 123 */       return new AllowedMethodsHandler(handler, strings);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\AllowedMethodsHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */