/*     */ package cn.hutool.core.bean;
/*     */ 
/*     */ import cn.hutool.core.annotation.AnnotationUtil;
/*     */ import cn.hutool.core.annotation.PropIgnore;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ModifierUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.TypeUtil;
/*     */ import java.beans.Transient;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.lang.reflect.Type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class PropDesc
/*     */ {
/*     */   final Field field;
/*     */   protected Method getter;
/*     */   protected Method setter;
/*     */   
/*     */   public PropDesc(Field field, Method getter, Method setter) {
/*  45 */     this.field = field;
/*  46 */     this.getter = ClassUtil.setAccessible(getter);
/*  47 */     this.setter = ClassUtil.setAccessible(setter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFieldName() {
/*  56 */     return ReflectUtil.getFieldName(this.field);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getRawFieldName() {
/*  66 */     return (null == this.field) ? null : this.field.getName();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Field getField() {
/*  75 */     return this.field;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Type getFieldType() {
/*  85 */     if (null != this.field) {
/*  86 */       return TypeUtil.getType(this.field);
/*     */     }
/*  88 */     return findPropType(this.getter, this.setter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Class<?> getFieldClass() {
/*  98 */     if (null != this.field) {
/*  99 */       return TypeUtil.getClass(this.field);
/*     */     }
/* 101 */     return findPropClass(this.getter, this.setter);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getGetter() {
/* 110 */     return this.getter;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Method getSetter() {
/* 119 */     return this.setter;
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
/*     */   public boolean isReadable(boolean checkTransient) {
/* 131 */     if (null == this.getter && false == ModifierUtil.isPublic(this.field)) {
/* 132 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 136 */     if (checkTransient && isTransientForGet()) {
/* 137 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 141 */     return (false == isIgnoreGet());
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
/*     */   public Object getValue(Object bean) {
/* 154 */     if (null != this.getter)
/* 155 */       return ReflectUtil.invoke(bean, this.getter, new Object[0]); 
/* 156 */     if (ModifierUtil.isPublic(this.field)) {
/* 157 */       return ReflectUtil.getFieldValue(bean, this.field);
/*     */     }
/*     */     
/* 160 */     return null;
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
/*     */   public Object getValue(Object bean, Type targetType, boolean ignoreError) {
/* 174 */     Object result = null;
/*     */     try {
/* 176 */       result = getValue(bean);
/* 177 */     } catch (Exception e) {
/* 178 */       if (false == ignoreError) {
/* 179 */         throw new BeanException(e, "Get value of [{}] error!", new Object[] { getFieldName() });
/*     */       }
/*     */     } 
/*     */     
/* 183 */     if (null != result && null != targetType)
/*     */     {
/*     */ 
/*     */       
/* 187 */       return Convert.convertWithCheck(targetType, result, null, ignoreError);
/*     */     }
/* 189 */     return result;
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
/*     */   public boolean isWritable(boolean checkTransient) {
/* 201 */     if (null == this.setter && false == ModifierUtil.isPublic(this.field)) {
/* 202 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 206 */     if (checkTransient && isTransientForSet()) {
/* 207 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 211 */     return (false == isIgnoreSet());
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
/*     */   public PropDesc setValue(Object bean, Object value) {
/* 225 */     if (null != this.setter) {
/* 226 */       ReflectUtil.invoke(bean, this.setter, new Object[] { value });
/* 227 */     } else if (ModifierUtil.isPublic(this.field)) {
/* 228 */       ReflectUtil.setFieldValue(bean, this.field, value);
/*     */     } 
/* 230 */     return this;
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
/*     */   public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError) {
/* 244 */     return setValue(bean, value, ignoreNull, ignoreError, true);
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
/*     */   public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError, boolean override) {
/* 259 */     if (null == value && ignoreNull) {
/* 260 */       return this;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 265 */     if (false == override && null != getValue(bean)) {
/* 266 */       return this;
/*     */     }
/*     */ 
/*     */     
/* 270 */     if (null != value) {
/* 271 */       Class<?> propClass = getFieldClass();
/* 272 */       if (false == propClass.isInstance(value)) {
/* 273 */         value = Convert.convertWithCheck(propClass, value, null, ignoreError);
/*     */       }
/*     */     } 
/*     */ 
/*     */     
/* 278 */     if (null != value || false == ignoreNull) {
/*     */       try {
/* 280 */         setValue(bean, value);
/* 281 */       } catch (Exception e) {
/* 282 */         if (false == ignoreError) {
/* 283 */           throw new BeanException(e, "Set value of [{}] error!", new Object[] { getFieldName() });
/*     */         }
/*     */       } 
/*     */     }
/*     */ 
/*     */     
/* 289 */     return this;
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
/*     */   private Type findPropType(Method getter, Method setter) {
/* 302 */     Type type = null;
/* 303 */     if (null != getter) {
/* 304 */       type = TypeUtil.getReturnType(getter);
/*     */     }
/* 306 */     if (null == type && null != setter) {
/* 307 */       type = TypeUtil.getParamType(setter, 0);
/*     */     }
/* 309 */     return type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Class<?> findPropClass(Method getter, Method setter) {
/* 320 */     Class<?> type = null;
/* 321 */     if (null != getter) {
/* 322 */       type = TypeUtil.getReturnClass(getter);
/*     */     }
/* 324 */     if (null == type && null != setter) {
/* 325 */       type = TypeUtil.getFirstParamClass(setter);
/*     */     }
/* 327 */     return type;
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
/*     */   private boolean isIgnoreSet() {
/* 341 */     return (AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || 
/* 342 */       AnnotationUtil.hasAnnotation(this.setter, PropIgnore.class));
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
/*     */   private boolean isIgnoreGet() {
/* 356 */     return (AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || 
/* 357 */       AnnotationUtil.hasAnnotation(this.getter, PropIgnore.class));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isTransientForGet() {
/* 367 */     boolean isTransient = ModifierUtil.hasModifier(this.field, new ModifierUtil.ModifierType[] { ModifierUtil.ModifierType.TRANSIENT });
/*     */ 
/*     */     
/* 370 */     if (false == isTransient && null != this.getter) {
/* 371 */       isTransient = ModifierUtil.hasModifier(this.getter, new ModifierUtil.ModifierType[] { ModifierUtil.ModifierType.TRANSIENT });
/*     */ 
/*     */       
/* 374 */       if (false == isTransient) {
/* 375 */         isTransient = AnnotationUtil.hasAnnotation(this.getter, Transient.class);
/*     */       }
/*     */     } 
/*     */     
/* 379 */     return isTransient;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isTransientForSet() {
/* 389 */     boolean isTransient = ModifierUtil.hasModifier(this.field, new ModifierUtil.ModifierType[] { ModifierUtil.ModifierType.TRANSIENT });
/*     */ 
/*     */     
/* 392 */     if (false == isTransient && null != this.setter) {
/* 393 */       isTransient = ModifierUtil.hasModifier(this.setter, new ModifierUtil.ModifierType[] { ModifierUtil.ModifierType.TRANSIENT });
/*     */ 
/*     */       
/* 396 */       if (false == isTransient) {
/* 397 */         isTransient = AnnotationUtil.hasAnnotation(this.setter, Transient.class);
/*     */       }
/*     */     } 
/*     */     
/* 401 */     return isTransient;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\PropDesc.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */