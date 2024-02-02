package io.undertow.servlet;

import java.io.IOException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import javax.servlet.UnavailableException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class UndertowServletLogger_$logger extends DelegatingBasicLogger implements UndertowServletLogger, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = UndertowServletLogger_$logger.class.getName();
   private static final Locale LOCALE;

   public UndertowServletLogger_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void stoppingServletDueToPermanentUnavailability(String servlet, UnavailableException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.stoppingServletDueToPermanentUnavailability$str(), (Object)servlet);
   }

   protected String stoppingServletDueToPermanentUnavailability$str() {
      return "UT015002: Stopping servlet %s due to permanent unavailability";
   }

   public final void stoppingServletUntilDueToTemporaryUnavailability(String name, Date till, UnavailableException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.stoppingServletUntilDueToTemporaryUnavailability$str(), name, till);
   }

   protected String stoppingServletUntilDueToTemporaryUnavailability$str() {
      return "UT015003: Stopping servlet %s till %s due to temporary unavailability";
   }

   public final void errorInvokingListener(String method, Class<?> listenerClass, Throwable t) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)t, (String)this.errorInvokingListener$str(), method, listenerClass);
   }

   protected String errorInvokingListener$str() {
      return "UT015005: Error invoking method %s on listener %s";
   }

   public final void ioExceptionDispatchingAsyncEvent(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.ioExceptionDispatchingAsyncEvent$str(), (Object[])());
   }

   protected String ioExceptionDispatchingAsyncEvent$str() {
      return "UT015006: IOException dispatching async event";
   }

   public final void servletStackTracesAll(String deploymentName) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.servletStackTracesAll$str(), (Object)deploymentName);
   }

   protected String servletStackTracesAll$str() {
      return "UT015007: Stack trace on error enabled for deployment %s, please do not enable for production use";
   }

   public final void failedtoLoadPersistentSessions(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)e, (String)this.failedtoLoadPersistentSessions$str(), (Object[])());
   }

   protected String failedtoLoadPersistentSessions$str() {
      return "UT015008: Failed to load development mode persistent sessions";
   }

   public final void failedToPersistSessionAttribute(String attributeName, Object value, String sessionID, Exception e) {
      super.log.logf(FQCN, Logger.Level.WARN, e, this.failedToPersistSessionAttribute$str(), attributeName, value, sessionID);
   }

   protected String failedToPersistSessionAttribute$str() {
      return "UT015009: Failed to persist session attribute %s with value %s for session %s";
   }

   public final void failedToPersistSessions(Exception e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)e, (String)this.failedToPersistSessions$str(), (Object[])());
   }

   protected String failedToPersistSessions$str() {
      return "UT015010: Failed to persist sessions";
   }

   public final void errorGeneratingErrorPage(String originalErrorPage, Object originalException, int code, Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.errorGeneratingErrorPage$str(), originalErrorPage, originalException, code);
   }

   protected String errorGeneratingErrorPage$str() {
      return "UT015012: Failed to generate error page %s for original exception: %s. Generating error page resulted in a %s.";
   }

   public final void errorReadingRewriteConfiguration(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)e, (String)this.errorReadingRewriteConfiguration$str(), (Object[])());
   }

   protected String errorReadingRewriteConfiguration$str() {
      return "UT015014: Error reading rewrite configuration";
   }

   protected String invalidRewriteConfiguration$str() {
      return "UT015015: Error reading rewrite configuration: %s";
   }

   public final IllegalArgumentException invalidRewriteConfiguration(String line) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidRewriteConfiguration$str(), line));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String invalidRewriteMap$str() {
      return "UT015016: Invalid rewrite map class: %s";
   }

   public final IllegalArgumentException invalidRewriteMap(String className) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidRewriteMap$str(), className));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidRewriteFlags2$str() {
      return "UT015017: Error reading rewrite flags in line %s as %s";
   }

   public final IllegalArgumentException invalidRewriteFlags(String line, String flags) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidRewriteFlags2$str(), line, flags));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String invalidRewriteFlags1$str() {
      return "UT015018: Error reading rewrite flags in line %s";
   }

   public final IllegalArgumentException invalidRewriteFlags(String line) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.invalidRewriteFlags1$str(), line));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void failedToDestroy(Object object, Throwable t) {
      super.log.logf(FQCN, Logger.Level.ERROR, t, this.failedToDestroy$str(), object);
   }

   protected String failedToDestroy$str() {
      return "UT015019: Failed to destroy %s";
   }

   public final void unsecuredMethodsOnPath(String path, Set<String> missing) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.unsecuredMethodsOnPath$str(), path, missing);
   }

   protected String unsecuredMethodsOnPath$str() {
      return "UT015020: Path %s is secured for some HTTP methods, however it is not secured for %s";
   }

   public final void failureDispatchingAsyncEvent(Throwable t) {
      super.log.logf(FQCN, Logger.Level.ERROR, t, this.failureDispatchingAsyncEvent$str());
   }

   protected String failureDispatchingAsyncEvent$str() {
      return "UT015021: Failure dispatching async event";
   }

   public final void requestedResourceDoesNotExistForIncludeMethod(String path) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.requestedResourceDoesNotExistForIncludeMethod$str(), (Object)path);
   }

   protected String requestedResourceDoesNotExistForIncludeMethod$str() {
      return "UT015022: Requested resource at %s does not exist for include method";
   }

   protected String contextDestroyed$str() {
      return "UT015023: This Context has been already destroyed";
   }

   public final IllegalStateException contextDestroyed() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.contextDestroyed$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
