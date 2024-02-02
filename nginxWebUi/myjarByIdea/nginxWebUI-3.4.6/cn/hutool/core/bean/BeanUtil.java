package cn.hutool.core.bean;

import cn.hutool.core.bean.copier.BeanCopier;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.bean.copier.ValueProvider;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Editor;
import cn.hutool.core.map.CaseInsensitiveMap;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ModifierUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BeanUtil {
   public static boolean isReadableBean(Class<?> clazz) {
      return hasGetter(clazz) || hasPublicField(clazz);
   }

   public static boolean isBean(Class<?> clazz) {
      return hasSetter(clazz) || hasPublicField(clazz);
   }

   public static boolean hasSetter(Class<?> clazz) {
      if (ClassUtil.isNormalClass(clazz)) {
         Method[] var1 = clazz.getMethods();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            if (method.getParameterCount() == 1 && method.getName().startsWith("set")) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasGetter(Class<?> clazz) {
      if (ClassUtil.isNormalClass(clazz)) {
         Method[] var1 = clazz.getMethods();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Method method = var1[var3];
            if (method.getParameterCount() == 0 && (method.getName().startsWith("get") || method.getName().startsWith("is"))) {
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasPublicField(Class<?> clazz) {
      if (ClassUtil.isNormalClass(clazz)) {
         Field[] var1 = clazz.getFields();
         int var2 = var1.length;

         for(int var3 = 0; var3 < var2; ++var3) {
            Field field = var1[var3];
            if (ModifierUtil.isPublic(field) && !ModifierUtil.isStatic(field)) {
               return true;
            }
         }
      }

      return false;
   }

   public static DynaBean createDynaBean(Object bean) {
      return new DynaBean(bean);
   }

   public static PropertyEditor findEditor(Class<?> type) {
      return PropertyEditorManager.findEditor(type);
   }

   public static BeanDesc getBeanDesc(Class<?> clazz) {
      return BeanDescCache.INSTANCE.getBeanDesc(clazz, () -> {
         return new BeanDesc(clazz);
      });
   }

   public static void descForEach(Class<?> clazz, Consumer<? super PropDesc> action) {
      getBeanDesc(clazz).getProps().forEach(action);
   }

   public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeanException {
      BeanInfo beanInfo;
      try {
         beanInfo = Introspector.getBeanInfo(clazz);
      } catch (IntrospectionException var3) {
         throw new BeanException(var3);
      }

      return (PropertyDescriptor[])ArrayUtil.filter(beanInfo.getPropertyDescriptors(), (t) -> {
         return !"class".equals(t.getName());
      });
   }

   public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
      return BeanInfoCache.INSTANCE.getPropertyDescriptorMap(clazz, ignoreCase, () -> {
         return internalGetPropertyDescriptorMap(clazz, ignoreCase);
      });
   }

   private static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
      PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
      Map<String, PropertyDescriptor> map = ignoreCase ? new CaseInsensitiveMap(propertyDescriptors.length, 1.0F) : new HashMap(propertyDescriptors.length, 1.0F);
      PropertyDescriptor[] var4 = propertyDescriptors;
      int var5 = propertyDescriptors.length;

      for(int var6 = 0; var6 < var5; ++var6) {
         PropertyDescriptor propertyDescriptor = var4[var6];
         ((Map)map).put(propertyDescriptor.getName(), propertyDescriptor);
      }

      return (Map)map;
   }

   public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) throws BeanException {
      return getPropertyDescriptor(clazz, fieldName, false);
   }

   public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName, boolean ignoreCase) throws BeanException {
      Map<String, PropertyDescriptor> map = getPropertyDescriptorMap(clazz, ignoreCase);
      return null == map ? null : (PropertyDescriptor)map.get(fieldName);
   }

   public static Object getFieldValue(Object bean, String fieldNameOrIndex) {
      if (null != bean && null != fieldNameOrIndex) {
         if (bean instanceof Map) {
            return ((Map)bean).get(fieldNameOrIndex);
         } else if (bean instanceof Collection) {
            try {
               return CollUtil.get((Collection)bean, Integer.parseInt(fieldNameOrIndex));
            } catch (NumberFormatException var3) {
               return CollUtil.map((Collection)bean, (beanEle) -> {
                  return getFieldValue(beanEle, fieldNameOrIndex);
               }, false);
            }
         } else if (ArrayUtil.isArray(bean)) {
            try {
               return ArrayUtil.get(bean, Integer.parseInt(fieldNameOrIndex));
            } catch (NumberFormatException var4) {
               return ArrayUtil.map(bean, Object.class, (beanEle) -> {
                  return getFieldValue(beanEle, fieldNameOrIndex);
               });
            }
         } else {
            return ReflectUtil.getFieldValue(bean, fieldNameOrIndex);
         }
      } else {
         return null;
      }
   }

   public static void setFieldValue(Object bean, String fieldNameOrIndex, Object value) {
      if (bean instanceof Map) {
         ((Map)bean).put(fieldNameOrIndex, value);
      } else if (bean instanceof List) {
         CollUtil.setOrAppend((List)bean, Convert.toInt(fieldNameOrIndex), value);
      } else if (ArrayUtil.isArray(bean)) {
         ArrayUtil.setOrAppend(bean, Convert.toInt(fieldNameOrIndex), value);
      } else {
         ReflectUtil.setFieldValue(bean, fieldNameOrIndex, value);
      }

   }

   public static <T> T getProperty(Object bean, String expression) {
      return null != bean && !StrUtil.isBlank(expression) ? BeanPath.create(expression).get(bean) : null;
   }

   public static void setProperty(Object bean, String expression, Object value) {
      BeanPath.create(expression).set(bean, value);
   }

   /** @deprecated */
   @Deprecated
   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
      return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
   }

   /** @deprecated */
   @Deprecated
   public static <T> T mapToBeanIgnoreCase(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
      return fillBeanWithMapIgnoreCase(map, ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
   }

   /** @deprecated */
   @Deprecated
   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, CopyOptions copyOptions) {
      return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), copyOptions);
   }

   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isToCamelCase, CopyOptions copyOptions) {
      return fillBeanWithMap(map, ReflectUtil.newInstanceIfPossible(beanClass), isToCamelCase, copyOptions);
   }

   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isIgnoreError) {
      return fillBeanWithMap(map, bean, false, isIgnoreError);
   }

   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, boolean isIgnoreError) {
      return fillBeanWithMap(map, bean, isToCamelCase, CopyOptions.create().setIgnoreError(isIgnoreError));
   }

   public static <T> T fillBeanWithMapIgnoreCase(Map<?, ?> map, T bean, boolean isIgnoreError) {
      return fillBeanWithMap(map, bean, CopyOptions.create().setIgnoreCase(true).setIgnoreError(isIgnoreError));
   }

   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, CopyOptions copyOptions) {
      return fillBeanWithMap(map, bean, false, copyOptions);
   }

   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, CopyOptions copyOptions) {
      if (MapUtil.isEmpty(map)) {
         return bean;
      } else {
         if (isToCamelCase) {
            map = MapUtil.toCamelCaseMap(map);
         }

         copyProperties(map, (Object)bean, (CopyOptions)copyOptions);
         return bean;
      }
   }

   public static <T> T toBean(Object source, Class<T> clazz) {
      return toBean((Object)source, (Class)clazz, (CopyOptions)null);
   }

   public static <T> T toBeanIgnoreError(Object source, Class<T> clazz) {
      return toBean(source, clazz, CopyOptions.create().setIgnoreError(true));
   }

   public static <T> T toBeanIgnoreCase(Object source, Class<T> clazz, boolean ignoreError) {
      return toBean(source, clazz, CopyOptions.create().setIgnoreCase(true).setIgnoreError(ignoreError));
   }

   public static <T> T toBean(Object source, Class<T> clazz, CopyOptions options) {
      return toBean(source, () -> {
         return ReflectUtil.newInstanceIfPossible(clazz);
      }, options);
   }

   public static <T> T toBean(Object source, Supplier<T> targetSupplier, CopyOptions options) {
      if (null != source && null != targetSupplier) {
         T target = targetSupplier.get();
         copyProperties(source, target, options);
         return target;
      } else {
         return null;
      }
   }

   public static <T> T toBean(Class<T> beanClass, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
      return null != beanClass && null != valueProvider ? fillBean(ReflectUtil.newInstanceIfPossible(beanClass), valueProvider, copyOptions) : null;
   }

   public static <T> T fillBean(T bean, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
      return null == valueProvider ? bean : BeanCopier.create(valueProvider, bean, copyOptions).copy();
   }

   public static Map<String, Object> beanToMap(Object bean, String... properties) {
      int mapSize = 16;
      Editor<String> keyEditor = null;
      if (ArrayUtil.isNotEmpty((Object[])properties)) {
         mapSize = properties.length;
         Set<String> propertiesSet = CollUtil.set(false, properties);
         keyEditor = (property) -> {
            return propertiesSet.contains(property) ? property : null;
         };
      }

      return beanToMap(bean, new LinkedHashMap(mapSize, 1.0F), false, keyEditor);
   }

   public static Map<String, Object> beanToMap(Object bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
      return null == bean ? null : beanToMap(bean, new LinkedHashMap(), isToUnderlineCase, ignoreNullValue);
   }

   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean isToUnderlineCase, boolean ignoreNullValue) {
      return null == bean ? null : beanToMap(bean, targetMap, ignoreNullValue, (key) -> {
         return isToUnderlineCase ? StrUtil.toUnderlineCase(key) : key;
      });
   }

   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNullValue, Editor<String> keyEditor) {
      return null == bean ? null : (Map)BeanCopier.create(bean, targetMap, CopyOptions.create().setIgnoreNullValue(ignoreNullValue).setFieldNameEditor(keyEditor)).copy();
   }

   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, CopyOptions copyOptions) {
      return null == bean ? null : (Map)BeanCopier.create(bean, targetMap, copyOptions).copy();
   }

   public static <T> T copyProperties(Object source, Class<T> tClass, String... ignoreProperties) {
      if (null == source) {
         return null;
      } else {
         T target = ReflectUtil.newInstanceIfPossible(tClass);
         copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
         return target;
      }
   }

   public static void copyProperties(Object source, Object target, String... ignoreProperties) {
      copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
   }

   public static void copyProperties(Object source, Object target, boolean ignoreCase) {
      BeanCopier.create(source, target, CopyOptions.create().setIgnoreCase(ignoreCase)).copy();
   }

   public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
      if (null != source) {
         BeanCopier.create(source, target, (CopyOptions)ObjectUtil.defaultIfNull(copyOptions, (Supplier)(CopyOptions::create))).copy();
      }
   }

   public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType, CopyOptions copyOptions) {
      if (null == collection) {
         return null;
      } else {
         return (List)(collection.isEmpty() ? new ArrayList(0) : (List)collection.stream().map((source) -> {
            T target = ReflectUtil.newInstanceIfPossible(targetType);
            copyProperties(source, target, copyOptions);
            return target;
         }).collect(Collectors.toList()));
      }
   }

   public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
      return copyToList(collection, targetType, CopyOptions.create());
   }

   public static boolean isMatchName(Object bean, String beanClassName, boolean isSimple) {
      return null != bean && !StrUtil.isBlank(beanClassName) ? ClassUtil.getClassName(bean, isSimple).equals(isSimple ? StrUtil.upperFirst(beanClassName) : beanClassName) : false;
   }

   public static <T> T edit(T bean, Editor<Field> editor) {
      if (bean == null) {
         return null;
      } else {
         Field[] fields = ReflectUtil.getFields(bean.getClass());
         Field[] var3 = fields;
         int var4 = fields.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Field field = var3[var5];
            if (!ModifierUtil.isStatic(field)) {
               editor.edit(field);
            }
         }

         return bean;
      }
   }

   public static <T> T trimStrFields(T bean, String... ignoreFields) {
      return edit(bean, (field) -> {
         if (ignoreFields != null && ArrayUtil.containsIgnoreCase(ignoreFields, field.getName())) {
            return field;
         } else {
            if (String.class.equals(field.getType())) {
               String val = (String)ReflectUtil.getFieldValue(bean, field);
               if (null != val) {
                  String trimVal = StrUtil.trim(val);
                  if (!val.equals(trimVal)) {
                     ReflectUtil.setFieldValue(bean, (Field)field, trimVal);
                  }
               }
            }

            return field;
         }
      });
   }

   public static boolean isNotEmpty(Object bean, String... ignoreFiledNames) {
      return !isEmpty(bean, ignoreFiledNames);
   }

   public static boolean isEmpty(Object bean, String... ignoreFiledNames) {
      if (null != bean) {
         Field[] var2 = ReflectUtil.getFields(bean.getClass());
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            if (!ModifierUtil.isStatic(field) && !ArrayUtil.contains(ignoreFiledNames, field.getName()) && null != ReflectUtil.getFieldValue(bean, field)) {
               return false;
            }
         }
      }

      return true;
   }

   public static boolean hasNullField(Object bean, String... ignoreFiledNames) {
      if (null == bean) {
         return true;
      } else {
         Field[] var2 = ReflectUtil.getFields(bean.getClass());
         int var3 = var2.length;

         for(int var4 = 0; var4 < var3; ++var4) {
            Field field = var2[var4];
            if (!ModifierUtil.isStatic(field) && !ArrayUtil.contains(ignoreFiledNames, field.getName()) && null == ReflectUtil.getFieldValue(bean, field)) {
               return true;
            }
         }

         return false;
      }
   }

   public static String getFieldName(String getterOrSetterName) {
      if (!getterOrSetterName.startsWith("get") && !getterOrSetterName.startsWith("set")) {
         if (getterOrSetterName.startsWith("is")) {
            return StrUtil.removePreAndLowerFirst(getterOrSetterName, 2);
         } else {
            throw new IllegalArgumentException("Invalid Getter or Setter name: " + getterOrSetterName);
         }
      } else {
         return StrUtil.removePreAndLowerFirst(getterOrSetterName, 3);
      }
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$getPropertyDescriptorMap$58f3b7cb$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/bean/BeanUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;Z)Ljava/util/Map;")) {
               return () -> {
                  return internalGetPropertyDescriptorMap(clazz, ignoreCase);
               };
            }
            break;
         case "lambda$getBeanDesc$e7c7684d$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/bean/BeanUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)Lcn/hutool/core/bean/BeanDesc;")) {
               return () -> {
                  return new BeanDesc(clazz);
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
