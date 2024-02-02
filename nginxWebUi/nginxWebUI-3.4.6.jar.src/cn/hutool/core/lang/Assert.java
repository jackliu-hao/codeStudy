/*      */ package cn.hutool.core.lang;
/*      */ 
/*      */ import cn.hutool.core.collection.CollUtil;
/*      */ import cn.hutool.core.map.MapUtil;
/*      */ import cn.hutool.core.util.ArrayUtil;
/*      */ import cn.hutool.core.util.ObjectUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.util.Map;
/*      */ import java.util.function.Supplier;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class Assert
/*      */ {
/*      */   private static final String TEMPLATE_VALUE_MUST_BE_BETWEEN_AND = "The value must be between {} and {}.";
/*      */   
/*      */   public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
/*   36 */     if (false == expression) {
/*   37 */       throw (X)supplier.get();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*   54 */     isTrue(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isTrue(boolean expression) throws IllegalArgumentException {
/*   68 */     isTrue(expression, "[Assertion failed] - this expression must be true", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> void isFalse(boolean expression, Supplier<X> errorSupplier) throws X {
/*   88 */     if (expression) {
/*   89 */       throw (X)errorSupplier.get();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  106 */     isFalse(expression, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isFalse(boolean expression) throws IllegalArgumentException {
/*  120 */     isFalse(expression, "[Assertion failed] - this expression must be false", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> void isNull(Object object, Supplier<X> errorSupplier) throws X {
/*  140 */     if (null != object) {
/*  141 */       throw (X)errorSupplier.get();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isNull(Object object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  158 */     isNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isNull(Object object) throws IllegalArgumentException {
/*  172 */     isNull(object, "[Assertion failed] - the object argument must be null", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
/*  196 */     if (null == object) {
/*  197 */       throw (X)errorSupplier.get();
/*      */     }
/*  199 */     return object;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  217 */     return notNull(object, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T notNull(T object) throws IllegalArgumentException {
/*  233 */     return notNull(object, "[Assertion failed] - this argument is required; it must not be null", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence, X extends Throwable> T notEmpty(T text, Supplier<X> errorSupplier) throws X {
/*  257 */     if (StrUtil.isEmpty((CharSequence)text)) {
/*  258 */       throw (X)errorSupplier.get();
/*      */     }
/*  260 */     return text;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T notEmpty(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  279 */     return notEmpty(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T notEmpty(T text) throws IllegalArgumentException {
/*  296 */     return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence, X extends Throwable> T notBlank(T text, Supplier<X> errorMsgSupplier) throws X {
/*  318 */     if (StrUtil.isBlank((CharSequence)text)) {
/*  319 */       throw (X)errorMsgSupplier.get();
/*      */     }
/*  321 */     return text;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T notBlank(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  340 */     return notBlank(text, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence> T notBlank(T text) throws IllegalArgumentException {
/*  357 */     return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T extends CharSequence, X extends Throwable> T notContain(CharSequence textToSearch, T substring, Supplier<X> errorSupplier) throws X {
/*  381 */     if (StrUtil.contains(textToSearch, (CharSequence)substring)) {
/*  382 */       throw (X)errorSupplier.get();
/*      */     }
/*  384 */     return substring;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String notContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  402 */     return notContain(textToSearch, substring, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String notContain(String textToSearch, String substring) throws IllegalArgumentException {
/*  418 */     return notContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", new Object[] { substring });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T, X extends Throwable> T[] notEmpty(T[] array, Supplier<X> errorSupplier) throws X {
/*  442 */     if (ArrayUtil.isEmpty((Object[])array)) {
/*  443 */       throw (X)errorSupplier.get();
/*      */     }
/*  445 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T[] notEmpty(T[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  463 */     return notEmpty(array, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T[] notEmpty(T[] array) throws IllegalArgumentException {
/*  479 */     return notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T, X extends Throwable> T[] noNullElements(T[] array, Supplier<X> errorSupplier) throws X {
/*  502 */     if (ArrayUtil.hasNull((Object[])array)) {
/*  503 */       throw (X)errorSupplier.get();
/*      */     }
/*  505 */     return array;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T[] noNullElements(T[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  523 */     return noNullElements(array, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T[] noNullElements(T[] array) throws IllegalArgumentException {
/*  539 */     return noNullElements(array, "[Assertion failed] - this array must not contain any null elements", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E, T extends Iterable<E>, X extends Throwable> T notEmpty(T collection, Supplier<X> errorSupplier) throws X {
/*  563 */     if (CollUtil.isEmpty((Iterable)collection)) {
/*  564 */       throw (X)errorSupplier.get();
/*      */     }
/*  566 */     return collection;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E, T extends Iterable<E>> T notEmpty(T collection, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  585 */     return notEmpty(collection, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <E, T extends Iterable<E>> T notEmpty(T collection) throws IllegalArgumentException {
/*  602 */     return notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V, T extends Map<K, V>, X extends Throwable> T notEmpty(T map, Supplier<X> errorSupplier) throws X {
/*  627 */     if (MapUtil.isEmpty((Map)map)) {
/*  628 */       throw (X)errorSupplier.get();
/*      */     }
/*  630 */     return map;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V, T extends Map<K, V>> T notEmpty(T map, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  650 */     return notEmpty(map, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <K, V, T extends Map<K, V>> T notEmpty(T map) throws IllegalArgumentException {
/*  668 */     return notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T isInstanceOf(Class<?> type, T obj) {
/*  686 */     return isInstanceOf(type, obj, "Object [{}] is not instanceof [{}]", new Object[] { obj, type });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <T> T isInstanceOf(Class<?> type, T obj, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  706 */     notNull(type, "Type to check against must not be null", new Object[0]);
/*  707 */     if (false == type.isInstance(obj)) {
/*  708 */       throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
/*      */     }
/*  710 */     return obj;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isAssignable(Class<?> superType, Class<?> subType) throws IllegalArgumentException {
/*  725 */     isAssignable(superType, subType, "{} is not assignable to {})", new Object[] { subType, superType });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void isAssignable(Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/*  742 */     notNull(superType, "Type to check against must not be null", new Object[0]);
/*  743 */     if (subType == null || !superType.isAssignableFrom(subType)) {
/*  744 */       throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void state(boolean expression, Supplier<String> errorMsgSupplier) throws IllegalStateException {
/*  763 */     if (false == expression) {
/*  764 */       throw new IllegalStateException((String)errorMsgSupplier.get());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void state(boolean expression, String errorMsgTemplate, Object... params) throws IllegalStateException {
/*  781 */     if (false == expression) {
/*  782 */       throw new IllegalStateException(StrUtil.format(errorMsgTemplate, params));
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void state(boolean expression) throws IllegalStateException {
/*  797 */     state(expression, "[Assertion failed] - this state invariant must be true", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int checkIndex(int index, int size) throws IllegalArgumentException, IndexOutOfBoundsException {
/*  815 */     return checkIndex(index, size, "[Assertion failed]", new Object[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int checkIndex(int index, int size, String errorMsgTemplate, Object... params) throws IllegalArgumentException, IndexOutOfBoundsException {
/*  835 */     if (index < 0 || index >= size) {
/*  836 */       throw new IndexOutOfBoundsException(badIndexMsg(index, size, errorMsgTemplate, params));
/*      */     }
/*  838 */     return index;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> int checkBetween(int value, int min, int max, Supplier<? extends X> errorSupplier) throws X {
/*  854 */     if (value < min || value > max) {
/*  855 */       throw (X)errorSupplier.get();
/*      */     }
/*      */     
/*  858 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int checkBetween(int value, int min, int max, String errorMsgTemplate, Object... params) {
/*  873 */     return checkBetween(value, min, max, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int checkBetween(int value, int min, int max) {
/*  886 */     return checkBetween(value, min, max, "The value must be between {} and {}.", new Object[] { Integer.valueOf(min), Integer.valueOf(max) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> long checkBetween(long value, long min, long max, Supplier<? extends X> errorSupplier) throws X {
/*  902 */     if (value < min || value > max) {
/*  903 */       throw (X)errorSupplier.get();
/*      */     }
/*      */     
/*  906 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long checkBetween(long value, long min, long max, String errorMsgTemplate, Object... params) {
/*  921 */     return checkBetween(value, min, max, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long checkBetween(long value, long min, long max) {
/*  934 */     return checkBetween(value, min, max, "The value must be between {} and {}.", new Object[] { Long.valueOf(min), Long.valueOf(max) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> double checkBetween(double value, double min, double max, Supplier<? extends X> errorSupplier) throws X {
/*  950 */     if (value < min || value > max) {
/*  951 */       throw (X)errorSupplier.get();
/*      */     }
/*      */     
/*  954 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double checkBetween(double value, double min, double max, String errorMsgTemplate, Object... params) {
/*  969 */     return checkBetween(value, min, max, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static double checkBetween(double value, double min, double max) {
/*  982 */     return checkBetween(value, min, max, "The value must be between {} and {}.", new Object[] { Double.valueOf(min), Double.valueOf(max) });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Number checkBetween(Number value, Number min, Number max) {
/*  995 */     notNull(value);
/*  996 */     notNull(min);
/*  997 */     notNull(max);
/*  998 */     double valueDouble = value.doubleValue();
/*  999 */     double minDouble = min.doubleValue();
/* 1000 */     double maxDouble = max.doubleValue();
/* 1001 */     if (valueDouble < minDouble || valueDouble > maxDouble) {
/* 1002 */       throw new IllegalArgumentException(StrUtil.format("The value must be between {} and {}.", new Object[] { min, max }));
/*      */     }
/* 1004 */     return value;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void notEquals(Object obj1, Object obj2) {
/* 1018 */     notEquals(obj1, obj2, "({}) must be not equals ({})", new Object[] { obj1, obj2 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void notEquals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/* 1034 */     notEquals(obj1, obj2, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> void notEquals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
/* 1047 */     if (ObjectUtil.equals(obj1, obj2)) {
/* 1048 */       throw (X)errorSupplier.get();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void equals(Object obj1, Object obj2) {
/* 1064 */     equals(obj1, obj2, "({}) must be equals ({})", new Object[] { obj1, obj2 });
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void equals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
/* 1080 */     equals(obj1, obj2, () -> new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static <X extends Throwable> void equals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
/* 1093 */     if (ObjectUtil.notEqual(obj1, obj2)) {
/* 1094 */       throw (X)errorSupplier.get();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static String badIndexMsg(int index, int size, String desc, Object... params) {
/* 1112 */     if (index < 0)
/* 1113 */       return StrUtil.format("{} ({}) must not be negative", new Object[] { StrUtil.format(desc, params), Integer.valueOf(index) }); 
/* 1114 */     if (size < 0) {
/* 1115 */       throw new IllegalArgumentException("negative size: " + size);
/*      */     }
/* 1117 */     return StrUtil.format("{} ({}) must be less than size ({})", new Object[] { StrUtil.format(desc, params), Integer.valueOf(index), Integer.valueOf(size) });
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\Assert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */