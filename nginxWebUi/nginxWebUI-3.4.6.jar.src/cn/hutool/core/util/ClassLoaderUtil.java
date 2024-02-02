/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.convert.BasicType;
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.JarClassLoader;
/*     */ import cn.hutool.core.lang.Pair;
/*     */ import cn.hutool.core.map.WeakConcurrentMap;
/*     */ import java.io.File;
/*     */ import java.lang.reflect.Array;
/*     */ import java.security.AccessController;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ClassLoaderUtil
/*     */ {
/*     */   private static final String ARRAY_SUFFIX = "[]";
/*     */   private static final String INTERNAL_ARRAY_PREFIX = "[";
/*     */   private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
/*     */   private static final char PACKAGE_SEPARATOR = '.';
/*     */   private static final char INNER_CLASS_SEPARATOR = '$';
/*  52 */   private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new ConcurrentHashMap<>(32);
/*  53 */   private static final WeakConcurrentMap<Pair<String, ClassLoader>, Class<?>> CLASS_CACHE = new WeakConcurrentMap();
/*     */   
/*     */   static {
/*  56 */     List<Class<?>> primitiveTypes = new ArrayList<>(32);
/*     */     
/*  58 */     primitiveTypes.addAll(BasicType.PRIMITIVE_WRAPPER_MAP.keySet());
/*     */     
/*  60 */     primitiveTypes.add(boolean[].class);
/*  61 */     primitiveTypes.add(byte[].class);
/*  62 */     primitiveTypes.add(char[].class);
/*  63 */     primitiveTypes.add(double[].class);
/*  64 */     primitiveTypes.add(float[].class);
/*  65 */     primitiveTypes.add(int[].class);
/*  66 */     primitiveTypes.add(long[].class);
/*  67 */     primitiveTypes.add(short[].class);
/*  68 */     primitiveTypes.add(void.class);
/*  69 */     for (Class<?> primitiveType : primitiveTypes) {
/*  70 */       PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ClassLoader getContextClassLoader() {
/*  81 */     if (System.getSecurityManager() == null) {
/*  82 */       return Thread.currentThread().getContextClassLoader();
/*     */     }
/*     */     
/*  85 */     return AccessController.<ClassLoader>doPrivileged(() -> Thread.currentThread().getContextClassLoader());
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
/*     */   public static ClassLoader getSystemClassLoader() {
/*  98 */     if (System.getSecurityManager() == null) {
/*  99 */       return ClassLoader.getSystemClassLoader();
/*     */     }
/*     */     
/* 102 */     return AccessController.<ClassLoader>doPrivileged(ClassLoader::getSystemClassLoader);
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
/*     */   public static ClassLoader getClassLoader() {
/* 121 */     ClassLoader classLoader = getContextClassLoader();
/* 122 */     if (classLoader == null) {
/* 123 */       classLoader = ClassLoaderUtil.class.getClassLoader();
/* 124 */       if (null == classLoader) {
/* 125 */         classLoader = getSystemClassLoader();
/*     */       }
/*     */     } 
/* 128 */     return classLoader;
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
/*     */   public static Class<?> loadClass(String name) throws UtilException {
/* 148 */     return loadClass(name, true);
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
/*     */   public static Class<?> loadClass(String name, boolean isInitialized) throws UtilException {
/* 167 */     return loadClass(name, null, isInitialized);
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
/*     */   public static Class<?> loadClass(String name, ClassLoader classLoader, boolean isInitialized) throws UtilException {
/* 189 */     Assert.notNull(name, "Name must not be null", new Object[0]);
/*     */ 
/*     */     
/* 192 */     name = name.replace('/', '.');
/* 193 */     if (null == classLoader) {
/* 194 */       classLoader = getClassLoader();
/*     */     }
/*     */ 
/*     */     
/* 198 */     Class<?> clazz = loadPrimitiveClass(name);
/* 199 */     if (clazz == null) {
/* 200 */       String finalName = name;
/* 201 */       ClassLoader finalClassLoader = classLoader;
/* 202 */       clazz = (Class)CLASS_CACHE.computeIfAbsent(Pair.of(name, classLoader), key -> doLoadClass(finalName, finalClassLoader, isInitialized));
/*     */     } 
/* 204 */     return clazz;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> loadPrimitiveClass(String name) {
/* 214 */     Class<?> result = null;
/* 215 */     if (StrUtil.isNotBlank(name)) {
/* 216 */       name = name.trim();
/* 217 */       if (name.length() <= 8) {
/* 218 */         result = PRIMITIVE_TYPE_NAME_MAP.get(name);
/*     */       }
/*     */     } 
/* 221 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static JarClassLoader getJarClassLoader(File jarOrDir) {
/* 232 */     return JarClassLoader.load(jarOrDir);
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
/*     */   public static Class<?> loadClass(File jarOrDir, String name) {
/*     */     try {
/* 245 */       return getJarClassLoader(jarOrDir).loadClass(name);
/* 246 */     } catch (ClassNotFoundException e) {
/* 247 */       throw new UtilException(e);
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
/*     */   public static boolean isPresent(String className) {
/* 262 */     return isPresent(className, null);
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
/*     */   public static boolean isPresent(String className, ClassLoader classLoader) {
/*     */     try {
/* 276 */       loadClass(className, classLoader, false);
/* 277 */       return true;
/* 278 */     } catch (Throwable ex) {
/* 279 */       return false;
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
/*     */   private static Class<?> doLoadClass(String name, ClassLoader classLoader, boolean isInitialized) {
/*     */     Class<?> clazz;
/* 293 */     if (name.endsWith("[]")) {
/*     */       
/* 295 */       String elementClassName = name.substring(0, name.length() - "[]".length());
/* 296 */       Class<?> elementClass = loadClass(elementClassName, classLoader, isInitialized);
/* 297 */       clazz = Array.newInstance(elementClass, 0).getClass();
/* 298 */     } else if (name.startsWith("[L") && name.endsWith(";")) {
/*     */       
/* 300 */       String elementName = name.substring("[L".length(), name.length() - 1);
/* 301 */       Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
/* 302 */       clazz = Array.newInstance(elementClass, 0).getClass();
/* 303 */     } else if (name.startsWith("[")) {
/*     */       
/* 305 */       String elementName = name.substring("[".length());
/* 306 */       Class<?> elementClass = loadClass(elementName, classLoader, isInitialized);
/* 307 */       clazz = Array.newInstance(elementClass, 0).getClass();
/*     */     } else {
/*     */       
/* 310 */       if (null == classLoader) {
/* 311 */         classLoader = getClassLoader();
/*     */       }
/*     */       try {
/* 314 */         clazz = Class.forName(name, isInitialized, classLoader);
/* 315 */       } catch (ClassNotFoundException ex) {
/*     */         
/* 317 */         clazz = tryLoadInnerClass(name, classLoader, isInitialized);
/* 318 */         if (null == clazz) {
/* 319 */           throw new UtilException(ex);
/*     */         }
/*     */       } 
/*     */     } 
/* 323 */     return clazz;
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
/*     */   private static Class<?> tryLoadInnerClass(String name, ClassLoader classLoader, boolean isInitialized) {
/* 337 */     int lastDotIndex = name.lastIndexOf('.');
/* 338 */     if (lastDotIndex > 0) {
/* 339 */       String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);
/*     */       try {
/* 341 */         return Class.forName(innerClassName, isInitialized, classLoader);
/* 342 */       } catch (ClassNotFoundException classNotFoundException) {}
/*     */     } 
/*     */ 
/*     */     
/* 346 */     return null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ClassLoaderUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */