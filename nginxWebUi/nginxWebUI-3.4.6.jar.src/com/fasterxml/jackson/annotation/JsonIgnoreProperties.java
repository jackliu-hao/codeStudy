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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */ public @interface JsonIgnoreProperties
/*     */ {
/*     */   String[] value() default {};
/*     */   
/*     */   boolean ignoreUnknown() default false;
/*     */   
/*     */   boolean allowGetters() default false;
/*     */   
/*     */   boolean allowSetters() default false;
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JsonIgnoreProperties>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/* 118 */     protected static final Value EMPTY = new Value(Collections.emptySet(), false, false, false, true);
/*     */ 
/*     */     
/*     */     protected final Set<String> _ignored;
/*     */ 
/*     */     
/*     */     protected final boolean _ignoreUnknown;
/*     */ 
/*     */     
/*     */     protected final boolean _allowGetters;
/*     */ 
/*     */     
/*     */     protected final boolean _allowSetters;
/*     */     
/*     */     protected final boolean _merge;
/*     */ 
/*     */     
/*     */     protected Value(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
/* 136 */       if (ignored == null) {
/* 137 */         this._ignored = Collections.emptySet();
/*     */       } else {
/* 139 */         this._ignored = ignored;
/*     */       } 
/* 141 */       this._ignoreUnknown = ignoreUnknown;
/* 142 */       this._allowGetters = allowGetters;
/* 143 */       this._allowSetters = allowSetters;
/* 144 */       this._merge = merge;
/*     */     }
/*     */     
/*     */     public static Value from(JsonIgnoreProperties src) {
/* 148 */       if (src == null) {
/* 149 */         return EMPTY;
/*     */       }
/* 151 */       return construct(_asSet(src.value()), src
/* 152 */           .ignoreUnknown(), src.allowGetters(), src.allowSetters(), false);
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
/*     */     public static Value construct(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
/* 169 */       if (_empty(ignored, ignoreUnknown, allowGetters, allowSetters, merge)) {
/* 170 */         return EMPTY;
/*     */       }
/* 172 */       return new Value(ignored, ignoreUnknown, allowGetters, allowSetters, merge);
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
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value empty() {
/* 192 */       return EMPTY;
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
/* 206 */       return (base == null) ? overrides : base
/* 207 */         .withOverrides(overrides);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value mergeAll(Value... values) {
/* 215 */       Value result = null;
/* 216 */       for (Value curr : values) {
/* 217 */         if (curr != null) {
/* 218 */           result = (result == null) ? curr : result.withOverrides(curr);
/*     */         }
/*     */       } 
/* 221 */       return result;
/*     */     }
/*     */     
/*     */     public static Value forIgnoredProperties(Set<String> propNames) {
/* 225 */       return EMPTY.withIgnored(propNames);
/*     */     }
/*     */     
/*     */     public static Value forIgnoredProperties(String... propNames) {
/* 229 */       if (propNames.length == 0) {
/* 230 */         return EMPTY;
/*     */       }
/* 232 */       return EMPTY.withIgnored(_asSet(propNames));
/*     */     }
/*     */     
/*     */     public static Value forIgnoreUnknown(boolean state) {
/* 236 */       return state ? EMPTY.withIgnoreUnknown() : EMPTY
/* 237 */         .withoutIgnoreUnknown();
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withOverrides(Value overrides) {
/* 247 */       if (overrides == null || overrides == EMPTY) {
/* 248 */         return this;
/*     */       }
/*     */ 
/*     */       
/* 252 */       if (!overrides._merge) {
/* 253 */         return overrides;
/*     */       }
/* 255 */       if (_equals(this, overrides)) {
/* 256 */         return this;
/*     */       }
/*     */ 
/*     */       
/* 260 */       Set<String> ignored = _merge(this._ignored, overrides._ignored);
/* 261 */       boolean ignoreUnknown = (this._ignoreUnknown || overrides._ignoreUnknown);
/* 262 */       boolean allowGetters = (this._allowGetters || overrides._allowGetters);
/* 263 */       boolean allowSetters = (this._allowSetters || overrides._allowSetters);
/*     */ 
/*     */       
/* 266 */       return construct(ignored, ignoreUnknown, allowGetters, allowSetters, true);
/*     */     }
/*     */     
/*     */     public Value withIgnored(Set<String> ignored) {
/* 270 */       return construct(ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
/*     */     }
/*     */     
/*     */     public Value withIgnored(String... ignored) {
/* 274 */       return construct(_asSet(ignored), this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
/*     */     }
/*     */     
/*     */     public Value withoutIgnored() {
/* 278 */       return construct(null, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge);
/*     */     }
/*     */     
/*     */     public Value withIgnoreUnknown() {
/* 282 */       return this._ignoreUnknown ? this : 
/* 283 */         construct(this._ignored, true, this._allowGetters, this._allowSetters, this._merge);
/*     */     }
/*     */     public Value withoutIgnoreUnknown() {
/* 286 */       return !this._ignoreUnknown ? this : 
/* 287 */         construct(this._ignored, false, this._allowGetters, this._allowSetters, this._merge);
/*     */     }
/*     */     
/*     */     public Value withAllowGetters() {
/* 291 */       return this._allowGetters ? this : 
/* 292 */         construct(this._ignored, this._ignoreUnknown, true, this._allowSetters, this._merge);
/*     */     }
/*     */     public Value withoutAllowGetters() {
/* 295 */       return !this._allowGetters ? this : 
/* 296 */         construct(this._ignored, this._ignoreUnknown, false, this._allowSetters, this._merge);
/*     */     }
/*     */     
/*     */     public Value withAllowSetters() {
/* 300 */       return this._allowSetters ? this : 
/* 301 */         construct(this._ignored, this._ignoreUnknown, this._allowGetters, true, this._merge);
/*     */     }
/*     */     public Value withoutAllowSetters() {
/* 304 */       return !this._allowSetters ? this : 
/* 305 */         construct(this._ignored, this._ignoreUnknown, this._allowGetters, false, this._merge);
/*     */     }
/*     */     
/*     */     public Value withMerge() {
/* 309 */       return this._merge ? this : 
/* 310 */         construct(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, true);
/*     */     }
/*     */     
/*     */     public Value withoutMerge() {
/* 314 */       return !this._merge ? this : 
/* 315 */         construct(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, false);
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<JsonIgnoreProperties> valueFor() {
/* 320 */       return JsonIgnoreProperties.class;
/*     */     }
/*     */ 
/*     */     
/*     */     protected Object readResolve() {
/* 325 */       if (_empty(this._ignored, this._ignoreUnknown, this._allowGetters, this._allowSetters, this._merge)) {
/* 326 */         return EMPTY;
/*     */       }
/* 328 */       return this;
/*     */     }
/*     */     
/*     */     public Set<String> getIgnored() {
/* 332 */       return this._ignored;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<String> findIgnoredForSerialization() {
/* 343 */       if (this._allowGetters) {
/* 344 */         return Collections.emptySet();
/*     */       }
/* 346 */       return this._ignored;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Set<String> findIgnoredForDeserialization() {
/* 357 */       if (this._allowSetters) {
/* 358 */         return Collections.emptySet();
/*     */       }
/* 360 */       return this._ignored;
/*     */     }
/*     */     
/*     */     public boolean getIgnoreUnknown() {
/* 364 */       return this._ignoreUnknown;
/*     */     }
/*     */     
/*     */     public boolean getAllowGetters() {
/* 368 */       return this._allowGetters;
/*     */     }
/*     */     
/*     */     public boolean getAllowSetters() {
/* 372 */       return this._allowSetters;
/*     */     }
/*     */     
/*     */     public boolean getMerge() {
/* 376 */       return this._merge;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 381 */       return String.format("JsonIgnoreProperties.Value(ignored=%s,ignoreUnknown=%s,allowGetters=%s,allowSetters=%s,merge=%s)", new Object[] { this._ignored, 
/* 382 */             Boolean.valueOf(this._ignoreUnknown), Boolean.valueOf(this._allowGetters), Boolean.valueOf(this._allowSetters), Boolean.valueOf(this._merge) });
/*     */     }
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 387 */       return this._ignored.size() + (this._ignoreUnknown ? 1 : -3) + (this._allowGetters ? 3 : -7) + (this._allowSetters ? 7 : -11) + (this._merge ? 11 : -13);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 397 */       if (o == this) return true; 
/* 398 */       if (o == null) return false; 
/* 399 */       return (o.getClass() == getClass() && 
/* 400 */         _equals(this, (Value)o));
/*     */     }
/*     */ 
/*     */     
/*     */     private static boolean _equals(Value a, Value b) {
/* 405 */       return (a._ignoreUnknown == b._ignoreUnknown && a._merge == b._merge && a._allowGetters == b._allowGetters && a._allowSetters == b._allowSetters && a._ignored
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 410 */         .equals(b._ignored));
/*     */     }
/*     */ 
/*     */     
/*     */     private static Set<String> _asSet(String[] v) {
/* 415 */       if (v == null || v.length == 0) {
/* 416 */         return Collections.emptySet();
/*     */       }
/* 418 */       Set<String> s = new HashSet<String>(v.length);
/* 419 */       for (String str : v) {
/* 420 */         s.add(str);
/*     */       }
/* 422 */       return s;
/*     */     }
/*     */ 
/*     */     
/*     */     private static Set<String> _merge(Set<String> s1, Set<String> s2) {
/* 427 */       if (s1.isEmpty())
/* 428 */         return s2; 
/* 429 */       if (s2.isEmpty()) {
/* 430 */         return s1;
/*     */       }
/* 432 */       HashSet<String> result = new HashSet<String>(s1.size() + s2.size());
/* 433 */       result.addAll(s1);
/* 434 */       result.addAll(s2);
/* 435 */       return result;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     private static boolean _empty(Set<String> ignored, boolean ignoreUnknown, boolean allowGetters, boolean allowSetters, boolean merge) {
/* 441 */       if (ignoreUnknown == EMPTY._ignoreUnknown && allowGetters == EMPTY._allowGetters && allowSetters == EMPTY._allowSetters && merge == EMPTY._merge)
/*     */       {
/*     */ 
/*     */         
/* 445 */         return (ignored == null || ignored.size() == 0);
/*     */       }
/* 447 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonIgnoreProperties.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */