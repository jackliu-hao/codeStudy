package cn.hutool.core.lang;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import java.util.Map;
import java.util.function.Supplier;

public class Assert {
   private static final String TEMPLATE_VALUE_MUST_BE_BETWEEN_AND = "The value must be between {} and {}.";

   public static <X extends Throwable> void isTrue(boolean expression, Supplier<? extends X> supplier) throws X {
      if (!expression) {
         throw (Throwable)supplier.get();
      }
   }

   public static void isTrue(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      isTrue(expression, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static void isTrue(boolean expression) throws IllegalArgumentException {
      isTrue(expression, "[Assertion failed] - this expression must be true");
   }

   public static <X extends Throwable> void isFalse(boolean expression, Supplier<X> errorSupplier) throws X {
      if (expression) {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static void isFalse(boolean expression, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      isFalse(expression, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static void isFalse(boolean expression) throws IllegalArgumentException {
      isFalse(expression, "[Assertion failed] - this expression must be false");
   }

   public static <X extends Throwable> void isNull(Object object, Supplier<X> errorSupplier) throws X {
      if (null != object) {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static void isNull(Object object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      isNull(object, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static void isNull(Object object) throws IllegalArgumentException {
      isNull(object, "[Assertion failed] - the object argument must be null");
   }

   public static <T, X extends Throwable> T notNull(T object, Supplier<X> errorSupplier) throws X {
      if (null == object) {
         throw (Throwable)errorSupplier.get();
      } else {
         return object;
      }
   }

   public static <T> T notNull(T object, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notNull(object, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <T> T notNull(T object) throws IllegalArgumentException {
      return notNull(object, "[Assertion failed] - this argument is required; it must not be null");
   }

   public static <T extends CharSequence, X extends Throwable> T notEmpty(T text, Supplier<X> errorSupplier) throws X {
      if (StrUtil.isEmpty(text)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return text;
      }
   }

   public static <T extends CharSequence> T notEmpty(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notEmpty(text, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <T extends CharSequence> T notEmpty(T text) throws IllegalArgumentException {
      return notEmpty(text, "[Assertion failed] - this String argument must have length; it must not be null or empty");
   }

   public static <T extends CharSequence, X extends Throwable> T notBlank(T text, Supplier<X> errorMsgSupplier) throws X {
      if (StrUtil.isBlank(text)) {
         throw (Throwable)errorMsgSupplier.get();
      } else {
         return text;
      }
   }

   public static <T extends CharSequence> T notBlank(T text, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notBlank(text, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <T extends CharSequence> T notBlank(T text) throws IllegalArgumentException {
      return notBlank(text, "[Assertion failed] - this String argument must have text; it must not be null, empty, or blank");
   }

   public static <T extends CharSequence, X extends Throwable> T notContain(CharSequence textToSearch, T substring, Supplier<X> errorSupplier) throws X {
      if (StrUtil.contains(textToSearch, substring)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return substring;
      }
   }

   public static String notContain(String textToSearch, String substring, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return (String)notContain(textToSearch, substring, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static String notContain(String textToSearch, String substring) throws IllegalArgumentException {
      return notContain(textToSearch, substring, "[Assertion failed] - this String argument must not contain the substring [{}]", substring);
   }

   public static <T, X extends Throwable> T[] notEmpty(T[] array, Supplier<X> errorSupplier) throws X {
      if (ArrayUtil.isEmpty(array)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return array;
      }
   }

   public static <T> T[] notEmpty(T[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notEmpty(array, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <T> T[] notEmpty(T[] array) throws IllegalArgumentException {
      return notEmpty(array, "[Assertion failed] - this array must not be empty: it must contain at least 1 element");
   }

   public static <T, X extends Throwable> T[] noNullElements(T[] array, Supplier<X> errorSupplier) throws X {
      if (ArrayUtil.hasNull(array)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return array;
      }
   }

   public static <T> T[] noNullElements(T[] array, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return noNullElements(array, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <T> T[] noNullElements(T[] array) throws IllegalArgumentException {
      return noNullElements(array, "[Assertion failed] - this array must not contain any null elements");
   }

   public static <E, T extends Iterable<E>, X extends Throwable> T notEmpty(T collection, Supplier<X> errorSupplier) throws X {
      if (CollUtil.isEmpty(collection)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return collection;
      }
   }

   public static <E, T extends Iterable<E>> T notEmpty(T collection, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notEmpty(collection, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <E, T extends Iterable<E>> T notEmpty(T collection) throws IllegalArgumentException {
      return notEmpty(collection, "[Assertion failed] - this collection must not be empty: it must contain at least 1 element");
   }

   public static <K, V, T extends Map<K, V>, X extends Throwable> T notEmpty(T map, Supplier<X> errorSupplier) throws X {
      if (MapUtil.isEmpty(map)) {
         throw (Throwable)errorSupplier.get();
      } else {
         return map;
      }
   }

   public static <K, V, T extends Map<K, V>> T notEmpty(T map, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      return notEmpty(map, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <K, V, T extends Map<K, V>> T notEmpty(T map) throws IllegalArgumentException {
      return notEmpty(map, "[Assertion failed] - this map must not be empty; it must contain at least one entry");
   }

   public static <T> T isInstanceOf(Class<?> type, T obj) {
      return isInstanceOf(type, obj, "Object [{}] is not instanceof [{}]", obj, type);
   }

   public static <T> T isInstanceOf(Class<?> type, T obj, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      notNull(type, "Type to check against must not be null");
      if (!type.isInstance(obj)) {
         throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      } else {
         return obj;
      }
   }

   public static void isAssignable(Class<?> superType, Class<?> subType) throws IllegalArgumentException {
      isAssignable(superType, subType, "{} is not assignable to {})", subType, superType);
   }

   public static void isAssignable(Class<?> superType, Class<?> subType, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      notNull(superType, "Type to check against must not be null");
      if (subType == null || !superType.isAssignableFrom(subType)) {
         throw new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      }
   }

   public static void state(boolean expression, Supplier<String> errorMsgSupplier) throws IllegalStateException {
      if (!expression) {
         throw new IllegalStateException((String)errorMsgSupplier.get());
      }
   }

   public static void state(boolean expression, String errorMsgTemplate, Object... params) throws IllegalStateException {
      if (!expression) {
         throw new IllegalStateException(StrUtil.format(errorMsgTemplate, params));
      }
   }

   public static void state(boolean expression) throws IllegalStateException {
      state(expression, "[Assertion failed] - this state invariant must be true");
   }

   public static int checkIndex(int index, int size) throws IllegalArgumentException, IndexOutOfBoundsException {
      return checkIndex(index, size, "[Assertion failed]");
   }

   public static int checkIndex(int index, int size, String errorMsgTemplate, Object... params) throws IllegalArgumentException, IndexOutOfBoundsException {
      if (index >= 0 && index < size) {
         return index;
      } else {
         throw new IndexOutOfBoundsException(badIndexMsg(index, size, errorMsgTemplate, params));
      }
   }

   public static <X extends Throwable> int checkBetween(int value, int min, int max, Supplier<? extends X> errorSupplier) throws X {
      if (value >= min && value <= max) {
         return value;
      } else {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static int checkBetween(int value, int min, int max, String errorMsgTemplate, Object... params) {
      return checkBetween(value, min, max, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static int checkBetween(int value, int min, int max) {
      return checkBetween(value, min, max, "The value must be between {} and {}.", min, max);
   }

   public static <X extends Throwable> long checkBetween(long value, long min, long max, Supplier<? extends X> errorSupplier) throws X {
      if (value >= min && value <= max) {
         return value;
      } else {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static long checkBetween(long value, long min, long max, String errorMsgTemplate, Object... params) {
      return checkBetween(value, min, max, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static long checkBetween(long value, long min, long max) {
      return checkBetween(value, min, max, "The value must be between {} and {}.", min, max);
   }

   public static <X extends Throwable> double checkBetween(double value, double min, double max, Supplier<? extends X> errorSupplier) throws X {
      if (!(value < min) && !(value > max)) {
         return value;
      } else {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static double checkBetween(double value, double min, double max, String errorMsgTemplate, Object... params) {
      return checkBetween(value, min, max, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static double checkBetween(double value, double min, double max) {
      return checkBetween(value, min, max, "The value must be between {} and {}.", min, max);
   }

   public static Number checkBetween(Number value, Number min, Number max) {
      notNull(value);
      notNull(min);
      notNull(max);
      double valueDouble = value.doubleValue();
      double minDouble = min.doubleValue();
      double maxDouble = max.doubleValue();
      if (!(valueDouble < minDouble) && !(valueDouble > maxDouble)) {
         return value;
      } else {
         throw new IllegalArgumentException(StrUtil.format("The value must be between {} and {}.", new Object[]{min, max}));
      }
   }

   public static void notEquals(Object obj1, Object obj2) {
      notEquals(obj1, obj2, "({}) must be not equals ({})", obj1, obj2);
   }

   public static void notEquals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      notEquals(obj1, obj2, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <X extends Throwable> void notEquals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
      if (ObjectUtil.equals(obj1, obj2)) {
         throw (Throwable)errorSupplier.get();
      }
   }

   public static void equals(Object obj1, Object obj2) {
      equals(obj1, obj2, "({}) must be equals ({})", obj1, obj2);
   }

   public static void equals(Object obj1, Object obj2, String errorMsgTemplate, Object... params) throws IllegalArgumentException {
      equals(obj1, obj2, () -> {
         return new IllegalArgumentException(StrUtil.format(errorMsgTemplate, params));
      });
   }

   public static <X extends Throwable> void equals(Object obj1, Object obj2, Supplier<X> errorSupplier) throws X {
      if (ObjectUtil.notEqual(obj1, obj2)) {
         throw (Throwable)errorSupplier.get();
      }
   }

   private static String badIndexMsg(int index, int size, String desc, Object... params) {
      if (index < 0) {
         return StrUtil.format("{} ({}) must not be negative", new Object[]{StrUtil.format(desc, params), index});
      } else if (size < 0) {
         throw new IllegalArgumentException("negative size: " + size);
      } else {
         return StrUtil.format("{} ({}) must be less than size ({})", new Object[]{StrUtil.format(desc, params), index, size});
      }
   }
}
