package cn.hutool.core.bean;

import cn.hutool.core.lang.Assert;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public class BeanDesc implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Class<?> beanClass;
   private final Map<String, PropDesc> propMap = new LinkedHashMap();

   public BeanDesc(Class<?> beanClass) {
      Assert.notNull(beanClass);
      this.beanClass = beanClass;
      this.init();
   }

   public String getName() {
      return this.beanClass.getName();
   }

   public String getSimpleName() {
      return this.beanClass.getSimpleName();
   }

   public Map<String, PropDesc> getPropMap(boolean ignoreCase) {
      return (Map)(ignoreCase ? new CaseInsensitiveMap(1.0F, this.propMap) : this.propMap);
   }

   public Collection<PropDesc> getProps() {
      return this.propMap.values();
   }

   public PropDesc getProp(String fieldName) {
      return (PropDesc)this.propMap.get(fieldName);
   }

   public Field getField(String fieldName) {
      PropDesc desc = (PropDesc)this.propMap.get(fieldName);
      return null == desc ? null : desc.getField();
   }

   public Method getGetter(String fieldName) {
      PropDesc desc = (PropDesc)this.propMap.get(fieldName);
      return null == desc ? null : desc.getGetter();
   }

   public Method getSetter(String fieldName) {
      PropDesc desc = (PropDesc)this.propMap.get(fieldName);
      return null == desc ? null : desc.getSetter();
   }

   private BeanDesc init() {
      Method[] gettersAndSetters = ReflectUtil.getMethods(this.beanClass, ReflectUtil::isGetterOrSetterIgnoreCase);
      Field[] var3 = ReflectUtil.getFields(this.beanClass);
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         if (!ModifierUtil.isStatic(field) && !ReflectUtil.isOuterClassField(field)) {
            PropDesc prop = this.createProp(field, gettersAndSetters);
            this.propMap.putIfAbsent(prop.getFieldName(), prop);
         }
      }

      return this;
   }

   private PropDesc createProp(Field field, Method[] methods) {
      PropDesc prop = this.findProp(field, methods, false);
      if (null == prop.getter || null == prop.setter) {
         PropDesc propIgnoreCase = this.findProp(field, methods, true);
         if (null == prop.getter) {
            prop.getter = propIgnoreCase.getter;
         }

         if (null == prop.setter) {
            prop.setter = propIgnoreCase.setter;
         }
      }

      return prop;
   }

   private PropDesc findProp(Field field, Method[] gettersOrSetters, boolean ignoreCase) {
      String fieldName = field.getName();
      Class<?> fieldType = field.getType();
      boolean isBooleanField = BooleanUtil.isBoolean(fieldType);
      Method getter = null;
      Method setter = null;
      Method[] var10 = gettersOrSetters;
      int var11 = gettersOrSetters.length;

      for(int var12 = 0; var12 < var11; ++var12) {
         Method method = var10[var12];
         String methodName = method.getName();
         if (method.getParameterCount() == 0) {
            if (this.isMatchGetter(methodName, fieldName, isBooleanField, ignoreCase)) {
               getter = method;
            }
         } else if (this.isMatchSetter(methodName, fieldName, isBooleanField, ignoreCase) && fieldType.isAssignableFrom(method.getParameterTypes()[0])) {
            setter = method;
         }

         if (null != getter && null != setter) {
            break;
         }
      }

      return new PropDesc(field, getter, setter);
   }

   private boolean isMatchGetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
      String handledFieldName;
      if (ignoreCase) {
         methodName = methodName.toLowerCase();
         handledFieldName = fieldName.toLowerCase();
         fieldName = handledFieldName;
      } else {
         handledFieldName = StrUtil.upperFirst(fieldName);
      }

      if (isBooleanField) {
         if (fieldName.startsWith("is")) {
            if (methodName.equals(fieldName) || ("get" + handledFieldName).equals(methodName) || ("is" + handledFieldName).equals(methodName)) {
               return true;
            }
         } else if (("is" + handledFieldName).equals(methodName)) {
            return true;
         }
      }

      return ("get" + handledFieldName).equals(methodName);
   }

   private boolean isMatchSetter(String methodName, String fieldName, boolean isBooleanField, boolean ignoreCase) {
      String handledFieldName;
      if (ignoreCase) {
         methodName = methodName.toLowerCase();
         handledFieldName = fieldName.toLowerCase();
         fieldName = handledFieldName;
      } else {
         handledFieldName = StrUtil.upperFirst(fieldName);
      }

      if (!methodName.startsWith("set")) {
         return false;
      } else {
         return !isBooleanField || !fieldName.startsWith("is") || !("set" + StrUtil.removePrefix(fieldName, "is")).equals(methodName) && !("set" + handledFieldName).equals(methodName) ? ("set" + handledFieldName).equals(methodName) : true;
      }
   }
}
