/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.collection.IterUtil;
/*     */ import cn.hutool.core.comparator.CompareUtil;
/*     */ import cn.hutool.core.convert.Convert;
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import java.lang.reflect.Array;
/*     */ import java.math.BigDecimal;
/*     */ import java.util.Collection;
/*     */ import java.util.Enumeration;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ import java.util.Objects;
/*     */ import java.util.function.Supplier;
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
/*     */ public class ObjectUtil
/*     */ {
/*     */   public static boolean equals(Object obj1, Object obj2) {
/*  41 */     return equal(obj1, obj2);
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
/*     */   public static boolean equal(Object obj1, Object obj2) {
/*  59 */     if (obj1 instanceof BigDecimal && obj2 instanceof BigDecimal) {
/*  60 */       return NumberUtil.equals((BigDecimal)obj1, (BigDecimal)obj2);
/*     */     }
/*  62 */     return Objects.equals(obj1, obj2);
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
/*     */   public static boolean notEqual(Object obj1, Object obj2) {
/*  74 */     return (false == equal(obj1, obj2));
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
/*     */   public static int length(Object obj) {
/*  92 */     if (obj == null) {
/*  93 */       return 0;
/*     */     }
/*  95 */     if (obj instanceof CharSequence) {
/*  96 */       return ((CharSequence)obj).length();
/*     */     }
/*  98 */     if (obj instanceof Collection) {
/*  99 */       return ((Collection)obj).size();
/*     */     }
/* 101 */     if (obj instanceof Map) {
/* 102 */       return ((Map)obj).size();
/*     */     }
/*     */ 
/*     */     
/* 106 */     if (obj instanceof Iterator) {
/* 107 */       Iterator<?> iter = (Iterator)obj;
/* 108 */       int count = 0;
/* 109 */       while (iter.hasNext()) {
/* 110 */         count++;
/* 111 */         iter.next();
/*     */       } 
/* 113 */       return count;
/*     */     } 
/* 115 */     if (obj instanceof Enumeration) {
/* 116 */       Enumeration<?> enumeration = (Enumeration)obj;
/* 117 */       int count = 0;
/* 118 */       while (enumeration.hasMoreElements()) {
/* 119 */         count++;
/* 120 */         enumeration.nextElement();
/*     */       } 
/* 122 */       return count;
/*     */     } 
/* 124 */     if (obj.getClass().isArray() == true) {
/* 125 */       return Array.getLength(obj);
/*     */     }
/* 127 */     return -1;
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
/*     */   public static boolean contains(Object obj, Object element) {
/* 147 */     if (obj == null) {
/* 148 */       return false;
/*     */     }
/* 150 */     if (obj instanceof String) {
/* 151 */       if (element == null) {
/* 152 */         return false;
/*     */       }
/* 154 */       return ((String)obj).contains(element.toString());
/*     */     } 
/* 156 */     if (obj instanceof Collection) {
/* 157 */       return ((Collection)obj).contains(element);
/*     */     }
/* 159 */     if (obj instanceof Map) {
/* 160 */       return ((Map)obj).containsValue(element);
/*     */     }
/*     */     
/* 163 */     if (obj instanceof Iterator) {
/* 164 */       Iterator<?> iter = (Iterator)obj;
/* 165 */       while (iter.hasNext()) {
/* 166 */         Object o = iter.next();
/* 167 */         if (equal(o, element)) {
/* 168 */           return true;
/*     */         }
/*     */       } 
/* 171 */       return false;
/*     */     } 
/* 173 */     if (obj instanceof Enumeration) {
/* 174 */       Enumeration<?> enumeration = (Enumeration)obj;
/* 175 */       while (enumeration.hasMoreElements()) {
/* 176 */         Object o = enumeration.nextElement();
/* 177 */         if (equal(o, element)) {
/* 178 */           return true;
/*     */         }
/*     */       } 
/* 181 */       return false;
/*     */     } 
/* 183 */     if (obj.getClass().isArray() == true) {
/* 184 */       int len = Array.getLength(obj);
/* 185 */       for (int i = 0; i < len; i++) {
/* 186 */         Object o = Array.get(obj, i);
/* 187 */         if (equal(o, element)) {
/* 188 */           return true;
/*     */         }
/*     */       } 
/*     */     } 
/* 192 */     return false;
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
/*     */   public static boolean isNull(Object obj) {
/* 209 */     return (null == obj || obj.equals(null));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isNotNull(Object obj) {
/* 219 */     return (false == isNull(obj));
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
/*     */   public static boolean isEmpty(Object obj) {
/* 239 */     if (null == obj) {
/* 240 */       return true;
/*     */     }
/*     */     
/* 243 */     if (obj instanceof CharSequence)
/* 244 */       return StrUtil.isEmpty((CharSequence)obj); 
/* 245 */     if (obj instanceof Map)
/* 246 */       return MapUtil.isEmpty((Map)obj); 
/* 247 */     if (obj instanceof Iterable)
/* 248 */       return IterUtil.isEmpty((Iterable)obj); 
/* 249 */     if (obj instanceof Iterator)
/* 250 */       return IterUtil.isEmpty((Iterator)obj); 
/* 251 */     if (ArrayUtil.isArray(obj)) {
/* 252 */       return ArrayUtil.isEmpty(obj);
/*     */     }
/*     */     
/* 255 */     return false;
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
/*     */   public static boolean isNotEmpty(Object obj) {
/* 274 */     return (false == isEmpty(obj));
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
/*     */   public static <T> T defaultIfNull(T object, T defaultValue) {
/* 295 */     return isNull(object) ? defaultValue : object;
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
/*     */   public static <T> T defaultIfNull(T source, Supplier<? extends T> defaultValueSupplier) {
/* 309 */     if (isNull(source)) {
/* 310 */       return defaultValueSupplier.get();
/*     */     }
/* 312 */     return source;
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
/*     */   public static <T> T defaultIfNull(Object source, Supplier<? extends T> handle, T defaultValue) {
/* 326 */     if (isNotNull(source)) {
/* 327 */       return handle.get();
/*     */     }
/* 329 */     return defaultValue;
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
/*     */   public static <T> T defaultIfEmpty(String str, Supplier<? extends T> handle, T defaultValue) {
/* 343 */     if (StrUtil.isNotEmpty(str)) {
/* 344 */       return handle.get();
/*     */     }
/* 346 */     return defaultValue;
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
/*     */   public static <T extends CharSequence> T defaultIfEmpty(T str, T defaultValue) {
/* 367 */     return StrUtil.isEmpty((CharSequence)str) ? defaultValue : str;
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
/*     */   public static <T extends CharSequence> T defaultIfEmpty(T str, Supplier<? extends T> defaultValueSupplier) {
/* 381 */     if (StrUtil.isEmpty((CharSequence)str)) {
/* 382 */       return defaultValueSupplier.get();
/*     */     }
/* 384 */     return str;
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
/*     */   public static <T extends CharSequence> T defaultIfBlank(T str, T defaultValue) {
/* 405 */     return StrUtil.isBlank((CharSequence)str) ? defaultValue : str;
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
/*     */   public static <T extends CharSequence> T defaultIfBlank(T str, Supplier<? extends T> defaultValueSupplier) {
/* 419 */     if (StrUtil.isBlank((CharSequence)str)) {
/* 420 */       return defaultValueSupplier.get();
/*     */     }
/* 422 */     return str;
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
/*     */   public static <T> T clone(T obj) {
/* 436 */     T result = ArrayUtil.clone(obj);
/* 437 */     if (null == result) {
/* 438 */       if (obj instanceof Cloneable) {
/* 439 */         result = ReflectUtil.invoke(obj, "clone", new Object[0]);
/*     */       } else {
/* 441 */         result = cloneByStream(obj);
/*     */       } 
/*     */     }
/* 444 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <T> T cloneIfPossible(T obj) {
/* 455 */     T clone = null;
/*     */     try {
/* 457 */       clone = clone(obj);
/* 458 */     } catch (Exception exception) {}
/*     */ 
/*     */     
/* 461 */     return (clone == null) ? obj : clone;
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
/*     */   public static <T> T cloneByStream(T obj) {
/* 474 */     return SerializeUtil.clone(obj);
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
/*     */   public static <T> byte[] serialize(T obj) {
/* 486 */     return SerializeUtil.serialize(obj);
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
/*     */   public static <T> T deserialize(byte[] bytes) {
/* 502 */     return SerializeUtil.deserialize(bytes);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isBasicType(Object object) {
/* 513 */     if (null == object) {
/* 514 */       return false;
/*     */     }
/* 516 */     return ClassUtil.isBasicType(object.getClass());
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
/*     */   public static boolean isValidIfNumber(Object obj) {
/* 528 */     if (obj instanceof Number) {
/* 529 */       return NumberUtil.isValidNumber((Number)obj);
/*     */     }
/* 531 */     return true;
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
/*     */   public static <T extends Comparable<? super T>> int compare(T c1, T c2) {
/* 545 */     return CompareUtil.compare((Comparable)c1, (Comparable)c2);
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
/*     */   public static <T extends Comparable<? super T>> int compare(T c1, T c2, boolean nullGreater) {
/* 560 */     return CompareUtil.compare((Comparable)c1, (Comparable)c2, nullGreater);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Class<?> getTypeArgument(Object obj) {
/* 571 */     return getTypeArgument(obj, 0);
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
/*     */   public static Class<?> getTypeArgument(Object obj, int index) {
/* 583 */     return ClassUtil.getTypeArgument(obj.getClass(), index);
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
/*     */   public static String toString(Object obj) {
/* 599 */     if (null == obj) {
/* 600 */       return "null";
/*     */     }
/* 602 */     if (obj instanceof Map) {
/* 603 */       return obj.toString();
/*     */     }
/*     */     
/* 606 */     return Convert.toStr(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int emptyCount(Object... objs) {
/* 616 */     return ArrayUtil.emptyCount(objs);
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
/*     */   public static boolean hasNull(Object... objs) {
/* 628 */     return ArrayUtil.hasNull(objs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean hasEmpty(Object... objs) {
/* 639 */     return ArrayUtil.hasEmpty(objs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAllEmpty(Object... objs) {
/* 649 */     return ArrayUtil.isAllEmpty(objs);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean isAllNotEmpty(Object... objs) {
/* 659 */     return ArrayUtil.isAllNotEmpty(objs);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\ObjectUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */