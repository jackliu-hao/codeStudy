/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.func.Func1;
/*     */ import cn.hutool.core.lang.func.LambdaUtil;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.lang.reflect.Field;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.function.Function;
/*     */ import java.util.function.Predicate;
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
/*     */ public class EnumUtil
/*     */ {
/*     */   public static boolean isEnum(Class<?> clazz) {
/*  32 */     Assert.notNull(clazz);
/*  33 */     return clazz.isEnum();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isEnum(Object obj) {
/*  43 */     Assert.notNull(obj);
/*  44 */     return obj.getClass().isEnum();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Enum<?> e) {
/*  55 */     return (null != e) ? e.name() : null;
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
/*     */   public static <E extends Enum<E>> E getEnumAt(Class<E> enumClass, int index) {
/*  68 */     Enum[] arrayOfEnum = (Enum[])enumClass.getEnumConstants();
/*  69 */     return (index >= 0 && index < arrayOfEnum.length) ? (E)arrayOfEnum[index] : null;
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
/*     */   public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value) {
/*  82 */     return Enum.valueOf(enumClass, value);
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
/*     */   public static <E extends Enum<E>> E fromString(Class<E> enumClass, String value, E defaultValue) {
/*  97 */     return (E)ObjectUtil.<Enum>defaultIfNull(fromStringQuietly(enumClass, value), (Enum)defaultValue);
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
/*     */   public static <E extends Enum<E>> E fromStringQuietly(Class<E> enumClass, String value) {
/* 110 */     if (null == enumClass || StrUtil.isBlank(value)) {
/* 111 */       return null;
/*     */     }
/*     */     
/*     */     try {
/* 115 */       return fromString(enumClass, value);
/* 116 */     } catch (IllegalArgumentException e) {
/* 117 */       return null;
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
/*     */   public static <E extends Enum<E>> E likeValueOf(Class<E> enumClass, Object value) {
/* 131 */     if (value instanceof CharSequence) {
/* 132 */       value = value.toString().trim();
/*     */     }
/*     */     
/* 135 */     Field[] fields = ReflectUtil.getFields(enumClass);
/* 136 */     Enum[] arrayOfEnum = (Enum[])enumClass.getEnumConstants();
/*     */     
/* 138 */     for (Field field : fields) {
/* 139 */       String fieldName = field.getName();
/* 140 */       if (!field.getType().isEnum() && !"ENUM$VALUES".equals(fieldName) && !"ordinal".equals(fieldName))
/*     */       {
/*     */ 
/*     */         
/* 144 */         for (Enum<?> enumObj : arrayOfEnum) {
/* 145 */           if (ObjectUtil.equal(value, ReflectUtil.getFieldValue(enumObj, field)))
/* 146 */             return (E)enumObj; 
/*     */         } 
/*     */       }
/*     */     } 
/* 150 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<String> getNames(Class<? extends Enum<?>> clazz) {
/* 160 */     Enum[] arrayOfEnum = (Enum[])clazz.getEnumConstants();
/* 161 */     if (null == arrayOfEnum) {
/* 162 */       return null;
/*     */     }
/* 164 */     List<String> list = new ArrayList<>(arrayOfEnum.length);
/* 165 */     for (Enum<?> e : arrayOfEnum) {
/* 166 */       list.add(e.name());
/*     */     }
/* 168 */     return list;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<Object> getFieldValues(Class<? extends Enum<?>> clazz, String fieldName) {
/* 179 */     Enum[] arrayOfEnum = (Enum[])clazz.getEnumConstants();
/* 180 */     if (null == arrayOfEnum) {
/* 181 */       return null;
/*     */     }
/* 183 */     List<Object> list = new ArrayList(arrayOfEnum.length);
/* 184 */     for (Enum<?> e : arrayOfEnum) {
/* 185 */       list.add(ReflectUtil.getFieldValue(e, fieldName));
/*     */     }
/* 187 */     return list;
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
/*     */   public static List<String> getFieldNames(Class<? extends Enum<?>> clazz) {
/* 203 */     List<String> names = new ArrayList<>();
/* 204 */     Field[] fields = ReflectUtil.getFields(clazz);
/*     */     
/* 206 */     for (Field field : fields) {
/* 207 */       String name = field.getName();
/* 208 */       if (!field.getType().isEnum() && !name.contains("$VALUES") && !"ordinal".equals(name))
/*     */       {
/*     */         
/* 211 */         if (false == names.contains(name))
/* 212 */           names.add(name); 
/*     */       }
/*     */     } 
/* 215 */     return names;
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
/*     */   public static <E extends Enum<E>> E getBy(Class<E> enumClass, Predicate<? super E> predicate) {
/* 228 */     return (E)Arrays.<Enum>stream((Enum[])enumClass.getEnumConstants())
/* 229 */       .filter(predicate).findFirst().orElse(null);
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
/*     */   public static <E extends Enum<E>, C> E getBy(Func1<E, C> condition, C value) {
/* 242 */     Class<E> implClass = LambdaUtil.getRealClass(condition);
/* 243 */     if (Enum.class.equals(implClass)) {
/* 244 */       implClass = LambdaUtil.getRealClass(condition);
/*     */     }
/* 246 */     return (E)Arrays.<Enum>stream((Enum[])implClass.getEnumConstants()).filter(e -> condition.callWithRuntimeException(e).equals(value)).findAny().orElse(null);
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
/*     */   public static <E extends Enum<E>, F, C> F getFieldBy(Func1<E, F> field, Function<E, C> condition, C value) {
/* 263 */     Class<E> implClass = LambdaUtil.getRealClass(field);
/* 264 */     if (Enum.class.equals(implClass)) {
/* 265 */       implClass = LambdaUtil.getRealClass(field);
/*     */     }
/* 267 */     return Arrays.stream((Object[])implClass.getEnumConstants())
/*     */       
/* 269 */       .filter(e -> condition.apply(e).equals(value))
/*     */       
/* 271 */       .findFirst().map(field::callWithRuntimeException).orElse(null);
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
/*     */   public static <E extends Enum<E>> LinkedHashMap<String, E> getEnumMap(Class<E> enumClass) {
/* 284 */     LinkedHashMap<String, E> map = new LinkedHashMap<>();
/* 285 */     for (Enum enum_ : (Enum[])enumClass.getEnumConstants()) {
/* 286 */       map.put(enum_.name(), (E)enum_);
/*     */     }
/* 288 */     return map;
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
/*     */   public static Map<String, Object> getNameFieldMap(Class<? extends Enum<?>> clazz, String fieldName) {
/* 300 */     Enum[] arrayOfEnum = (Enum[])clazz.getEnumConstants();
/* 301 */     if (null == arrayOfEnum) {
/* 302 */       return null;
/*     */     }
/* 304 */     Map<String, Object> map = MapUtil.newHashMap(arrayOfEnum.length, true);
/* 305 */     for (Enum<?> e : arrayOfEnum) {
/* 306 */       map.put(e.name(), ReflectUtil.getFieldValue(e, fieldName));
/*     */     }
/* 308 */     return map;
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
/*     */   public static <E extends Enum<E>> boolean contains(Class<E> enumClass, String val) {
/* 320 */     return getEnumMap(enumClass).containsKey(val);
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
/*     */   public static <E extends Enum<E>> boolean notContains(Class<E> enumClass, String val) {
/* 332 */     return (false == contains(enumClass, val));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean equalsIgnoreCase(Enum<?> e, String val) {
/* 343 */     return StrUtil.equalsIgnoreCase(toString(e), val);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean equals(Enum<?> e, String val) {
/* 354 */     return StrUtil.equals(toString(e), val);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\EnumUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */