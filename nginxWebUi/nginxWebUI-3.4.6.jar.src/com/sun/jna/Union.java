/*     */ package com.sun.jna;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Union
/*     */   extends Structure
/*     */ {
/*     */   private Structure.StructField activeField;
/*     */   
/*     */   protected Union() {}
/*     */   
/*     */   protected Union(Pointer p) {
/*  49 */     super(p);
/*     */   }
/*     */   
/*     */   protected Union(Pointer p, int alignType) {
/*  53 */     super(p, alignType);
/*     */   }
/*     */   
/*     */   protected Union(TypeMapper mapper) {
/*  57 */     super(mapper);
/*     */   }
/*     */   
/*     */   protected Union(Pointer p, int alignType, TypeMapper mapper) {
/*  61 */     super(p, alignType, mapper);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected List<String> getFieldOrder() {
/*  69 */     List<Field> flist = getFieldList();
/*  70 */     List<String> list = new ArrayList<String>(flist.size());
/*  71 */     for (Field f : flist) {
/*  72 */       list.add(f.getName());
/*     */     }
/*  74 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(Class<?> type) {
/*  85 */     ensureAllocated();
/*  86 */     for (Structure.StructField f : fields().values()) {
/*  87 */       if (f.type == type) {
/*  88 */         this.activeField = f;
/*     */         return;
/*     */       } 
/*     */     } 
/*  92 */     throw new IllegalArgumentException("No field of type " + type + " in " + this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setType(String fieldName) {
/* 102 */     ensureAllocated();
/* 103 */     Structure.StructField f = fields().get(fieldName);
/* 104 */     if (f != null) {
/* 105 */       this.activeField = f;
/*     */     } else {
/*     */       
/* 108 */       throw new IllegalArgumentException("No field named " + fieldName + " in " + this);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object readField(String fieldName) {
/* 119 */     ensureAllocated();
/* 120 */     setType(fieldName);
/* 121 */     return super.readField(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeField(String fieldName) {
/* 130 */     ensureAllocated();
/* 131 */     setType(fieldName);
/* 132 */     super.writeField(fieldName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeField(String fieldName, Object value) {
/* 141 */     ensureAllocated();
/* 142 */     setType(fieldName);
/* 143 */     super.writeField(fieldName, value);
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
/*     */   public Object getTypedValue(Class<?> type) {
/* 159 */     ensureAllocated();
/* 160 */     for (Structure.StructField f : fields().values()) {
/* 161 */       if (f.type == type) {
/* 162 */         this.activeField = f;
/* 163 */         read();
/* 164 */         return getFieldValue(this.activeField.field);
/*     */       } 
/*     */     } 
/* 167 */     throw new IllegalArgumentException("No field of type " + type + " in " + this);
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
/*     */   public Object setTypedValue(Object object) {
/* 181 */     Structure.StructField f = findField(object.getClass());
/* 182 */     if (f != null) {
/* 183 */       this.activeField = f;
/* 184 */       setFieldValue(f.field, object);
/* 185 */       return this;
/*     */     } 
/* 187 */     throw new IllegalArgumentException("No field of type " + object.getClass() + " in " + this);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Structure.StructField findField(Class<?> type) {
/* 196 */     ensureAllocated();
/* 197 */     for (Structure.StructField f : fields().values()) {
/* 198 */       if (f.type.isAssignableFrom(type)) {
/* 199 */         return f;
/*     */       }
/*     */     } 
/* 202 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected void writeField(Structure.StructField field) {
/* 208 */     if (field == this.activeField) {
/* 209 */       super.writeField(field);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected Object readField(Structure.StructField field) {
/* 219 */     if (field == this.activeField || (
/* 220 */       !Structure.class.isAssignableFrom(field.type) && 
/* 221 */       !String.class.isAssignableFrom(field.type) && 
/* 222 */       !WString.class.isAssignableFrom(field.type))) {
/* 223 */       return super.readField(field);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 229 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   protected int getNativeAlignment(Class<?> type, Object value, boolean isFirstElement) {
/* 235 */     return super.getNativeAlignment(type, value, true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\Union.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */