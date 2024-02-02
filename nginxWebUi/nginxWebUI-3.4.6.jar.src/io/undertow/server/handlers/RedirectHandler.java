/*     */ package io.undertow.server.handlers;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributeParser;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.server.HandlerWrapper;
/*     */ import io.undertow.server.HttpHandler;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.builder.HandlerBuilder;
/*     */ import io.undertow.util.Headers;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
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
/*     */ public class RedirectHandler
/*     */   implements HttpHandler
/*     */ {
/*     */   private final ExchangeAttribute attribute;
/*     */   
/*     */   public RedirectHandler(String location) {
/*  48 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(getClass().getClassLoader());
/*  49 */     this.attribute = parser.parse(location);
/*     */   }
/*     */   
/*     */   public RedirectHandler(String location, ClassLoader classLoader) {
/*  53 */     ExchangeAttributeParser parser = ExchangeAttributes.parser(classLoader);
/*  54 */     this.attribute = parser.parse(location);
/*     */   }
/*     */   
/*     */   public RedirectHandler(ExchangeAttribute attribute) {
/*  58 */     this.attribute = attribute;
/*     */   }
/*     */ 
/*     */   
/*     */   public void handleRequest(HttpServerExchange exchange) throws Exception {
/*  63 */     exchange.setStatusCode(302);
/*  64 */     exchange.getResponseHeaders().put(Headers.LOCATION, this.attribute.readAttribute(exchange));
/*  65 */     exchange.endExchange();
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  70 */     return "redirect( '" + this.attribute.toString() + "' )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements HandlerBuilder
/*     */   {
/*     */     public String name() {
/*  77 */       return "redirect";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  82 */       Map<String, Class<?>> params = new HashMap<>();
/*  83 */       params.put("value", ExchangeAttribute.class);
/*     */       
/*  85 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  90 */       return Collections.singleton("value");
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  95 */       return "value";
/*     */     }
/*     */ 
/*     */     
/*     */     public HandlerWrapper build(Map<String, Object> config) {
/* 100 */       return new RedirectHandler.Wrapper((ExchangeAttribute)config.get("value"));
/*     */     }
/*     */   }
/*     */   
/*     */   private static class Wrapper
/*     */     implements HandlerWrapper
/*     */   {
/*     */     private final ExchangeAttribute value;
/*     */     
/*     */     private Wrapper(ExchangeAttribute value) {
/* 110 */       this.value = value;
/*     */     }
/*     */ 
/*     */     
/*     */     public HttpHandler wrap(HttpHandler handler) {
/* 115 */       return new RedirectHandler(this.value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\RedirectHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */