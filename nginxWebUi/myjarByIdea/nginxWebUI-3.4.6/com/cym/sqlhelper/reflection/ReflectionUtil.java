package com.cym.sqlhelper.reflection;

import java.beans.Introspector;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ReflectionUtil {
   private static Map<SerializableFunction<?, ?>, Field> cache = new ConcurrentHashMap();
   private static final Map<Class<?>, Field[]> declaredFieldsCache = new ConcurrentHashMap(256);
   private static final Field[] EMPTY_FIELD_ARRAY = new Field[0];

   public static <T, R> String getFieldName(SerializableFunction<T, R> function) {
      Field field = getField(function);
      return field.getName();
   }

   public static Field getField(SerializableFunction<?, ?> function) {
      return (Field)cache.computeIfAbsent(function, ReflectionUtil::findField);
   }

   public static Field findField(SerializableFunction<?, ?> function) {
      Field field = null;
      String fieldName = null;

      try {
         Method method = function.getClass().getDeclaredMethod("writeReplace");
         method.setAccessible(Boolean.TRUE);
         SerializedLambda serializedLambda = (SerializedLambda)method.invoke(function);
         String implMethodName = serializedLambda.getImplMethodName();
         if (implMethodName.startsWith("get") && implMethodName.length() > 3) {
            fieldName = Introspector.decapitalize(implMethodName.substring(3));
         } else {
            if (!implMethodName.startsWith("is") || implMethodName.length() <= 2) {
               if (implMethodName.startsWith("lambda$")) {
                  throw new IllegalArgumentException("SerializableFunction不能传递lambda表达式,只能使用方法引用");
               }

               throw new IllegalArgumentException(implMethodName + "不是Getter方法引用");
            }

            fieldName = Introspector.decapitalize(implMethodName.substring(2));
         }

         String declaredClass = serializedLambda.getImplClass().replace("/", ".");
         Class<?> aClass = Class.forName(declaredClass, false, getDefaultClassLoader());
         field = findField(aClass, fieldName, (Class)null);
      } catch (Exception var8) {
         var8.printStackTrace();
      }

      if (field != null) {
         return field;
      } else {
         throw new NoSuchFieldError(fieldName);
      }
   }

   public static Field findField(Class<?> clazz, String name, Class<?> type) {
      for(Class<?> searchType = clazz; Object.class != searchType && searchType != null; searchType = searchType.getSuperclass()) {
         Field[] fields = getDeclaredFields(searchType);
         Field[] var5 = fields;
         int var6 = fields.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Field field = var5[var7];
            if ((name == null || name.equals(field.getName())) && (type == null || type.equals(field.getType()))) {
               return field;
            }
         }
      }

      return null;
   }

   private static Field[] getDeclaredFields(Class<?> clazz) {
      Field[] result = (Field[])declaredFieldsCache.get(clazz);
      if (result == null) {
         try {
            result = clazz.getDeclaredFields();
            declaredFieldsCache.put(clazz, result.length == 0 ? EMPTY_FIELD_ARRAY : result);
         } catch (Throwable var3) {
            throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", var3);
         }
      }

      return result;
   }

   public static ClassLoader getDefaultClassLoader() {
      ClassLoader cl = null;

      try {
         cl = Thread.currentThread().getContextClassLoader();
      } catch (Throwable var3) {
      }

      if (cl == null) {
         cl = ReflectionUtil.class.getClassLoader();
         if (cl == null) {
            try {
               cl = ClassLoader.getSystemClassLoader();
            } catch (Throwable var2) {
            }
         }
      }

      return cl;
   }
}
