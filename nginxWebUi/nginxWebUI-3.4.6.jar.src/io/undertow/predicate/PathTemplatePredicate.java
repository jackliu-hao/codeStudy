/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.UndertowLogger;
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.util.PathTemplate;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.TreeMap;
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
/*     */ public class PathTemplatePredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final ExchangeAttribute attribute;
/*     */   private final String template;
/*     */   private final PathTemplate value;
/*  44 */   private static final boolean traceEnabled = UndertowLogger.PREDICATE_LOGGER.isTraceEnabled();
/*     */ 
/*     */   
/*     */   public PathTemplatePredicate(String template, ExchangeAttribute attribute) {
/*  48 */     this.attribute = attribute;
/*  49 */     this.template = template;
/*  50 */     this.value = PathTemplate.create(template);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean resolve(HttpServerExchange exchange) {
/*  55 */     Map<String, String> params = new HashMap<>();
/*  56 */     String path = this.attribute.readAttribute(exchange);
/*  57 */     if (path == null) {
/*  58 */       return false;
/*     */     }
/*  60 */     boolean result = this.value.matches(path, params);
/*  61 */     if (traceEnabled) {
/*  62 */       UndertowLogger.PREDICATE_LOGGER.tracef("Path template [%s] %s input [%s] for %s.", new Object[] { this.template, result ? "MATCHES" : "DOES NOT MATCH", path, exchange });
/*     */     }
/*  64 */     if (result) {
/*  65 */       Map<String, Object> context = (Map<String, Object>)exchange.getAttachment(PREDICATE_CONTEXT);
/*  66 */       if (context == null) {
/*  67 */         exchange.putAttachment(PREDICATE_CONTEXT, context = new TreeMap<>());
/*     */       }
/*  69 */       if (traceEnabled) {
/*  70 */         params.entrySet().forEach(param -> UndertowLogger.PREDICATE_LOGGER.tracef("Storing template match [%s=%s] for %s.", param.getKey(), param.getValue(), exchange));
/*     */       }
/*  72 */       context.putAll(params);
/*     */     } 
/*  74 */     return result;
/*     */   }
/*     */   
/*     */   public String toString() {
/*  78 */     if (this.attribute == ExchangeAttributes.relativePath()) {
/*  79 */       return "path-template( '" + this.template + "' )";
/*     */     }
/*  81 */     return "path-template( value='" + this.template + "', match='" + this.attribute.toString() + "' )";
/*     */   }
/*     */ 
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/*  89 */       return "path-template";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  94 */       Map<String, Class<?>> params = new HashMap<>();
/*  95 */       params.put("value", String.class);
/*  96 */       params.put("match", ExchangeAttribute.class);
/*  97 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 102 */       Set<String> params = new HashSet<>();
/* 103 */       params.add("value");
/* 104 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 109 */       return "value";
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 114 */       ExchangeAttribute match = (ExchangeAttribute)config.get("match");
/* 115 */       if (match == null) {
/* 116 */         match = ExchangeAttributes.relativePath();
/*     */       }
/* 118 */       String value = (String)config.get("value");
/* 119 */       return new PathTemplatePredicate(value, match);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\PathTemplatePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */