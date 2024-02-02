/*     */ package io.undertow.servlet.predicate;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.attribute.ExchangeAttributes;
/*     */ import io.undertow.predicate.Predicate;
/*     */ import io.undertow.predicate.PredicateBuilder;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import io.undertow.server.handlers.resource.Resource;
/*     */ import io.undertow.server.handlers.resource.ResourceManager;
/*     */ import io.undertow.servlet.handlers.ServletRequestContext;
/*     */ import java.io.IOException;
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
/*     */ public class FilePredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final ExchangeAttribute location;
/*     */   private final boolean requireContent;
/*     */   
/*     */   public FilePredicate(ExchangeAttribute location) {
/*  47 */     this(location, false);
/*     */   }
/*     */   
/*     */   public FilePredicate(ExchangeAttribute location, boolean requireContent) {
/*  51 */     this.location = location;
/*  52 */     this.requireContent = requireContent;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean resolve(HttpServerExchange value) {
/*  57 */     String location = this.location.readAttribute(value);
/*  58 */     ServletRequestContext src = (ServletRequestContext)value.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  59 */     if (src == null) {
/*  60 */       return false;
/*     */     }
/*  62 */     ResourceManager manager = src.getDeployment().getDeploymentInfo().getResourceManager();
/*  63 */     if (manager == null) {
/*  64 */       return false;
/*     */     }
/*     */     try {
/*  67 */       Resource resource = manager.getResource(location);
/*  68 */       if (resource == null) {
/*  69 */         return false;
/*     */       }
/*  71 */       if (resource.isDirectory()) {
/*  72 */         return false;
/*     */       }
/*  74 */       if (this.requireContent) {
/*  75 */         return (resource.getContentLength() != null && resource.getContentLength().longValue() > 0L);
/*     */       }
/*  77 */       return true;
/*     */     }
/*  79 */     catch (IOException e) {
/*  80 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  86 */     return "file( " + this.location.toString() + " )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/*  93 */       return "file";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  98 */       Map<String, Class<?>> params = new HashMap<>();
/*  99 */       params.put("value", ExchangeAttribute.class);
/* 100 */       params.put("require-content", Boolean.class);
/* 101 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/* 106 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 111 */       return "value";
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 116 */       ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 117 */       Boolean requireContent = (Boolean)config.get("require-content");
/* 118 */       if (value == null) {
/* 119 */         value = ExchangeAttributes.relativePath();
/*     */       }
/* 121 */       return new FilePredicate(value, (requireContent == null) ? false : requireContent.booleanValue());
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\predicate\FilePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */