/*     */ package org.noear.snack.core.exts;
/*     */ 
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ import org.noear.snack.annotation.NodeName;
/*     */ import org.noear.snack.annotation.ONodeAttr;
/*     */ import org.noear.snack.core.utils.StringUtil;
/*     */ import org.noear.snack.exception.SnackException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FieldWrap
/*     */ {
/*     */   public final Field field;
/*     */   public final Class<?> type;
/*     */   public final Type genericType;
/*     */   public final boolean readonly;
/*     */   private String name;
/*     */   private String format;
/*     */   private boolean serialize = true;
/*     */   private boolean deserialize = true;
/*     */   private boolean incNull = true;
/*     */   private Method _setter;
/*     */   
/*     */   public FieldWrap(Class<?> clz, Field f, boolean isFinal) {
/*  33 */     this.field = f;
/*  34 */     this.type = f.getType();
/*  35 */     this.genericType = f.getGenericType();
/*  36 */     this.readonly = isFinal;
/*     */     
/*  38 */     this.field.setAccessible(true);
/*     */     
/*  40 */     NodeName anno = f.<NodeName>getAnnotation(NodeName.class);
/*  41 */     if (anno != null) {
/*  42 */       this.name = anno.value();
/*     */     }
/*     */     
/*  45 */     ONodeAttr attr = f.<ONodeAttr>getAnnotation(ONodeAttr.class);
/*  46 */     if (attr != null) {
/*  47 */       this.name = attr.name();
/*  48 */       this.format = attr.format();
/*  49 */       this.incNull = attr.incNull();
/*     */ 
/*     */       
/*  52 */       if (attr.ignore()) {
/*  53 */         this.serialize = false;
/*  54 */         this.deserialize = false;
/*     */       } else {
/*  56 */         this.serialize = attr.serialize();
/*  57 */         this.deserialize = attr.deserialize();
/*     */       } 
/*     */     } 
/*     */     
/*  61 */     if (StringUtil.isEmpty(this.name)) {
/*  62 */       this.name = this.field.getName();
/*     */     }
/*     */     
/*  65 */     this._setter = doFindSetter(clz, f);
/*     */   }
/*     */   
/*     */   @Deprecated
/*     */   public String name() {
/*  70 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/*  77 */     return this.name;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFormat() {
/*  84 */     return this.format;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDeserialize() {
/*  91 */     return this.deserialize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isSerialize() {
/*  98 */     return this.serialize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isIncNull() {
/* 105 */     return this.incNull;
/*     */   }
/*     */ 
/*     */   
/*     */   public void setValue(Object tObj, Object val) {
/* 110 */     setValue(tObj, val, true);
/*     */   }
/*     */   
/*     */   public void setValue(Object tObj, Object val, boolean disFun) {
/* 114 */     if (this.readonly) {
/*     */       return;
/*     */     }
/*     */     
/*     */     try {
/* 119 */       if (this._setter == null || disFun) {
/* 120 */         this.field.set(tObj, val);
/*     */       } else {
/* 122 */         this._setter.invoke(tObj, new Object[] { val });
/*     */       } 
/* 124 */     } catch (IllegalArgumentException ex) {
/* 125 */       if (val == null) {
/* 126 */         throw new IllegalArgumentException(this.field.getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failur!", ex);
/*     */       }
/*     */       
/* 129 */       throw new IllegalArgumentException(this.field
/* 130 */           .getName() + "(" + this.field.getType().getSimpleName() + ") Type receive failure ：val(" + val
/* 131 */           .getClass().getSimpleName() + ")", ex);
/* 132 */     } catch (IllegalAccessException e) {
/* 133 */       throw new SnackException(e);
/* 134 */     } catch (RuntimeException e) {
/* 135 */       throw e;
/* 136 */     } catch (Throwable e) {
/* 137 */       throw new RuntimeException(e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public Object getValue(Object tObj) {
/*     */     try {
/* 143 */       return this.field.get(tObj);
/* 144 */     } catch (IllegalAccessException ex) {
/* 145 */       throw new SnackException(ex);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Method doFindSetter(Class<?> tCls, Field field) {
/* 153 */     String fieldName = field.getName();
/* 154 */     String firstLetter = fieldName.substring(0, 1).toUpperCase();
/* 155 */     String setMethodName = "set" + firstLetter + fieldName.substring(1);
/*     */     
/*     */     try {
/* 158 */       Method setFun = tCls.getMethod(setMethodName, new Class[] { field.getType() });
/* 159 */       if (setFun != null) {
/* 160 */         return setFun;
/*     */       }
/* 162 */     } catch (NoSuchMethodException noSuchMethodException) {
/*     */     
/* 164 */     } catch (RuntimeException e) {
/* 165 */       throw e;
/* 166 */     } catch (Throwable e) {
/* 167 */       throw new RuntimeException(e);
/*     */     } 
/* 169 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\core\exts\FieldWrap.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */