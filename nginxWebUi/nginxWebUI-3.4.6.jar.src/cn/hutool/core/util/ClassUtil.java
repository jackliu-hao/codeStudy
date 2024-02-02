/*      */ package cn.hutool.core.util;
/*      */ 
/*      */ import cn.hutool.core.bean.NullWrapperBean;
/*      */ import cn.hutool.core.convert.BasicType;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.io.FileUtil;
/*      */ import cn.hutool.core.io.IORuntimeException;
/*      */ import cn.hutool.core.io.resource.ResourceUtil;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.lang.ClassScanner;
/*      */ import cn.hutool.core.lang.Filter;
/*      */ import cn.hutool.core.lang.Singleton;
/*      */ import java.io.IOException;
/*      */ import java.lang.annotation.Annotation;
/*      */ import java.lang.reflect.Field;
/*      */ import java.lang.reflect.Method;
/*      */ import java.lang.reflect.Modifier;
/*      */ import java.lang.reflect.Type;
/*      */ import java.net.URI;
/*      */ import java.net.URL;
/*      */ import java.time.temporal.TemporalAccessor;
/*      */ import java.util.Date;
/*      */ import java.util.Enumeration;
/*      */ import java.util.HashSet;
/*      */ import java.util.List;
/*      */ import java.util.Locale;
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
/*      */ 
/*      */ 
/*      */ 
/*      */ public class ClassUtil
/*      */ {
/*      */   public static <T> Class<T> getClass(T obj) {
/*   46 */     return (null == obj) ? null : (Class)obj.getClass();
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
/*      */   public static Class<?> getEnclosingClass(Class<?> clazz) {
/*   58 */     return (null == clazz) ? null : clazz.getEnclosingClass();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isTopLevelClass(Class<?> clazz) {
/*   69 */     if (null == clazz) {
/*   70 */       return false;
/*      */     }
/*   72 */     return (null == getEnclosingClass(clazz));
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
/*      */   public static String getClassName(Object obj, boolean isSimple) {
/*   84 */     if (null == obj) {
/*   85 */       return null;
/*      */     }
/*   87 */     Class<?> clazz = obj.getClass();
/*   88 */     return getClassName(clazz, isSimple);
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
/*      */   public static String getClassName(Class<?> clazz, boolean isSimple) {
/*  107 */     if (null == clazz) {
/*  108 */       return null;
/*      */     }
/*  110 */     return isSimple ? clazz.getSimpleName() : clazz.getName();
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
/*      */   public static String getShortClassName(String className) {
/*  122 */     List<String> packages = StrUtil.split(className, '.');
/*  123 */     if (null == packages || packages.size() < 2) {
/*  124 */       return className;
/*      */     }
/*      */     
/*  127 */     int size = packages.size();
/*  128 */     StringBuilder result = StrUtil.builder();
/*  129 */     result.append(((String)packages.get(0)).charAt(0));
/*  130 */     for (int i = 1; i < size - 1; i++) {
/*  131 */       result.append('.').append(((String)packages.get(i)).charAt(0));
/*      */     }
/*  133 */     result.append('.').append(packages.get(size - 1));
/*  134 */     return result.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?>[] getClasses(Object... objects) {
/*  144 */     Class<?>[] classes = new Class[objects.length];
/*      */     
/*  146 */     for (int i = 0; i < objects.length; i++) {
/*  147 */       Object obj = objects[i];
/*  148 */       if (obj instanceof NullWrapperBean) {
/*      */         
/*  150 */         classes[i] = ((NullWrapperBean)obj).getWrappedClass();
/*  151 */       } else if (null == obj) {
/*  152 */         classes[i] = Object.class;
/*      */       } else {
/*  154 */         classes[i] = obj.getClass();
/*      */       } 
/*      */     } 
/*  157 */     return classes;
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
/*      */   public static boolean equals(Class<?> clazz, String className, boolean ignoreCase) {
/*  170 */     if (null == clazz || StrUtil.isBlank(className)) {
/*  171 */       return false;
/*      */     }
/*  173 */     if (ignoreCase) {
/*  174 */       return (className.equalsIgnoreCase(clazz.getName()) || className.equalsIgnoreCase(clazz.getSimpleName()));
/*      */     }
/*  176 */     return (className.equals(clazz.getName()) || className.equals(clazz.getSimpleName()));
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
/*      */   public static Set<Class<?>> scanPackageByAnnotation(String packageName, Class<? extends Annotation> annotationClass) {
/*  191 */     return ClassScanner.scanPackageByAnnotation(packageName, annotationClass);
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
/*      */   public static Set<Class<?>> scanPackageBySuper(String packageName, Class<?> superClass) {
/*  203 */     return ClassScanner.scanPackageBySuper(packageName, superClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<Class<?>> scanPackage() {
/*  213 */     return ClassScanner.scanPackage();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<Class<?>> scanPackage(String packageName) {
/*  224 */     return ClassScanner.scanPackage(packageName);
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
/*      */   public static Set<Class<?>> scanPackage(String packageName, Filter<Class<?>> classFilter) {
/*  237 */     return ClassScanner.scanPackage(packageName, classFilter);
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
/*  250 */     return ReflectUtil.getPublicMethodNames(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getPublicMethods(Class<?> clazz) {
/*  260 */     return ReflectUtil.getPublicMethods(clazz);
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
/*  271 */     return ReflectUtil.getPublicMethods(clazz, filter);
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
/*  282 */     return ReflectUtil.getPublicMethods(clazz, excludeMethods);
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
/*  293 */     return ReflectUtil.getPublicMethods(clazz, excludeMethodNames);
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
/*  306 */     return ReflectUtil.getPublicMethod(clazz, methodName, paramTypes);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getDeclaredMethodNames(Class<?> clazz) {
/*  317 */     return ReflectUtil.getMethodNames(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method[] getDeclaredMethods(Class<?> clazz) {
/*  327 */     return ReflectUtil.getMethods(clazz);
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
/*      */   public static Method getDeclaredMethodOfObj(Object obj, String methodName, Object... args) throws SecurityException {
/*  340 */     return getDeclaredMethod(obj.getClass(), methodName, getClasses(args));
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
/*      */   public static Method getDeclaredMethod(Class<?> clazz, String methodName, Class<?>... parameterTypes) throws SecurityException {
/*  353 */     return ReflectUtil.getMethod(clazz, methodName, parameterTypes);
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
/*      */   public static Field getDeclaredField(Class<?> clazz, String fieldName) throws SecurityException {
/*  367 */     if (null == clazz || StrUtil.isBlank(fieldName)) {
/*  368 */       return null;
/*      */     }
/*      */     try {
/*  371 */       return clazz.getDeclaredField(fieldName);
/*  372 */     } catch (NoSuchFieldException noSuchFieldException) {
/*      */ 
/*      */       
/*  375 */       return null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Field[] getDeclaredFields(Class<?> clazz) throws SecurityException {
/*  386 */     if (null == clazz) {
/*  387 */       return null;
/*      */     }
/*  389 */     return clazz.getDeclaredFields();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getClassPathResources() {
/*  400 */     return getClassPathResources(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getClassPathResources(boolean isDecode) {
/*  411 */     return getClassPaths("", isDecode);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getClassPaths(String packageName) {
/*  421 */     return getClassPaths(packageName, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Set<String> getClassPaths(String packageName, boolean isDecode) {
/*      */     Enumeration<URL> resources;
/*  433 */     String packagePath = packageName.replace(".", "/");
/*      */     
/*      */     try {
/*  436 */       resources = getClassLoader().getResources(packagePath);
/*  437 */     } catch (IOException e) {
/*  438 */       throw new UtilException(e, "Loading classPath [{}] error!", new Object[] { packagePath });
/*      */     } 
/*  440 */     Set<String> paths = new HashSet<>();
/*      */     
/*  442 */     while (resources.hasMoreElements()) {
/*  443 */       String path = ((URL)resources.nextElement()).getPath();
/*  444 */       paths.add(isDecode ? URLUtil.decode(path, CharsetUtil.systemCharsetName()) : path);
/*      */     } 
/*  446 */     return paths;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getClassPath() {
/*  456 */     return getClassPath(false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getClassPath(boolean isEncoded) {
/*  467 */     URL classPathURL = getClassPathURL();
/*  468 */     String url = isEncoded ? classPathURL.getPath() : URLUtil.getDecodedPath(classPathURL);
/*  469 */     return FileUtil.normalize(url);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static URL getClassPathURL() {
/*  478 */     return getResourceURL("");
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
/*      */   public static URL getResourceURL(String resource) throws IORuntimeException {
/*  495 */     return ResourceUtil.getResource(resource);
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
/*      */   public static List<URL> getResources(String resource) {
/*  512 */     return ResourceUtil.getResources(resource);
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
/*      */   public static URL getResourceUrl(String resource, Class<?> baseClass) {
/*  524 */     return ResourceUtil.getResource(resource, baseClass);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] getJavaClassPaths() {
/*  531 */     return System.getProperty("java.class.path").split(System.getProperty("path.separator"));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ClassLoader getContextClassLoader() {
/*  541 */     return ClassLoaderUtil.getContextClassLoader();
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
/*      */   public static ClassLoader getClassLoader() {
/*  557 */     return ClassLoaderUtil.getClassLoader();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAllAssignableFrom(Class<?>[] types1, Class<?>[] types2) {
/*  568 */     if (ArrayUtil.isEmpty(types1) && ArrayUtil.isEmpty(types2)) {
/*  569 */       return true;
/*      */     }
/*  571 */     if (null == types1 || null == types2)
/*      */     {
/*  573 */       return false;
/*      */     }
/*  575 */     if (types1.length != types2.length) {
/*  576 */       return false;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  581 */     for (int i = 0; i < types1.length; i++) {
/*  582 */       Class<?> type1 = types1[i];
/*  583 */       Class<?> type2 = types2[i];
/*  584 */       if (isBasicType(type1) && isBasicType(type2)) {
/*      */         
/*  586 */         if (BasicType.unWrap(type1) != BasicType.unWrap(type2)) {
/*  587 */           return false;
/*      */         }
/*  589 */       } else if (false == type1.isAssignableFrom(type2)) {
/*  590 */         return false;
/*      */       } 
/*      */     } 
/*  593 */     return true;
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
/*      */   public static <T> Class<T> loadClass(String className, boolean isInitialized) {
/*  606 */     return (Class)ClassLoaderUtil.loadClass(className, isInitialized);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> Class<T> loadClass(String className) {
/*  617 */     return loadClass(className, true);
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
/*      */   public static <T> T invoke(String classNameWithMethodName, Object[] args) {
/*  634 */     return invoke(classNameWithMethodName, false, args);
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
/*      */   public static <T> T invoke(String classNameWithMethodName, boolean isSingleton, Object... args) {
/*  649 */     if (StrUtil.isBlank(classNameWithMethodName)) {
/*  650 */       throw new UtilException("Blank classNameDotMethodName!");
/*      */     }
/*      */     
/*  653 */     int splitIndex = classNameWithMethodName.lastIndexOf('#');
/*  654 */     if (splitIndex <= 0) {
/*  655 */       splitIndex = classNameWithMethodName.lastIndexOf('.');
/*      */     }
/*  657 */     if (splitIndex <= 0) {
/*  658 */       throw new UtilException("Invalid classNameWithMethodName [{}]!", new Object[] { classNameWithMethodName });
/*      */     }
/*      */     
/*  661 */     String className = classNameWithMethodName.substring(0, splitIndex);
/*  662 */     String methodName = classNameWithMethodName.substring(splitIndex + 1);
/*      */     
/*  664 */     return invoke(className, methodName, isSingleton, args);
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
/*      */   public static <T> T invoke(String className, String methodName, Object[] args) {
/*  680 */     return invoke(className, methodName, false, args);
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
/*      */   public static <T> T invoke(String className, String methodName, boolean isSingleton, Object... args) {
/*  696 */     Class<Object> clazz = loadClass(className);
/*      */     try {
/*  698 */       Method method = getDeclaredMethod(clazz, methodName, getClasses(args));
/*  699 */       if (null == method) {
/*  700 */         throw new NoSuchMethodException(StrUtil.format("No such method: [{}]", new Object[] { methodName }));
/*      */       }
/*  702 */       if (isStatic(method)) {
/*  703 */         return ReflectUtil.invoke((Object)null, method, args);
/*      */       }
/*  705 */       return ReflectUtil.invoke(isSingleton ? Singleton.get(clazz, new Object[0]) : clazz.newInstance(), method, args);
/*      */     }
/*  707 */     catch (Exception e) {
/*  708 */       throw new UtilException(e);
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
/*      */   public static boolean isPrimitiveWrapper(Class<?> clazz) {
/*  721 */     if (null == clazz) {
/*  722 */       return false;
/*      */     }
/*  724 */     return BasicType.WRAPPER_PRIMITIVE_MAP.containsKey(clazz);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isBasicType(Class<?> clazz) {
/*  734 */     if (null == clazz) {
/*  735 */       return false;
/*      */     }
/*  737 */     return (clazz.isPrimitive() || isPrimitiveWrapper(clazz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isSimpleTypeOrArray(Class<?> clazz) {
/*  748 */     if (null == clazz) {
/*  749 */       return false;
/*      */     }
/*  751 */     return (isSimpleValueType(clazz) || (clazz.isArray() && isSimpleValueType(clazz.getComponentType())));
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
/*      */   public static boolean isSimpleValueType(Class<?> clazz) {
/*  772 */     return (isBasicType(clazz) || clazz
/*  773 */       .isEnum() || CharSequence.class
/*  774 */       .isAssignableFrom(clazz) || Number.class
/*  775 */       .isAssignableFrom(clazz) || Date.class
/*  776 */       .isAssignableFrom(clazz) || clazz
/*  777 */       .equals(URI.class) || clazz
/*  778 */       .equals(URL.class) || clazz
/*  779 */       .equals(Locale.class) || clazz
/*  780 */       .equals(Class.class) || TemporalAccessor.class
/*      */       
/*  782 */       .isAssignableFrom(clazz));
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
/*      */   public static boolean isAssignable(Class<?> targetType, Class<?> sourceType) {
/*  797 */     if (null == targetType || null == sourceType) {
/*  798 */       return false;
/*      */     }
/*      */ 
/*      */     
/*  802 */     if (targetType.isAssignableFrom(sourceType)) {
/*  803 */       return true;
/*      */     }
/*      */ 
/*      */     
/*  807 */     if (targetType.isPrimitive()) {
/*      */       
/*  809 */       Class<?> resolvedPrimitive = (Class)BasicType.WRAPPER_PRIMITIVE_MAP.get(sourceType);
/*  810 */       return targetType.equals(resolvedPrimitive);
/*      */     } 
/*      */     
/*  813 */     Class<?> resolvedWrapper = (Class)BasicType.PRIMITIVE_WRAPPER_MAP.get(sourceType);
/*  814 */     return (resolvedWrapper != null && targetType.isAssignableFrom(resolvedWrapper));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPublic(Class<?> clazz) {
/*  825 */     if (null == clazz) {
/*  826 */       throw new NullPointerException("Class to provided is null.");
/*      */     }
/*  828 */     return Modifier.isPublic(clazz.getModifiers());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isPublic(Method method) {
/*  838 */     Assert.notNull(method, "Method to provided is null.", new Object[0]);
/*  839 */     return Modifier.isPublic(method.getModifiers());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotPublic(Class<?> clazz) {
/*  849 */     return (false == isPublic(clazz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isNotPublic(Method method) {
/*  859 */     return (false == isPublic(method));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isStatic(Method method) {
/*  869 */     Assert.notNull(method, "Method to provided is null.", new Object[0]);
/*  870 */     return Modifier.isStatic(method.getModifiers());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Method setAccessible(Method method) {
/*  880 */     if (null != method && false == method.isAccessible()) {
/*  881 */       method.setAccessible(true);
/*      */     }
/*  883 */     return method;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAbstract(Class<?> clazz) {
/*  893 */     return Modifier.isAbstract(clazz.getModifiers());
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
/*      */   public static boolean isNormalClass(Class<?> clazz) {
/*  913 */     return (null != clazz && false == clazz
/*  914 */       .isInterface() && false == 
/*  915 */       isAbstract(clazz) && false == clazz
/*  916 */       .isEnum() && false == clazz
/*  917 */       .isArray() && false == clazz
/*  918 */       .isAnnotation() && false == clazz
/*  919 */       .isSynthetic() && false == clazz
/*  920 */       .isPrimitive());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isEnum(Class<?> clazz) {
/*  931 */     return (null != clazz && clazz.isEnum());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getTypeArgument(Class<?> clazz) {
/*  941 */     return getTypeArgument(clazz, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Class<?> getTypeArgument(Class<?> clazz, int index) {
/*  952 */     Type argumentType = TypeUtil.getTypeArgument(clazz, index);
/*  953 */     return TypeUtil.getClass(argumentType);
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
/*      */   public static String getPackage(Class<?> clazz) {
/*  965 */     if (clazz == null) {
/*  966 */       return "";
/*      */     }
/*  968 */     String className = clazz.getName();
/*  969 */     int packageEndIndex = className.lastIndexOf(".");
/*  970 */     if (packageEndIndex == -1) {
/*  971 */       return "";
/*      */     }
/*  973 */     return className.substring(0, packageEndIndex);
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
/*      */   public static String getPackagePath(Class<?> clazz) {
/*  985 */     return getPackage(clazz).replace('.', '/');
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
/*      */   public static Object getDefaultValue(Class<?> clazz) {
/* 1003 */     if (clazz.isPrimitive()) {
/* 1004 */       return getPrimitiveDefaultValue(clazz);
/*      */     }
/* 1006 */     return null;
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
/*      */   public static Object getPrimitiveDefaultValue(Class<?> clazz) {
/* 1023 */     if (long.class == clazz)
/* 1024 */       return Long.valueOf(0L); 
/* 1025 */     if (int.class == clazz)
/* 1026 */       return Integer.valueOf(0); 
/* 1027 */     if (short.class == clazz)
/* 1028 */       return Short.valueOf((short)0); 
/* 1029 */     if (char.class == clazz)
/* 1030 */       return Character.valueOf(false); 
/* 1031 */     if (byte.class == clazz)
/* 1032 */       return Byte.valueOf((byte)0); 
/* 1033 */     if (double.class == clazz)
/* 1034 */       return Double.valueOf(0.0D); 
/* 1035 */     if (float.class == clazz)
/* 1036 */       return Float.valueOf(0.0F); 
/* 1037 */     if (boolean.class == clazz) {
/* 1038 */       return Boolean.valueOf(false);
/*      */     }
/* 1040 */     return null;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object[] getDefaultValues(Class<?>... classes) {
/* 1051 */     Object[] values = new Object[classes.length];
/* 1052 */     for (int i = 0; i < classes.length; i++) {
/* 1053 */       values[i] = getDefaultValue(classes[i]);
/*      */     }
/* 1055 */     return values;
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
/*      */   public static boolean isJdkClass(Class<?> clazz) {
/* 1071 */     Package objectPackage = clazz.getPackage();
/* 1072 */     if (null == objectPackage) {
/* 1073 */       return false;
/*      */     }
/* 1075 */     String objectPackageName = objectPackage.getName();
/* 1076 */     return (objectPackageName.startsWith("java.") || objectPackageName
/* 1077 */       .startsWith("javax.") || clazz
/* 1078 */       .getClassLoader() == null);
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
/*      */   public static URL getLocation(Class<?> clazz) {
/* 1091 */     if (null == clazz) {
/* 1092 */       return null;
/*      */     }
/* 1094 */     return clazz.getProtectionDomain().getCodeSource().getLocation();
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
/*      */   public static String getLocationPath(Class<?> clazz) {
/* 1107 */     URL location = getLocation(clazz);
/* 1108 */     if (null == location) {
/* 1109 */       return null;
/*      */     }
/* 1111 */     return location.getPath();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isAbstractOrInterface(Class<?> clazz) {
/* 1122 */     return (isAbstract(clazz) || isInterface(clazz));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isInterface(Class<?> clazz) {
/* 1133 */     return clazz.isInterface();
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ClassUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */