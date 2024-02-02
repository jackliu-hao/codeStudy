package org.jboss.threads;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Locale;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.DelegatingBasicLogger;
import org.jboss.logging.Logger;

public class Messages_$logger extends DelegatingBasicLogger implements Messages, BasicLogger, Serializable {
   private static final long serialVersionUID = 1L;
   private static final String FQCN = Messages_$logger.class.getName();
   private static final Locale LOCALE;

   public Messages_$logger(Logger log) {
      super(log);
   }

   protected Locale getLoggingLocale() {
      return LOCALE;
   }

   public final void version(String version) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.INFO, (Throwable)null, (String)this.version$str(), (Object)version);
   }

   protected String version$str() {
      return "JBoss Threads version %s";
   }

   protected String shutDownInitiated$str() {
      return "JBTHR00009: Executor has been shut down";
   }

   public final StoppedExecutorException shutDownInitiated() {
      StoppedExecutorException result = new StoppedExecutorException(String.format(this.getLoggingLocale(), this.shutDownInitiated$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   private static void _copyStackTraceMinusOne(Throwable e) {
      StackTraceElement[] st = e.getStackTrace();
      e.setStackTrace((StackTraceElement[])Arrays.copyOfRange(st, 1, st.length));
   }

   protected String cannotAwaitWithin$str() {
      return "JBTHR00012: Cannot await termination of a thread pool from one of its own threads";
   }

   public final IllegalStateException cannotAwaitWithin() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.cannotAwaitWithin$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String noInterruptHandlers$str() {
      return "JBTHR00103: The current thread does not support interrupt handlers";
   }

   public final IllegalStateException noInterruptHandlers() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.noInterruptHandlers$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   protected String notShutDown$str() {
      return "JBTHR00104: Executor is not shut down";
   }

   public final IllegalStateException notShutDown() {
      IllegalStateException result = new IllegalStateException(String.format(this.getLoggingLocale(), this.notShutDown$str()));
      _copyStackTraceMinusOne(result);
      return result;
   }

   public final void interruptHandlerThrew(Throwable cause, InterruptHandler interruptHandler) {
      super.log.logf((String)FQCN, (Logger.Level)Logger.Level.ERROR, (Throwable)cause, (String)this.interruptHandlerThrew$str(), (Object)interruptHandler);
   }

   protected String interruptHandlerThrew$str() {
      return "JBTHR00108: Interrupt handler %s threw an exception";
   }

   protected String notAllowedContainerManaged$str() {
      return "JBTHR00200: %s() not allowed on container-managed executor";
   }

   public final SecurityException notAllowedContainerManaged(String methodName) {
      SecurityException result = new SecurityException(String.format(this.getLoggingLocale(), this.notAllowedContainerManaged$str(), methodName));
      _copyStackTraceMinusOne(result);
      return result;
   }

   static {
      LOCALE = Locale.ROOT;
   }
}
