/*     */ package com.fasterxml.jackson.annotation;
/*     */ 
/*     */ import java.io.Serializable;
/*     */ import java.lang.annotation.ElementType;
/*     */ import java.lang.annotation.Retention;
/*     */ import java.lang.annotation.RetentionPolicy;
/*     */ import java.lang.annotation.Target;
/*     */ import java.lang.reflect.Member;
/*     */ import java.lang.reflect.Modifier;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.TYPE})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JsonAutoDetect
/*     */ {
/*     */   Visibility getterVisibility() default Visibility.DEFAULT;
/*     */   
/*     */   Visibility isGetterVisibility() default Visibility.DEFAULT;
/*     */   
/*     */   Visibility setterVisibility() default Visibility.DEFAULT;
/*     */   
/*     */   Visibility creatorVisibility() default Visibility.DEFAULT;
/*     */   
/*     */   Visibility fieldVisibility() default Visibility.DEFAULT;
/*     */   
/*     */   public enum Visibility
/*     */   {
/*  43 */     ANY,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  48 */     NON_PRIVATE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  54 */     PROTECTED_AND_PUBLIC,
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  59 */     PUBLIC_ONLY,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  65 */     NONE,
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*  72 */     DEFAULT;
/*     */     
/*     */     public boolean isVisible(Member m) {
/*  75 */       switch (this) {
/*     */         case CREATOR:
/*  77 */           return true;
/*     */         case FIELD:
/*  79 */           return false;
/*     */         case GETTER:
/*  81 */           return !Modifier.isPrivate(m.getModifiers());
/*     */         case IS_GETTER:
/*  83 */           if (Modifier.isProtected(m.getModifiers())) {
/*  84 */             return true;
/*     */           }
/*     */         
/*     */         case NONE:
/*  88 */           return Modifier.isPublic(m.getModifiers());
/*     */       } 
/*  90 */       return false;
/*     */     }
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
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
/*     */     implements JacksonAnnotationValue<JsonAutoDetect>, Serializable
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 141 */     private static final JsonAutoDetect.Visibility DEFAULT_FIELD_VISIBILITY = JsonAutoDetect.Visibility.PUBLIC_ONLY;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     protected static final Value DEFAULT = new Value(DEFAULT_FIELD_VISIBILITY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.PUBLIC_ONLY, JsonAutoDetect.Visibility.ANY, JsonAutoDetect.Visibility.PUBLIC_ONLY);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 160 */     protected static final Value NO_OVERRIDES = new Value(JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT, JsonAutoDetect.Visibility.DEFAULT);
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _fieldVisibility;
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _getterVisibility;
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _isGetterVisibility;
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _setterVisibility;
/*     */     
/*     */     protected final JsonAutoDetect.Visibility _creatorVisibility;
/*     */     
/*     */     private Value(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators) {
/* 173 */       this._fieldVisibility = fields;
/* 174 */       this._getterVisibility = getters;
/* 175 */       this._isGetterVisibility = isGetters;
/* 176 */       this._setterVisibility = setters;
/* 177 */       this._creatorVisibility = creators;
/*     */     }
/*     */     
/*     */     public static Value defaultVisibility() {
/* 181 */       return DEFAULT;
/*     */     }
/*     */     
/*     */     public static Value noOverrides() {
/* 185 */       return NO_OVERRIDES;
/*     */     }
/*     */     
/*     */     public static Value from(JsonAutoDetect src) {
/* 189 */       return construct(src.fieldVisibility(), src
/* 190 */           .getterVisibility(), src.isGetterVisibility(), src.setterVisibility(), src
/* 191 */           .creatorVisibility());
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value construct(PropertyAccessor acc, JsonAutoDetect.Visibility visibility) {
/* 201 */       JsonAutoDetect.Visibility fields = JsonAutoDetect.Visibility.DEFAULT;
/* 202 */       JsonAutoDetect.Visibility getters = JsonAutoDetect.Visibility.DEFAULT;
/* 203 */       JsonAutoDetect.Visibility isGetters = JsonAutoDetect.Visibility.DEFAULT;
/* 204 */       JsonAutoDetect.Visibility setters = JsonAutoDetect.Visibility.DEFAULT;
/* 205 */       JsonAutoDetect.Visibility creators = JsonAutoDetect.Visibility.DEFAULT;
/* 206 */       switch (acc) {
/*     */         case CREATOR:
/* 208 */           creators = visibility;
/*     */           break;
/*     */         case FIELD:
/* 211 */           fields = visibility;
/*     */           break;
/*     */         case GETTER:
/* 214 */           getters = visibility;
/*     */           break;
/*     */         case IS_GETTER:
/* 217 */           isGetters = visibility;
/*     */           break;
/*     */ 
/*     */         
/*     */         case SETTER:
/* 222 */           setters = visibility;
/*     */           break;
/*     */         case ALL:
/* 225 */           fields = getters = isGetters = setters = creators = visibility;
/*     */           break;
/*     */       } 
/* 228 */       return construct(fields, getters, isGetters, setters, creators);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value construct(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators) {
/* 235 */       Value v = _predefined(fields, getters, isGetters, setters, creators);
/* 236 */       if (v == null) {
/* 237 */         v = new Value(fields, getters, isGetters, setters, creators);
/*     */       }
/* 239 */       return v;
/*     */     }
/*     */     
/*     */     public Value withFieldVisibility(JsonAutoDetect.Visibility v) {
/* 243 */       return construct(v, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value withGetterVisibility(JsonAutoDetect.Visibility v) {
/* 248 */       return construct(this._fieldVisibility, v, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value withIsGetterVisibility(JsonAutoDetect.Visibility v) {
/* 253 */       return construct(this._fieldVisibility, this._getterVisibility, v, this._setterVisibility, this._creatorVisibility);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value withSetterVisibility(JsonAutoDetect.Visibility v) {
/* 258 */       return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, v, this._creatorVisibility);
/*     */     }
/*     */ 
/*     */     
/*     */     public Value withCreatorVisibility(JsonAutoDetect.Visibility v) {
/* 263 */       return construct(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, v);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value merge(Value base, Value overrides) {
/* 269 */       return (base == null) ? overrides : base
/* 270 */         .withOverrides(overrides);
/*     */     }
/*     */     
/*     */     public Value withOverrides(Value overrides) {
/* 274 */       if (overrides == null || overrides == NO_OVERRIDES || overrides == this) {
/* 275 */         return this;
/*     */       }
/* 277 */       if (_equals(this, overrides)) {
/* 278 */         return this;
/*     */       }
/* 280 */       JsonAutoDetect.Visibility fields = overrides._fieldVisibility;
/* 281 */       if (fields == JsonAutoDetect.Visibility.DEFAULT) {
/* 282 */         fields = this._fieldVisibility;
/*     */       }
/* 284 */       JsonAutoDetect.Visibility getters = overrides._getterVisibility;
/* 285 */       if (getters == JsonAutoDetect.Visibility.DEFAULT) {
/* 286 */         getters = this._getterVisibility;
/*     */       }
/* 288 */       JsonAutoDetect.Visibility isGetters = overrides._isGetterVisibility;
/* 289 */       if (isGetters == JsonAutoDetect.Visibility.DEFAULT) {
/* 290 */         isGetters = this._isGetterVisibility;
/*     */       }
/* 292 */       JsonAutoDetect.Visibility setters = overrides._setterVisibility;
/* 293 */       if (setters == JsonAutoDetect.Visibility.DEFAULT) {
/* 294 */         setters = this._setterVisibility;
/*     */       }
/* 296 */       JsonAutoDetect.Visibility creators = overrides._creatorVisibility;
/* 297 */       if (creators == JsonAutoDetect.Visibility.DEFAULT) {
/* 298 */         creators = this._creatorVisibility;
/*     */       }
/* 300 */       return construct(fields, getters, isGetters, setters, creators);
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<JsonAutoDetect> valueFor() {
/* 305 */       return JsonAutoDetect.class;
/*     */     }
/*     */     
/* 308 */     public JsonAutoDetect.Visibility getFieldVisibility() { return this._fieldVisibility; }
/* 309 */     public JsonAutoDetect.Visibility getGetterVisibility() { return this._getterVisibility; }
/* 310 */     public JsonAutoDetect.Visibility getIsGetterVisibility() { return this._isGetterVisibility; }
/* 311 */     public JsonAutoDetect.Visibility getSetterVisibility() { return this._setterVisibility; } public JsonAutoDetect.Visibility getCreatorVisibility() {
/* 312 */       return this._creatorVisibility;
/*     */     }
/*     */     
/*     */     protected Object readResolve() {
/* 316 */       Value v = _predefined(this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility);
/*     */       
/* 318 */       return (v == null) ? this : v;
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 323 */       return String.format("JsonAutoDetect.Value(fields=%s,getters=%s,isGetters=%s,setters=%s,creators=%s)", new Object[] { this._fieldVisibility, this._getterVisibility, this._isGetterVisibility, this._setterVisibility, this._creatorVisibility });
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 331 */       return 1 + this._fieldVisibility.ordinal() ^ 3 * this._getterVisibility
/* 332 */         .ordinal() - 7 * this._isGetterVisibility
/* 333 */         .ordinal() + 11 * this._setterVisibility
/* 334 */         .ordinal() ^ 13 * this._creatorVisibility
/* 335 */         .ordinal();
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 341 */       if (o == this) return true; 
/* 342 */       if (o == null) return false; 
/* 343 */       return (o.getClass() == getClass() && _equals(this, (Value)o));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static Value _predefined(JsonAutoDetect.Visibility fields, JsonAutoDetect.Visibility getters, JsonAutoDetect.Visibility isGetters, JsonAutoDetect.Visibility setters, JsonAutoDetect.Visibility creators) {
/* 350 */       if (fields == DEFAULT_FIELD_VISIBILITY) {
/* 351 */         if (getters == DEFAULT._getterVisibility && isGetters == DEFAULT._isGetterVisibility && setters == DEFAULT._setterVisibility && creators == DEFAULT._creatorVisibility)
/*     */         {
/*     */ 
/*     */           
/* 355 */           return DEFAULT;
/*     */         }
/* 357 */       } else if (fields == JsonAutoDetect.Visibility.DEFAULT && 
/* 358 */         getters == JsonAutoDetect.Visibility.DEFAULT && isGetters == JsonAutoDetect.Visibility.DEFAULT && setters == JsonAutoDetect.Visibility.DEFAULT && creators == JsonAutoDetect.Visibility.DEFAULT) {
/*     */ 
/*     */ 
/*     */         
/* 362 */         return NO_OVERRIDES;
/*     */       } 
/*     */       
/* 365 */       return null;
/*     */     }
/*     */ 
/*     */     
/*     */     private static boolean _equals(Value a, Value b) {
/* 370 */       return (a._fieldVisibility == b._fieldVisibility && a._getterVisibility == b._getterVisibility && a._isGetterVisibility == b._isGetterVisibility && a._setterVisibility == b._setterVisibility && a._creatorVisibility == b._creatorVisibility);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JsonAutoDetect.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */