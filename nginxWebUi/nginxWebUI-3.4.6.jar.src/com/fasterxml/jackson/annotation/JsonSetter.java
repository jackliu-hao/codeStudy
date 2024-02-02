/*     */ package com.fasterxml.jackson.annotation;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
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
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonSetter
/*     */ {
/*     */   String value() default "";
/*     */   
/*     */   Nulls nulls() default Nulls.DEFAULT;
/*     */   
/*     */   Nulls contentNulls() default Nulls.DEFAULT;
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonSetter>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */     private final Nulls _nulls;
/*     */     private final Nulls _contentNulls;
/*  75 */     protected static final Value EMPTY = new Value(Nulls.DEFAULT, Nulls.DEFAULT);
/*     */     
/*     */     protected Value(Nulls nulls, Nulls contentNulls) {
/*  78 */       this._nulls = nulls;
/*  79 */       this._contentNulls = contentNulls;
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<JsonSetter> valueFor() {
/*  84 */       return JsonSetter.class;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object readResolve() {
/*  89 */       if (_empty(this._nulls, this._contentNulls)) {
/*  90 */         return EMPTY;
/*     */       }
/*  92 */       return this;
/*     */     }
/*     */     
/*     */     public static Value from(JsonSetter src) {
/*  96 */       if (src == null) {
/*  97 */         return EMPTY;
/*     */       }
/*  99 */       return construct(src.nulls(), src.contentNulls());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value construct(Nulls nulls, Nulls contentNulls) {
/* 110 */       if (nulls == null) {
/* 111 */         nulls = Nulls.DEFAULT;
/*     */       }
/* 113 */       if (contentNulls == null) {
/* 114 */         contentNulls = Nulls.DEFAULT;
/*     */       }
/* 116 */       if (_empty(nulls, contentNulls)) {
/* 117 */         return EMPTY;
/*     */       }
/* 119 */       return new Value(nulls, contentNulls);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value empty() {
/* 130 */       return EMPTY;
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
/*     */     public static Value merge(Value base, Value overrides) {
/* 144 */       return (base == null) ? overrides : base
/* 145 */         .withOverrides(overrides);
/*     */     }
/*     */     
/*     */     public static Value forValueNulls(Nulls nulls) {
/* 149 */       return construct(nulls, Nulls.DEFAULT);
/*     */     }
/*     */     
/*     */     public static Value forValueNulls(Nulls nulls, Nulls contentNulls) {
/* 153 */       return construct(nulls, contentNulls);
/*     */     }
/*     */     
/*     */     public static Value forContentNulls(Nulls nulls) {
/* 157 */       return construct(Nulls.DEFAULT, nulls);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withOverrides(Value overrides) {
/* 167 */       if (overrides == null || overrides == EMPTY) {
/* 168 */         return this;
/*     */       }
/* 170 */       Nulls nulls = overrides._nulls;
/* 171 */       Nulls contentNulls = overrides._contentNulls;
/*     */       
/* 173 */       if (nulls == Nulls.DEFAULT) {
/* 174 */         nulls = this._nulls;
/*     */       }
/* 176 */       if (contentNulls == Nulls.DEFAULT) {
/* 177 */         contentNulls = this._contentNulls;
/*     */       }
/*     */       
/* 180 */       if (nulls == this._nulls && contentNulls == this._contentNulls) {
/* 181 */         return this;
/*     */       }
/* 183 */       return construct(nulls, contentNulls);
/*     */     }
/*     */     
/*     */     public Value withValueNulls(Nulls nulls) {
/* 187 */       if (nulls == null) {
/* 188 */         nulls = Nulls.DEFAULT;
/*     */       }
/* 190 */       if (nulls == this._nulls) {
/* 191 */         return this;
/*     */       }
/* 193 */       return construct(nulls, this._contentNulls);
/*     */     }
/*     */     
/*     */     public Value withValueNulls(Nulls valueNulls, Nulls contentNulls) {
/* 197 */       if (valueNulls == null) {
/* 198 */         valueNulls = Nulls.DEFAULT;
/*     */       }
/* 200 */       if (contentNulls == null) {
/* 201 */         contentNulls = Nulls.DEFAULT;
/*     */       }
/* 203 */       if (valueNulls == this._nulls && contentNulls == this._contentNulls) {
/* 204 */         return this;
/*     */       }
/* 206 */       return construct(valueNulls, contentNulls);
/*     */     }
/*     */     
/*     */     public Value withContentNulls(Nulls nulls) {
/* 210 */       if (nulls == null) {
/* 211 */         nulls = Nulls.DEFAULT;
/*     */       }
/* 213 */       if (nulls == this._contentNulls) {
/* 214 */         return this;
/*     */       }
/* 216 */       return construct(this._nulls, nulls);
/*     */     }
/*     */     
/* 219 */     public Nulls getValueNulls() { return this._nulls; } public Nulls getContentNulls() {
/* 220 */       return this._contentNulls;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Nulls nonDefaultValueNulls() {
/* 227 */       return (this._nulls == Nulls.DEFAULT) ? null : this._nulls;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Nulls nonDefaultContentNulls() {
/* 235 */       return (this._contentNulls == Nulls.DEFAULT) ? null : this._contentNulls;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 246 */       return String.format("JsonSetter.Value(valueNulls=%s,contentNulls=%s)", new Object[] { this._nulls, this._contentNulls });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 252 */       return this._nulls.ordinal() + (this._contentNulls.ordinal() << 2);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 257 */       if (o == this) return true; 
/* 258 */       if (o == null) return false; 
/* 259 */       if (o.getClass() == getClass()) {
/* 260 */         Value other = (Value)o;
/* 261 */         return (other._nulls == this._nulls && other._contentNulls == this._contentNulls);
/*     */       } 
/*     */       
/* 264 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static boolean _empty(Nulls nulls, Nulls contentNulls) {
/* 274 */       return (nulls == Nulls.DEFAULT && contentNulls == Nulls.DEFAULT);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonSetter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */