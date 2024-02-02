/*     */ package org.noear.snack.core.utils;
/*     */ import java.lang.reflect.ParameterizedType;
/*     */ import java.lang.reflect.Type;
/*     */ import java.lang.reflect.TypeVariable;
/*     */ import java.lang.reflect.WildcardType;
/*     */ import java.math.BigDecimal;
/*     */ import java.math.BigInteger;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collection;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Properties;
/*     */ import java.util.TreeSet;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.noear.snack.core.exts.EnumWrap;
/*     */ import org.noear.snack.exception.SnackException;
/*     */ 
/*     */ public class TypeUtil {
/*  20 */   public static final BigInteger INT_LOW = BigInteger.valueOf(-9007199254740991L);
/*  21 */   public static final BigInteger INT_HIGH = BigInteger.valueOf(9007199254740991L);
/*  22 */   public static final BigDecimal DEC_LOW = BigDecimal.valueOf(-9007199254740991L);
/*  23 */   public static final BigDecimal DEC_HIGH = BigDecimal.valueOf(9007199254740991L);
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object strTo(String str, Class<?> clz) {
/*  29 */     if (Integer.class.isAssignableFrom(clz) || int.class == clz)
/*  30 */       return Integer.valueOf(Integer.parseInt(str)); 
/*  31 */     if (Long.class.isAssignableFrom(clz) || long.class == clz) {
/*  32 */       return Long.valueOf(Long.parseLong(str));
/*     */     }
/*  34 */     throw new SnackException("unsupport type " + str);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*  39 */   private static Map<String, EnumWrap> enumCached = new ConcurrentHashMap<>();
/*     */   
/*     */   public static EnumWrap createEnum(Class<?> clz) {
/*  42 */     String key = clz.getName();
/*  43 */     EnumWrap val = enumCached.get(key);
/*  44 */     if (val == null) {
/*  45 */       val = new EnumWrap(clz);
/*  46 */       enumCached.put(key, val);
/*     */     } 
/*     */     
/*  49 */     return val;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Type getCollectionItemType(Type fieldType) {
/*  54 */     if (fieldType instanceof ParameterizedType) {
/*  55 */       return getCollectionItemType((ParameterizedType)fieldType);
/*     */     }
/*  57 */     if (fieldType instanceof Class) {
/*  58 */       return getCollectionItemType((Class)fieldType);
/*     */     }
/*  60 */     return Object.class;
/*     */   }
/*     */   
/*     */   private static Type getCollectionItemType(Class<?> clazz) {
/*  64 */     return clazz.getName().startsWith("java.") ? Object.class : 
/*     */       
/*  66 */       getCollectionItemType(getCollectionSuperType(clazz));
/*     */   }
/*     */   
/*     */   private static Type getCollectionSuperType(Class<?> clazz) {
/*  70 */     Type assignable = null;
/*  71 */     for (Type type : clazz.getGenericInterfaces()) {
/*  72 */       Class<?> rawClass = getRawClass(type);
/*  73 */       if (rawClass == Collection.class) {
/*  74 */         return type;
/*     */       }
/*  76 */       if (Collection.class.isAssignableFrom(rawClass)) {
/*  77 */         assignable = type;
/*     */       }
/*     */     } 
/*  80 */     return (assignable == null) ? clazz.getGenericSuperclass() : assignable;
/*     */   }
/*     */   
/*     */   private static Type getCollectionItemType(ParameterizedType parameterizedType) {
/*  84 */     Type rawType = parameterizedType.getRawType();
/*  85 */     Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
/*  86 */     if (rawType == Collection.class) {
/*  87 */       return getWildcardTypeUpperBounds(actualTypeArguments[0]);
/*     */     }
/*  89 */     Class<?> rawClass = (Class)rawType;
/*  90 */     Map<TypeVariable, Type> typeParameterMap = createTypeParameterMap((TypeVariable[])rawClass.getTypeParameters(), actualTypeArguments);
/*  91 */     Type superType = getCollectionSuperType(rawClass);
/*  92 */     if (superType instanceof ParameterizedType) {
/*  93 */       Class<?> superClass = getRawClass(superType);
/*  94 */       Type[] superClassTypeParameters = ((ParameterizedType)superType).getActualTypeArguments();
/*  95 */       return (superClassTypeParameters.length > 0) ? 
/*  96 */         getCollectionItemType(makeParameterizedType(superClass, superClassTypeParameters, typeParameterMap)) : 
/*  97 */         getCollectionItemType(superClass);
/*     */     } 
/*  99 */     return getCollectionItemType((Class)superType);
/*     */   }
/*     */   
/*     */   private static Map<TypeVariable, Type> createTypeParameterMap(TypeVariable[] typeParameters, Type[] actualTypeArguments) {
/* 103 */     int length = typeParameters.length;
/* 104 */     Map<TypeVariable, Type> typeParameterMap = new HashMap<>(length);
/* 105 */     for (int i = 0; i < length; i++) {
/* 106 */       typeParameterMap.put(typeParameters[i], actualTypeArguments[i]);
/*     */     }
/* 108 */     return typeParameterMap;
/*     */   }
/*     */   
/*     */   private static ParameterizedType makeParameterizedType(Class<?> rawClass, Type[] typeParameters, Map<TypeVariable, Type> typeParameterMap) {
/* 112 */     int length = typeParameters.length;
/* 113 */     Type[] actualTypeArguments = new Type[length];
/* 114 */     System.arraycopy(typeParameters, 0, actualTypeArguments, 0, length);
/* 115 */     for (int i = 0; i < actualTypeArguments.length; i++) {
/* 116 */       Type actualTypeArgument = actualTypeArguments[i];
/* 117 */       if (actualTypeArgument instanceof TypeVariable) {
/* 118 */         actualTypeArguments[i] = typeParameterMap.get(actualTypeArgument);
/*     */       }
/*     */     } 
/* 121 */     return new ParameterizedTypeImpl(rawClass, actualTypeArguments, null);
/*     */   }
/*     */   
/*     */   private static Type getWildcardTypeUpperBounds(Type type) {
/* 125 */     if (type instanceof WildcardType) {
/* 126 */       WildcardType wildcardType = (WildcardType)type;
/* 127 */       Type[] upperBounds = wildcardType.getUpperBounds();
/* 128 */       return (upperBounds.length > 0) ? upperBounds[0] : Object.class;
/*     */     } 
/*     */     
/* 131 */     if (type instanceof ParameterizedType) {
/* 132 */       return ((ParameterizedType)type).getRawType();
/*     */     }
/*     */     
/* 135 */     return type;
/*     */   }
/*     */   public static Collection createCollection(Type type, boolean isThrow) {
/*     */     Collection list;
/* 139 */     if (type == null) {
/* 140 */       return new ArrayList();
/*     */     }
/*     */ 
/*     */     
/* 144 */     if (type == ArrayList.class) {
/* 145 */       return new ArrayList();
/*     */     }
/*     */     
/* 148 */     Class<?> rawClass = getRawClass(type);
/*     */     
/* 150 */     if (rawClass == AbstractCollection.class || rawClass == Collection.class) {
/*     */       
/* 152 */       list = new ArrayList();
/* 153 */     } else if (rawClass.isAssignableFrom(HashSet.class)) {
/* 154 */       list = new HashSet();
/* 155 */     } else if (rawClass.isAssignableFrom(LinkedHashSet.class)) {
/* 156 */       list = new LinkedHashSet();
/* 157 */     } else if (rawClass.isAssignableFrom(TreeSet.class)) {
/* 158 */       list = new TreeSet();
/* 159 */     } else if (rawClass.isAssignableFrom(ArrayList.class)) {
/* 160 */       list = new ArrayList();
/* 161 */     } else if (rawClass.isAssignableFrom(EnumSet.class)) {
/*     */       Type<Object> itemType;
/* 163 */       if (type instanceof ParameterizedType) {
/* 164 */         itemType = ((ParameterizedType)type).getActualTypeArguments()[0];
/*     */       } else {
/* 166 */         itemType = Object.class;
/*     */       } 
/* 168 */       list = EnumSet.noneOf((Class)itemType);
/*     */     } else {
/*     */       try {
/* 171 */         list = (Collection)rawClass.newInstance();
/* 172 */       } catch (Throwable e) {
/* 173 */         if (isThrow) {
/* 174 */           throw new SnackException("create instance error, class " + rawClass.getName(), e);
/*     */         }
/* 176 */         return null;
/*     */       } 
/*     */     } 
/*     */     
/* 180 */     return list;
/*     */   }
/*     */ 
/*     */   
/*     */   public static Map createMap(Type type) {
/* 185 */     if (type == null) {
/* 186 */       return new HashMap<>();
/*     */     }
/*     */ 
/*     */     
/* 190 */     if (type == HashMap.class) {
/* 191 */       return new HashMap<>();
/*     */     }
/*     */     
/* 194 */     if (type == Properties.class) {
/* 195 */       return new Properties();
/*     */     }
/*     */     
/* 198 */     if (type == Hashtable.class) {
/* 199 */       return new Hashtable<>();
/*     */     }
/*     */     
/* 202 */     if (type == IdentityHashMap.class) {
/* 203 */       return new IdentityHashMap<>();
/*     */     }
/*     */     
/* 206 */     if (type == SortedMap.class || type == TreeMap.class) {
/* 207 */       return new TreeMap<>();
/*     */     }
/*     */     
/* 210 */     if (type == ConcurrentMap.class || type == ConcurrentHashMap.class) {
/* 211 */       return new ConcurrentHashMap<>();
/*     */     }
/*     */     
/* 214 */     if (type == LinkedHashMap.class) {
/* 215 */       return new LinkedHashMap<>();
/*     */     }
/*     */     
/* 218 */     if (type == Map.class) {
/* 219 */       return new HashMap<>();
/*     */     }
/*     */     
/* 222 */     if (type instanceof ParameterizedType) {
/* 223 */       ParameterizedType parameterizedType = (ParameterizedType)type;
/*     */       
/* 225 */       Type rawType = parameterizedType.getRawType();
/* 226 */       if (EnumMap.class.equals(rawType)) {
/* 227 */         Type[] actualArgs = parameterizedType.getActualTypeArguments();
/* 228 */         return new EnumMap<>((Class<Enum>)actualArgs[0]);
/*     */       } 
/*     */       
/* 231 */       return createMap(rawType);
/*     */     } 
/*     */     
/* 234 */     Class<?> clazz = (Class)type;
/* 235 */     if (clazz.isInterface()) {
/* 236 */       throw new SnackException("unsupport type " + type);
/*     */     }
/*     */     
/*     */     try {
/* 240 */       return (Map)clazz.newInstance();
/* 241 */     } catch (Throwable e) {
/* 242 */       throw new SnackException("unsupport type " + type, e);
/*     */     } 
/*     */   }
/*     */   
/*     */   public static Class<?> getRawClass(Type type) {
/* 247 */     if (type instanceof Class)
/* 248 */       return (Class)type; 
/* 249 */     if (type instanceof ParameterizedType) {
/* 250 */       return getRawClass(((ParameterizedType)type).getRawType());
/*     */     }
/* 252 */     throw new SnackException("unsupport type " + type);
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
/*     */   public static ParameterizedType toParameterizedType(Type type) {
/* 273 */     ParameterizedType result = null;
/* 274 */     if (type instanceof ParameterizedType) {
/* 275 */       result = (ParameterizedType)type;
/* 276 */     } else if (type instanceof Class) {
/* 277 */       Class<?> clazz = (Class)type;
/* 278 */       Type genericSuper = clazz.getGenericSuperclass();
/* 279 */       if (null == genericSuper || Object.class.equals(genericSuper)) {
/*     */         
/* 281 */         Type[] genericInterfaces = clazz.getGenericInterfaces();
/* 282 */         if (genericInterfaces != null && genericInterfaces.length > 0)
/*     */         {
/* 284 */           genericSuper = genericInterfaces[0];
/*     */         }
/*     */       } 
/* 287 */       result = toParameterizedType(genericSuper);
/*     */     } 
/* 289 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/* 294 */   private static final Map<Type, Map<TypeVariable, Type>> genericInfoCached = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Map<TypeVariable, Type> getGenericInfo(Type type) {
/* 303 */     Map<TypeVariable, Type> tmp = genericInfoCached.get(type);
/* 304 */     if (tmp == null) {
/* 305 */       synchronized (type) {
/* 306 */         tmp = genericInfoCached.get(type);
/*     */         
/* 308 */         if (tmp == null) {
/* 309 */           tmp = createTypeGenericMap(type);
/* 310 */           genericInfoCached.put(type, tmp);
/*     */         } 
/*     */       } 
/*     */     }
/*     */     
/* 315 */     return tmp;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static Map<TypeVariable, Type> createTypeGenericMap(Type<?> type) {
/* 326 */     Map<TypeVariable, Type> typeMap = new HashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 335 */     while (null != type) {
/* 336 */       ParameterizedType parameterizedType = toParameterizedType(type);
/* 337 */       if (null == parameterizedType) {
/*     */         break;
/*     */       }
/* 340 */       Type[] typeArguments = parameterizedType.getActualTypeArguments();
/* 341 */       Class<?> rawType = (Class)parameterizedType.getRawType();
/* 342 */       TypeVariable[] typeParameters = (TypeVariable[])rawType.getTypeParameters();
/*     */ 
/*     */       
/* 345 */       for (int i = 0; i < typeParameters.length; i++) {
/* 346 */         Type value = typeArguments[i];
/*     */         
/* 348 */         if (false == value instanceof TypeVariable) {
/* 349 */           typeMap.put(typeParameters[i], value);
/*     */         }
/*     */       } 
/*     */       
/* 353 */       type = rawType;
/*     */     } 
/*     */     
/* 356 */     return typeMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\snack\cor\\utils\TypeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */