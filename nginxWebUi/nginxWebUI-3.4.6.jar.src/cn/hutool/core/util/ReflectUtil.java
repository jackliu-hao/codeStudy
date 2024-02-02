/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.annotation.Alias;
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.collection.UniqueKeySet;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.exceptions.InvocationTargetRuntimeException;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.reflect.MethodHandleUtil;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.map.WeakConcurrentMap;
/*      */ import java.lang.invoke.SerializedLambda;
/*      */ import java.lang.reflect.Array;
/*      */ import java.lang.reflect.Constructor;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.InvocationTargetException;
/*      */ import java.lang.reflect.Method;
/*      */ import java.util.AbstractMap;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.Set;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ReflectUtil
/*      */ {
/*   42 */   private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap();
/*      */ 
/*      */ 
/*      */   
/*   46 */   private static final WeakConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new WeakConcurrentMap();
/*      */ 
/*      */ 
/*      */   
/*   50 */   private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE = new WeakConcurrentMap();
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
/*   64 */     if (null == clazz) {
/*   65 */       return null;
/*      */     }
/*      */     
/*   68 */     Constructor[] arrayOfConstructor = (Constructor[])getConstructors(clazz);
/*      */     
/*   70 */     for (Constructor<?> constructor : arrayOfConstructor) {
/*   71 */       Class<?>[] pts = constructor.getParameterTypes();
/*   72 */       if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
/*      */         
/*   74 */         setAccessible(constructor);
/*   75 */         return (Constructor)constructor;
/*      */       } 
/*      */     } 
/*   78 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
/*   91 */     Assert.notNull(beanClass);
/*   92 */     return (Constructor<T>[])CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, () -> getConstructorsDirectly(beanClass));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
/*  103 */     return beanClass.getDeclaredConstructors();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean hasField(Class<?> beanClass, String name) throws SecurityException {
/*  118 */     return (null != getField(beanClass, name));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getFieldName(Field field) {
/*  129 */     if (null == field) {
/*  130 */       return null;
/*      */     }
/*      */     
/*  133 */     Alias alias = field.<Alias>getAnnotation(Alias.class);
/*  134 */     if (null != alias) {
/*  135 */       return alias.value();
/*      */     }
/*      */     
/*  138 */     return field.getName();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Field getField(Class<?> beanClass, String name) throws SecurityException {
/*  150 */     Field[] fields = getFields(beanClass);
/*  151 */     return ArrayUtil.<Field>firstMatch(field -> name.equals(getFieldName(field)), fields);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Map<String, Field> getFieldMap(Class<?> beanClass) {
/*  163 */     Field[] fields = getFields(beanClass);
/*  164 */     HashMap<String, Field> map = MapUtil.newHashMap(fields.length, true);
/*  165 */     for (Field field : fields) {
/*  166 */       map.put(field.getName(), field);
/*      */     }
/*  168 */     return map;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Field[] getFields(Class<?> beanClass) throws SecurityException {
/*  180 */     Assert.notNull(beanClass);
/*  181 */     return (Field[])FIELDS_CACHE.computeIfAbsent(beanClass, () -> getFieldsDirectly(beanClass, true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Field[] getFields(Class<?> beanClass, Filter<Field> fieldFilter) throws SecurityException {
/*  196 */     return ArrayUtil.<Field>filter(getFields(beanClass), fieldFilter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
/*  209 */     Assert.notNull(beanClass);
/*      */     
/*  211 */     Field[] allFields = null;
/*  212 */     Class<?> searchType = beanClass;
/*      */     
/*  214 */     while (searchType != null) {
/*  215 */       Field[] declaredFields = searchType.getDeclaredFields();
/*  216 */       if (null == allFields) {
/*  217 */         allFields = declaredFields;
/*      */       } else {
/*  219 */         allFields = ArrayUtil.<Field>append(allFields, declaredFields);
/*      */       } 
/*  221 */       searchType = withSuperClassFields ? searchType.getSuperclass() : null;
/*      */     } 
/*      */     
/*  224 */     return allFields;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getFieldValue(Object obj, String fieldName) throws UtilException {
/*  236 */     if (null == obj || StrUtil.isBlank(fieldName)) {
/*  237 */       return null;
/*      */     }
/*  239 */     return getFieldValue(obj, getField((obj instanceof Class) ? (Class)obj : obj.getClass(), fieldName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getStaticFieldValue(Field field) throws UtilException {
/*  251 */     return getFieldValue((Object)null, field);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object getFieldValue(Object obj, Field field) throws UtilException {
/*      */     Object result;
/*  263 */     if (null == field) {
/*  264 */       return null;
/*      */     }
/*  266 */     if (obj instanceof Class)
/*      */     {
/*  268 */       obj = null;
/*      */     }
/*      */     
/*  271 */     setAccessible(field);
/*      */     
/*      */     try {
/*  274 */       result = field.get(obj);
/*  275 */     } catch (IllegalAccessException e) {
/*  276 */       throw new UtilException(e, "IllegalAccess for {}.{}", new Object[] { field.getDeclaringClass(), field.getName() });
/*      */     } 
/*  278 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] getFieldsValue(Object obj) {
/*  289 */     if (null != obj) {
/*  290 */       Field[] fields = getFields((obj instanceof Class) ? (Class)obj : obj.getClass());
/*  291 */       if (null != fields) {
/*  292 */         Object[] values = new Object[fields.length];
/*  293 */         for (int i = 0; i < fields.length; i++) {
/*  294 */           values[i] = getFieldValue(obj, fields[i]);
/*      */         }
/*  296 */         return values;
/*      */       } 
/*      */     } 
/*  299 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
/*  311 */     Assert.notNull(obj);
/*  312 */     Assert.notBlank(fieldName);
/*      */     
/*  314 */     Field field = getField((obj instanceof Class) ? (Class)obj : obj.getClass(), fieldName);
/*  315 */     Assert.notNull(field, "Field [{}] is not exist in [{}]", new Object[] { fieldName, obj.getClass().getName() });
/*  316 */     setFieldValue(obj, field, value);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
/*  328 */     Assert.notNull(field, "Field in [{}] not exist !", new Object[] { obj });
/*      */     
/*  330 */     Class<?> fieldType = field.getType();
/*  331 */     if (null != value) {
/*  332 */       if (false == fieldType.isAssignableFrom(value.getClass())) {
/*      */         
/*  334 */         Object targetValue = Convert.convert(fieldType, value);
/*  335 */         if (null != targetValue) {
/*  336 */           value = targetValue;
/*      */         }
/*      */       } 
/*      */     } else {
/*      */       
/*  341 */       value = ClassUtil.getDefaultValue(fieldType);
/*      */     } 
/*      */     
/*  344 */     setAccessible(field);
/*      */     try {
/*  346 */       field.set((obj instanceof Class) ? null : obj, value);
/*  347 */     } catch (IllegalAccessException e) {
/*  348 */       throw new UtilException(e, "IllegalAccess for {}.{}", new Object[] { obj, field.getName() });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isOuterClassField(Field field) {
/*  361 */     return "this$0".equals(field.getName());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getPublicMethodNames(Class<?> clazz) {
/*  374 */     HashSet<String> methodSet = new HashSet<>();
/*  375 */     Method[] methodArray = getPublicMethods(clazz);
/*  376 */     if (ArrayUtil.isNotEmpty(methodArray)) {
/*  377 */       for (Method method : methodArray) {
/*  378 */         methodSet.add(method.getName());
/*      */       }
/*      */     }
/*  381 */     return methodSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getPublicMethods(Class<?> clazz) {
/*  391 */     return (null == clazz) ? null : clazz.getMethods();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
/*      */     List<Method> methodList;
/*  403 */     if (null == clazz) {
/*  404 */       return null;
/*      */     }
/*      */     
/*  407 */     Method[] methods = getPublicMethods(clazz);
/*      */     
/*  409 */     if (null != filter) {
/*  410 */       methodList = new ArrayList<>();
/*  411 */       for (Method method : methods) {
/*  412 */         if (filter.accept(method)) {
/*  413 */           methodList.add(method);
/*      */         }
/*      */       } 
/*      */     } else {
/*  417 */       methodList = CollUtil.newArrayList((Object[])methods);
/*      */     } 
/*  419 */     return methodList;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
/*  430 */     HashSet<Method> excludeMethodSet = CollUtil.newHashSet((Object[])excludeMethods);
/*  431 */     return getPublicMethods(clazz, method -> (false == excludeMethodSet.contains(method)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
/*  442 */     HashSet<String> excludeMethodNameSet = CollUtil.newHashSet((Object[])excludeMethodNames);
/*  443 */     return getPublicMethods(clazz, method -> (false == excludeMethodNameSet.contains(method.getName())));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
/*      */     try {
/*  457 */       return clazz.getMethod(methodName, paramTypes);
/*  458 */     } catch (NoSuchMethodException ex) {
/*  459 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
/*  477 */     if (null == obj || StrUtil.isBlank(methodName)) {
/*  478 */       return null;
/*      */     }
/*  480 */     return getMethod(obj.getClass(), methodName, ClassUtil.getClasses(args));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethodIgnoreCase(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
/*  498 */     return getMethod(clazz, true, methodName, paramTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
/*  515 */     return getMethod(clazz, false, methodName, paramTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
/*  532 */     if (null == clazz || StrUtil.isBlank(methodName)) {
/*  533 */       return null;
/*      */     }
/*      */     
/*  536 */     Method[] methods = getMethods(clazz);
/*  537 */     if (ArrayUtil.isNotEmpty(methods)) {
/*  538 */       for (Method method : methods) {
/*  539 */         if (StrUtil.equals(methodName, method.getName(), ignoreCase) && 
/*  540 */           ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes) && false == method
/*      */           
/*  542 */           .isBridge()) {
/*  543 */           return method;
/*      */         }
/*      */       } 
/*      */     }
/*  547 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethodByName(Class<?> clazz, String methodName) throws SecurityException {
/*  564 */     return getMethodByName(clazz, false, methodName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethodByNameIgnoreCase(Class<?> clazz, String methodName) throws SecurityException {
/*  581 */     return getMethodByName(clazz, true, methodName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method getMethodByName(Class<?> clazz, boolean ignoreCase, String methodName) throws SecurityException {
/*  599 */     if (null == clazz || StrUtil.isBlank(methodName)) {
/*  600 */       return null;
/*      */     }
/*      */     
/*  603 */     Method[] methods = getMethods(clazz);
/*  604 */     if (ArrayUtil.isNotEmpty(methods)) {
/*  605 */       for (Method method : methods) {
/*  606 */         if (StrUtil.equals(methodName, method.getName(), ignoreCase) && false == method
/*      */           
/*  608 */           .isBridge()) {
/*  609 */           return method;
/*      */         }
/*      */       } 
/*      */     }
/*  613 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getMethodNames(Class<?> clazz) throws SecurityException {
/*  625 */     HashSet<String> methodSet = new HashSet<>();
/*  626 */     Method[] methods = getMethods(clazz);
/*  627 */     for (Method method : methods) {
/*  628 */       methodSet.add(method.getName());
/*      */     }
/*  630 */     return methodSet;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getMethods(Class<?> clazz, Filter<Method> filter) throws SecurityException {
/*  642 */     if (null == clazz) {
/*  643 */       return null;
/*      */     }
/*  645 */     return ArrayUtil.<Method>filter(getMethods(clazz), filter);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
/*  656 */     Assert.notNull(beanClass);
/*  657 */     return (Method[])METHODS_CACHE.computeIfAbsent(beanClass, () -> getMethodsDirectly(beanClass, true, true));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) throws SecurityException {
/*  677 */     Assert.notNull(beanClass);
/*      */     
/*  679 */     if (beanClass.isInterface())
/*      */     {
/*  681 */       return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
/*      */     }
/*      */     
/*  684 */     UniqueKeySet<String, Method> result = new UniqueKeySet(true, ReflectUtil::getUniqueKey);
/*  685 */     Class<?> searchType = beanClass;
/*  686 */     while (searchType != null && (false != 
/*  687 */       withMethodFromObject || Object.class != searchType)) {
/*      */ 
/*      */       
/*  690 */       result.addAllIfAbsent(Arrays.asList(searchType.getDeclaredMethods()));
/*  691 */       result.addAllIfAbsent(getDefaultMethodsFromInterface(searchType));
/*      */ 
/*      */       
/*  694 */       searchType = (withSupers && false == searchType.isInterface()) ? searchType.getSuperclass() : null;
/*      */     } 
/*      */     
/*  697 */     return (Method[])result.toArray((Object[])new Method[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEqualsMethod(Method method) {
/*  707 */     if (method == null || 1 != method
/*  708 */       .getParameterCount() || false == "equals"
/*  709 */       .equals(method.getName())) {
/*  710 */       return false;
/*      */     }
/*  712 */     return (method.getParameterTypes()[0] == Object.class);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isHashCodeMethod(Method method) {
/*  722 */     return (method != null && "hashCode"
/*  723 */       .equals(method.getName()) && 
/*  724 */       isEmptyParam(method));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isToStringMethod(Method method) {
/*  734 */     return (method != null && "toString"
/*  735 */       .equals(method.getName()) && 
/*  736 */       isEmptyParam(method));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEmptyParam(Method method) {
/*  747 */     return (method.getParameterCount() == 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGetterOrSetterIgnoreCase(Method method) {
/*  763 */     return isGetterOrSetter(method, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isGetterOrSetter(Method method, boolean ignoreCase) {
/*  781 */     if (null == method) {
/*  782 */       return false;
/*      */     }
/*      */ 
/*      */     
/*  786 */     int parameterCount = method.getParameterCount();
/*  787 */     if (parameterCount > 1) {
/*  788 */       return false;
/*      */     }
/*      */     
/*  791 */     String name = method.getName();
/*      */     
/*  793 */     if ("getClass".equals(name)) {
/*  794 */       return false;
/*      */     }
/*  796 */     if (ignoreCase) {
/*  797 */       name = name.toLowerCase();
/*      */     }
/*  799 */     switch (parameterCount) {
/*      */       case 0:
/*  801 */         return (name.startsWith("get") || name.startsWith("is"));
/*      */       case 1:
/*  803 */         return name.startsWith("set");
/*      */     } 
/*  805 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T newInstance(String clazz) throws UtilException {
/*      */     try {
/*  821 */       return (T)Class.forName(clazz).newInstance();
/*  822 */     } catch (Exception e) {
/*  823 */       throw new UtilException(e, "Instance class [{}] error!", new Object[] { clazz });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T newInstance(Class<T> clazz, Object... params) throws UtilException {
/*  837 */     if (ArrayUtil.isEmpty(params)) {
/*  838 */       Constructor<T> constructor1 = getConstructor(clazz, new Class[0]);
/*      */       try {
/*  840 */         return constructor1.newInstance(new Object[0]);
/*  841 */       } catch (Exception e) {
/*  842 */         throw new UtilException(e, "Instance class [{}] error!", new Object[] { clazz });
/*      */       } 
/*      */     } 
/*      */     
/*  846 */     Class<?>[] paramTypes = ClassUtil.getClasses(params);
/*  847 */     Constructor<T> constructor = getConstructor(clazz, paramTypes);
/*  848 */     if (null == constructor) {
/*  849 */       throw new UtilException("No Constructor matched for parameter types: [{}]", new Object[] { paramTypes });
/*      */     }
/*      */     try {
/*  852 */       return constructor.newInstance(params);
/*  853 */     } catch (Exception e) {
/*  854 */       throw new UtilException(e, "Instance class [{}] error!", new Object[] { clazz });
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T newInstanceIfPossible(Class<T> type) {
/*      */     Class<HashMap> clazz1;
/*      */     Class<HashSet> clazz;
/*  875 */     Assert.notNull(type);
/*      */ 
/*      */     
/*  878 */     if (type.isPrimitive()) {
/*  879 */       return (T)ClassUtil.getPrimitiveDefaultValue(type);
/*      */     }
/*      */ 
/*      */     
/*  883 */     if (type.isAssignableFrom(AbstractMap.class))
/*  884 */     { clazz1 = HashMap.class; }
/*  885 */     else { Class<ArrayList> clazz2; if (clazz1.isAssignableFrom(List.class)) {
/*  886 */         clazz2 = ArrayList.class;
/*  887 */       } else if (clazz2.isAssignableFrom(Set.class)) {
/*  888 */         clazz = HashSet.class;
/*      */       }  }
/*      */     
/*      */     try {
/*  892 */       return newInstance((Class)clazz, new Object[0]);
/*  893 */     } catch (Exception exception) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */       
/*  899 */       if (clazz.isEnum()) {
/*  900 */         return clazz.getEnumConstants()[0];
/*      */       }
/*      */ 
/*      */       
/*  904 */       if (clazz.isArray()) {
/*  905 */         return (T)Array.newInstance(clazz.getComponentType(), 0);
/*      */       }
/*      */       
/*  908 */       Constructor[] arrayOfConstructor = (Constructor[])getConstructors(clazz);
/*      */       
/*  910 */       for (Constructor<T> constructor : arrayOfConstructor) {
/*  911 */         Class<?>[] parameterTypes = constructor.getParameterTypes();
/*  912 */         if (0 != parameterTypes.length) {
/*      */ 
/*      */           
/*  915 */           setAccessible(constructor);
/*      */           try {
/*  917 */             return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
/*  918 */           } catch (Exception exception1) {}
/*      */         } 
/*      */       } 
/*      */       
/*  922 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T invokeStatic(Method method, Object... args) throws UtilException {
/*  937 */     return invoke((Object)null, method, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T invokeWithCheck(Object obj, Method method, Object... args) throws UtilException {
/*  957 */     Class<?>[] types = method.getParameterTypes();
/*  958 */     if (null != args) {
/*  959 */       Assert.isTrue((args.length == types.length), "Params length [{}] is not fit for param length [{}] of method !", new Object[] { Integer.valueOf(args.length), Integer.valueOf(types.length) });
/*      */       
/*  961 */       for (int i = 0; i < args.length; i++) {
/*  962 */         Class<?> type = types[i];
/*  963 */         if (type.isPrimitive() && null == args[i])
/*      */         {
/*  965 */           args[i] = ClassUtil.getDefaultValue(type);
/*      */         }
/*      */       } 
/*      */     } 
/*      */     
/*  970 */     return invoke(obj, method, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T invoke(Object obj, Method method, Object... args) throws InvocationTargetRuntimeException, UtilException {
/*      */     try {
/*  995 */       return invokeRaw(obj, method, args);
/*  996 */     } catch (InvocationTargetException e) {
/*  997 */       throw new InvocationTargetRuntimeException(e);
/*  998 */     } catch (IllegalAccessException e) {
/*  999 */       throw new UtilException(e);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T invokeRaw(Object obj, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
/* 1026 */     setAccessible(method);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1034 */     Class<?>[] parameterTypes = method.getParameterTypes();
/* 1035 */     Object[] actualArgs = new Object[parameterTypes.length];
/* 1036 */     if (null != args) {
/* 1037 */       for (int i = 0; i < actualArgs.length; i++) {
/* 1038 */         if (i >= args.length || null == args[i]) {
/*      */           
/* 1040 */           actualArgs[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
/* 1041 */         } else if (args[i] instanceof cn.hutool.core.bean.NullWrapperBean) {
/*      */           
/* 1043 */           actualArgs[i] = null;
/* 1044 */         } else if (false == parameterTypes[i].isAssignableFrom(args[i].getClass())) {
/*      */           
/* 1046 */           Object targetValue = Convert.convert(parameterTypes[i], args[i]);
/* 1047 */           if (null != targetValue) {
/* 1048 */             actualArgs[i] = targetValue;
/*      */           }
/*      */         } else {
/* 1051 */           actualArgs[i] = args[i];
/*      */         } 
/*      */       } 
/*      */     }
/*      */     
/* 1056 */     if (method.isDefault())
/*      */     {
/*      */       
/* 1059 */       return (T)MethodHandleUtil.invokeSpecial(obj, method, args);
/*      */     }
/*      */     
/* 1062 */     return (T)method.invoke(ClassUtil.isStatic(method) ? null : obj, actualArgs);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T invoke(Object obj, String methodName, Object... args) throws UtilException {
/* 1079 */     Assert.notNull(obj, "Object to get method must be not null!", new Object[0]);
/* 1080 */     Assert.notBlank(methodName, "Method name must be not blank!", new Object[0]);
/*      */     
/* 1082 */     Method method = getMethodOfObj(obj, methodName, args);
/* 1083 */     if (null == method) {
/* 1084 */       throw new UtilException("No such method: [{}] from [{}]", new Object[] { methodName, obj.getClass() });
/*      */     }
/* 1086 */     return invoke(obj, method, args);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends java.lang.reflect.AccessibleObject> T setAccessible(T accessibleObject) {
/* 1098 */     if (null != accessibleObject && false == accessibleObject.isAccessible()) {
/* 1099 */       accessibleObject.setAccessible(true);
/*      */     }
/* 1101 */     return accessibleObject;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String getUniqueKey(Method method) {
/* 1114 */     StringBuilder sb = new StringBuilder();
/* 1115 */     sb.append(method.getReturnType().getName()).append('#');
/* 1116 */     sb.append(method.getName());
/* 1117 */     Class<?>[] parameters = method.getParameterTypes();
/* 1118 */     for (int i = 0; i < parameters.length; i++) {
/* 1119 */       if (i == 0) {
/* 1120 */         sb.append(':');
/*      */       } else {
/* 1122 */         sb.append(',');
/*      */       } 
/* 1124 */       sb.append(parameters[i].getName());
/*      */     } 
/* 1126 */     return sb.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
/* 1136 */     List<Method> result = new ArrayList<>();
/* 1137 */     for (Class<?> ifc : clazz.getInterfaces()) {
/* 1138 */       for (Method m : ifc.getMethods()) {
/* 1139 */         if (false == ModifierUtil.isAbstract(m)) {
/* 1140 */           result.add(m);
/*      */         }
/*      */       } 
/*      */     } 
/* 1144 */     return result;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ReflectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */