/*     */ package io.undertow.predicate;
/*     */ 
/*     */ import io.undertow.attribute.ExchangeAttribute;
/*     */ import io.undertow.server.HttpServerExchange;
/*     */ import java.util.Arrays;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
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
/*     */ public class ContainsPredicate
/*     */   implements Predicate
/*     */ {
/*     */   private final ExchangeAttribute attribute;
/*     */   private final String[] values;
/*     */   
/*     */   ContainsPredicate(ExchangeAttribute attribute, String[] values) {
/*  41 */     this.attribute = attribute;
/*  42 */     this.values = new String[values.length];
/*  43 */     System.arraycopy(values, 0, this.values, 0, values.length);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean resolve(HttpServerExchange value) {
/*  48 */     String attr = this.attribute.readAttribute(value);
/*  49 */     if (attr == null) {
/*  50 */       return false;
/*     */     }
/*  52 */     for (int i = 0; i < this.values.length; i++) {
/*  53 */       if (attr.contains(this.values[i])) {
/*  54 */         return true;
/*     */       }
/*     */     } 
/*  57 */     return false;
/*     */   }
/*     */   
/*     */   public ExchangeAttribute getAttribute() {
/*  61 */     return this.attribute;
/*     */   }
/*     */   
/*     */   public String[] getValues() {
/*  65 */     String[] ret = new String[this.values.length];
/*  66 */     System.arraycopy(this.values, 0, ret, 0, this.values.length);
/*  67 */     return ret;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*  72 */     return "contains( search={" + String.join(", ", Arrays.asList((CharSequence[])this.values)) + "}, value='" + this.attribute.toString() + "' )";
/*     */   }
/*     */   
/*     */   public static class Builder
/*     */     implements PredicateBuilder
/*     */   {
/*     */     public String name() {
/*  79 */       return "contains";
/*     */     }
/*     */ 
/*     */     
/*     */     public Map<String, Class<?>> parameters() {
/*  84 */       Map<String, Class<?>> params = new HashMap<>();
/*  85 */       params.put("value", ExchangeAttribute.class);
/*  86 */       params.put("search", String[].class);
/*  87 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public Set<String> requiredParameters() {
/*  92 */       Set<String> params = new HashSet<>();
/*  93 */       params.add("value");
/*  94 */       params.add("search");
/*  95 */       return params;
/*     */     }
/*     */ 
/*     */     
/*     */     public String defaultParameter() {
/* 100 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     public Predicate build(Map<String, Object> config) {
/* 105 */       String[] search = (String[])config.get("search");
/* 106 */       ExchangeAttribute values = (ExchangeAttribute)config.get("value");
/* 107 */       return new ContainsPredicate(values, search);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\predicate\ContainsPredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */