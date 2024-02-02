package cn.hutool.core.lang.reflect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.WeakConcurrentMap;
import cn.hutool.core.util.TypeUtil;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public class ActualTypeMapperPool {
   private static final WeakConcurrentMap<Type, Map<Type, Type>> CACHE = new WeakConcurrentMap();

   public static Map<Type, Type> get(Type type) {
      return (Map)CACHE.computeIfAbsent(type, (key) -> {
         return createTypeMap(type);
      });
   }

   public static Map<String, Type> getStrKeyMap(Type type) {
      return Convert.toMap(String.class, Type.class, get(type));
   }

   public static Type getActualType(Type type, TypeVariable<?> typeVariable) {
      Map<Type, Type> typeTypeMap = get(type);

      Type result;
      for(result = (Type)typeTypeMap.get(typeVariable); result instanceof TypeVariable; result = (Type)typeTypeMap.get(result)) {
      }

      return result;
   }

   public static Type[] getActualTypes(Type type, Type... typeVariables) {
      Type[] result = new Type[typeVariables.length];

      for(int i = 0; i < typeVariables.length; ++i) {
         result[i] = typeVariables[i] instanceof TypeVariable ? getActualType(type, (TypeVariable)typeVariables[i]) : typeVariables[i];
      }

      return result;
   }

   private static Map<Type, Type> createTypeMap(Type type) {
      HashMap typeMap;
      Class rawType;
      for(typeMap = new HashMap(); null != type; type = rawType) {
         ParameterizedType parameterizedType = TypeUtil.toParameterizedType((Type)type);
         if (null == parameterizedType) {
            break;
         }

         Type[] typeArguments = parameterizedType.getActualTypeArguments();
         rawType = (Class)parameterizedType.getRawType();
         Type[] typeParameters = rawType.getTypeParameters();

         for(int i = 0; i < typeParameters.length; ++i) {
            Type value = typeArguments[i];
            if (!(value instanceof TypeVariable)) {
               typeMap.put(typeParameters[i], value);
            }
         }
      }

      return typeMap;
   }
}
