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
/*     */ @Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
/*     */ @Retention(RetentionPolicy.RUNTIME)
/*     */ @JacksonAnnotation
/*     */ public @interface JacksonInject
/*     */ {
/*     */   String value() default "";
/*     */   
/*     */   OptBoolean useInput() default OptBoolean.DEFAULT;
/*     */   
/*     */   public static class Value
/*     */     implements JacksonAnnotationValue<JacksonInject>, Serializable
/*     */   {
/*     */     private static final long serialVersionUID = 1L;
/*  66 */     protected static final Value EMPTY = new Value(null, null);
/*     */ 
/*     */     
/*     */     protected final Object _id;
/*     */ 
/*     */     
/*     */     protected final Boolean _useInput;
/*     */ 
/*     */ 
/*     */     
/*     */     protected Value(Object id, Boolean useInput) {
/*  77 */       this._id = id;
/*  78 */       this._useInput = useInput;
/*     */     }
/*     */ 
/*     */     
/*     */     public Class<JacksonInject> valueFor() {
/*  83 */       return JacksonInject.class;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public static Value empty() {
/*  93 */       return EMPTY;
/*     */     }
/*     */     
/*     */     public static Value construct(Object id, Boolean useInput) {
/*  97 */       if ("".equals(id)) {
/*  98 */         id = null;
/*     */       }
/* 100 */       if (_empty(id, useInput)) {
/* 101 */         return EMPTY;
/*     */       }
/* 103 */       return new Value(id, useInput);
/*     */     }
/*     */     
/*     */     public static Value from(JacksonInject src) {
/* 107 */       if (src == null) {
/* 108 */         return EMPTY;
/*     */       }
/* 110 */       return construct(src.value(), src.useInput().asBoolean());
/*     */     }
/*     */     
/*     */     public static Value forId(Object id) {
/* 114 */       return construct(id, null);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Value withId(Object id) {
/* 124 */       if (id == null) {
/* 125 */         if (this._id == null) {
/* 126 */           return this;
/*     */         }
/* 128 */       } else if (id.equals(this._id)) {
/* 129 */         return this;
/*     */       } 
/* 131 */       return new Value(id, this._useInput);
/*     */     }
/*     */     
/*     */     public Value withUseInput(Boolean useInput) {
/* 135 */       if (useInput == null) {
/* 136 */         if (this._useInput == null) {
/* 137 */           return this;
/*     */         }
/* 139 */       } else if (useInput.equals(this._useInput)) {
/* 140 */         return this;
/*     */       } 
/* 142 */       return new Value(this._id, useInput);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Object getId() {
/* 151 */       return this._id; } public Boolean getUseInput() {
/* 152 */       return this._useInput;
/*     */     }
/*     */     public boolean hasId() {
/* 155 */       return (this._id != null);
/*     */     }
/*     */     
/*     */     public boolean willUseInput(boolean defaultSetting) {
/* 159 */       return (this._useInput == null) ? defaultSetting : this._useInput.booleanValue();
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
/* 170 */       return String.format("JacksonInject.Value(id=%s,useInput=%s)", new Object[] { this._id, this._useInput });
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public int hashCode() {
/* 176 */       int h = 1;
/* 177 */       if (this._id != null) {
/* 178 */         h += this._id.hashCode();
/*     */       }
/* 180 */       if (this._useInput != null) {
/* 181 */         h += this._useInput.hashCode();
/*     */       }
/* 183 */       return h;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean equals(Object o) {
/* 188 */       if (o == this) return true; 
/* 189 */       if (o == null) return false; 
/* 190 */       if (o.getClass() == getClass()) {
/* 191 */         Value other = (Value)o;
/* 192 */         if (OptBoolean.equals(this._useInput, other._useInput)) {
/* 193 */           if (this._id == null) {
/* 194 */             return (other._id == null);
/*     */           }
/* 196 */           return this._id.equals(other._id);
/*     */         } 
/*     */       } 
/* 199 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private static boolean _empty(Object id, Boolean useInput) {
/* 209 */       return (id == null && useInput == null);
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\fasterxml\jackson\annotation\JacksonInject.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */