package org.wildfly.common;

import java.util.Collection;
import java.util.Map;
import org.wildfly.common._private.CommonMessages;
import org.wildfly.common.annotation.NotNull;

public final class Assert {
   private Assert() {
   }

   @NotNull
   public static <T> T checkNotNullParam(String name, T value) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked(name, value);
      return value;
   }

   @NotNull
   public static <T> T checkNotNullParamWithNullPointerException(String name, T value) throws NullPointerException {
      checkNotNullParamChecked("name", name);
      if (value == null) {
         throw CommonMessages.msg.nullParamNPE(name);
      } else {
         return value;
      }
   }

   private static <T> void checkNotNullParamChecked(String name, T value) {
      if (value == null) {
         throw CommonMessages.msg.nullParam(name);
      }
   }

   @NotNull
   public static <T> T checkNotNullArrayParam(String name, int index, T value) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (value == null) {
         throw CommonMessages.msg.nullArrayParam(index, name);
      } else {
         return value;
      }
   }

   @NotNull
   public static String checkNotEmptyParam(String name, String value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.isEmpty()) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static CharSequence checkNotEmptyParam(String name, CharSequence value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length() == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static <E, T extends Collection<E>> T checkNotEmptyParam(String name, T value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.isEmpty()) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static <K, V, T extends Map<K, V>> T checkNotEmptyParam(String name, T value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.isEmpty()) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static <T> T[] checkNotEmptyParam(String name, T[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static boolean[] checkNotEmptyParam(String name, boolean[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static byte[] checkNotEmptyParam(String name, byte[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static short[] checkNotEmptyParam(String name, short[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static int[] checkNotEmptyParam(String name, int[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static long[] checkNotEmptyParam(String name, long[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static float[] checkNotEmptyParam(String name, float[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   @NotNull
   public static double[] checkNotEmptyParam(String name, double[] value) {
      checkNotNullParamChecked("name", name);
      checkNotNullParamChecked("value", value);
      if (value.length == 0) {
         throw CommonMessages.msg.emptyParam(name);
      } else {
         return value;
      }
   }

   public static void checkMinimumParameter(String name, int min, int actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual < min) {
         throw CommonMessages.msg.paramLessThan(name, (long)min);
      }
   }

   public static void checkMinimumParameter(String name, long min, long actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual < min) {
         throw CommonMessages.msg.paramLessThan(name, min);
      }
   }

   public static void checkMinimumParameter(String name, float min, float actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual < min) {
         throw CommonMessages.msg.paramLessThan(name, (double)min);
      }
   }

   public static void checkMinimumParameter(String name, double min, double actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual < min) {
         throw CommonMessages.msg.paramLessThan(name, min);
      }
   }

   public static void checkMaximumParameter(String name, int max, int actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual > max) {
         throw CommonMessages.msg.paramGreaterThan(name, (long)max);
      }
   }

   public static void checkMaximumParameter(String name, long max, long actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual > max) {
         throw CommonMessages.msg.paramGreaterThan(name, max);
      }
   }

   public static void checkMaximumParameter(String name, float max, float actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual > max) {
         throw CommonMessages.msg.paramGreaterThan(name, (double)max);
      }
   }

   public static void checkMaximumParameter(String name, double max, double actual) throws IllegalArgumentException {
      checkNotNullParamChecked("name", name);
      if (actual > max) {
         throw CommonMessages.msg.paramGreaterThan(name, max);
      }
   }

   public static void checkArrayBounds(Object[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkNotNullParamChecked("array", array);
      checkArrayBounds(array.length, offs, len);
   }

   public static void checkArrayBounds(byte[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkNotNullParamChecked("array", array);
      checkArrayBounds(array.length, offs, len);
   }

   public static void checkArrayBounds(char[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkNotNullParamChecked("array", array);
      checkArrayBounds(array.length, offs, len);
   }

   public static void checkArrayBounds(int[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkNotNullParamChecked("array", array);
      checkArrayBounds(array.length, offs, len);
   }

   public static void checkArrayBounds(long[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkNotNullParamChecked("array", array);
      checkArrayBounds(array.length, offs, len);
   }

   public static void checkArrayBounds(int arrayLength, int offs, int len) throws ArrayIndexOutOfBoundsException {
      checkMinimumParameter("offs", 0, offs);
      checkMinimumParameter("len", 0, len);
      if (offs > arrayLength) {
         throw CommonMessages.msg.arrayOffsetGreaterThanLength(offs, arrayLength);
      } else if (offs + len > arrayLength) {
         throw CommonMessages.msg.arrayOffsetLengthGreaterThanLength(offs, len, arrayLength);
      }
   }

   @NotNull
   public static <T> T assertNotNull(T value) {
      assert value != null : CommonMessages.msg.unexpectedNullValue();

      return value;
   }

   @NotNull
   public static <T> T assertHoldsLock(@NotNull T monitor) {
      assert Thread.holdsLock(checkNotNullParam("monitor", monitor)) : CommonMessages.msg.expectedLockHold(monitor);

      return monitor;
   }

   @NotNull
   public static <T> T assertNotHoldsLock(@NotNull T monitor) {
      assert !Thread.holdsLock(checkNotNullParam("monitor", monitor)) : CommonMessages.msg.expectedLockNotHold(monitor);

      return monitor;
   }

   public static boolean assertTrue(boolean expr) {
      assert expr : CommonMessages.msg.expectedBoolean(expr);

      return expr;
   }

   public static boolean assertFalse(boolean expr) {
      assert !expr : CommonMessages.msg.expectedBoolean(expr);

      return expr;
   }

   public static IllegalStateException unreachableCode() {
      return CommonMessages.msg.unreachableCode();
   }

   @NotNull
   public static IllegalStateException impossibleSwitchCase(@NotNull Object obj) {
      checkNotNullParamChecked("obj", obj);
      return CommonMessages.msg.impossibleSwitchCase(obj);
   }

   @NotNull
   public static IllegalStateException impossibleSwitchCase(int val) {
      return CommonMessages.msg.impossibleSwitchCase(val);
   }

   @NotNull
   public static IllegalStateException impossibleSwitchCase(long val) {
      return CommonMessages.msg.impossibleSwitchCase(val);
   }

   @NotNull
   public static UnsupportedOperationException unsupported() {
      StackTraceElement element = (new Throwable()).getStackTrace()[1];
      return CommonMessages.msg.unsupported(element.getMethodName(), element.getClassName());
   }
}
