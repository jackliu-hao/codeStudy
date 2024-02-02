package cn.hutool.core.util;

import cn.hutool.core.annotation.Alias;
import cn.hutool.core.bean.NullWrapperBean;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.UniqueKeySet;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.InvocationTargetRuntimeException;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.reflect.MethodHandleUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.map.WeakConcurrentMap;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectUtil {
   private static final WeakConcurrentMap<Class<?>, Constructor<?>[]> CONSTRUCTORS_CACHE = new WeakConcurrentMap();
   private static final WeakConcurrentMap<Class<?>, Field[]> FIELDS_CACHE = new WeakConcurrentMap();
   private static final WeakConcurrentMap<Class<?>, Method[]> METHODS_CACHE = new WeakConcurrentMap();

   public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameterTypes) {
      if (null == clazz) {
         return null;
      } else {
         Constructor<?>[] constructors = getConstructors(clazz);
         Constructor[] var4 = constructors;
         int var5 = constructors.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Constructor<?> constructor = var4[var6];
            Class<?>[] pts = constructor.getParameterTypes();
            if (ClassUtil.isAllAssignableFrom(pts, parameterTypes)) {
               setAccessible(constructor);
               return constructor;
            }
         }

         return null;
      }
   }

   public static <T> Constructor<T>[] getConstructors(Class<T> beanClass) throws SecurityException {
      Assert.notNull(beanClass);
      return (Constructor[])((Constructor[])CONSTRUCTORS_CACHE.computeIfAbsent(beanClass, () -> {
         return getConstructorsDirectly(beanClass);
      }));
   }

   public static Constructor<?>[] getConstructorsDirectly(Class<?> beanClass) throws SecurityException {
      return beanClass.getDeclaredConstructors();
   }

   public static boolean hasField(Class<?> beanClass, String name) throws SecurityException {
      return null != getField(beanClass, name);
   }

   public static String getFieldName(Field field) {
      if (null == field) {
         return null;
      } else {
         Alias alias = (Alias)field.getAnnotation(Alias.class);
         return null != alias ? alias.value() : field.getName();
      }
   }

   public static Field getField(Class<?> beanClass, String name) throws SecurityException {
      Field[] fields = getFields(beanClass);
      return (Field)ArrayUtil.firstMatch((field) -> {
         return name.equals(getFieldName(field));
      }, fields);
   }

   public static Map<String, Field> getFieldMap(Class<?> beanClass) {
      Field[] fields = getFields(beanClass);
      HashMap<String, Field> map = MapUtil.newHashMap(fields.length, true);
      Field[] var3 = fields;
      int var4 = fields.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Field field = var3[var5];
         map.put(field.getName(), field);
      }

      return map;
   }

   public static Field[] getFields(Class<?> beanClass) throws SecurityException {
      Assert.notNull(beanClass);
      return (Field[])FIELDS_CACHE.computeIfAbsent(beanClass, () -> {
         return getFieldsDirectly(beanClass, true);
      });
   }

   public static Field[] getFields(Class<?> beanClass, Filter<Field> fieldFilter) throws SecurityException {
      return (Field[])ArrayUtil.filter(getFields(beanClass), fieldFilter);
   }

   public static Field[] getFieldsDirectly(Class<?> beanClass, boolean withSuperClassFields) throws SecurityException {
      Assert.notNull(beanClass);
      Field[] allFields = null;

      for(Class<?> searchType = beanClass; searchType != null; searchType = withSuperClassFields ? searchType.getSuperclass() : null) {
         Field[] declaredFields = searchType.getDeclaredFields();
         if (null == allFields) {
            allFields = declaredFields;
         } else {
            allFields = (Field[])ArrayUtil.append((Object[])allFields, declaredFields);
         }
      }

      return allFields;
   }

   public static Object getFieldValue(Object obj, String fieldName) throws UtilException {
      return null != obj && !StrUtil.isBlank(fieldName) ? getFieldValue(obj, getField(obj instanceof Class ? (Class)obj : obj.getClass(), fieldName)) : null;
   }

   public static Object getStaticFieldValue(Field field) throws UtilException {
      return getFieldValue((Object)null, (Field)field);
   }

   public static Object getFieldValue(Object obj, Field field) throws UtilException {
      if (null == field) {
         return null;
      } else {
         if (obj instanceof Class) {
            obj = null;
         }

         setAccessible(field);

         try {
            Object result = field.get(obj);
            return result;
         } catch (IllegalAccessException var4) {
            throw new UtilException(var4, "IllegalAccess for {}.{}", new Object[]{field.getDeclaringClass(), field.getName()});
         }
      }
   }

   public static Object[] getFieldsValue(Object obj) {
      if (null != obj) {
         Field[] fields = getFields(obj instanceof Class ? (Class)obj : obj.getClass());
         if (null != fields) {
            Object[] values = new Object[fields.length];

            for(int i = 0; i < fields.length; ++i) {
               values[i] = getFieldValue(obj, fields[i]);
            }

            return values;
         }
      }

      return null;
   }

   public static void setFieldValue(Object obj, String fieldName, Object value) throws UtilException {
      Assert.notNull(obj);
      Assert.notBlank(fieldName);
      Field field = getField(obj instanceof Class ? (Class)obj : obj.getClass(), fieldName);
      Assert.notNull(field, "Field [{}] is not exist in [{}]", fieldName, obj.getClass().getName());
      setFieldValue(obj, field, value);
   }

   public static void setFieldValue(Object obj, Field field, Object value) throws UtilException {
      Assert.notNull(field, "Field in [{}] not exist !", obj);
      Class<?> fieldType = field.getType();
      if (null != value) {
         if (!fieldType.isAssignableFrom(value.getClass())) {
            Object targetValue = Convert.convert(fieldType, value);
            if (null != targetValue) {
               value = targetValue;
            }
         }
      } else {
         value = ClassUtil.getDefaultValue(fieldType);
      }

      setAccessible(field);

      try {
         field.set(obj instanceof Class ? null : obj, value);
      } catch (IllegalAccessException var5) {
         throw new UtilException(var5, "IllegalAccess for {}.{}", new Object[]{obj, field.getName()});
      }
   }

   public static boolean isOuterClassField(Field field) {
      return "this$0".equals(field.getName());
   }

   public static Set<String> getPublicMethodNames(Class<?> clazz) {
      HashSet<String> methodSet = new HashSet();
      Method[] methodArray = getPublicMethods(clazz);
      if (ArrayUtil.isNotEmpty((Object[])methodArray)) {
         Method[] var3 = methodArray;
         int var4 = methodArray.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            Method method = var3[var5];
            methodSet.add(method.getName());
         }
      }

      return methodSet;
   }

   public static Method[] getPublicMethods(Class<?> clazz) {
      return null == clazz ? null : clazz.getMethods();
   }

   public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
      if (null == clazz) {
         return null;
      } else {
         Method[] methods = getPublicMethods(clazz);
         ArrayList methodList;
         if (null != filter) {
            methodList = new ArrayList();
            Method[] var4 = methods;
            int var5 = methods.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Method method = var4[var6];
               if (filter.accept(method)) {
                  methodList.add(method);
               }
            }
         } else {
            methodList = CollUtil.newArrayList((Object[])methods);
         }

         return methodList;
      }
   }

   public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
      HashSet<Method> excludeMethodSet = CollUtil.newHashSet((Object[])excludeMethods);
      return getPublicMethods(clazz, (method) -> {
         return !excludeMethodSet.contains(method);
      });
   }

   public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
      HashSet<String> excludeMethodNameSet = CollUtil.newHashSet((Object[])excludeMethodNames);
      return getPublicMethods(clazz, (method) -> {
         return !excludeMethodNameSet.contains(method.getName());
      });
   }

   public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
      try {
         return clazz.getMethod(methodName, paramTypes);
      } catch (NoSuchMethodException var4) {
         return null;
      }
   }

   public static Method getMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
      return null != obj && !StrUtil.isBlank(methodName) ? getMethod(obj.getClass(), methodName, ClassUtil.getClasses(args)) : null;
   }

   public static Method getMethodIgnoreCase(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
      return getMethod(clazz, true, methodName, paramTypes);
   }

   public static Method getMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
      return getMethod(clazz, false, methodName, paramTypes);
   }

   public static Method getMethod(Class<?> clazz, boolean ignoreCase, String methodName, Class<?>... paramTypes) throws SecurityException {
      if (null != clazz && !StrUtil.isBlank(methodName)) {
         Method[] methods = getMethods(clazz);
         if (ArrayUtil.isNotEmpty((Object[])methods)) {
            Method[] var5 = methods;
            int var6 = methods.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               Method method = var5[var7];
               if (StrUtil.equals(methodName, method.getName(), ignoreCase) && ClassUtil.isAllAssignableFrom(method.getParameterTypes(), paramTypes) && !method.isBridge()) {
                  return method;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static Method getMethodByName(Class<?> clazz, String methodName) throws SecurityException {
      return getMethodByName(clazz, false, methodName);
   }

   public static Method getMethodByNameIgnoreCase(Class<?> clazz, String methodName) throws SecurityException {
      return getMethodByName(clazz, true, methodName);
   }

   public static Method getMethodByName(Class<?> clazz, boolean ignoreCase, String methodName) throws SecurityException {
      if (null != clazz && !StrUtil.isBlank(methodName)) {
         Method[] methods = getMethods(clazz);
         if (ArrayUtil.isNotEmpty((Object[])methods)) {
            Method[] var4 = methods;
            int var5 = methods.length;

            for(int var6 = 0; var6 < var5; ++var6) {
               Method method = var4[var6];
               if (StrUtil.equals(methodName, method.getName(), ignoreCase) && !method.isBridge()) {
                  return method;
               }
            }
         }

         return null;
      } else {
         return null;
      }
   }

   public static Set<String> getMethodNames(Class<?> clazz) throws SecurityException {
      HashSet<String> methodSet = new HashSet();
      Method[] methods = getMethods(clazz);
      Method[] var3 = methods;
      int var4 = methods.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Method method = var3[var5];
         methodSet.add(method.getName());
      }

      return methodSet;
   }

   public static Method[] getMethods(Class<?> clazz, Filter<Method> filter) throws SecurityException {
      return null == clazz ? null : (Method[])ArrayUtil.filter(getMethods(clazz), filter);
   }

   public static Method[] getMethods(Class<?> beanClass) throws SecurityException {
      Assert.notNull(beanClass);
      return (Method[])METHODS_CACHE.computeIfAbsent(beanClass, () -> {
         return getMethodsDirectly(beanClass, true, true);
      });
   }

   public static Method[] getMethodsDirectly(Class<?> beanClass, boolean withSupers, boolean withMethodFromObject) throws SecurityException {
      Assert.notNull(beanClass);
      if (beanClass.isInterface()) {
         return withSupers ? beanClass.getMethods() : beanClass.getDeclaredMethods();
      } else {
         UniqueKeySet<String, Method> result = new UniqueKeySet(true, ReflectUtil::getUniqueKey);

         for(Class<?> searchType = beanClass; searchType != null && (withMethodFromObject || Object.class != searchType); searchType = withSupers && !searchType.isInterface() ? searchType.getSuperclass() : null) {
            result.addAllIfAbsent(Arrays.asList(searchType.getDeclaredMethods()));
            result.addAllIfAbsent(getDefaultMethodsFromInterface(searchType));
         }

         return (Method[])result.toArray(new Method[0]);
      }
   }

   public static boolean isEqualsMethod(Method method) {
      if (method != null && 1 == method.getParameterCount() && "equals".equals(method.getName())) {
         return method.getParameterTypes()[0] == Object.class;
      } else {
         return false;
      }
   }

   public static boolean isHashCodeMethod(Method method) {
      return method != null && "hashCode".equals(method.getName()) && isEmptyParam(method);
   }

   public static boolean isToStringMethod(Method method) {
      return method != null && "toString".equals(method.getName()) && isEmptyParam(method);
   }

   public static boolean isEmptyParam(Method method) {
      return method.getParameterCount() == 0;
   }

   public static boolean isGetterOrSetterIgnoreCase(Method method) {
      return isGetterOrSetter(method, true);
   }

   public static boolean isGetterOrSetter(Method method, boolean ignoreCase) {
      if (null == method) {
         return false;
      } else {
         int parameterCount = method.getParameterCount();
         if (parameterCount > 1) {
            return false;
         } else {
            String name = method.getName();
            if ("getClass".equals(name)) {
               return false;
            } else {
               if (ignoreCase) {
                  name = name.toLowerCase();
               }

               switch (parameterCount) {
                  case 0:
                     return name.startsWith("get") || name.startsWith("is");
                  case 1:
                     return name.startsWith("set");
                  default:
                     return false;
               }
            }
         }
      }
   }

   public static <T> T newInstance(String clazz) throws UtilException {
      try {
         return Class.forName(clazz).newInstance();
      } catch (Exception var2) {
         throw new UtilException(var2, "Instance class [{}] error!", new Object[]{clazz});
      }
   }

   public static <T> T newInstance(Class<T> clazz, Object... params) throws UtilException {
      if (ArrayUtil.isEmpty(params)) {
         Constructor<T> constructor = getConstructor(clazz);

         try {
            return constructor.newInstance();
         } catch (Exception var5) {
            throw new UtilException(var5, "Instance class [{}] error!", new Object[]{clazz});
         }
      } else {
         Class<?>[] paramTypes = ClassUtil.getClasses(params);
         Constructor<T> constructor = getConstructor(clazz, paramTypes);
         if (null == constructor) {
            throw new UtilException("No Constructor matched for parameter types: [{}]", new Object[]{paramTypes});
         } else {
            try {
               return constructor.newInstance(params);
            } catch (Exception var6) {
               throw new UtilException(var6, "Instance class [{}] error!", new Object[]{clazz});
            }
         }
      }
   }

   public static <T> T newInstanceIfPossible(Class<T> type) {
      Assert.notNull(type);
      if (type.isPrimitive()) {
         return ClassUtil.getPrimitiveDefaultValue(type);
      } else {
         if (type.isAssignableFrom(AbstractMap.class)) {
            type = HashMap.class;
         } else if (type.isAssignableFrom(List.class)) {
            type = ArrayList.class;
         } else if (type.isAssignableFrom(Set.class)) {
            type = HashSet.class;
         }

         try {
            return newInstance(type);
         } catch (Exception var9) {
            if (type.isEnum()) {
               return type.getEnumConstants()[0];
            } else if (type.isArray()) {
               return Array.newInstance(type.getComponentType(), 0);
            } else {
               Constructor<T>[] constructors = getConstructors(type);
               Constructor[] var3 = constructors;
               int var4 = constructors.length;

               for(int var5 = 0; var5 < var4; ++var5) {
                  Constructor<T> constructor = var3[var5];
                  Class<?>[] parameterTypes = constructor.getParameterTypes();
                  if (0 != parameterTypes.length) {
                     setAccessible(constructor);

                     try {
                        return constructor.newInstance(ClassUtil.getDefaultValues(parameterTypes));
                     } catch (Exception var8) {
                     }
                  }
               }

               return null;
            }
         }
      }
   }

   public static <T> T invokeStatic(Method method, Object... args) throws UtilException {
      return invoke((Object)null, (Method)method, args);
   }

   public static <T> T invokeWithCheck(Object obj, Method method, Object... args) throws UtilException {
      Class<?>[] types = method.getParameterTypes();
      if (null != args) {
         Assert.isTrue(args.length == types.length, "Params length [{}] is not fit for param length [{}] of method !", args.length, types.length);

         for(int i = 0; i < args.length; ++i) {
            Class<?> type = types[i];
            if (type.isPrimitive() && null == args[i]) {
               args[i] = ClassUtil.getDefaultValue(type);
            }
         }
      }

      return invoke(obj, method, args);
   }

   public static <T> T invoke(Object obj, Method method, Object... args) throws InvocationTargetRuntimeException, UtilException {
      try {
         return invokeRaw(obj, method, args);
      } catch (InvocationTargetException var4) {
         throw new InvocationTargetRuntimeException(var4);
      } catch (IllegalAccessException var5) {
         throw new UtilException(var5);
      }
   }

   public static <T> T invokeRaw(Object obj, Method method, Object... args) throws InvocationTargetException, IllegalAccessException {
      setAccessible(method);
      Class<?>[] parameterTypes = method.getParameterTypes();
      Object[] actualArgs = new Object[parameterTypes.length];
      if (null != args) {
         for(int i = 0; i < actualArgs.length; ++i) {
            if (i < args.length && null != args[i]) {
               if (args[i] instanceof NullWrapperBean) {
                  actualArgs[i] = null;
               } else if (!parameterTypes[i].isAssignableFrom(args[i].getClass())) {
                  Object targetValue = Convert.convert(parameterTypes[i], args[i]);
                  if (null != targetValue) {
                     actualArgs[i] = targetValue;
                  }
               } else {
                  actualArgs[i] = args[i];
               }
            } else {
               actualArgs[i] = ClassUtil.getDefaultValue(parameterTypes[i]);
            }
         }
      }

      return method.isDefault() ? MethodHandleUtil.invokeSpecial(obj, method, args) : method.invoke(ClassUtil.isStatic(method) ? null : obj, actualArgs);
   }

   public static <T> T invoke(Object obj, String methodName, Object... args) throws UtilException {
      Assert.notNull(obj, "Object to get method must be not null!");
      Assert.notBlank(methodName, "Method name must be not blank!");
      Method method = getMethodOfObj(obj, methodName, args);
      if (null == method) {
         throw new UtilException("No such method: [{}] from [{}]", new Object[]{methodName, obj.getClass()});
      } else {
         return invoke(obj, method, args);
      }
   }

   public static <T extends AccessibleObject> T setAccessible(T accessibleObject) {
      if (null != accessibleObject && !accessibleObject.isAccessible()) {
         accessibleObject.setAccessible(true);
      }

      return accessibleObject;
   }

   private static String getUniqueKey(Method method) {
      StringBuilder sb = new StringBuilder();
      sb.append(method.getReturnType().getName()).append('#');
      sb.append(method.getName());
      Class<?>[] parameters = method.getParameterTypes();

      for(int i = 0; i < parameters.length; ++i) {
         if (i == 0) {
            sb.append(':');
         } else {
            sb.append(',');
         }

         sb.append(parameters[i].getName());
      }

      return sb.toString();
   }

   private static List<Method> getDefaultMethodsFromInterface(Class<?> clazz) {
      List<Method> result = new ArrayList();
      Class[] var2 = clazz.getInterfaces();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Class<?> ifc = var2[var4];
         Method[] var6 = ifc.getMethods();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            Method m = var6[var8];
            if (!ModifierUtil.isAbstract(m)) {
               result.add(m);
            }
         }
      }

      return result;
   }

   // $FF: synthetic method
   private static Object $deserializeLambda$(SerializedLambda lambda) {
      switch (lambda.getImplMethodName()) {
         case "lambda$getFields$54eedd5e$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Field;")) {
               return () -> {
                  return getFieldsDirectly(beanClass, true);
               };
            }
            break;
         case "lambda$getConstructors$8f84531$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Constructor;")) {
               return () -> {
                  return getConstructorsDirectly(beanClass);
               };
            }
            break;
         case "lambda$getMethods$ea73458f$1":
            if (lambda.getImplMethodKind() == 6 && lambda.getFunctionalInterfaceClass().equals("cn/hutool/core/lang/func/Func0") && lambda.getFunctionalInterfaceMethodName().equals("call") && lambda.getFunctionalInterfaceMethodSignature().equals("()Ljava/lang/Object;") && lambda.getImplClass().equals("cn/hutool/core/util/ReflectUtil") && lambda.getImplMethodSignature().equals("(Ljava/lang/Class;)[Ljava/lang/reflect/Method;")) {
               return () -> {
                  return getMethodsDirectly(beanClass, true, true);
               };
            }
      }

      throw new IllegalArgumentException("Invalid lambda deserialization");
   }
}
