/*     */ package io.undertow.servlet;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.util.Arrays;
/*     */ import java.util.Date;
/*     */ import java.util.Locale;
/*     */ import java.util.Set;
/*     */ import javax.servlet.UnavailableException;
/*     */ import org.jboss.logging.BasicLogger;
/*     */ import org.jboss.logging.DelegatingBasicLogger;
/*     */ import org.jboss.logging.Logger;
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
/*     */ public class UndertowServletLogger_$logger
/*     */   extends DelegatingBasicLogger
/*     */   implements UndertowServletLogger, BasicLogger, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  32 */   private static final String FQCN = UndertowServletLogger_$logger.class.getName();
/*     */   public UndertowServletLogger_$logger(Logger log) {
/*  34 */     super(log);
/*     */   }
/*  36 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  38 */     return LOCALE;
/*     */   }
/*     */   
/*     */   public final void stoppingServletDueToPermanentUnavailability(String servlet, UnavailableException e) {
/*  42 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, stoppingServletDueToPermanentUnavailability$str(), servlet);
/*     */   }
/*     */   protected String stoppingServletDueToPermanentUnavailability$str() {
/*  45 */     return "UT015002: Stopping servlet %s due to permanent unavailability";
/*     */   }
/*     */   
/*     */   public final void stoppingServletUntilDueToTemporaryUnavailability(String name, Date till, UnavailableException e) {
/*  49 */     this.log.logf(FQCN, Logger.Level.ERROR, (Throwable)e, stoppingServletUntilDueToTemporaryUnavailability$str(), name, till);
/*     */   }
/*     */   protected String stoppingServletUntilDueToTemporaryUnavailability$str() {
/*  52 */     return "UT015003: Stopping servlet %s till %s due to temporary unavailability";
/*     */   }
/*     */   
/*     */   public final void errorInvokingListener(String method, Class<?> listenerClass, Throwable t) {
/*  56 */     this.log.logf(FQCN, Logger.Level.ERROR, t, errorInvokingListener$str(), method, listenerClass);
/*     */   }
/*     */   protected String errorInvokingListener$str() {
/*  59 */     return "UT015005: Error invoking method %s on listener %s";
/*     */   }
/*     */   
/*     */   public final void ioExceptionDispatchingAsyncEvent(IOException e) {
/*  63 */     this.log.logf(FQCN, Logger.Level.ERROR, e, ioExceptionDispatchingAsyncEvent$str(), new Object[0]);
/*     */   }
/*     */   protected String ioExceptionDispatchingAsyncEvent$str() {
/*  66 */     return "UT015006: IOException dispatching async event";
/*     */   }
/*     */   
/*     */   public final void servletStackTracesAll(String deploymentName) {
/*  70 */     this.log.logf(FQCN, Logger.Level.WARN, null, servletStackTracesAll$str(), deploymentName);
/*     */   }
/*     */   protected String servletStackTracesAll$str() {
/*  73 */     return "UT015007: Stack trace on error enabled for deployment %s, please do not enable for production use";
/*     */   }
/*     */   
/*     */   public final void failedtoLoadPersistentSessions(Exception e) {
/*  77 */     this.log.logf(FQCN, Logger.Level.WARN, e, failedtoLoadPersistentSessions$str(), new Object[0]);
/*     */   }
/*     */   protected String failedtoLoadPersistentSessions$str() {
/*  80 */     return "UT015008: Failed to load development mode persistent sessions";
/*     */   }
/*     */   
/*     */   public final void failedToPersistSessionAttribute(String attributeName, Object value, String sessionID, Exception e) {
/*  84 */     this.log.logf(FQCN, Logger.Level.WARN, e, failedToPersistSessionAttribute$str(), attributeName, value, sessionID);
/*     */   }
/*     */   protected String failedToPersistSessionAttribute$str() {
/*  87 */     return "UT015009: Failed to persist session attribute %s with value %s for session %s";
/*     */   }
/*     */   
/*     */   public final void failedToPersistSessions(Exception e) {
/*  91 */     this.log.logf(FQCN, Logger.Level.WARN, e, failedToPersistSessions$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToPersistSessions$str() {
/*  94 */     return "UT015010: Failed to persist sessions";
/*     */   }
/*     */   
/*     */   public final void errorGeneratingErrorPage(String originalErrorPage, Object originalException, int code, Throwable cause) {
/*  98 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, errorGeneratingErrorPage$str(), originalErrorPage, originalException, Integer.valueOf(code));
/*     */   }
/*     */   protected String errorGeneratingErrorPage$str() {
/* 101 */     return "UT015012: Failed to generate error page %s for original exception: %s. Generating error page resulted in a %s.";
/*     */   }
/*     */   
/*     */   public final void errorReadingRewriteConfiguration(IOException e) {
/* 105 */     this.log.logf(FQCN, Logger.Level.ERROR, e, errorReadingRewriteConfiguration$str(), new Object[0]);
/*     */   }
/*     */   protected String errorReadingRewriteConfiguration$str() {
/* 108 */     return "UT015014: Error reading rewrite configuration";
/*     */   }
/*     */   protected String invalidRewriteConfiguration$str() {
/* 111 */     return "UT015015: Error reading rewrite configuration: %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidRewriteConfiguration(String line) {
/* 115 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidRewriteConfiguration$str(), new Object[] { line }));
/* 116 */     _copyStackTraceMinusOne(result);
/* 117 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/* 120 */     StackTraceElement[] st = e.getStackTrace();
/* 121 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String invalidRewriteMap$str() {
/* 124 */     return "UT015016: Invalid rewrite map class: %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidRewriteMap(String className) {
/* 128 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidRewriteMap$str(), new Object[] { className }));
/* 129 */     _copyStackTraceMinusOne(result);
/* 130 */     return result;
/*     */   }
/*     */   protected String invalidRewriteFlags2$str() {
/* 133 */     return "UT015017: Error reading rewrite flags in line %s as %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidRewriteFlags(String line, String flags) {
/* 137 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidRewriteFlags2$str(), new Object[] { line, flags }));
/* 138 */     _copyStackTraceMinusOne(result);
/* 139 */     return result;
/*     */   }
/*     */   protected String invalidRewriteFlags1$str() {
/* 142 */     return "UT015018: Error reading rewrite flags in line %s";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException invalidRewriteFlags(String line) {
/* 146 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), invalidRewriteFlags1$str(), new Object[] { line }));
/* 147 */     _copyStackTraceMinusOne(result);
/* 148 */     return result;
/*     */   }
/*     */   
/*     */   public final void failedToDestroy(Object object, Throwable t) {
/* 152 */     this.log.logf(FQCN, Logger.Level.ERROR, t, failedToDestroy$str(), object);
/*     */   }
/*     */   protected String failedToDestroy$str() {
/* 155 */     return "UT015019: Failed to destroy %s";
/*     */   }
/*     */   
/*     */   public final void unsecuredMethodsOnPath(String path, Set<String> missing) {
/* 159 */     this.log.logf(FQCN, Logger.Level.WARN, null, unsecuredMethodsOnPath$str(), path, missing);
/*     */   }
/*     */   protected String unsecuredMethodsOnPath$str() {
/* 162 */     return "UT015020: Path %s is secured for some HTTP methods, however it is not secured for %s";
/*     */   }
/*     */   
/*     */   public final void failureDispatchingAsyncEvent(Throwable t) {
/* 166 */     this.log.logf(FQCN, Logger.Level.ERROR, t, failureDispatchingAsyncEvent$str(), new Object[0]);
/*     */   }
/*     */   protected String failureDispatchingAsyncEvent$str() {
/* 169 */     return "UT015021: Failure dispatching async event";
/*     */   }
/*     */   
/*     */   public final void requestedResourceDoesNotExistForIncludeMethod(String path) {
/* 173 */     this.log.logf(FQCN, Logger.Level.WARN, null, requestedResourceDoesNotExistForIncludeMethod$str(), path);
/*     */   }
/*     */   protected String requestedResourceDoesNotExistForIncludeMethod$str() {
/* 176 */     return "UT015022: Requested resource at %s does not exist for include method";
/*     */   }
/*     */   protected String contextDestroyed$str() {
/* 179 */     return "UT015023: This Context has been already destroyed";
/*     */   }
/*     */   
/*     */   public final IllegalStateException contextDestroyed() {
/* 183 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), contextDestroyed$str(), new Object[0]));
/* 184 */     _copyStackTraceMinusOne(result);
/* 185 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\UndertowServletLogger_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */