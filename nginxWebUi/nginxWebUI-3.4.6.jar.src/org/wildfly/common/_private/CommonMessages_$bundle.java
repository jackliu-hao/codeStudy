/*     */ package org.wildfly.common._private;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.security.Permission;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import org.wildfly.common.codec.DecodeException;
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
/*     */ public class CommonMessages_$bundle
/*     */   implements CommonMessages, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  28 */   public static final CommonMessages_$bundle INSTANCE = new CommonMessages_$bundle();
/*     */   protected Object readResolve() {
/*  30 */     return INSTANCE;
/*     */   }
/*  32 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  34 */     return LOCALE;
/*     */   }
/*     */   protected String nullParam$str() {
/*  37 */     return "Parameter '%s' may not be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException nullParam(String paramName) {
/*  41 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), nullParam$str(), new Object[] { paramName }));
/*  42 */     _copyStackTraceMinusOne(result);
/*  43 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  46 */     StackTraceElement[] st = e.getStackTrace();
/*  47 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String paramLessThan$str() {
/*  50 */     return "COM00001: Parameter '%s' must not be less than %d";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramLessThan(String name, long min) {
/*  54 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramLessThan$str(), new Object[] { name, Long.valueOf(min) }));
/*  55 */     _copyStackTraceMinusOne(result);
/*  56 */     return result;
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramLessThan(String name, double min) {
/*  60 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramLessThan$str(), new Object[] { name, Double.valueOf(min) }));
/*  61 */     _copyStackTraceMinusOne(result);
/*  62 */     return result;
/*     */   }
/*     */   protected String paramGreaterThan$str() {
/*  65 */     return "COM00002: Parameter '%s' must not be greater than than %d";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramGreaterThan(String name, long max) {
/*  69 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramGreaterThan$str(), new Object[] { name, Long.valueOf(max) }));
/*  70 */     _copyStackTraceMinusOne(result);
/*  71 */     return result;
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException paramGreaterThan(String name, double max) {
/*  75 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), paramGreaterThan$str(), new Object[] { name, Double.valueOf(max) }));
/*  76 */     _copyStackTraceMinusOne(result);
/*  77 */     return result;
/*     */   }
/*     */   protected String arrayOffsetGreaterThanLength$str() {
/*  80 */     return "COM00003: Given offset of %d is greater than array length of %d";
/*     */   }
/*     */   
/*     */   public final ArrayIndexOutOfBoundsException arrayOffsetGreaterThanLength(int offs, int arrayLength) {
/*  84 */     ArrayIndexOutOfBoundsException result = new ArrayIndexOutOfBoundsException(String.format(getLoggingLocale(), arrayOffsetGreaterThanLength$str(), new Object[] { Integer.valueOf(offs), Integer.valueOf(arrayLength) }));
/*  85 */     _copyStackTraceMinusOne(result);
/*  86 */     return result;
/*     */   }
/*     */   protected String arrayOffsetLengthGreaterThanLength$str() {
/*  89 */     return "COM00004: Given offset of %d plus length of %d is greater than array length of %d";
/*     */   }
/*     */   
/*     */   public final ArrayIndexOutOfBoundsException arrayOffsetLengthGreaterThanLength(int offs, int len, int arrayLength) {
/*  93 */     ArrayIndexOutOfBoundsException result = new ArrayIndexOutOfBoundsException(String.format(getLoggingLocale(), arrayOffsetLengthGreaterThanLength$str(), new Object[] { Integer.valueOf(offs), Integer.valueOf(len), Integer.valueOf(arrayLength) }));
/*  94 */     _copyStackTraceMinusOne(result);
/*  95 */     return result;
/*     */   }
/*     */   protected String nullArrayParam$str() {
/*  98 */     return "COM00005: Array index %d of parameter '%s' may not be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException nullArrayParam(int index, String name) {
/* 102 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), nullArrayParam$str(), new Object[] { Integer.valueOf(index), name }));
/* 103 */     _copyStackTraceMinusOne(result);
/* 104 */     return result;
/*     */   }
/*     */   protected String nullParamNPE$str() {
/* 107 */     return "COM00006: Parameter '%s' may not be null";
/*     */   }
/*     */   
/*     */   public final NullPointerException nullParamNPE(String name) {
/* 111 */     NullPointerException result = new NullPointerException(String.format(getLoggingLocale(), nullParamNPE$str(), new Object[] { name }));
/* 112 */     _copyStackTraceMinusOne(result);
/* 113 */     return result;
/*     */   }
/*     */   protected String invalidPermissionAction$str() {
/* 116 */     return "COM00007: Invalid permission action '%s'";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidPermissionAction(String action) {
/* 120 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidPermissionAction$str(), new Object[] { action }));
/* 121 */     _copyStackTraceMinusOne(result);
/* 122 */     return result;
/*     */   }
/*     */   protected String emptyParam$str() {
/* 125 */     return "COM00008: Parameter '%s' must not be empty";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException emptyParam(String name) {
/* 129 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), emptyParam$str(), new Object[] { name }));
/* 130 */     _copyStackTraceMinusOne(result);
/* 131 */     return result;
/*     */   }
/*     */   protected String invalidExpressionSyntax$str() {
/* 134 */     return "COM00009: Invalid expression syntax at position %d";
/*     */   }
/*     */   
/*     */   public final String invalidExpressionSyntax(int index) {
/* 138 */     return String.format(getLoggingLocale(), invalidExpressionSyntax$str(), new Object[] { Integer.valueOf(index) });
/*     */   }
/*     */   protected String unresolvedEnvironmentProperty$str() {
/* 141 */     return "COM00010: No environment property found named \"%s\"";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException unresolvedEnvironmentProperty(String name) {
/* 145 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), unresolvedEnvironmentProperty$str(), new Object[] { name }));
/* 146 */     _copyStackTraceMinusOne(result);
/* 147 */     return result;
/*     */   }
/*     */   protected String unresolvedSystemProperty$str() {
/* 150 */     return "COM00011: No system property found named \"%s\"";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException unresolvedSystemProperty(String name) {
/* 154 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), unresolvedSystemProperty$str(), new Object[] { name }));
/* 155 */     _copyStackTraceMinusOne(result);
/* 156 */     return result;
/*     */   }
/*     */   protected String invalidAddressBytes$str() {
/* 159 */     return "COM00012: Invalid address length of %d; must be 4 or 16";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidAddressBytes(int length) {
/* 163 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidAddressBytes$str(), new Object[] { Integer.valueOf(length) }));
/* 164 */     _copyStackTraceMinusOne(result);
/* 165 */     return result;
/*     */   }
/*     */   protected String invalidAddress$str() {
/* 168 */     return "COM00013: Invalid address string \"%s\"";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidAddress(String address) {
/* 172 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidAddress$str(), new Object[] { address }));
/* 173 */     _copyStackTraceMinusOne(result);
/* 174 */     return result;
/*     */   }
/*     */   protected String unsupported$str() {
/* 177 */     return "COM00100: Method \"%s\" of class \"%s\" is not implemented";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException unsupported(String methodName, String className) {
/* 181 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), unsupported$str(), new Object[] { methodName, className }));
/* 182 */     _copyStackTraceMinusOne(result);
/* 183 */     return result;
/*     */   }
/*     */   protected String privilegedActionFailed$str() {
/* 186 */     return "COM00200: Privileged action failed";
/*     */   }
/*     */   
/*     */   public final PrivilegedActionException privilegedActionFailed(Exception e) {
/* 190 */     PrivilegedActionException result = new PrivilegedActionException(e);
/* 191 */     _copyStackTraceMinusOne(result);
/* 192 */     return result;
/*     */   }
/*     */   protected String readOnlyPermissionCollection$str() {
/* 195 */     return "COM00300: Permission collection is read-only";
/*     */   }
/*     */   
/*     */   public final SecurityException readOnlyPermissionCollection() {
/* 199 */     SecurityException result = new SecurityException(String.format(getLoggingLocale(), readOnlyPermissionCollection$str(), new Object[0]));
/* 200 */     _copyStackTraceMinusOne(result);
/* 201 */     return result;
/*     */   }
/*     */   protected String invalidPermissionType$str() {
/* 204 */     return "COM00301: Invalid permission type (expected %s, actual value was %s)";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidPermissionType(Class<? extends Permission> expectedType, Class<? extends Permission> actualType) {
/* 208 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidPermissionType$str(), new Object[] { expectedType, actualType }));
/* 209 */     _copyStackTraceMinusOne(result);
/* 210 */     return result;
/*     */   }
/*     */   protected String invalidOddFields$str() {
/* 213 */     return "COM00400: Invalid serialized remote exception cause object with odd number of strings in fields key/value list";
/*     */   }
/*     */   
/*     */   public final IllegalStateException invalidOddFields() {
/* 217 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), invalidOddFields$str(), new Object[0]));
/* 218 */     _copyStackTraceMinusOne(result);
/* 219 */     return result;
/*     */   }
/*     */   protected String cannotContainNullFieldNameOrValue$str() {
/* 222 */     return "COM00401: Field name or field value cannot be null";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException cannotContainNullFieldNameOrValue() {
/* 226 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), cannotContainNullFieldNameOrValue$str(), new Object[0]));
/* 227 */     _copyStackTraceMinusOne(result);
/* 228 */     return result;
/*     */   }
/*     */   protected String corruptedStream$str() {
/* 231 */     return "COM00402: Remote exception stream is corrupted and cannot be read";
/*     */   }
/*     */   
/*     */   public final IOException corruptedStream() {
/* 235 */     IOException result = new IOException(String.format(getLoggingLocale(), corruptedStream$str(), new Object[0]));
/* 236 */     _copyStackTraceMinusOne(result);
/* 237 */     return result;
/*     */   }
/*     */   protected String remoteException2$str() {
/* 240 */     return "Remote exception %s: %s";
/*     */   }
/*     */   
/*     */   public final String remoteException(String exceptionClassName, String message) {
/* 244 */     return String.format(getLoggingLocale(), remoteException2$str(), new Object[] { exceptionClassName, message });
/*     */   }
/*     */   protected String remoteException1$str() {
/* 247 */     return "Remote exception %s";
/*     */   }
/*     */   
/*     */   public final String remoteException(String exceptionClassName) {
/* 251 */     return String.format(getLoggingLocale(), remoteException1$str(), new Object[] { exceptionClassName });
/*     */   }
/*     */   protected String unexpectedPadding$str() {
/* 254 */     return "COM00500: Unexpected padding";
/*     */   }
/*     */   
/*     */   public final DecodeException unexpectedPadding() {
/* 258 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), unexpectedPadding$str(), new Object[0]));
/* 259 */     _copyStackTraceMinusOne((Throwable)result);
/* 260 */     return result;
/*     */   }
/*     */   protected String expectedPadding$str() {
/* 263 */     return "COM00501: Expected padding";
/*     */   }
/*     */   
/*     */   public final DecodeException expectedPadding() {
/* 267 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), expectedPadding$str(), new Object[0]));
/* 268 */     _copyStackTraceMinusOne((Throwable)result);
/* 269 */     return result;
/*     */   }
/*     */   protected String incompleteDecode$str() {
/* 272 */     return "COM00502: Incomplete decode";
/*     */   }
/*     */   
/*     */   public final DecodeException incompleteDecode() {
/* 276 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), incompleteDecode$str(), new Object[0]));
/* 277 */     _copyStackTraceMinusOne((Throwable)result);
/* 278 */     return result;
/*     */   }
/*     */   protected String expectedPaddingCharacters$str() {
/* 281 */     return "COM00503: Expected %d padding characters";
/*     */   }
/*     */   
/*     */   public final DecodeException expectedPaddingCharacters(int numExpected) {
/* 285 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), expectedPaddingCharacters$str(), new Object[] { Integer.valueOf(numExpected) }));
/* 286 */     _copyStackTraceMinusOne((Throwable)result);
/* 287 */     return result;
/*     */   }
/*     */   protected String invalidBase32Character$str() {
/* 290 */     return "COM00504: Invalid base 32 character";
/*     */   }
/*     */   
/*     */   public final DecodeException invalidBase32Character() {
/* 294 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), invalidBase32Character$str(), new Object[0]));
/* 295 */     _copyStackTraceMinusOne((Throwable)result);
/* 296 */     return result;
/*     */   }
/*     */   protected String expectedEvenNumberOfHexCharacters$str() {
/* 299 */     return "COM00505: Expected an even number of hex characters";
/*     */   }
/*     */   
/*     */   public final DecodeException expectedEvenNumberOfHexCharacters() {
/* 303 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), expectedEvenNumberOfHexCharacters$str(), new Object[0]));
/* 304 */     _copyStackTraceMinusOne((Throwable)result);
/* 305 */     return result;
/*     */   }
/*     */   protected String invalidHexCharacter$str() {
/* 308 */     return "COM00506: Invalid hex character";
/*     */   }
/*     */   
/*     */   public final DecodeException invalidHexCharacter() {
/* 312 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), invalidHexCharacter$str(), new Object[0]));
/* 313 */     _copyStackTraceMinusOne((Throwable)result);
/* 314 */     return result;
/*     */   }
/*     */   protected String expectedTwoPaddingCharacters$str() {
/* 317 */     return "COM00507: Expected two padding characters";
/*     */   }
/*     */   
/*     */   public final DecodeException expectedTwoPaddingCharacters() {
/* 321 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), expectedTwoPaddingCharacters$str(), new Object[0]));
/* 322 */     _copyStackTraceMinusOne((Throwable)result);
/* 323 */     return result;
/*     */   }
/*     */   protected String invalidBase64Character$str() {
/* 326 */     return "COM00508: Invalid base 64 character";
/*     */   }
/*     */   
/*     */   public final DecodeException invalidBase64Character() {
/* 330 */     DecodeException result = new DecodeException(String.format(getLoggingLocale(), invalidBase64Character$str(), new Object[0]));
/* 331 */     _copyStackTraceMinusOne((Throwable)result);
/* 332 */     return result;
/*     */   }
/*     */   protected String tooLarge$str() {
/* 335 */     return "COM00509: Byte string builder is too large to grow";
/*     */   }
/*     */   
/*     */   public final IllegalStateException tooLarge() {
/* 339 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), tooLarge$str(), new Object[0]));
/* 340 */     _copyStackTraceMinusOne(result);
/* 341 */     return result;
/*     */   }
/*     */   protected String unexpectedNullValue$str() {
/* 344 */     return "COM01000: Internal error: Assertion failure: Unexpectedly null value";
/*     */   }
/*     */   
/*     */   public final String unexpectedNullValue() {
/* 348 */     return String.format(getLoggingLocale(), unexpectedNullValue$str(), new Object[0]);
/*     */   }
/*     */   protected String expectedLockHold$str() {
/* 351 */     return "COM01001: Internal error: Assertion failure: Current thread expected to hold lock for %s";
/*     */   }
/*     */   
/*     */   public final String expectedLockHold(Object monitor) {
/* 355 */     return String.format(getLoggingLocale(), expectedLockHold$str(), new Object[] { monitor });
/*     */   }
/*     */   protected String expectedLockNotHold$str() {
/* 358 */     return "COM01002: Internal error: Assertion failure: Current thread expected to not hold lock for %s";
/*     */   }
/*     */   
/*     */   public final String expectedLockNotHold(Object monitor) {
/* 362 */     return String.format(getLoggingLocale(), expectedLockNotHold$str(), new Object[] { monitor });
/*     */   }
/*     */   protected String expectedBoolean$str() {
/* 365 */     return "COM01003: Internal error: Assertion failure: Expected boolean value to be %s";
/*     */   }
/*     */   
/*     */   public final String expectedBoolean(boolean expr) {
/* 369 */     return String.format(getLoggingLocale(), expectedBoolean$str(), new Object[] { Boolean.valueOf(expr) });
/*     */   }
/*     */   protected String unreachableCode$str() {
/* 372 */     return "COM02000: Internal error: Unreachable code has been reached";
/*     */   }
/*     */   
/*     */   public final IllegalStateException unreachableCode() {
/* 376 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), unreachableCode$str(), new Object[0]));
/* 377 */     _copyStackTraceMinusOne(result);
/* 378 */     return result;
/*     */   }
/*     */   protected String impossibleSwitchCase$str() {
/* 381 */     return "COM02001: Internal error: Impossible switch condition encountered: %s";
/*     */   }
/*     */   
/*     */   public final IllegalStateException impossibleSwitchCase(Object cond) {
/* 385 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), impossibleSwitchCase$str(), new Object[] { cond }));
/* 386 */     _copyStackTraceMinusOne(result);
/* 387 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\_private\CommonMessages_$bundle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */