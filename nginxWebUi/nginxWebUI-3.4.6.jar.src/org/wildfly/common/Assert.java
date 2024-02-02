/*     */ package org.wildfly.common;
/*     */ 
/*     */ import org.wildfly.common._private.CommonMessages;
/*     */ import org.wildfly.common.annotation.NotNull;
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
/*     */ public final class Assert
/*     */ {
/*     */   @NotNull
/*     */   public static <T> T checkNotNullParam(String name, T value) throws IllegalArgumentException {
/*  48 */     checkNotNullParamChecked("name", name);
/*  49 */     checkNotNullParamChecked(name, value);
/*  50 */     return value;
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
/*     */   @NotNull
/*     */   public static <T> T checkNotNullParamWithNullPointerException(String name, T value) throws NullPointerException {
/*  65 */     checkNotNullParamChecked("name", name);
/*  66 */     if (value == null) throw CommonMessages.msg.nullParamNPE(name); 
/*  67 */     return value;
/*     */   }
/*     */   
/*     */   private static <T> void checkNotNullParamChecked(String name, T value) {
/*  71 */     if (value == null) throw CommonMessages.msg.nullParam(name);
/*     */   
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
/*     */   @NotNull
/*     */   public static <T> T checkNotNullArrayParam(String name, int index, T value) throws IllegalArgumentException {
/*  87 */     checkNotNullParamChecked("name", name);
/*  88 */     if (value == null) throw CommonMessages.msg.nullArrayParam(index, name); 
/*  89 */     return value;
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
/*     */   @NotNull
/*     */   public static String checkNotEmptyParam(String name, String value) {
/* 102 */     checkNotNullParamChecked("name", name);
/* 103 */     checkNotNullParamChecked("value", value);
/* 104 */     if (value.isEmpty()) throw CommonMessages.msg.emptyParam(name); 
/* 105 */     return value;
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
/*     */   @NotNull
/*     */   public static CharSequence checkNotEmptyParam(String name, CharSequence value) {
/* 118 */     checkNotNullParamChecked("name", name);
/* 119 */     checkNotNullParamChecked("value", value);
/* 120 */     if (value.length() == 0) throw CommonMessages.msg.emptyParam(name); 
/* 121 */     return value;
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
/*     */   @NotNull
/*     */   public static <E, T extends java.util.Collection<E>> T checkNotEmptyParam(String name, T value) {
/* 134 */     checkNotNullParamChecked("name", name);
/* 135 */     checkNotNullParamChecked("value", value);
/* 136 */     if (value.isEmpty()) throw CommonMessages.msg.emptyParam(name); 
/* 137 */     return value;
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
/*     */   @NotNull
/*     */   public static <K, V, T extends java.util.Map<K, V>> T checkNotEmptyParam(String name, T value) {
/* 150 */     checkNotNullParamChecked("name", name);
/* 151 */     checkNotNullParamChecked("value", value);
/* 152 */     if (value.isEmpty()) throw CommonMessages.msg.emptyParam(name); 
/* 153 */     return value;
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
/*     */   @NotNull
/*     */   public static <T> T[] checkNotEmptyParam(String name, T[] value) {
/* 166 */     checkNotNullParamChecked("name", name);
/* 167 */     checkNotNullParamChecked("value", value);
/* 168 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 169 */     return value;
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
/*     */   @NotNull
/*     */   public static boolean[] checkNotEmptyParam(String name, boolean[] value) {
/* 182 */     checkNotNullParamChecked("name", name);
/* 183 */     checkNotNullParamChecked("value", value);
/* 184 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 185 */     return value;
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
/*     */   @NotNull
/*     */   public static byte[] checkNotEmptyParam(String name, byte[] value) {
/* 198 */     checkNotNullParamChecked("name", name);
/* 199 */     checkNotNullParamChecked("value", value);
/* 200 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 201 */     return value;
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
/*     */   @NotNull
/*     */   public static short[] checkNotEmptyParam(String name, short[] value) {
/* 214 */     checkNotNullParamChecked("name", name);
/* 215 */     checkNotNullParamChecked("value", value);
/* 216 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 217 */     return value;
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
/*     */   @NotNull
/*     */   public static int[] checkNotEmptyParam(String name, int[] value) {
/* 230 */     checkNotNullParamChecked("name", name);
/* 231 */     checkNotNullParamChecked("value", value);
/* 232 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 233 */     return value;
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
/*     */   @NotNull
/*     */   public static long[] checkNotEmptyParam(String name, long[] value) {
/* 246 */     checkNotNullParamChecked("name", name);
/* 247 */     checkNotNullParamChecked("value", value);
/* 248 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 249 */     return value;
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
/*     */   @NotNull
/*     */   public static float[] checkNotEmptyParam(String name, float[] value) {
/* 262 */     checkNotNullParamChecked("name", name);
/* 263 */     checkNotNullParamChecked("value", value);
/* 264 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 265 */     return value;
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
/*     */   @NotNull
/*     */   public static double[] checkNotEmptyParam(String name, double[] value) {
/* 278 */     checkNotNullParamChecked("name", name);
/* 279 */     checkNotNullParamChecked("value", value);
/* 280 */     if (value.length == 0) throw CommonMessages.msg.emptyParam(name); 
/* 281 */     return value;
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
/*     */   public static void checkMinimumParameter(String name, int min, int actual) throws IllegalArgumentException {
/* 293 */     checkNotNullParamChecked("name", name);
/* 294 */     if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMinimumParameter(String name, long min, long actual) throws IllegalArgumentException {
/* 306 */     checkNotNullParamChecked("name", name);
/* 307 */     if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMinimumParameter(String name, float min, float actual) throws IllegalArgumentException {
/* 319 */     checkNotNullParamChecked("name", name);
/* 320 */     if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMinimumParameter(String name, double min, double actual) throws IllegalArgumentException {
/* 332 */     checkNotNullParamChecked("name", name);
/* 333 */     if (actual < min) throw CommonMessages.msg.paramLessThan(name, min);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMaximumParameter(String name, int max, int actual) throws IllegalArgumentException {
/* 345 */     checkNotNullParamChecked("name", name);
/* 346 */     if (actual > max) throw CommonMessages.msg.paramGreaterThan(name, max);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMaximumParameter(String name, long max, long actual) throws IllegalArgumentException {
/* 358 */     checkNotNullParamChecked("name", name);
/* 359 */     if (actual > max) throw CommonMessages.msg.paramGreaterThan(name, max);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMaximumParameter(String name, float max, float actual) throws IllegalArgumentException {
/* 371 */     checkNotNullParamChecked("name", name);
/* 372 */     if (actual > max) throw CommonMessages.msg.paramGreaterThan(name, max);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkMaximumParameter(String name, double max, double actual) throws IllegalArgumentException {
/* 384 */     checkNotNullParamChecked("name", name);
/* 385 */     if (actual > max) throw CommonMessages.msg.paramGreaterThan(name, max);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void checkArrayBounds(Object[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 397 */     checkNotNullParamChecked("array", array);
/* 398 */     checkArrayBounds(array.length, offs, len);
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
/*     */   public static void checkArrayBounds(byte[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 410 */     checkNotNullParamChecked("array", array);
/* 411 */     checkArrayBounds(array.length, offs, len);
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
/*     */   public static void checkArrayBounds(char[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 423 */     checkNotNullParamChecked("array", array);
/* 424 */     checkArrayBounds(array.length, offs, len);
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
/*     */   public static void checkArrayBounds(int[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 436 */     checkNotNullParamChecked("array", array);
/* 437 */     checkArrayBounds(array.length, offs, len);
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
/*     */   public static void checkArrayBounds(long[] array, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 449 */     checkNotNullParamChecked("array", array);
/* 450 */     checkArrayBounds(array.length, offs, len);
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
/*     */   public static void checkArrayBounds(int arrayLength, int offs, int len) throws ArrayIndexOutOfBoundsException {
/* 462 */     checkMinimumParameter("offs", 0, offs);
/* 463 */     checkMinimumParameter("len", 0, len);
/* 464 */     if (offs > arrayLength) throw CommonMessages.msg.arrayOffsetGreaterThanLength(offs, arrayLength); 
/* 465 */     if (offs + len > arrayLength) throw CommonMessages.msg.arrayOffsetLengthGreaterThanLength(offs, len, arrayLength);
/*     */   
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static <T> T assertNotNull(T value) {
/* 478 */     assert value != null : CommonMessages.msg.unexpectedNullValue();
/* 479 */     return value;
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
/*     */   @NotNull
/*     */   public static <T> T assertHoldsLock(@NotNull T monitor) {
/* 493 */     assert Thread.holdsLock(checkNotNullParam("monitor", monitor)) : CommonMessages.msg.expectedLockHold(monitor);
/* 494 */     return monitor;
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
/*     */   @NotNull
/*     */   public static <T> T assertNotHoldsLock(@NotNull T monitor) {
/* 508 */     assert !Thread.holdsLock(checkNotNullParam("monitor", monitor)) : CommonMessages.msg.expectedLockNotHold(monitor);
/* 509 */     return monitor;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean assertTrue(boolean expr) {
/* 520 */     assert expr : CommonMessages.msg.expectedBoolean(expr);
/* 521 */     return expr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean assertFalse(boolean expr) {
/* 532 */     assert !expr : CommonMessages.msg.expectedBoolean(expr);
/* 533 */     return expr;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IllegalStateException unreachableCode() {
/* 542 */     return CommonMessages.msg.unreachableCode();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static IllegalStateException impossibleSwitchCase(@NotNull Object obj) {
/* 553 */     checkNotNullParamChecked("obj", obj);
/* 554 */     return CommonMessages.msg.impossibleSwitchCase(obj);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static IllegalStateException impossibleSwitchCase(int val) {
/* 565 */     return CommonMessages.msg.impossibleSwitchCase(Integer.valueOf(val));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static IllegalStateException impossibleSwitchCase(long val) {
/* 576 */     return CommonMessages.msg.impossibleSwitchCase(Long.valueOf(val));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @NotNull
/*     */   public static UnsupportedOperationException unsupported() {
/* 586 */     StackTraceElement element = (new Throwable()).getStackTrace()[1];
/* 587 */     return CommonMessages.msg.unsupported(element.getMethodName(), element.getClassName());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\Assert.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */