package org.noear.solon.core.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenericUtil {
   private static final Map<Type, Map<String, Type>> genericInfoCached = new HashMap();

   public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> genericIfc) {
      Type[] var2 = clazz.getGenericInterfaces();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Type type0 = var2[var4];
         if (type0 instanceof ParameterizedType) {
            ParameterizedType type = (ParameterizedType)type0;
            Class<?> rawType = (Class)type.getRawType();
            if (rawType == genericIfc || getGenericInterfaces(rawType).contains(genericIfc)) {
               return (Class[])Arrays.stream(type.getActualTypeArguments()).map((item) -> {
                  return (Class)item;
               }).toArray((x$0) -> {
                  return new Class[x$0];
               });
            }
         }
      }

      Type type1 = clazz.getGenericSuperclass();
      if (type1 instanceof ParameterizedType) {
         ParameterizedType type = (ParameterizedType)type1;
         return (Class[])Arrays.stream(type.getActualTypeArguments()).map((item) -> {
            return (Class)item;
         }).toArray((x$0) -> {
            return new Class[x$0];
         });
      } else {
         return null;
      }
   }

   private static List<Class<?>> getGenericInterfaces(Class<?> clazz) {
      return getGenericInterfaces(clazz, new ArrayList());
   }

   private static List<Class<?>> getGenericInterfaces(Class<?> clazz, List<Class<?>> classes) {
      Type[] interfaces = clazz.getGenericInterfaces();
      Type[] var3 = interfaces;
      int var4 = interfaces.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         Type type = var3[var5];
         if (type instanceof ParameterizedType) {
            Class<?> aClass = (Class)((ParameterizedType)type).getRawType();
            classes.add(aClass);
            Type[] var8 = aClass.getGenericInterfaces();
            int var9 = var8.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               Type type0 = var8[var10];
               if (type0 instanceof ParameterizedType) {
                  getGenericInterfaces((Class)((ParameterizedType)type0).getRawType(), classes);
               }
            }
         }
      }

      return classes;
   }

   public static ParameterizedType toParameterizedType(Type type) {
      ParameterizedType result = null;
      if (type instanceof ParameterizedType) {
         result = (ParameterizedType)type;
      } else if (type instanceof Class) {
         Class<?> clazz = (Class)type;
         Type genericSuper = clazz.getGenericSuperclass();
         if (null == genericSuper || Object.class.equals(genericSuper)) {
            Type[] genericInterfaces = clazz.getGenericInterfaces();
            if (genericInterfaces != null && genericInterfaces.length > 0) {
               genericSuper = genericInterfaces[0];
            }
         }

         result = toParameterizedType(genericSuper);
      }

      return result;
   }

   public static Map<String, Type> getGenericInfo(Type type) {
      Map<String, Type> tmp = (Map)genericInfoCached.get(type);
      if (tmp == null) {
         synchronized(type) {
            tmp = (Map)genericInfoCached.get(type);
            if (tmp == null) {
               tmp = createTypeGenericMap(type);
               genericInfoCached.put(type, tmp);
            }
         }
      }

      return tmp;
   }

   private static Map<String, Type> createTypeGenericMap(Type type) {
      HashMap typeMap;
      Class rawType;
      for(typeMap = new HashMap(); null != type; type = rawType) {
         ParameterizedType parameterizedType = toParameterizedType((Type)type);
         if (null == parameterizedType) {
            break;
         }

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         rawType = (Class)parameterizedType.getRawType();
         TypeVariable[] typeParameters = rawType.getTypeParameters();

         for(int i = 0; i < typeParameters.length; ++i) {
            Type value = typeArguments[i];
            if (!(value instanceof TypeVariable)) {
               typeMap.put(typeParameters[i].getTypeName(), value);
            }
         }
      }

      return typeMap;
   }
}
