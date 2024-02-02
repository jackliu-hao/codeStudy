package org.noear.snack.core.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.AbstractCollection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.noear.snack.core.exts.EnumWrap;
import org.noear.snack.exception.SnackException;

public class TypeUtil {
   public static final BigInteger INT_LOW = BigInteger.valueOf(-9007199254740991L);
   public static final BigInteger INT_HIGH = BigInteger.valueOf(9007199254740991L);
   public static final BigDecimal DEC_LOW = BigDecimal.valueOf(-9007199254740991L);
   public static final BigDecimal DEC_HIGH = BigDecimal.valueOf(9007199254740991L);
   private static Map<String, EnumWrap> enumCached = new ConcurrentHashMap();
   private static final Map<Type, Map<TypeVariable, Type>> genericInfoCached = new HashMap();

   public static Object strTo(String str, Class<?> clz) {
      if (!Integer.class.isAssignableFrom(clz) && Integer.TYPE != clz) {
         if (!Long.class.isAssignableFrom(clz) && Long.TYPE != clz) {
            throw new SnackException("unsupport type " + str);
         } else {
            return Long.parseLong(str);
         }
      } else {
         return Integer.parseInt(str);
      }
   }

   public static EnumWrap createEnum(Class<?> clz) {
      String key = clz.getName();
      EnumWrap val = (EnumWrap)enumCached.get(key);
      if (val == null) {
         val = new EnumWrap(clz);
         enumCached.put(key, val);
      }

      return val;
   }

   public static Type getCollectionItemType(Type fieldType) {
      if (fieldType instanceof ParameterizedType) {
         return getCollectionItemType((ParameterizedType)fieldType);
      } else {
         return (Type)(fieldType instanceof Class ? getCollectionItemType((Class)fieldType) : Object.class);
      }
   }

   private static Type getCollectionItemType(Class<?> clazz) {
      return (Type)(clazz.getName().startsWith("java.") ? Object.class : getCollectionItemType(getCollectionSuperType(clazz)));
   }

   private static Type getCollectionSuperType(Class<?> clazz) {
      Type assignable = null;
      Type[] var2 = clazz.getGenericInterfaces();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Type type = var2[var4];
         Class<?> rawClass = getRawClass(type);
         if (rawClass == Collection.class) {
            return type;
         }

         if (Collection.class.isAssignableFrom(rawClass)) {
            assignable = type;
         }
      }

      return assignable == null ? clazz.getGenericSuperclass() : assignable;
   }

   private static Type getCollectionItemType(ParameterizedType parameterizedType) {
      Type rawType = parameterizedType.getRawType();
      Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
      if (rawType == Collection.class) {
         return getWildcardTypeUpperBounds(actualTypeArguments[0]);
      } else {
         Class<?> rawClass = (Class)rawType;
         Map<TypeVariable, Type> typeParameterMap = createTypeParameterMap(rawClass.getTypeParameters(), actualTypeArguments);
         Type superType = getCollectionSuperType(rawClass);
         if (superType instanceof ParameterizedType) {
            Class<?> superClass = getRawClass(superType);
            Type[] superClassTypeParameters = ((ParameterizedType)superType).getActualTypeArguments();
            return superClassTypeParameters.length > 0 ? getCollectionItemType(makeParameterizedType(superClass, superClassTypeParameters, typeParameterMap)) : getCollectionItemType(superClass);
         } else {
            return getCollectionItemType((Class)superType);
         }
      }
   }

   private static Map<TypeVariable, Type> createTypeParameterMap(TypeVariable[] typeParameters, Type[] actualTypeArguments) {
      int length = typeParameters.length;
      Map<TypeVariable, Type> typeParameterMap = new HashMap(length);

      for(int i = 0; i < length; ++i) {
         typeParameterMap.put(typeParameters[i], actualTypeArguments[i]);
      }

      return typeParameterMap;
   }

   private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters, Map<TypeVariable, Type> typeParameterMap) {
      int length = typeParameters.length;
      Type[] actualTypeArguments = new Type[length];
      System.arraycopy(typeParameters, 0, actualTypeArguments, 0, length);

      for(int i = 0; i < actualTypeArguments.length; ++i) {
         Type actualTypeArgument = actualTypeArguments[i];
         if (actualTypeArgument instanceof TypeVariable) {
            actualTypeArguments[i] = (Type)typeParameterMap.get(actualTypeArgument);
         }
      }

      return new ParameterizedTypeImpl(rawClass, actualTypeArguments, (Type)null);
   }

   private static Type getWildcardTypeUpperBounds(Type type) {
      if (type instanceof WildcardType) {
         WildcardType wildcardType = (WildcardType)type;
         Type[] upperBounds = wildcardType.getUpperBounds();
         return (Type)(upperBounds.length > 0 ? upperBounds[0] : Object.class);
      } else {
         return type instanceof ParameterizedType ? ((ParameterizedType)type).getRawType() : type;
      }
   }

   public static Collection createCollection(Type type, boolean isThrow) {
      if (type == null) {
         return new ArrayList();
      } else if (type == ArrayList.class) {
         return new ArrayList();
      } else {
         Class<?> rawClass = getRawClass(type);
         Object list;
         if (rawClass != AbstractCollection.class && rawClass != Collection.class) {
            if (rawClass.isAssignableFrom(HashSet.class)) {
               list = new HashSet();
            } else if (rawClass.isAssignableFrom(LinkedHashSet.class)) {
               list = new LinkedHashSet();
            } else if (rawClass.isAssignableFrom(TreeSet.class)) {
               list = new TreeSet();
            } else if (rawClass.isAssignableFrom(ArrayList.class)) {
               list = new ArrayList();
            } else if (rawClass.isAssignableFrom(EnumSet.class)) {
               Object itemType;
               if (type instanceof ParameterizedType) {
                  itemType = ((ParameterizedType)type).getActualTypeArguments()[0];
               } else {
                  itemType = Object.class;
               }

               list = EnumSet.noneOf((Class)itemType);
            } else {
               try {
                  list = (Collection)rawClass.newInstance();
               } catch (Throwable var5) {
                  if (isThrow) {
                     throw new SnackException("create instance error, class " + rawClass.getName(), var5);
                  }

                  return null;
               }
            }
         } else {
            list = new ArrayList();
         }

         return (Collection)list;
      }
   }

   public static Map createMap(Type type) {
      if (type == null) {
         return new HashMap();
      } else if (type == HashMap.class) {
         return new HashMap();
      } else if (type == Properties.class) {
         return new Properties();
      } else if (type == Hashtable.class) {
         return new Hashtable();
      } else if (type == IdentityHashMap.class) {
         return new IdentityHashMap();
      } else if (type != SortedMap.class && type != TreeMap.class) {
         if (type != ConcurrentMap.class && type != ConcurrentHashMap.class) {
            if (type == LinkedHashMap.class) {
               return new LinkedHashMap();
            } else if (type == Map.class) {
               return new HashMap();
            } else if (type instanceof ParameterizedType) {
               ParameterizedType parameterizedType = (ParameterizedType)type;
               Type rawType = parameterizedType.getRawType();
               if (EnumMap.class.equals(rawType)) {
                  Type[] actualArgs = parameterizedType.getActualTypeArguments();
                  return new EnumMap((Class)actualArgs[0]);
               } else {
                  return createMap(rawType);
               }
            } else {
               Class<?> clazz = (Class)type;
               if (clazz.isInterface()) {
                  throw new SnackException("unsupport type " + type);
               } else {
                  try {
                     return (Map)clazz.newInstance();
                  } catch (Throwable var4) {
                     throw new SnackException("unsupport type " + type, var4);
                  }
               }
            }
         } else {
            return new ConcurrentHashMap();
         }
      } else {
         return new TreeMap();
      }
   }

   public static Class<?> getRawClass(Type type) {
      if (type instanceof Class) {
         return (Class)type;
      } else if (type instanceof ParameterizedType) {
         return getRawClass(((ParameterizedType)type).getRawType());
      } else {
         throw new SnackException("unsupport type " + type);
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
            if (genericInterfaces != null && genericInterfaces.length > 0) {
               genericSuper = genericInterfaces[0];
            }
         }

         result = toParameterizedType(genericSuper);
      }

      return result;
   }

   public static Map<TypeVariable, Type> getGenericInfo(Type type) {
      Map<TypeVariable, Type> tmp = (Map)genericInfoCached.get(type);
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

   private static Map<TypeVariable, Type> createTypeGenericMap(Type type) {
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
               typeMap.put(typeParameters[i], value);
            }
         }
      }

      return typeMap;
   }
}
