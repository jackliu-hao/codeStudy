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
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.TYPE, ElementType.PARAMETER})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonInclude
/*     */ {
/*     */   Include value() default Include.ALWAYS;
/*     */   
/*     */   Include content() default Include.ALWAYS;
/*     */   
/*     */   Class<?> valueFilter() default Void.class;
/*     */   
/*     */   Class<?> contentFilter() default Void.class;
/*     */   
/*     */   public enum Include
/*     */   {
/* 109 */     ALWAYS,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 115 */     NON_NULL,
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
/* 130 */     NON_ABSENT,
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
/* 177 */     NON_EMPTY,
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
/* 200 */     NON_DEFAULT,
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
/* 213 */     CUSTOM,
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
/* 224 */     USE_DEFAULTS;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonInclude>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 247 */     protected static final Value EMPTY = new Value(JsonInclude.Include.USE_DEFAULTS, JsonInclude.Include.USE_DEFAULTS, null, null);
/*     */ 
/*     */     
/*     */     protected final JsonInclude.Include _valueInclusion;
/*     */ 
/*     */     
/*     */     protected final JsonInclude.Include _contentInclusion;
/*     */ 
/*     */     
/*     */     protected final Class<?> _valueFilter;
/*     */ 
/*     */     
/*     */     protected final Class<?> _contentFilter;
/*     */ 
/*     */ 
/*     */     
/*     */     public Value(JsonInclude src) {
/* 264 */       this(src.value(), src.content(), src
/* 265 */           .valueFilter(), src.contentFilter());
/*     */     }
/*     */ 
/*     */     
/*     */     protected Value(JsonInclude.Include vi, JsonInclude.Include ci, Class<?> valueFilter, Class<?> contentFilter) {
/* 270 */       this._valueInclusion = (vi == null) ? JsonInclude.Include.USE_DEFAULTS : vi;
/* 271 */       this._contentInclusion = (ci == null) ? JsonInclude.Include.USE_DEFAULTS : ci;
/* 272 */       this._valueFilter = (valueFilter == Void.class) ? null : valueFilter;
/* 273 */       this._contentFilter = (contentFilter == Void.class) ? null : contentFilter;
/*     */     }
/*     */     
/*     */     public static Value empty() {
/* 277 */       return EMPTY;
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
/*     */ 
/*     */     
/*     */     public static Value merge(Value base, Value overrides) {
/* 293 */       return (base == null) ? overrides : base
/* 294 */         .withOverrides(overrides);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value mergeAll(Value... values) {
/* 302 */       Value result = null;
/* 303 */       for (Value curr : values) {
/* 304 */         if (curr != null) {
/* 305 */           result = (result == null) ? curr : result.withOverrides(curr);
/*     */         }
/*     */       } 
/* 308 */       return result;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object readResolve() {
/* 313 */       if (this._valueInclusion == JsonInclude.Include.USE_DEFAULTS && this._contentInclusion == JsonInclude.Include.USE_DEFAULTS && this._valueFilter == null && this._contentFilter == null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 318 */         return EMPTY;
/*     */       }
/* 320 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withOverrides(Value overrides) {
/* 330 */       if (overrides == null || overrides == EMPTY) {
/* 331 */         return this;
/*     */       }
/* 333 */       JsonInclude.Include vi = overrides._valueInclusion;
/* 334 */       JsonInclude.Include ci = overrides._contentInclusion;
/* 335 */       Class<?> vf = overrides._valueFilter;
/* 336 */       Class<?> cf = overrides._contentFilter;
/*     */       
/* 338 */       boolean viDiff = (vi != this._valueInclusion && vi != JsonInclude.Include.USE_DEFAULTS);
/* 339 */       boolean ciDiff = (ci != this._contentInclusion && ci != JsonInclude.Include.USE_DEFAULTS);
/* 340 */       boolean filterDiff = (vf != this._valueFilter || cf != this._valueFilter);
/*     */       
/* 342 */       if (viDiff) {
/* 343 */         if (ciDiff) {
/* 344 */           return new Value(vi, ci, vf, cf);
/*     */         }
/* 346 */         return new Value(vi, this._contentInclusion, vf, cf);
/* 347 */       }  if (ciDiff)
/* 348 */         return new Value(this._valueInclusion, ci, vf, cf); 
/* 349 */       if (filterDiff) {
/* 350 */         return new Value(this._valueInclusion, this._contentInclusion, vf, cf);
/*     */       }
/* 352 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value construct(JsonInclude.Include valueIncl, JsonInclude.Include contentIncl) {
/* 359 */       if ((valueIncl == JsonInclude.Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == JsonInclude.Include.USE_DEFAULTS || contentIncl == null))
/*     */       {
/* 361 */         return EMPTY;
/*     */       }
/* 363 */       return new Value(valueIncl, contentIncl, null, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value construct(JsonInclude.Include valueIncl, JsonInclude.Include contentIncl, Class<?> valueFilter, Class<?> contentFilter) {
/* 374 */       if (valueFilter == Void.class) {
/* 375 */         valueFilter = null;
/*     */       }
/* 377 */       if (contentFilter == Void.class) {
/* 378 */         contentFilter = null;
/*     */       }
/* 380 */       if ((valueIncl == JsonInclude.Include.USE_DEFAULTS || valueIncl == null) && (contentIncl == JsonInclude.Include.USE_DEFAULTS || contentIncl == null) && valueFilter == null && contentFilter == null)
/*     */       {
/*     */ 
/*     */ 
/*     */         
/* 385 */         return EMPTY;
/*     */       }
/* 387 */       return new Value(valueIncl, contentIncl, valueFilter, contentFilter);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value from(JsonInclude src) {
/* 395 */       if (src == null) {
/* 396 */         return EMPTY;
/*     */       }
/* 398 */       JsonInclude.Include vi = src.value();
/* 399 */       JsonInclude.Include ci = src.content();
/*     */       
/* 401 */       if (vi == JsonInclude.Include.USE_DEFAULTS && ci == JsonInclude.Include.USE_DEFAULTS) {
/* 402 */         return EMPTY;
/*     */       }
/* 404 */       Class<?> vf = src.valueFilter();
/* 405 */       if (vf == Void.class) {
/* 406 */         vf = null;
/*     */       }
/* 408 */       Class<?> cf = src.contentFilter();
/* 409 */       if (cf == Void.class) {
/* 410 */         cf = null;
/*     */       }
/* 412 */       return new Value(vi, ci, vf, cf);
/*     */     }
/*     */     
/*     */     public Value withValueInclusion(JsonInclude.Include incl) {
/* 416 */       return (incl == this._valueInclusion) ? this : new Value(incl, this._contentInclusion, this._valueFilter, this._contentFilter);
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
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withValueFilter(Class<?> filter) {
/*     */       JsonInclude.Include incl;
/* 434 */       if (filter == null || filter == Void.class) {
/* 435 */         incl = JsonInclude.Include.USE_DEFAULTS;
/* 436 */         filter = null;
/*     */       } else {
/* 438 */         incl = JsonInclude.Include.CUSTOM;
/*     */       } 
/* 440 */       return construct(incl, this._contentInclusion, filter, this._contentFilter);
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
/*     */ 
/*     */     
/*     */     public Value withContentFilter(Class<?> filter) {
/*     */       JsonInclude.Include incl;
/* 457 */       if (filter == null || filter == Void.class) {
/* 458 */         incl = JsonInclude.Include.USE_DEFAULTS;
/* 459 */         filter = null;
/*     */       } else {
/* 461 */         incl = JsonInclude.Include.CUSTOM;
/*     */       } 
/* 463 */       return construct(this._valueInclusion, incl, this._valueFilter, filter);
/*     */     }
/*     */     
/*     */     public Value withContentInclusion(JsonInclude.Include incl) {
/* 467 */       return (incl == this._contentInclusion) ? this : new Value(this._valueInclusion, incl, this._valueFilter, this._contentFilter);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Class<JsonInclude> valueFor() {
/* 473 */       return JsonInclude.class;
/*     */     }
/*     */     
/*     */     public JsonInclude.Include getValueInclusion() {
/* 477 */       return this._valueInclusion;
/*     */     }
/*     */     
/*     */     public JsonInclude.Include getContentInclusion() {
/* 481 */       return this._contentInclusion;
/*     */     }
/*     */     
/*     */     public Class<?> getValueFilter() {
/* 485 */       return this._valueFilter;
/*     */     }
/*     */     
/*     */     public Class<?> getContentFilter() {
/* 489 */       return this._contentFilter;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 494 */       StringBuilder sb = new StringBuilder(80);
/* 495 */       sb.append("JsonInclude.Value(value=")
/* 496 */         .append(this._valueInclusion)
/* 497 */         .append(",content=")
/* 498 */         .append(this._contentInclusion);
/* 499 */       if (this._valueFilter != null) {
/* 500 */         sb.append(",valueFilter=").append(this._valueFilter.getName()).append(".class");
/*     */       }
/* 502 */       if (this._contentFilter != null) {
/* 503 */         sb.append(",contentFilter=").append(this._contentFilter.getName()).append(".class");
/*     */       }
/* 505 */       return sb.append(')').toString();
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 510 */       return (this._valueInclusion.hashCode() << 2) + this._contentInclusion
/* 511 */         .hashCode();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 516 */       if (o == this) return true; 
/* 517 */       if (o == null) return false; 
/* 518 */       if (o.getClass() != getClass()) return false; 
/* 519 */       Value other = (Value)o;
/*     */       
/* 521 */       return (other._valueInclusion == this._valueInclusion && other._contentInclusion == this._contentInclusion && other._valueFilter == this._valueFilter && other._contentFilter == this._contentFilter);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonInclude.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */