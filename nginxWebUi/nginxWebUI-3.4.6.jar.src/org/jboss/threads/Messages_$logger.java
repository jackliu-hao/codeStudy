/*    */ package org.jboss.threads;
/*    */ 
/*    */ import java.io.Serializable;
/*    */ import java.util.Arrays;
/*    */ import java.util.Locale;
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.DelegatingBasicLogger;
/*    */ import org.jboss.logging.Logger;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Messages_$logger
/*    */   extends DelegatingBasicLogger
/*    */   implements Messages, BasicLogger, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 27 */   private static final String FQCN = Messages_$logger.class.getName();
/*    */   public Messages_$logger(Logger log) {
/* 29 */     super(log);
/*    */   }
/* 31 */   private static final Locale LOCALE = Locale.ROOT;
/*    */   protected Locale getLoggingLocale() {
/* 33 */     return LOCALE;
/*    */   }
/*    */   
/*    */   public final void version(String version) {
/* 37 */     this.log.logf(FQCN, Logger.Level.INFO, null, version$str(), version);
/*    */   }
/*    */   protected String version$str() {
/* 40 */     return "JBoss Threads version %s";
/*    */   }
/*    */   protected String shutDownInitiated$str() {
/* 43 */     return "JBTHR00009: Executor has been shut down";
/*    */   }
/*    */   
/*    */   public final StoppedExecutorException shutDownInitiated() {
/* 47 */     StoppedExecutorException result = new StoppedExecutorException(String.format(getLoggingLocale(), shutDownInitiated$str(), new Object[0]));
/* 48 */     _copyStackTraceMinusOne(result);
/* 49 */     return result;
/*    */   }
/*    */   private static void _copyStackTraceMinusOne(Throwable e) {
/* 52 */     StackTraceElement[] st = e.getStackTrace();
/* 53 */     e.setStackTrace(Arrays.<StackTraceElement>copyOfRange(st, 1, st.length));
/*    */   }
/*    */   protected String cannotAwaitWithin$str() {
/* 56 */     return "JBTHR00012: Cannot await termination of a thread pool from one of its own threads";
/*    */   }
/*    */   
/*    */   public final IllegalStateException cannotAwaitWithin() {
/* 60 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), cannotAwaitWithin$str(), new Object[0]));
/* 61 */     _copyStackTraceMinusOne(result);
/* 62 */     return result;
/*    */   }
/*    */   protected String noInterruptHandlers$str() {
/* 65 */     return "JBTHR00103: The current thread does not support interrupt handlers";
/*    */   }
/*    */   
/*    */   public final IllegalStateException noInterruptHandlers() {
/* 69 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), noInterruptHandlers$str(), new Object[0]));
/* 70 */     _copyStackTraceMinusOne(result);
/* 71 */     return result;
/*    */   }
/*    */   protected String notShutDown$str() {
/* 74 */     return "JBTHR00104: Executor is not shut down";
/*    */   }
/*    */   
/*    */   public final IllegalStateException notShutDown() {
/* 78 */     IllegalStateException result = new IllegalStateException(String.format(getLoggingLocale(), notShutDown$str(), new Object[0]));
/* 79 */     _copyStackTraceMinusOne(result);
/* 80 */     return result;
/*    */   }
/*    */   
/*    */   public final void interruptHandlerThrew(Throwable cause, InterruptHandler interruptHandler) {
/* 84 */     this.log.logf(FQCN, Logger.Level.ERROR, cause, interruptHandlerThrew$str(), interruptHandler);
/*    */   }
/*    */   protected String interruptHandlerThrew$str() {
/* 87 */     return "JBTHR00108: Interrupt handler %s threw an exception";
/*    */   }
/*    */   protected String notAllowedContainerManaged$str() {
/* 90 */     return "JBTHR00200: %s() not allowed on container-managed executor";
/*    */   }
/*    */   
/*    */   public final SecurityException notAllowedContainerManaged(String methodName) {
/* 94 */     SecurityException result = new SecurityException(String.format(getLoggingLocale(), notAllowedContainerManaged$str(), new Object[] { methodName }));
/* 95 */     _copyStackTraceMinusOne(result);
/* 96 */     return result;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\Messages_$logger.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */