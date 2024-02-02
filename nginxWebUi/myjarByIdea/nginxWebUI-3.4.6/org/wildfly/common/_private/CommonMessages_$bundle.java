package org.wildfly.common._private;

import java.io.IOException;
import java.io.Serializable;
import java.security.Permission;
import java.security.PrivilegedActionException;
import java.util.Arrays;
import java.util.Locale;
import org.wildfly.common.codec.DecodeException;

public class CommonMessages_$bundle implements CommonMessages, Serializable {
   private static final long serialVersionUID = 1L;
   public static final CommonMessages_$bundle INSTANCE = new CommonMessages_$bundle();
   private static final Locale LOCALE;

   protected CommonMessages_$bundle() {
   }

   protected Object readResolve() {
      return INSTANCE;
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   protected String nullParam$str() {
      return "Parameter '%s' may not be null";
   }

   public final IllegalArgumentException nullParam(String paramName) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.nullParam$str(), paramName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String paramLessThan$str() {
      return "COM00001: Parameter '%s' must not be less than %d";
   }

   public final IllegalArgumentException paramLessThan(String name, long min) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramLessThan$str(), name, min));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final IllegalArgumentException paramLessThan(String name, double min) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramLessThan$str(), name, min));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String paramGreaterThan$str() {
      return "COM00002: Parameter '%s' must not be greater than than %d";
   }

   public final IllegalArgumentException paramGreaterThan(String name, long max) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramGreaterThan$str(), name, max));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final IllegalArgumentException paramGreaterThan(String name, double max) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.paramGreaterThan$str(), name, max));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String arrayOffsetGreaterThanLength$str() {
      return "COM00003: Given offset of %d is greater than array length of %d";
   }

   public final ArrayIndexOutOfBoundsException arrayOffsetGreaterThanLength(int offs, int arrayLength) {
      ArrayIndexOutOfBoundsException result = new ArrayIndexOutOfBoundsException(String.format(this.getLoggingLocale(), this.arrayOffsetGreaterThanLength$str(), offs, arrayLength));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String arrayOffsetLengthGreaterThanLength$str() {
      return "COM00004: Given offset of %d plus length of %d is greater than array length of %d";
   }

   public final ArrayIndexOutOfBoundsException arrayOffsetLengthGreaterThanLength(int offs, int len, int arrayLength) {
      ArrayIndexOutOfBoundsException result = new ArrayIndexOutOfBoundsException(String.format(this.getLoggingLocale(), this.arrayOffsetLengthGreaterThanLength$str(), offs, len, arrayLength));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String nullArrayParam$str() {
      return "COM00005: Array index %d of parameter '%s' may not be null";
   }

   public final IllegalArgumentException nullArrayParam(int index, String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.nullArrayParam$str(), index, name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String nullParamNPE$str() {
      return "COM00006: Parameter '%s' may not be null";
   }

   public final NullPointerException nullParamNPE(String name) {
      NullPointerException result = new NullPointerException(String.format(this.getLoggingLocale(), this.nullParamNPE$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidPermissionAction$str() {
      return "COM00007: Invalid permission action '%s'";
   }

   public final IllegalArgumentException invalidPermissionAction(String action) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidPermissionAction$str(), action));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String emptyParam$str() {
      return "COM00008: Parameter '%s' must not be empty";
   }

   public final IllegalArgumentException emptyParam(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.emptyParam$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidExpressionSyntax$str() {
      return "COM00009: Invalid expression syntax at position %d";
   }

   public final String invalidExpressionSyntax(int index) {
      return String.format(this.getLoggingLocale(), this.invalidExpressionSyntax$str(), index);
   }

   protected String unresolvedEnvironmentProperty$str() {
      return "COM00010: No environment property found named \"%s\"";
   }

   public final IllegalArgumentException unresolvedEnvironmentProperty(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.unresolvedEnvironmentProperty$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unresolvedSystemProperty$str() {
      return "COM00011: No system property found named \"%s\"";
   }

   public final IllegalArgumentException unresolvedSystemProperty(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.unresolvedSystemProperty$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidAddressBytes$str() {
      return "COM00012: Invalid address length of %d; must be 4 or 16";
   }

   public final IllegalArgumentException invalidAddressBytes(int length) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidAddressBytes$str(), length));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidAddress$str() {
      return "COM00013: Invalid address string \"%s\"";
   }

   public final IllegalArgumentException invalidAddress(String address) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidAddress$str(), address));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unsupported$str() {
      return "COM00100: Method \"%s\" of class \"%s\" is not implemented";
   }

   public final UnsupportedOperationException unsupported(String methodName, String className) {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.unsupported$str(), methodName, className));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String privilegedActionFailed$str() {
      return "COM00200: Privileged action failed";
   }

   public final PrivilegedActionException privilegedActionFailed(Exception e) {
      PrivilegedActionException result = new PrivilegedActionException(e);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String readOnlyPermissionCollection$str() {
      return "COM00300: Permission collection is read-only";
   }

   public final SecurityException readOnlyPermissionCollection() {
      SecurityException result = new SecurityException(String.format(this.getLoggingLocale(), this.readOnlyPermissionCollection$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidPermissionType$str() {
      return "COM00301: Invalid permission type (expected %s, actual value was %s)";
   }

   public final IllegalArgumentException invalidPermissionType(Class<? extends Permission> expectedType, Class<? extends Permission> actualType) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidPermissionType$str(), expectedType, actualType));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidOddFields$str() {
      return "COM00400: Invalid serialized remote exception cause object with odd number of strings in fields key/value list";
   }

   public final IllegalStateException invalidOddFields() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.invalidOddFields$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String cannotContainNullFieldNameOrValue$str() {
      return "COM00401: Field name or field value cannot be null";
   }

   public final IllegalArgumentException cannotContainNullFieldNameOrValue() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.cannotContainNullFieldNameOrValue$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String corruptedStream$str() {
      return "COM00402: Remote exception stream is corrupted and cannot be read";
   }

   public final IOException corruptedStream() {
      IOException result = new IOException(String.format(this.getLoggingLocale(), this.corruptedStream$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String remoteException2$str() {
      return "Remote exception %s: %s";
   }

   public final String remoteException(String exceptionClassName, String message) {
      return String.format(this.getLoggingLocale(), this.remoteException2$str(), exceptionClassName, message);
   }

   protected String remoteException1$str() {
      return "Remote exception %s";
   }

   public final String remoteException(String exceptionClassName) {
      return String.format(this.getLoggingLocale(), this.remoteException1$str(), exceptionClassName);
   }

   protected String unexpectedPadding$str() {
      return "COM00500: Unexpected padding";
   }

   public final DecodeException unexpectedPadding() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.unexpectedPadding$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedPadding$str() {
      return "COM00501: Expected padding";
   }

   public final DecodeException expectedPadding() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.expectedPadding$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String incompleteDecode$str() {
      return "COM00502: Incomplete decode";
   }

   public final DecodeException incompleteDecode() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.incompleteDecode$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedPaddingCharacters$str() {
      return "COM00503: Expected %d padding characters";
   }

   public final DecodeException expectedPaddingCharacters(int numExpected) {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.expectedPaddingCharacters$str(), numExpected));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidBase32Character$str() {
      return "COM00504: Invalid base 32 character";
   }

   public final DecodeException invalidBase32Character() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.invalidBase32Character$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedEvenNumberOfHexCharacters$str() {
      return "COM00505: Expected an even number of hex characters";
   }

   public final DecodeException expectedEvenNumberOfHexCharacters() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.expectedEvenNumberOfHexCharacters$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidHexCharacter$str() {
      return "COM00506: Invalid hex character";
   }

   public final DecodeException invalidHexCharacter() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.invalidHexCharacter$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String expectedTwoPaddingCharacters$str() {
      return "COM00507: Expected two padding characters";
   }

   public final DecodeException expectedTwoPaddingCharacters() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.expectedTwoPaddingCharacters$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidBase64Character$str() {
      return "COM00508: Invalid base 64 character";
   }

   public final DecodeException invalidBase64Character() {
      DecodeException result = new DecodeException(String.format(this.getLoggingLocale(), this.invalidBase64Character$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String tooLarge$str() {
      return "COM00509: Byte string builder is too large to grow";
   }

   public final IllegalStateException tooLarge() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.tooLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedNullValue$str() {
      return "COM01000: Internal error: Assertion failure: Unexpectedly null value";
   }

   public final String unexpectedNullValue() {
      return String.format(this.getLoggingLocale(), this.unexpectedNullValue$str());
   }

   protected String expectedLockHold$str() {
      return "COM01001: Internal error: Assertion failure: Current thread expected to hold lock for %s";
   }

   public final String expectedLockHold(Object monitor) {
      return String.format(this.getLoggingLocale(), this.expectedLockHold$str(), monitor);
   }

   protected String expectedLockNotHold$str() {
      return "COM01002: Internal error: Assertion failure: Current thread expected to not hold lock for %s";
   }

   public final String expectedLockNotHold(Object monitor) {
      return String.format(this.getLoggingLocale(), this.expectedLockNotHold$str(), monitor);
   }

   protected String expectedBoolean$str() {
      return "COM01003: Internal error: Assertion failure: Expected boolean value to be %s";
   }

   public final String expectedBoolean(boolean expr) {
      return String.format(this.getLoggingLocale(), this.expectedBoolean$str(), expr);
   }

   protected String unreachableCode$str() {
      return "COM02000: Internal error: Unreachable code has been reached";
   }

   public final IllegalStateException unreachableCode() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.unreachableCode$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String impossibleSwitchCase$str() {
      return "COM02001: Internal error: Impossible switch condition encountered: %s";
   }

   public final IllegalStateException impossibleSwitchCase(Object cond) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.impossibleSwitchCase$str(), cond));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
