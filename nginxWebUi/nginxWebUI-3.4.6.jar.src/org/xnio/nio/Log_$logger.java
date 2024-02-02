/*     */ package org.xnio.nio;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InterruptedIOException;
/*     */ import java.io.Serializable;
/*     */ import java.nio.channels.spi.SelectorProvider;
/*     */ import java.util.Arrays;
/*     */ import java.util.Locale;
/*     */ import java.util.concurrent.RejectedExecutionException;
/*     */ import org.jboss.logging.BasicLogger;
/*     */ import org.jboss.logging.DelegatingBasicLogger;
/*     */ import org.jboss.logging.Logger;
/*     */ import org.xnio.ClosedWorkerException;
/*     */ import org.xnio.channels.ReadTimeoutException;
/*     */ import org.xnio.channels.WriteTimeoutException;
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
/*     */ public class Log_$logger
/*     */   extends DelegatingBasicLogger
/*     */   implements Log, BasicLogger, Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  38 */   private static final String FQCN = Log_$logger.class.getName();
/*     */   public Log_$logger(Logger log) {
/*  40 */     super(log);
/*     */   }
/*  42 */   private static final Locale LOCALE = Locale.ROOT;
/*     */   protected Locale getLoggingLocale() {
/*  44 */     return LOCALE;
/*     */   }
/*     */   
/*     */   public final void greeting(String version) {
/*  48 */     this.log.logf(FQCN, Logger.Level.INFO, null, greeting$str(), version);
/*     */   }
/*     */   protected String greeting$str() {
/*  51 */     return "XNIO NIO Implementation Version %s";
/*     */   }
/*     */   
/*     */   public final void taskFailed(Runnable command, Throwable cause) {
/*  55 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, taskFailed$str(), command);
/*     */   }
/*     */   protected String taskFailed$str() {
/*  58 */     return "XNIO000011: Task %s failed with an exception";
/*     */   }
/*     */   protected String parameterOutOfRange$str() {
/*  61 */     return "XNIO000015: Parameter '%s' is out of range";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException parameterOutOfRange(String name) {
/*  65 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), parameterOutOfRange$str(), new Object[] { name }));
/*  66 */     _copyStackTraceMinusOne(result);
/*  67 */     return result;
/*     */   }
/*     */   private static void _copyStackTraceMinusOne(Throwable e) {
/*  70 */     StackTraceElement[] st = e.getStackTrace();
/*  71 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*     */   }
/*     */   protected String optionOutOfRange$str() {
/*  74 */     return "XNIO000039: Value for option '%s' is out of range";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException optionOutOfRange(String name) {
/*  78 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), optionOutOfRange$str(), new Object[] { name }));
/*  79 */     _copyStackTraceMinusOne(result);
/*  80 */     return result;
/*     */   }
/*     */   protected String readTimeout$str() {
/*  83 */     return "XNIO000800: Read timed out";
/*     */   }
/*     */   
/*     */   public final ReadTimeoutException readTimeout() {
/*  87 */     ReadTimeoutException result = new ReadTimeoutException(String.format(getLoggingLocale(), readTimeout$str(), new Object[0]));
/*  88 */     _copyStackTraceMinusOne((Throwable)result);
/*  89 */     return result;
/*     */   }
/*     */   protected String writeTimeout$str() {
/*  92 */     return "XNIO000801: Write timed out";
/*     */   }
/*     */   
/*     */   public final WriteTimeoutException writeTimeout() {
/*  96 */     WriteTimeoutException result = new WriteTimeoutException(String.format(getLoggingLocale(), writeTimeout$str(), new Object[0]));
/*  97 */     _copyStackTraceMinusOne((Throwable)result);
/*  98 */     return result;
/*     */   }
/*     */   protected String interruptedIO$str() {
/* 101 */     return "XNIO000808: I/O operation was interrupted";
/*     */   }
/*     */   
/*     */   public final InterruptedIOException interruptedIO() {
/* 105 */     InterruptedIOException result = new InterruptedIOException(String.format(getLoggingLocale(), interruptedIO$str(), new Object[0]));
/* 106 */     _copyStackTraceMinusOne(result);
/* 107 */     return result;
/*     */   }
/*     */   
/*     */   public final InterruptedIOException interruptedIO(int bytesTransferred) {
/* 111 */     InterruptedIOException result = new InterruptedIOException(String.format(getLoggingLocale(), interruptedIO$str(), new Object[0]));
/* 112 */     _copyStackTraceMinusOne(result);
/* 113 */     result.bytesTransferred = bytesTransferred;
/* 114 */     return result;
/*     */   }
/*     */   protected String workerShutDown$str() {
/* 117 */     return "XNIO000815: Worker is shut down: %s";
/*     */   }
/*     */   
/*     */   public final ClosedWorkerException workerShutDown(NioXnioWorker worker) {
/* 121 */     ClosedWorkerException result = new ClosedWorkerException(String.format(getLoggingLocale(), workerShutDown$str(), new Object[] { worker }));
/* 122 */     _copyStackTraceMinusOne((Throwable)result);
/* 123 */     return result;
/*     */   }
/*     */   protected String unsupported$str() {
/* 126 */     return "XNIO000900: Method '%s' is not supported on this implementation";
/*     */   }
/*     */   
/*     */   public final UnsupportedOperationException unsupported(String methodName) {
/* 130 */     UnsupportedOperationException result = new UnsupportedOperationException(String.format(getLoggingLocale(), unsupported$str(), new Object[] { methodName }));
/* 131 */     _copyStackTraceMinusOne(result);
/* 132 */     return result;
/*     */   }
/*     */   
/*     */   public final void failedToInvokeFileWatchCallback(Throwable cause) {
/* 136 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, failedToInvokeFileWatchCallback$str(), new Object[0]);
/*     */   }
/*     */   protected String failedToInvokeFileWatchCallback$str() {
/* 139 */     return "XNIO001006: Failed to invoke file watch callback";
/*     */   }
/*     */   protected String noThreads$str() {
/* 142 */     return "XNIO007000: No threads configured";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException noThreads() {
/* 146 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), noThreads$str(), new Object[0]));
/* 147 */     _copyStackTraceMinusOne(result);
/* 148 */     return result;
/*     */   }
/*     */   protected String balancingTokens$str() {
/* 151 */     return "XNIO007001: Balancing token count must be greater than zero and less than thread count";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException balancingTokens() {
/* 155 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), balancingTokens$str(), new Object[0]));
/* 156 */     _copyStackTraceMinusOne(result);
/* 157 */     return result;
/*     */   }
/*     */   protected String balancingConnectionCount$str() {
/* 160 */     return "XNIO007002: Balancing connection count must be greater than zero when tokens are used";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException balancingConnectionCount() {
/* 164 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), balancingConnectionCount$str(), new Object[0]));
/* 165 */     _copyStackTraceMinusOne(result);
/* 166 */     return result;
/*     */   }
/*     */   protected String bufferTooLarge$str() {
/* 169 */     return "XNIO007003: Buffer is too large";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException bufferTooLarge() {
/* 173 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), bufferTooLarge$str(), new Object[0]));
/* 174 */     _copyStackTraceMinusOne(result);
/* 175 */     return result;
/*     */   }
/*     */   protected String noSelectorProvider$str() {
/* 178 */     return "XNIO007004: No functional selector provider is available";
/*     */   }
/*     */   
/*     */   public final IllegalStateException noSelectorProvider() {
/* 182 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noSelectorProvider$str(), new Object[0]));
/* 183 */     _copyStackTraceMinusOne(result);
/* 184 */     return result;
/*     */   }
/*     */   protected String unexpectedSelectorOpenProblem$str() {
/* 187 */     return "XNIO007005: Unexpected exception opening a selector";
/*     */   }
/*     */   
/*     */   public final IllegalStateException unexpectedSelectorOpenProblem(Throwable cause) {
/* 191 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), unexpectedSelectorOpenProblem$str(), new Object[0]), cause);
/* 192 */     _copyStackTraceMinusOne(result);
/* 193 */     return result;
/*     */   }
/*     */   protected String notNioProvider$str() {
/* 196 */     return "XNIO007006: XNIO IO factory is from the wrong provider";
/*     */   }
/*     */   
/*     */   public final IllegalArgumentException notNioProvider() {
/* 200 */     IllegalArgumentException result = new IllegalArgumentException(String.format(getLoggingLocale(), notNioProvider$str(), new Object[0]));
/* 201 */     _copyStackTraceMinusOne(result);
/* 202 */     return result;
/*     */   }
/*     */   protected String threadExiting$str() {
/* 205 */     return "XNIO007007: Thread is terminating";
/*     */   }
/*     */   
/*     */   public final RejectedExecutionException threadExiting() {
/* 209 */     RejectedExecutionException result = new RejectedExecutionException(String.format(getLoggingLocale(), threadExiting$str(), new Object[0]));
/* 210 */     _copyStackTraceMinusOne(result);
/* 211 */     return result;
/*     */   }
/*     */   
/*     */   public final void selectionError(IOException e) {
/* 215 */     this.log.logf(FQCN, Logger.Level.WARN, null, selectionError$str(), e);
/*     */   }
/*     */   protected String selectionError$str() {
/* 218 */     return "XNIO008000: Received an I/O error on selection: %s";
/*     */   }
/*     */   
/*     */   public final void acceptFailed(IOException problem, int backOffTime) {
/* 222 */     this.log.logf(FQCN, Logger.Level.WARN, null, acceptFailed$str(), problem, Integer.valueOf(backOffTime));
/*     */   }
/*     */   protected String acceptFailed$str() {
/* 225 */     return "XNIO008001: Socket accept failed, backing off for %2$d milliseconds: %1$s";
/*     */   }
/*     */   
/*     */   public final void selectorProvider(SelectorProvider provider) {
/* 229 */     if (this.log.isEnabled(Logger.Level.TRACE)) {
/*     */       Class<?> providerClass;
/* 231 */       if (provider == null)
/* 232 */       { providerClass = null; }
/* 233 */       else { providerClass = provider.getClass(); }
/* 234 */        this.log.logf(FQCN, Logger.Level.TRACE, null, selectorProvider$str(), providerClass);
/*     */     } 
/*     */   }
/*     */   protected String selectorProvider$str() {
/* 238 */     return "Starting up with selector provider %s";
/*     */   }
/*     */   
/*     */   public final void selectors(Object mainSelectorCreator, Object tempSelectorCreator) {
/* 242 */     this.log.logf(FQCN, Logger.Level.TRACE, null, selectors$str(), mainSelectorCreator, tempSelectorCreator);
/*     */   }
/*     */   protected String selectors$str() {
/* 245 */     return "Using %s for main selectors and %s for temp selectors";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\Log_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */