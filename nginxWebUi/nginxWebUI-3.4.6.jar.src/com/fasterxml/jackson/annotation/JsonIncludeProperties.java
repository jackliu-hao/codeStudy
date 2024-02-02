/*     */ package com.fasterxml.jackson.annotation;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE, ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonIncludeProperties
/*     */ {
/*     */   String[] value() default {};
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonIncludeProperties>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*  57 */     protected static final Value ALL = new Value(null);
/*     */ 
/*     */ 
/*     */     
/*     */     protected final Set<String> _included;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     protected Value(Set<String> included) {
/*  67 */       this._included = included;
/*     */     }
/*     */ 
/*     */     
/*     */     public static Value from(JsonIncludeProperties src) {
/*  72 */       if (src == null) {
/*  73 */         return ALL;
/*     */       }
/*  75 */       return new Value(_asSet(src.value()));
/*     */     }
/*     */ 
/*     */     
/*     */     public static Value all() {
/*  80 */       return ALL;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<JsonIncludeProperties> valueFor() {
/*  86 */       return JsonIncludeProperties.class;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<String> getIncluded() {
/*  94 */       return this._included;
/*     */     }
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
/*     */     public Value withOverrides(Value overrides) {
/*     */       Set<String> otherIncluded;
/* 109 */       if (overrides == null || (otherIncluded = overrides.getIncluded()) == null) {
/* 110 */         return this;
/*     */       }
/*     */       
/* 113 */       if (this._included == null) {
/* 114 */         return overrides;
/*     */       }
/*     */       
/* 117 */       HashSet<String> toInclude = new HashSet<String>();
/* 118 */       for (String incl : otherIncluded) {
/* 119 */         if (this._included.contains(incl)) {
/* 120 */           toInclude.add(incl);
/*     */         }
/*     */       } 
/*     */       
/* 124 */       return new Value(toInclude);
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 129 */       return String.format("JsonIncludeProperties.Value(included=%s)", new Object[] { this._included });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 135 */       return (this._included == null) ? 0 : this._included.size();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 140 */       if (o == this) return true; 
/* 141 */       if (o == null) return false; 
/* 142 */       return (o.getClass() == getClass() && _equals(this._included, ((Value)o)._included));
/*     */     }
/*     */ 
/*     */     
/*     */     private static boolean _equals(Set<String> a, Set<String> b) {
/* 147 */       return (a == null) ? ((b == null)) : a
/*     */         
/* 149 */         .equals(b);
/*     */     }
/*     */ 
/*     */     
/*     */     private static Set<String> _asSet(String[] v) {
/* 154 */       if (v == null || v.length == 0) {
/* 155 */         return Collections.emptySet();
/*     */       }
/* 157 */       Set<String> s = new HashSet<String>(v.length);
/* 158 */       for (String str : v) {
/* 159 */         s.add(str);
/*     */       }
/* 161 */       return s;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonIncludeProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */