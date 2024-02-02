package org.xnio.nio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.io.Serializable;
import java.nio.channels.spi.SelectorProvider;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;
import org.xnio.ClosedWorkerException;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.WriteTimeoutException;

public class Log_$logger extends DelegatingBasicLogger implements Log, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = Log_$logger.class.getName();
   private static final Locale LOCALE;

   public Log_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void greeting(String version) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.greeting$str(), (Object)version);
   }

   protected String greeting$str() {
      return "XNIO NIO Implementation Version %s";
   }

   public final void taskFailed(Runnable command, Throwable cause) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)cause, (String)this.taskFailed$str(), (Object)command);
   }

   protected String taskFailed$str() {
      return "XNIO000011: Task %s failed with an exception";
   }

   protected String parameterOutOfRange$str() {
      return "XNIO000015: Parameter '%s' is out of range";
   }

   public final IllegalArgumentException parameterOutOfRange(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.parameterOutOfRange$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String optionOutOfRange$str() {
      return "XNIO000039: Value for option '%s' is out of range";
   }

   public final IllegalArgumentException optionOutOfRange(String name) {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.optionOutOfRange$str(), name));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String readTimeout$str() {
      return "XNIO000800: Read timed out";
   }

   public final ReadTimeoutException readTimeout() {
      ReadTimeoutException result = new ReadTimeoutException(String.format(this.getLoggingLocale(), this.readTimeout$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String writeTimeout$str() {
      return "XNIO000801: Write timed out";
   }

   public final WriteTimeoutException writeTimeout() {
      WriteTimeoutException result = new WriteTimeoutException(String.format(this.getLoggingLocale(), this.writeTimeout$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String interruptedIO$str() {
      return "XNIO000808: I/O operation was interrupted";
   }

   public final InterruptedIOException interruptedIO() {
      InterruptedIOException result = new InterruptedIOException(String.format(this.getLoggingLocale(), this.interruptedIO$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final InterruptedIOException interruptedIO(int bytesTransferred) {
      InterruptedIOException result = new InterruptedIOException(String.format(this.getLoggingLocale(), this.interruptedIO$str()));
      _copyStackTraceMinusOne(result);
      result.bytesTransferred = bytesTransferred;
      return result;
   }

   protected String workerShutDown$str() {
      return "XNIO000815: Worker is shut down: %s";
   }

   public final ClosedWorkerException workerShutDown(NioXnioWorker worker) {
      ClosedWorkerException result = new ClosedWorkerException(String.format(this.getLoggingLocale(), this.workerShutDown$str(), worker));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unsupported$str() {
      return "XNIO000900: Method '%s' is not supported on this implementation";
   }

   public final UnsupportedOperationException unsupported(String methodName) {
      UnsupportedOperationException result = new UnsupportedOperationException(String.format(this.getLoggingLocale(), this.unsupported$str(), methodName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void failedToInvokeFileWatchCallback(Throwable cause) {
      super.log.logf(FQCN, Logger.Level.ERROR, cause, this.failedToInvokeFileWatchCallback$str());
   }

   protected String failedToInvokeFileWatchCallback$str() {
      return "XNIO001006: Failed to invoke file watch callback";
   }

   protected String noThreads$str() {
      return "XNIO007000: No threads configured";
   }

   public final IllegalArgumentException noThreads() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.noThreads$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String balancingTokens$str() {
      return "XNIO007001: Balancing token count must be greater than zero and less than thread count";
   }

   public final IllegalArgumentException balancingTokens() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.balancingTokens$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String balancingConnectionCount$str() {
      return "XNIO007002: Balancing connection count must be greater than zero when tokens are used";
   }

   public final IllegalArgumentException balancingConnectionCount() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.balancingConnectionCount$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String bufferTooLarge$str() {
      return "XNIO007003: Buffer is too large";
   }

   public final IllegalArgumentException bufferTooLarge() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.bufferTooLarge$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noSelectorProvider$str() {
      return "XNIO007004: No functional selector provider is available";
   }

   public final IllegalStateException noSelectorProvider() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noSelectorProvider$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String unexpectedSelectorOpenProblem$str() {
      return "XNIO007005: Unexpected exception opening a selector";
   }

   public final IllegalStateException unexpectedSelectorOpenProblem(Throwable cause) {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.unexpectedSelectorOpenProblem$str()), cause);
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notNioProvider$str() {
      return "XNIO007006: XNIO IO factory is from the wrong provider";
   }

   public final IllegalArgumentException notNioProvider() {
      IllegalArgumentException result = new IllegalArgumentException(String.format(this.getLoggingLocale(), this.notNioProvider$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String threadExiting$str() {
      return "XNIO007007: Thread is terminating";
   }

   public final RejectedExecutionException threadExiting() {
      RejectedExecutionException result = new RejectedExecutionException(String.format(this.getLoggingLocale(), this.threadExiting$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void selectionError(IOException e) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.selectionError$str(), (Object)e);
   }

   protected String selectionError$str() {
      return "XNIO008000: Received an I/O error on selection: %s";
   }

   public final void acceptFailed(IOException problem, int backOffTime) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.WARN, (Throwable)null, (String)this.acceptFailed$str(), problem, backOffTime);
   }

   protected String acceptFailed$str() {
      return "XNIO008001: Socket accept failed, backing off for %2$d milliseconds: %1$s";
   }

   public final void selectorProvider(SelectorProvider provider) {
      if (super.log.isEnabled(Logger.Level.TRACE)) {
         Class providerClass;
         if (provider == null) {
            providerClass = null;
         } else {
            providerClass = provider.getClass();
         }

         super.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)this.selectorProvider$str(), (Object)providerClass);
      }

   }

   protected String selectorProvider$str() {
      return "Starting up with selector provider %s";
   }

   public final void selectors(Object mainSelectorCreator, Object tempSelectorCreator) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.TRACE, (Throwable)null, (String)this.selectors$str(), mainSelectorCreator, tempSelectorCreator);
   }

   protected String selectors$str() {
      return "Using %s for main selectors and %s for temp selectors";
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
