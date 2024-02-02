package cn.hutool.core.util;

import cn.hutool.core.lang.ParameterizedTypeImpl;
import cn.hutool.core.lang.reflect.ActualTypeMapperPool;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Map;

public class TypeUtil {
   public static Class<?> getClass(Type type) {
      if (null != type) {
         if (type instanceof Class) {
            return (Class)type;
         }

         if (type instanceof ParameterizedType) {
            return (Class)((ParameterizedType)type).getRawType();
         }

         if (type instanceof TypeVariable) {
            return (Class)((TypeVariable)type).getBounds()[0];
         }

         if (type instanceof WildcardType) {
            Type[] upperBounds = ((WildcardType)type).getUpperBounds();
            if (upperBounds.length == 1) {
               return getClass(upperBounds[0]);
            }
         }
      }

      return null;
   }

   public static Type getType(Field field) {
      return null == field ? null : field.getGenericType();
   }

   public static Type getFieldType(Class<?> clazz, String fieldName) {
      return getType(ReflectUtil.getField(clazz, fieldName));
   }

   public static Class<?> getClass(Field field) {
      return null == field ? null : field.getType();
   }

   public static Type getFirstParamType(Method method) {
      return getParamType(method, 0);
   }

   public static Class<?> getFirstParamClass(Method method) {
      return getParamClass(method, 0);
   }

   public static Type getParamType(Method method, int index) {
      Type[] types = getParamTypes(method);
      return null != types && types.length > index ? types[index] : null;
   }

   public static Class<?> getParamClass(Method method, int index) {
      Class<?>[] classes = getParamClasses(method);
      return null != classes && classes.length > index ? classes[index] : null;
   }

   public static Type[] getParamTypes(Method method) {
      return null == method ? null : method.getGenericParameterTypes();
   }

   public static Class<?>[] getParamClasses(Method method) {
      return null == method ? null : method.getParameterTypes();
   }

   public static Type getReturnType(Method method) {
      return null == method ? null : method.getGenericReturnType();
   }

   public static Class<?> getReturnClass(Method method) {
      return null == method ? null : method.getReturnType();
   }

   public static Type getTypeArgument(Type type) {
      return getTypeArgument(type, 0);
   }

   public static Type getTypeArgument(Type type, int index) {
      Type[] typeArguments = getTypeArguments(type);
      return null != typeArguments && typeArguments.length > index ? typeArguments[index] : null;
   }

   public static Type[] getTypeArguments(Type type) {
      if (null == type) {
         return null;
      } else {
         ParameterizedType parameterizedType = toParameterizedType(type);
         return null == parameterizedType ? null : parameterizedType.getActualTypeArguments();
      }
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
            if (ArrayUtil.isNotEmpty((Object[])genericInterfaces)) {
               genericSuper = genericInterfaces[0];
            }
         }

         result = toParameterizedType(genericSuper);
      }

      return result;
   }

   public static boolean isUnknown(Type type) {
      return null == type || type instanceof TypeVariable;
   }

   public static boolean hasTypeVariable(Type... types) {
      Type[] var1 = types;
      int var2 = types.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         Type type = var1[var3];
         if (type instanceof TypeVariable) {
            return true;
         }
      }

      return false;
   }

   public static Map<Type, Type> getTypeMap(Class<?> clazz) {
      return ActualTypeMapperPool.get(clazz);
   }

   public static Type getActualType(Type type, Field field) {
      return null == field ? null : getActualType((Type)ObjectUtil.defaultIfNull(type, (Object)field.getDeclaringClass()), field.getGenericType());
   }

   public static Type getActualType(Type type, Type typeVariable) {
      if (typeVariable instanceof ParameterizedType) {
         return getActualType(type, (ParameterizedType)typeVariable);
      } else {
         return typeVariable instanceof TypeVariable ? ActualTypeMapperPool.getActualType(type, (TypeVariable)typeVariable) : typeVariable;
      }
   }

   public static Type getActualType(Type type, ParameterizedType parameterizedType) {
      Type[] actualTypeArguments = ((ParameterizedType)parameterizedType).getActualTypeArguments();
      if (hasTypeVariable(actualTypeArguments)) {
         actualTypeArguments = getActualTypes(type, ((ParameterizedType)parameterizedType).getActualTypeArguments());
         if (ArrayUtil.isNotEmpty((Object[])actualTypeArguments)) {
            parameterizedType = new ParameterizedTypeImpl(actualTypeArguments, ((ParameterizedType)parameterizedType).getOwnerType(), ((ParameterizedType)parameterizedType).getRawType());
         }
      }

      return (Type)parameterizedType;
   }

   public static Type[] getActualTypes(Type type, Type... typeVariables) {
      return ActualTypeMapperPool.getActualTypes(type, typeVariables);
   }
}
