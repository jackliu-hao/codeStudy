package cn.hutool.core.util;

import cn.hutool.core.bean.NullWrapperBean;
import cn.hutool.core.convert.BasicType;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ClassScanner;
import cn.hutool.core.lang.Filter;
import cn.hutool.core.lang.Singleton;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ClassUtil {
   public static <T> Class<T> getClass(T obj) {
      return null == obj ? null : obj.getClass();
   }

   public static Class<?> getEnclosingClass(Class<?> clazz) {
      return null == clazz ? null : clazz.getEnclosingClass();
   }

   public static boolean isTopLevelClass(Class<?> clazz) {
      if (null == clazz) {
         return false;
      } else {
         return null == getEnclosingClass(clazz);
      }
   }

   public static String getClassName(Object obj, boolean isSimple) {
      if (null == obj) {
         return null;
      } else {
         Class<?> clazz = obj.getClass();
         return getClassName(clazz, isSimple);
      }
   }

   public static String getClassName(Class<?> clazz, boolean isSimple) {
      if (null == clazz) {
         return null;
      } else {
         return isSimple ? clazz.getSimpleName() : clazz.getName();
      }
   }

   public static String getShortClassName(String className) {
      List<String> packages = StrUtil.split(className, '.');
      if (null != packages && packages.size() >= 2) {
         int size = packages.size();
         StringBuilder result = StrUtil.builder();
         result.append(((String)packages.get(0)).charAt(0));

         for(int i = 1; i < size - 1; ++i) {
            result.append('.').append(((String)packages.get(i)).charAt(0));
         }

         result.append('.').append((String)packages.get(size - 1));
         return result.toString();
      } else {
         return className;
      }
   }

   public static Class<?>[] getClasses(Object... objects) {
      Class<?>[] classes = new Class[objects.length];

      for(int i = 0; i < objects.length; ++i) {
         Object obj = objects[i];
         if (obj instanceof NullWrapperBean) {
            classes[i] = ((NullWrapperBean)obj).getWrappedClass();
         } else if (null == obj) {
            classes[i] = Object.class;
         } else {
            classes[i] = obj.getClass();
         }
      }

      return classes;
   }

   public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
      if (null != clazz && !StrUtil.isBlank(className)) {
         if (ignoreCase) {
            return className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName());
         } else {
            return className.equals(clazz.getName()) || className.equals(clazz.getSimpleName());
         }
      } else {
         return false;
      }
   }

   public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
      return ClassScanner.scanPackageByAnnotation(packageName, annotationClass);
   }

   public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
      return ClassScanner.scanPackageBySuper(packageName, superClass);
   }

   public static Set<Class<?>> scanPackage() {
      return ClassScanner.scanPackage();
   }

   public static Set<Class<?>> scanPackage(String packageName) {
      return ClassScanner.scanPackage(packageName);
   }

   public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
      return ClassScanner.scanPackage(packageName, classFilter);
   }

   public static Set<String> getPublicMethodNames(Class<?> clazz) {
      return ReflectUtil.getPublicMethodNames(clazz);
   }

   public static Method[] getPublicMethods(Class<?> clazz) {
      return ReflectUtil.getPublicMethods(clazz);
   }

   public static List<Method> getPublicMethods(Class<?> clazz, Filter<Method> filter) {
      return ReflectUtil.getPublicMethods(clazz, filter);
   }

   public static List<Method> getPublicMethods(Class<?> clazz, Method... excludeMethods) {
      return ReflectUtil.getPublicMethods(clazz, excludeMethods);
   }

   public static List<Method> getPublicMethods(Class<?> clazz, String... excludeMethodNames) {
      return ReflectUtil.getPublicMethods(clazz, excludeMethodNames);
   }

   public static Method getPublicMethod(Class<?> clazz, String methodName, Class<?>... paramTypes) throws SecurityException {
      return ReflectUtil.getPublicMethod(clazz, methodName, paramTypes);
   }

   public static Set<String> getDeclaredMethodNames(Class<?> clazz) {
      return ReflectUtil.getMethodNames(clazz);
   }

   public static Method[] getDeclaredMethods(Class<?> clazz) {
      return ReflectUtil.getMethods(clazz);
   }

   public static Method getDeclaredMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
      return getDeclaredMethod(obj.getClass(), methodName, getClasses(args));
   }

   public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
      return ReflectUtil.getMethod(clazz, methodName, parameterTypes);
   }

   public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
      if (null != clazz && !StrUtil.isBlank(fieldName)) {
         try {
            return clazz.getDeclaredField(fieldName);
         } catch (NoSuchFieldException var3) {
            return null;
         }
      } else {
         return null;
      }
   }

   public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
      return null == clazz ? null : clazz.getDeclaredFields();
   }

   public static Set<String> getClassPathResources() {
      return getClassPathResources(false);
   }

   public static Set<String> getClassPathResources(boolean isDecode) {
      return getClassPaths("", isDecode);
   }

   public static Set<String> getClassPaths(String packageName) {
      return getClassPaths(packageName, false);
   }

   public static Set<String> getClassPaths(String packageName, boolean isDecode) {
      String packagePath = packageName.replace(".", "/");

      Enumeration resources;
      try {
         resources = getClassLoader().getResources(packagePath);
      } catch (IOException var6) {
         throw new UtilException(var6, "Loading classPath [{}] error!", new Object[]{packagePath});
      }

      Set<String> paths = new HashSet();

      while(resources.hasMoreElements()) {
         String path = ((URL)resources.nextElement()).getPath();
         paths.add(isDecode ? URLUtil.decode(path, CharsetUtil.systemCharsetName()) : path);
      }

      return paths;
   }

   public static String getClassPath() {
      return getClassPath(false);
   }

   public static String getClassPath(boolean isEncoded) {
      URL classPathURL = getClassPathURL();
      String url = isEncoded ? classPathURL.getPath() : URLUtil.getDecodedPath(classPathURL);
      return FileUtil.normalize(url);
   }

   public static URL getClassPathURL() {
      return getResourceURL("");
   }

   public static URL getResourceURL(String resource) throws IORuntimeException {
      return ResourceUtil.getResource(resource);
   }

   public static List<URL> getResources(String resource) {
      return ResourceUtil.getResources(resource);
   }

   public static URL getResourceUrl(String resource, Class<?> baseClass) {
      return ResourceUtil.getResource(resource, baseClass);
   }

   public static String[] getJavaClassPaths() {
      return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
   }

   public static ClassLoader getContextClassLoader() {
      return ClassLoaderUtil.getContextClassLoader();
   }

   public static ClassLoader getClassLoader() {
      return ClassLoaderUtil.getClassLoader();
   }

   public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
      if (ArrayUtil.isEmpty((Object[])types1) && ArrayUtil.isEmpty((Object[])types2)) {
         return true;
      } else if (null != types1 && null != types2) {
         if (types1.length != types2.length) {
            return false;
         } else {
            for(int i = 0; i < types1.length; ++i) {
               Class<?> type1 = types1[i];
               Class<?> type2 = types2[i];
               if (isBasicType(type1) && isBasicType(type2)) {
                  if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
                     return false;
                  }
               } else if (!type1.isAssignableFrom(type2)) {
                  return false;
               }
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public static <T> Class<T> loadClass(String className, boolean isInitialized) {
      return ClassLoaderUtil.loadClass(className, isInitialized);
   }

   public static <T> Class<T> loadClass(String className) {
      return loadClass(className, true);
   }

   public static <T> T invoke(String classNameWithMethodName, Object[] args) {
      return invoke(classNameWithMethodName, false, args);
   }

   public static <T> T invoke(String classNameWithMethodName, boolean isSingleton, Object... args) {
      if (StrUtil.isBlank(classNameWithMethodName)) {
         throw new UtilException("Blank classNameDotMethodName!");
      } else {
         int splitIndex = classNameWithMethodName.lastIndexOf(35);
         if (splitIndex <= 0) {
            splitIndex = classNameWithMethodName.lastIndexOf(46);
         }

         if (splitIndex <= 0) {
            throw new UtilException("Invalid classNameWithMethodName [{}]!", new Object[]{classNameWithMethodName});
         } else {
            String className = classNameWithMethodName.substring(0, splitIndex);
            String methodName = classNameWithMethodName.substring(splitIndex + 1);
            return invoke(className, methodName, isSingleton, args);
         }
      }
   }

   public static <T> T invoke(String className, String methodName, Object[] args) {
      return invoke(className, methodName, false, args);
   }

   public static <T> T invoke(String className, String methodName, boolean isSingleton, Object... args) {
      Class<Object> clazz = loadClass(className);

      try {
         Method method = getDeclaredMethod(clazz, methodName, getClasses(args));
         if (null == method) {
            throw new NoSuchMethodException(StrUtil.format("No such method: [{}]", new Object[]{methodName}));
         } else {
            return isStatic(method) ? ReflectUtil.invoke((Object)null, (Method)method, args) : ReflectUtil.invoke(isSingleton ? Singleton.get(clazz) : clazz.newInstance(), method, args);
         }
      } catch (Exception var6) {
         throw new UtilException(var6);
      }
   }

   public static boolean isPrimitiveWrapper(Class<?> clazz) {
      return null == clazz ? false : BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
   }

   public static boolean isBasicType(Class<?> clazz) {
      if (null == clazz) {
         return false;
      } else {
         return clazz.isPrimitive() || isPrimitiveWrapper(clazz);
      }
   }

   public static boolean isSimpleTypeOrArray(Class<?> clazz) {
      if (null == clazz) {
         return false;
      } else {
         return isSimpleValueType(clazz) || clazz.isArray() && isSimpleValueType(clazz.getComponentType());
      }
   }

   public static boolean isSimpleValueType(Class<?> clazz) {
      return isBasicType(clazz) || clazz.isEnum() || CharSequence.class.isAssignableFrom(clazz) || Number.class.isAssignableFrom(clazz) || Date.class.isAssignableFrom(clazz) || clazz.equals(URI.class) || clazz.equals(URL.class) || clazz.equals(Locale.class) || clazz.equals(Class.class) || TemporalAccessor.class.isAssignableFrom(clazz);
   }

   public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
      if (null != targetType && null != sourceType) {
         if (targetType.isAssignableFrom(sourceType)) {
            return true;
         } else {
            Class resolvedWrapper;
            if (targetType.isPrimitive()) {
               resolvedWrapper = (Class)BasicType.WRAPPER_PRIMITIVE_MAP.get(sourceType);
               return targetType.equals(resolvedWrapper);
            } else {
               resolvedWrapper = (Class)BasicType.PRIMITIVE_WRAPPER_MAP.get(sourceType);
               return resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper);
            }
         }
      } else {
         return false;
      }
   }

   public static boolean isPublic(Class<?> clazz) {
      if (null == clazz) {
         throw new NullPointerException("Class to provided is null.");
      } else {
         return Modifier.isPublic(clazz.getModifiers());
      }
   }

   public static boolean isPublic(Method method) {
      Assert.notNull(method, "Method to provided is null.");
      return Modifier.isPublic(method.getModifiers());
   }

   public static boolean isNotPublic(Class<?> clazz) {
      return !isPublic(clazz);
   }

   public static boolean isNotPublic(Method method) {
      return !isPublic(method);
   }

   public static boolean isStatic(Method method) {
      Assert.notNull(method, "Method to provided is null.");
      return Modifier.isStatic(method.getModifiers());
   }

   public static Method setAccessible(Method method) {
      if (null != method && !method.isAccessible()) {
         method.setAccessible(true);
      }

      return method;
   }

   public static boolean isAbstract(Class<?> clazz) {
      return Modifier.isAbstract(clazz.getModifiers());
   }

   public static boolean isNormalClass(Class<?> clazz) {
      return null != clazz && !clazz.isInterface() && !isAbstract(clazz) && !clazz.isEnum() && !clazz.isArray() && !clazz.isAnnotation() && !clazz.isSynthetic() && !clazz.isPrimitive();
   }

   public static boolean isEnum(Class<?> clazz) {
      return null != clazz && clazz.isEnum();
   }

   public static Class<?> getTypeArgument(Class<?> clazz) {
      return getTypeArgument(clazz, 0);
   }

   public static Class<?> getTypeArgument(Class<?> clazz, int index) {
      Type argumentType = TypeUtil.getTypeArgument(clazz, index);
      return TypeUtil.getClass(argumentType);
   }

   public static String getPackage(Class<?> clazz) {
      if (clazz == null) {
         return "";
      } else {
         String className = clazz.getName();
         int packageEndIndex = className.lastIndexOf(".");
         return packageEndIndex == -1 ? "" : className.substring(0, packageEndIndex);
      }
   }

   public static String getPackagePath(Class<?> clazz) {
      return getPackage(clazz).replace('.', '/');
   }

   public static Object getDefaultValue(Class<?> clazz) {
      return clazz.isPrimitive() ? getPrimitiveDefaultValue(clazz) : null;
   }

   public static Object getPrimitiveDefaultValue(Class<?> clazz) {
      if (Long.TYPE == clazz) {
         return 0L;
      } else if (Integer.TYPE == clazz) {
         return 0;
      } else if (Short.TYPE == clazz) {
         return Short.valueOf((short)0);
      } else if (Character.TYPE == clazz) {
         return '\u0000';
      } else if (Byte.TYPE == clazz) {
         return 0;
      } else if (Double.TYPE == clazz) {
         return 0.0;
      } else if (Float.TYPE == clazz) {
         return 0.0F;
      } else {
         return Boolean.TYPE == clazz ? false : null;
      }
   }

   public static Object[] getDefaultValues(Class<?>... classes) {
      Object[] values = new Object[classes.length];

      for(int i = 0; i < classes.length; ++i) {
         values[i] = getDefaultValue(classes[i]);
      }

      return values;
   }

   public static boolean isJdkClass(Class<?> clazz) {
      Package objectPackage = clazz.getPackage();
      if (null == objectPackage) {
         return false;
      } else {
         String objectPackageName = objectPackage.getName();
         return objectPackageName.startsWith("java.") || objectPackageName.startsWith("javax.") || clazz.getClassLoader() == null;
      }
   }

   public static URL getLocation(Class<?> clazz) {
      return null == clazz ? null : clazz.getProtectionDomain().getCodeSource().getLocation();
   }

   public static String getLocationPath(Class<?> clazz) {
      URL location = getLocation(clazz);
      return null == location ? null : location.getPath();
   }

   public static boolean isAbstractOrInterface(Class<?> clazz) {
      return isAbstract(clazz) || isInterface(clazz);
   }

   public static boolean isInterface(Class<?> clazz) {
      return clazz.isInterface();
   }
}
