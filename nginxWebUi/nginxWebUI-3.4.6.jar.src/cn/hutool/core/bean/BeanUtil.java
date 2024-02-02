/*     */ package cn.hutool.core.bean;
/*     */ 
/*     */ import cn.hutool.core.bean.copier.BeanCopier;
/*     */ import cn.hutool.core.bean.copier.CopyOptions;
/*     */ import cn.hutool.core.bean.copier.ValueProvider;
/*     */ import cn.hutool.core.collection.CollUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.lang.Editor;
/*     */ import cn.hutool.core.map.CaseInsensitiveMap;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.ClassUtil;
/*     */ import cn.hutool.core.util.ModifierUtil;
/*     */ import cn.hutool.core.util.ObjectUtil;
/*     */ import cn.hutool.core.util.ReflectUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.beans.BeanInfo;
/*     */ import java.beans.IntrospectionException;
/*     */ import java.beans.Introspector;
/*     */ import java.beans.PropertyDescriptor;
/*     */ import java.beans.PropertyEditor;
/*     */ import java.beans.PropertyEditorManager;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.lang.reflect.Field;
/*     */ import java.lang.reflect.Method;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import java.util.function.Consumer;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Supplier;
/*     */ import java.util.stream.Collectors;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class BeanUtil
/*     */ {
/*     */   public static boolean isReadableBean(Class<?> clazz) {
/*  63 */     return (hasGetter(clazz) || hasPublicField(clazz));
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
/*     */   public static boolean isBean(Class<?> clazz) {
/*  80 */     return (hasSetter(clazz) || hasPublicField(clazz));
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
/*     */   public static boolean hasSetter(Class<?> clazz) {
/*  92 */     if (ClassUtil.isNormalClass(clazz)) {
/*  93 */       for (Method method : clazz.getMethods()) {
/*  94 */         if (method.getParameterCount() == 1 && method.getName().startsWith("set"))
/*     */         {
/*  96 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 100 */     return false;
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
/*     */   public static boolean hasGetter(Class<?> clazz) {
/* 112 */     if (ClassUtil.isNormalClass(clazz)) {
/* 113 */       for (Method method : clazz.getMethods()) {
/* 114 */         if (method.getParameterCount() == 0 && (
/* 115 */           method.getName().startsWith("get") || method.getName().startsWith("is"))) {
/* 116 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/* 121 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasPublicField(Class<?> clazz) {
/* 132 */     if (ClassUtil.isNormalClass(clazz)) {
/* 133 */       for (Field field : clazz.getFields()) {
/* 134 */         if (ModifierUtil.isPublic(field) && false == ModifierUtil.isStatic(field))
/*     */         {
/* 136 */           return true;
/*     */         }
/*     */       } 
/*     */     }
/* 140 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DynaBean createDynaBean(Object bean) {
/* 151 */     return new DynaBean(bean);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static PropertyEditor findEditor(Class<?> type) {
/* 161 */     return PropertyEditorManager.findEditor(type);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static BeanDesc getBeanDesc(Class<?> clazz) {
/* 172 */     return BeanDescCache.INSTANCE.getBeanDesc(clazz, () -> new BeanDesc(clazz));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void descForEach(Class<?> clazz, Consumer<? super PropDesc> action) {
/* 183 */     getBeanDesc(clazz).getProps().forEach(action);
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
/*     */   public static PropertyDescriptor[] getPropertyDescriptors(Class<?> clazz) throws BeanException {
/*     */     BeanInfo beanInfo;
/*     */     try {
/* 198 */       beanInfo = Introspector.getBeanInfo(clazz);
/* 199 */     } catch (IntrospectionException e) {
/* 200 */       throw new BeanException(e);
/*     */     } 
/* 202 */     return (PropertyDescriptor[])ArrayUtil.filter((Object[])beanInfo.getPropertyDescriptors(), t -> (false == "class".equals(t.getName())));
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
/*     */   public static Map<String, PropertyDescriptor> getPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
/* 217 */     return BeanInfoCache.INSTANCE.getPropertyDescriptorMap(clazz, ignoreCase, () -> internalGetPropertyDescriptorMap(clazz, ignoreCase));
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
/*     */   private static Map<String, PropertyDescriptor> internalGetPropertyDescriptorMap(Class<?> clazz, boolean ignoreCase) throws BeanException {
/* 229 */     PropertyDescriptor[] propertyDescriptors = getPropertyDescriptors(clazz);
/* 230 */     Map<String, PropertyDescriptor> map = ignoreCase ? (Map<String, PropertyDescriptor>)new CaseInsensitiveMap(propertyDescriptors.length, 1.0F) : new HashMap<>(propertyDescriptors.length, 1.0F);
/*     */ 
/*     */     
/* 233 */     for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
/* 234 */       map.put(propertyDescriptor.getName(), propertyDescriptor);
/*     */     }
/* 236 */     return map;
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
/*     */   public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName) throws BeanException {
/* 248 */     return getPropertyDescriptor(clazz, fieldName, false);
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
/*     */   public static PropertyDescriptor getPropertyDescriptor(Class<?> clazz, String fieldName, boolean ignoreCase) throws BeanException {
/* 261 */     Map<String, PropertyDescriptor> map = getPropertyDescriptorMap(clazz, ignoreCase);
/* 262 */     return (null == map) ? null : map.get(fieldName);
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
/*     */   public static Object getFieldValue(Object bean, String fieldNameOrIndex) {
/* 280 */     if (null == bean || null == fieldNameOrIndex) {
/* 281 */       return null;
/*     */     }
/*     */     
/* 284 */     if (bean instanceof Map)
/* 285 */       return ((Map)bean).get(fieldNameOrIndex); 
/* 286 */     if (bean instanceof Collection)
/*     */       try {
/* 288 */         return CollUtil.get((Collection)bean, Integer.parseInt(fieldNameOrIndex));
/* 289 */       } catch (NumberFormatException e) {
/*     */         
/* 291 */         return CollUtil.map((Collection)bean, beanEle -> getFieldValue(beanEle, fieldNameOrIndex), false);
/*     */       }  
/* 293 */     if (ArrayUtil.isArray(bean)) {
/*     */       try {
/* 295 */         return ArrayUtil.get(bean, Integer.parseInt(fieldNameOrIndex));
/* 296 */       } catch (NumberFormatException e) {
/*     */         
/* 298 */         return ArrayUtil.map(bean, Object.class, beanEle -> getFieldValue(beanEle, fieldNameOrIndex));
/*     */       } 
/*     */     }
/* 301 */     return ReflectUtil.getFieldValue(bean, fieldNameOrIndex);
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
/*     */   public static void setFieldValue(Object bean, String fieldNameOrIndex, Object value) {
/* 315 */     if (bean instanceof Map) {
/* 316 */       ((Map<String, Object>)bean).put(fieldNameOrIndex, value);
/* 317 */     } else if (bean instanceof List) {
/* 318 */       CollUtil.setOrAppend((List)bean, Convert.toInt(fieldNameOrIndex).intValue(), value);
/* 319 */     } else if (ArrayUtil.isArray(bean)) {
/* 320 */       ArrayUtil.setOrAppend(bean, Convert.toInt(fieldNameOrIndex).intValue(), value);
/*     */     } else {
/*     */       
/* 323 */       ReflectUtil.setFieldValue(bean, fieldNameOrIndex, value);
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
/*     */   public static <T> T getProperty(Object bean, String expression) {
/* 339 */     if (null == bean || StrUtil.isBlank(expression)) {
/* 340 */       return null;
/*     */     }
/* 342 */     return (T)BeanPath.create(expression).get(bean);
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
/*     */   public static void setProperty(Object bean, String expression, Object value) {
/* 355 */     BeanPath.create(expression).set(bean, value);
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
/*     */   @Deprecated
/*     */   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
/* 372 */     return fillBeanWithMap(map, (T)ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
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
/*     */   @Deprecated
/*     */   public static <T> T mapToBeanIgnoreCase(Map<?, ?> map, Class<T> beanClass, boolean isIgnoreError) {
/* 388 */     return fillBeanWithMapIgnoreCase(map, (T)ReflectUtil.newInstanceIfPossible(beanClass), isIgnoreError);
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
/*     */   @Deprecated
/*     */   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, CopyOptions copyOptions) {
/* 403 */     return fillBeanWithMap(map, (T)ReflectUtil.newInstanceIfPossible(beanClass), copyOptions);
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
/*     */   public static <T> T mapToBean(Map<?, ?> map, Class<T> beanClass, boolean isToCamelCase, CopyOptions copyOptions) {
/* 417 */     return fillBeanWithMap(map, (T)ReflectUtil.newInstanceIfPossible(beanClass), isToCamelCase, copyOptions);
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
/*     */   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isIgnoreError) {
/* 432 */     return fillBeanWithMap(map, bean, false, isIgnoreError);
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
/*     */   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, boolean isIgnoreError) {
/* 446 */     return fillBeanWithMap(map, bean, isToCamelCase, CopyOptions.create().setIgnoreError(isIgnoreError));
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
/*     */   public static <T> T fillBeanWithMapIgnoreCase(Map<?, ?> map, T bean, boolean isIgnoreError) {
/* 459 */     return fillBeanWithMap(map, bean, CopyOptions.create().setIgnoreCase(true).setIgnoreError(isIgnoreError));
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
/*     */   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, CopyOptions copyOptions) {
/* 472 */     return fillBeanWithMap(map, bean, false, copyOptions);
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
/*     */   public static <T> T fillBeanWithMap(Map<?, ?> map, T bean, boolean isToCamelCase, CopyOptions copyOptions) {
/* 487 */     if (MapUtil.isEmpty(map)) {
/* 488 */       return bean;
/*     */     }
/* 490 */     if (isToCamelCase) {
/* 491 */       map = MapUtil.toCamelCaseMap(map);
/*     */     }
/* 493 */     copyProperties(map, bean, copyOptions);
/* 494 */     return bean;
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
/*     */   public static <T> T toBean(Object source, Class<T> clazz) {
/* 509 */     return toBean(source, clazz, (CopyOptions)null);
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
/*     */   public static <T> T toBeanIgnoreError(Object source, Class<T> clazz) {
/* 522 */     return toBean(source, clazz, CopyOptions.create().setIgnoreError(true));
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
/*     */   public static <T> T toBeanIgnoreCase(Object source, Class<T> clazz, boolean ignoreError) {
/* 536 */     return toBean(source, clazz, 
/* 537 */         CopyOptions.create()
/* 538 */         .setIgnoreCase(true)
/* 539 */         .setIgnoreError(ignoreError));
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
/*     */   public static <T> T toBean(Object source, Class<T> clazz, CopyOptions options) {
/* 553 */     return toBean(source, () -> ReflectUtil.newInstanceIfPossible(clazz), options);
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
/*     */   public static <T> T toBean(Object source, Supplier<T> targetSupplier, CopyOptions options) {
/* 567 */     if (null == source || null == targetSupplier) {
/* 568 */       return null;
/*     */     }
/* 570 */     T target = targetSupplier.get();
/* 571 */     copyProperties(source, target, options);
/* 572 */     return target;
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
/*     */   public static <T> T toBean(Class<T> beanClass, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
/* 585 */     if (null == beanClass || null == valueProvider) {
/* 586 */       return null;
/*     */     }
/* 588 */     return fillBean((T)ReflectUtil.newInstanceIfPossible(beanClass), valueProvider, copyOptions);
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
/*     */   public static <T> T fillBean(T bean, ValueProvider<String> valueProvider, CopyOptions copyOptions) {
/* 601 */     if (null == valueProvider) {
/* 602 */       return bean;
/*     */     }
/*     */     
/* 605 */     return (T)BeanCopier.create(valueProvider, bean, copyOptions).copy();
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
/*     */   public static Map<String, Object> beanToMap(Object bean, String... properties) {
/* 619 */     int mapSize = 16;
/* 620 */     Editor<String> keyEditor = null;
/* 621 */     if (ArrayUtil.isNotEmpty((Object[])properties)) {
/* 622 */       mapSize = properties.length;
/* 623 */       Set<String> propertiesSet = CollUtil.set(false, (Object[])properties);
/* 624 */       keyEditor = (property -> propertiesSet.contains(property) ? property : null);
/*     */     } 
/*     */ 
/*     */     
/* 628 */     return beanToMap(bean, new LinkedHashMap<>(mapSize, 1.0F), false, keyEditor);
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
/*     */   public static Map<String, Object> beanToMap(Object bean, boolean isToUnderlineCase, boolean ignoreNullValue) {
/* 640 */     if (null == bean) {
/* 641 */       return null;
/*     */     }
/* 643 */     return beanToMap(bean, new LinkedHashMap<>(), isToUnderlineCase, ignoreNullValue);
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
/*     */   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean isToUnderlineCase, boolean ignoreNullValue) {
/* 657 */     if (null == bean) {
/* 658 */       return null;
/*     */     }
/*     */     
/* 661 */     return beanToMap(bean, targetMap, ignoreNullValue, key -> isToUnderlineCase ? StrUtil.toUnderlineCase(key) : key);
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
/*     */   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, boolean ignoreNullValue, Editor<String> keyEditor) {
/* 682 */     if (null == bean) {
/* 683 */       return null;
/*     */     }
/*     */     
/* 686 */     return (Map<String, Object>)BeanCopier.create(bean, targetMap, 
/* 687 */         CopyOptions.create()
/* 688 */         .setIgnoreNullValue(ignoreNullValue)
/* 689 */         .setFieldNameEditor(keyEditor))
/* 690 */       .copy();
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
/*     */   public static Map<String, Object> beanToMap(Object bean, Map<String, Object> targetMap, CopyOptions copyOptions) {
/* 712 */     if (null == bean) {
/* 713 */       return null;
/*     */     }
/*     */     
/* 716 */     return (Map<String, Object>)BeanCopier.create(bean, targetMap, copyOptions).copy();
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
/*     */   public static <T> T copyProperties(Object source, Class<T> tClass, String... ignoreProperties) {
/* 731 */     if (null == source) {
/* 732 */       return null;
/*     */     }
/* 734 */     T target = (T)ReflectUtil.newInstanceIfPossible(tClass);
/* 735 */     copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
/* 736 */     return target;
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
/*     */   public static void copyProperties(Object source, Object target, String... ignoreProperties) {
/* 748 */     copyProperties(source, target, CopyOptions.create().setIgnoreProperties(ignoreProperties));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copyProperties(Object source, Object target, boolean ignoreCase) {
/* 759 */     BeanCopier.create(source, target, CopyOptions.create().setIgnoreCase(ignoreCase)).copy();
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
/*     */   public static void copyProperties(Object source, Object target, CopyOptions copyOptions) {
/* 771 */     if (null == source) {
/*     */       return;
/*     */     }
/* 774 */     BeanCopier.create(source, target, (CopyOptions)ObjectUtil.defaultIfNull(copyOptions, CopyOptions::create)).copy();
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
/*     */   public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType, CopyOptions copyOptions) {
/* 789 */     if (null == collection) {
/* 790 */       return null;
/*     */     }
/* 792 */     if (collection.isEmpty()) {
/* 793 */       return new ArrayList<>(0);
/*     */     }
/* 795 */     return (List<T>)collection.stream().map(source -> {
/*     */           T target = (T)ReflectUtil.newInstanceIfPossible(targetType);
/*     */           copyProperties(source, target, copyOptions);
/*     */           return (Function)target;
/* 799 */         }).collect(Collectors.toList());
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
/*     */   public static <T> List<T> copyToList(Collection<?> collection, Class<T> targetType) {
/* 813 */     return copyToList(collection, targetType, CopyOptions.create());
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
/*     */   public static boolean isMatchName(Object bean, String beanClassName, boolean isSimple) {
/* 828 */     if (null == bean || StrUtil.isBlank(beanClassName)) {
/* 829 */       return false;
/*     */     }
/* 831 */     return ClassUtil.getClassName(bean, isSimple).equals(isSimple ? StrUtil.upperFirst(beanClassName) : beanClassName);
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
/*     */   public static <T> T edit(T bean, Editor<Field> editor) {
/* 845 */     if (bean == null) {
/* 846 */       return null;
/*     */     }
/*     */     
/* 849 */     Field[] fields = ReflectUtil.getFields(bean.getClass());
/* 850 */     for (Field field : fields) {
/* 851 */       if (!ModifierUtil.isStatic(field))
/*     */       {
/*     */         
/* 854 */         editor.edit(field); } 
/*     */     } 
/* 856 */     return bean;
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
/*     */   public static <T> T trimStrFields(T bean, String... ignoreFields) {
/* 870 */     return edit(bean, field -> {
/*     */           if (ignoreFields != null && ArrayUtil.containsIgnoreCase((CharSequence[])ignoreFields, field.getName())) {
/*     */             return field;
/*     */           }
/*     */           if (String.class.equals(field.getType())) {
/*     */             String val = (String)ReflectUtil.getFieldValue(bean, field);
/*     */             if (null != val) {
/*     */               String trimVal = StrUtil.trim(val);
/*     */               if (false == val.equals(trimVal)) {
/*     */                 ReflectUtil.setFieldValue(bean, field, trimVal);
/*     */               }
/*     */             } 
/*     */           } 
/*     */           return field;
/*     */         });
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
/*     */   public static boolean isNotEmpty(Object bean, String... ignoreFiledNames) {
/* 899 */     return (false == isEmpty(bean, ignoreFiledNames));
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
/*     */   public static boolean isEmpty(Object bean, String... ignoreFiledNames) {
/* 912 */     if (null != bean)
/* 913 */       for (Field field : ReflectUtil.getFields(bean.getClass())) {
/* 914 */         if (!ModifierUtil.isStatic(field))
/*     */         {
/*     */           
/* 917 */           if (false == ArrayUtil.contains((Object[])ignoreFiledNames, field.getName()) && null != 
/* 918 */             ReflectUtil.getFieldValue(bean, field)) {
/* 919 */             return false;
/*     */           }
/*     */         }
/*     */       }  
/* 923 */     return true;
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
/*     */   public static boolean hasNullField(Object bean, String... ignoreFiledNames) {
/* 936 */     if (null == bean) {
/* 937 */       return true;
/*     */     }
/* 939 */     for (Field field : ReflectUtil.getFields(bean.getClass())) {
/* 940 */       if (!ModifierUtil.isStatic(field))
/*     */       {
/*     */         
/* 943 */         if (false == ArrayUtil.contains((Object[])ignoreFiledNames, field.getName()) && null == 
/* 944 */           ReflectUtil.getFieldValue(bean, field))
/* 945 */           return true; 
/*     */       }
/*     */     } 
/* 948 */     return false;
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
/*     */   public static String getFieldName(String getterOrSetterName) {
/* 966 */     if (getterOrSetterName.startsWith("get") || getterOrSetterName.startsWith("set"))
/* 967 */       return StrUtil.removePreAndLowerFirst(getterOrSetterName, 3); 
/* 968 */     if (getterOrSetterName.startsWith("is")) {
/* 969 */       return StrUtil.removePreAndLowerFirst(getterOrSetterName, 2);
/*     */     }
/* 971 */     throw new IllegalArgumentException("Invalid Getter or Setter name: " + getterOrSetterName);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\bean\BeanUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */