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
/*     */ public class DirectoryPredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final ExchangeAttribute location;
/*     */   
/*     */   public DirectoryPredicate(ExchangeAttribute location) {
/*  46 */     this.location = location;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean resolve(HttpServerExchange value) {
/*  51 */     String location = this.location.readAttribute(value);
/*  52 */     ServletRequestContext src = (ServletRequestContext)value.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
/*  53 */     if (src == null) {
/*  54 */       return false;
/*     */     }
/*  56 */     ResourceManager manager = src.getDeployment().getDeploymentInfo().getResourceManager();
/*  57 */     if (manager == null) {
/*  58 */       return false;
/*     */     }
/*     */     try {
/*  61 */       Resource resource = manager.getResource(location);
/*  62 */       if (resource == null) {
/*  63 */         return false;
/*     */       }
/*  65 */       return resource.isDirectory();
/*  66 */     } catch (IOException e) {
/*  67 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  73 */     return "directory( " + this.location.toString() + " )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/*  80 */       return "directory";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  85 */       Map<String, Class<?>> params = new HashMap<>();
/*  86 */       params.put("value", ExchangeAttribute.class);
/*  87 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  92 */       return Collections.emptySet();
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/*  97 */       return "value";
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 102 */       ExchangeAttribute value = (ExchangeAttribute)config.get("value");
/* 103 */       if (value == null) {
/* 104 */         value = ExchangeAttributes.relativePath();
/*     */       }
/* 106 */       return new DirectoryPredicate(value);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\predicate\DirectoryPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */