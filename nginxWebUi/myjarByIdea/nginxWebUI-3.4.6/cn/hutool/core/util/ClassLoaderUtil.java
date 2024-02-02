package cn.hutool.core.util;

import cn.hutool.core.convert.BasicType;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.JarClassLoader;
import cn.hutool.core.lang.Pair;
import cn.hutool.core.map.WeakConcurrentMap;
import java.io.File;
import java.lang.reflect.Array;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassLoaderUtil {
   private static final String ARRAY_SUFFIX = "[]";
   private static final String INTERNAL_ARRAY_PREFIX = "[";
   private static final String NON_PRIMITIVE_ARRAY_PREFIX = "[L";
   private static final char PACKAGE_SEPARATOR = '.';
   private static final char INNER_CLASS_SEPARATOR = '$';
   private static final Map<String, Class<?>> PRIMITIVE_TYPE_NAME_MAP = new ConcurrentHashMap(32);
   private static final WeakConcurrentMap<Pair<String, ClassLoader>, Class<?>> CLASS_CACHE = new WeakConcurrentMap();

   public static ClassLoader getContextClassLoader() {
      return System.getSecurityManager() == null ? Thread.currentThread().getContextClassLoader() : (ClassLoader)AccessController.doPrivileged(() -> {
         return Thread.currentThread().getContextClassLoader();
      });
   }

   public static ClassLoader getSystemClassLoader() {
      return System.getSecurityManager() == null ? ClassLoader.getSystemClassLoader() : (ClassLoader)AccessController.doPrivileged(ClassLoader::getSystemClassLoader);
   }

   public static ClassLoader getClassLoader() {
      ClassLoader classLoader = getContextClassLoader();
      if (classLoader == null) {
         classLoader = ClassLoaderUtil.class.getClassLoader();
         if (null == classLoader) {
            classLoader = getSystemClassLoader();
         }
      }

      return classLoader;
   }

   public static Class<?> loadClass(String name) throws UtilException {
      return loadClass(name, true);
   }

   public static Class<?> loadClass(String name, boolean isInitialized) throws UtilException {
      return loadClass(name, (ClassLoader)null, isInitialized);
   }

   public static Class<?> loadClass(String name, ClassLoader classLoader, boolean isInitialized) throws UtilException {
      Assert.notNull(name, "Name must not be null");
      name = name.replace('/', '.');
      if (null == classLoader) {
         classLoader = getClassLoader();
      }

      Class<?> clazz = loadPrimitiveClass(name);
      if (clazz == null) {
         clazz = (Class)CLASS_CACHE.computeIfAbsent(Pair.of(name, classLoader), (key) -> {
            return doLoadClass(name, classLoader, isInitialized);
         });
      }

      return clazz;
   }

   public static Class<?> loadPrimitiveClass(String name) {
      Class<?> result = null;
      if (StrUtil.isNotBlank(name)) {
         name = name.trim();
         if (name.length() <= 8) {
            result = (Class)PRIMITIVE_TYPE_NAME_MAP.get(name);
         }
      }

      return result;
   }

   public static JarClassLoader getJarClassLoader(File jarOrDir) {
      return JarClassLoader.load(jarOrDir);
   }

   public static Class<?> loadClass(File jarOrDir, String name) {
      try {
         return getJarClassLoader(jarOrDir).loadClass(name);
      } catch (ClassNotFoundException var3) {
         throw new UtilException(var3);
      }
   }

   public static boolean isPresent(String className) {
      return isPresent(className, (ClassLoader)null);
   }

   public static boolean isPresent(String className, ClassLoader classLoader) {
      try {
         loadClass(className, classLoader, false);
         return true;
      } catch (Throwable var3) {
         return false;
      }
   }

   private static Class<?> doLoadClass(String name, ClassLoader classLoader, boolean isInitialized) {
      Class clazz;
      String elementName;
      Class elementClass;
      if (name.endsWith("[]")) {
         elementName = name.substring(0, name.length() - "[]".length());
         elementClass = loadClass(elementName, classLoader, isInitialized);
         clazz = Array.newInstance(elementClass, 0).getClass();
      } else if (name.startsWith("[L") && name.endsWith(";")) {
         elementName = name.substring("[L".length(), name.length() - 1);
         elementClass = loadClass(elementName, classLoader, isInitialized);
         clazz = Array.newInstance(elementClass, 0).getClass();
      } else if (name.startsWith("[")) {
         elementName = name.substring("[".length());
         elementClass = loadClass(elementName, classLoader, isInitialized);
         clazz = Array.newInstance(elementClass, 0).getClass();
      } else {
         if (null == classLoader) {
            classLoader = getClassLoader();
         }

         try {
            clazz = Class.forName(name, isInitialized, classLoader);
         } catch (ClassNotFoundException var6) {
            clazz = tryLoadInnerClass(name, classLoader, isInitialized);
            if (null == clazz) {
               throw new UtilException(var6);
            }
         }
      }

      return clazz;
   }

   private static Class<?> tryLoadInnerClass(String name, ClassLoader classLoader, boolean isInitialized) {
      int lastDotIndex = name.lastIndexOf(46);
      if (lastDotIndex > 0) {
         String innerClassName = name.substring(0, lastDotIndex) + '$' + name.substring(lastDotIndex + 1);

         try {
            return Class.forName(innerClassName, isInitialized, classLoader);
         } catch (ClassNotFoundException var6) {
         }
      }

      return null;
   }

   static {
      List<Class<?>> primitiveTypes = new ArrayList(32);
      primitiveTypes.addAll(BasicType.PRIMITIVE_WRAPPER_MAP.keySet());
      primitiveTypes.add(boolean[].class);
      primitiveTypes.add(byte[].class);
      primitiveTypes.add(char[].class);
      primitiveTypes.add(double[].class);
      primitiveTypes.add(float[].class);
      primitiveTypes.add(int[].class);
      primitiveTypes.add(long[].class);
      primitiveTypes.add(short[].class);
      primitiveTypes.add(Void.TYPE);
      Iterator var1 = primitiveTypes.iterator();

      while(var1.hasNext()) {
         Class<?> primitiveType = (Class)var1.next();
         PRIMITIVE_TYPE_NAME_MAP.put(primitiveType.getName(), primitiveType);
      }

   }
}
