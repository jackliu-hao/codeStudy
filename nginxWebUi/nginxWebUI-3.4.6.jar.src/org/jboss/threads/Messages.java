/*    */ package org.jboss.threads;
/*    */ 
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.Logger;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.LogMessage;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageLogger;
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
/*    */ @MessageLogger(projectCode = "JBTHR", length = 5)
/*    */ interface Messages
/*    */   extends BasicLogger
/*    */ {
/* 37 */   public static final Messages msg = (Messages)Logger.getMessageLogger(Messages.class, "org.jboss.threads");
/* 38 */   public static final Messages intMsg = (Messages)Logger.getMessageLogger(Messages.class, "org.jboss.threads.interrupt-handler");
/*    */   
/*    */   @Message("JBoss Threads version %s")
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   void version(String paramString);
/*    */   
/*    */   @Message(id = 9, value = "Executor has been shut down")
/*    */   StoppedExecutorException shutDownInitiated();
/*    */   
/*    */   @Message(id = 12, value = "Cannot await termination of a thread pool from one of its own threads")
/*    */   IllegalStateException cannotAwaitWithin();
/*    */   
/*    */   @Message(id = 103, value = "The current thread does not support interrupt handlers")
/*    */   IllegalStateException noInterruptHandlers();
/*    */   
/*    */   @Deprecated
/*    */   @Message(id = 104, value = "Executor is not shut down")
/*    */   IllegalStateException notShutDown();
/*    */   
/*    */   @Message(id = 108, value = "Interrupt handler %s threw an exception")
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   void interruptHandlerThrew(@Cause Throwable paramThrowable, InterruptHandler paramInterruptHandler);
/*    */   
/*    */   @Message(id = 200, value = "%s() not allowed on container-managed executor")
/*    */   SecurityException notAllowedContainerManaged(String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\jboss\threads\Messages.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */