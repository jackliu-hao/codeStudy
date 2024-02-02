package cn.hutool.core.bean;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.annotation.PropIgnore;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.TypeUtil;
import java.beans.Transient;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

public class PropDesc {
   final Field field;
   protected Method getter;
   protected Method setter;

   public PropDesc(Field field, Method getter, Method setter) {
      this.field = field;
      this.getter = ClassUtil.setAccessible(getter);
      this.setter = ClassUtil.setAccessible(setter);
   }

   public String getFieldName() {
      return ReflectUtil.getFieldName(this.field);
   }

   public String getRawFieldName() {
      return null == this.field ? null : this.field.getName();
   }

   public Field getField() {
      return this.field;
   }

   public Type getFieldType() {
      return null != this.field ? TypeUtil.getType(this.field) : this.findPropType(this.getter, this.setter);
   }

   public Class<?> getFieldClass() {
      return null != this.field ? TypeUtil.getClass(this.field) : this.findPropClass(this.getter, this.setter);
   }

   public Method getGetter() {
      return this.getter;
   }

   public Method getSetter() {
      return this.setter;
   }

   public boolean isReadable(boolean checkTransient) {
      if (null == this.getter && !ModifierUtil.isPublic(this.field)) {
         return false;
      } else if (checkTransient && this.isTransientForGet()) {
         return false;
      } else {
         return !this.isIgnoreGet();
      }
   }

   public Object getValue(Object bean) {
      if (null != this.getter) {
         return ReflectUtil.invoke(bean, this.getter);
      } else {
         return ModifierUtil.isPublic(this.field) ? ReflectUtil.getFieldValue(bean, this.field) : null;
      }
   }

   public Object getValue(Object bean, Type targetType, boolean ignoreError) {
      Object result = null;

      try {
         result = this.getValue(bean);
      } catch (Exception var6) {
         if (!ignoreError) {
            throw new BeanException(var6, "Get value of [{}] error!", new Object[]{this.getFieldName()});
         }
      }

      return null != result && null != targetType ? Convert.convertWithCheck(targetType, result, (Object)null, ignoreError) : result;
   }

   public boolean isWritable(boolean checkTransient) {
      if (null == this.setter && !ModifierUtil.isPublic(this.field)) {
         return false;
      } else if (checkTransient && this.isTransientForSet()) {
         return false;
      } else {
         return !this.isIgnoreSet();
      }
   }

   public PropDesc setValue(Object bean, Object value) {
      if (null != this.setter) {
         ReflectUtil.invoke(bean, this.setter, value);
      } else if (ModifierUtil.isPublic(this.field)) {
         ReflectUtil.setFieldValue(bean, this.field, value);
      }

      return this;
   }

   public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError) {
      return this.setValue(bean, value, ignoreNull, ignoreError, true);
   }

   public PropDesc setValue(Object bean, Object value, boolean ignoreNull, boolean ignoreError, boolean override) {
      if (null == value && ignoreNull) {
         return this;
      } else if (!override && null != this.getValue(bean)) {
         return this;
      } else {
         if (null != value) {
            Class<?> propClass = this.getFieldClass();
            if (!propClass.isInstance(value)) {
               value = Convert.convertWithCheck(propClass, value, (Object)null, ignoreError);
            }
         }

         if (null != value || !ignoreNull) {
            try {
               this.setValue(bean, value);
            } catch (Exception var7) {
               if (!ignoreError) {
                  throw new BeanException(var7, "Set value of [{}] error!", new Object[]{this.getFieldName()});
               }
            }
         }

         return this;
      }
   }

   private Type findPropType(Method getter, Method setter) {
      Type type = null;
      if (null != getter) {
         type = TypeUtil.getReturnType(getter);
      }

      if (null == type && null != setter) {
         type = TypeUtil.getParamType(setter, 0);
      }

      return type;
   }

   private Class<?> findPropClass(Method getter, Method setter) {
      Class<?> type = null;
      if (null != getter) {
         type = TypeUtil.getReturnClass(getter);
      }

      if (null == type && null != setter) {
         type = TypeUtil.getFirstParamClass(setter);
      }

      return type;
   }

   private boolean isIgnoreSet() {
      return AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || AnnotationUtil.hasAnnotation(this.setter, PropIgnore.class);
   }

   private boolean isIgnoreGet() {
      return AnnotationUtil.hasAnnotation(this.field, PropIgnore.class) || AnnotationUtil.hasAnnotation(this.getter, PropIgnore.class);
   }

   private boolean isTransientForGet() {
      boolean isTransient = ModifierUtil.hasModifier(this.field, ModifierUtil.ModifierType.TRANSIENT);
      if (!isTransient && null != this.getter) {
         isTransient = ModifierUtil.hasModifier(this.getter, ModifierUtil.ModifierType.TRANSIENT);
         if (!isTransient) {
            isTransient = AnnotationUtil.hasAnnotation(this.getter, Transient.class);
         }
      }

      return isTransient;
   }

   private boolean isTransientForSet() {
      boolean isTransient = ModifierUtil.hasModifier(this.field, ModifierUtil.ModifierType.TRANSIENT);
      if (!isTransient && null != this.setter) {
         isTransient = ModifierUtil.hasModifier(this.setter, ModifierUtil.ModifierType.TRANSIENT);
         if (!isTransient) {
            isTransient = AnnotationUtil.hasAnnotation(this.setter, Transient.class);
         }
      }

      return isTransient;
   }
}
