/*    */ package org.xnio.nio;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import java.io.InterruptedIOException;
/*    */ import java.nio.channels.spi.SelectorProvider;
/*    */ import java.util.concurrent.RejectedExecutionException;
/*    */ import org.jboss.logging.BasicLogger;
/*    */ import org.jboss.logging.Logger;
/*    */ import org.jboss.logging.annotations.Cause;
/*    */ import org.jboss.logging.annotations.Field;
/*    */ import org.jboss.logging.annotations.LogMessage;
/*    */ import org.jboss.logging.annotations.Message;
/*    */ import org.jboss.logging.annotations.MessageLogger;
/*    */ import org.jboss.logging.annotations.Transform;
/*    */ import org.xnio.ClosedWorkerException;
/*    */ import org.xnio.channels.ReadTimeoutException;
/*    */ import org.xnio.channels.WriteTimeoutException;
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
/*    */ @MessageLogger(projectCode = "XNIO")
/*    */ interface Log
/*    */   extends BasicLogger
/*    */ {
/* 46 */   public static final Log log = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio");
/* 47 */   public static final Log socketLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.socket");
/* 48 */   public static final Log selectorLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.selector");
/* 49 */   public static final Log tcpServerLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.tcp.server");
/* 50 */   public static final Log tcpServerConnectionLimitLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.tcp.server.connection-limit");
/* 51 */   public static final Log udpServerChannelLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.udp.server.channel");
/*    */   
/*    */   @LogMessage(level = Logger.Level.INFO)
/*    */   @Message("XNIO NIO Implementation Version %s")
/*    */   void greeting(String paramString);
/*    */   
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   @Message(id = 11, value = "Task %s failed with an exception")
/*    */   void taskFailed(Runnable paramRunnable, @Cause Throwable paramThrowable);
/*    */   
/*    */   @Message(id = 15, value = "Parameter '%s' is out of range")
/*    */   IllegalArgumentException parameterOutOfRange(String paramString);
/*    */   
/*    */   @Message(id = 39, value = "Value for option '%s' is out of range")
/*    */   IllegalArgumentException optionOutOfRange(String paramString);
/*    */   
/*    */   @Message(id = 800, value = "Read timed out")
/*    */   ReadTimeoutException readTimeout();
/*    */   
/*    */   @Message(id = 801, value = "Write timed out")
/*    */   WriteTimeoutException writeTimeout();
/*    */   
/*    */   @Message(id = 808, value = "I/O operation was interrupted")
/*    */   InterruptedIOException interruptedIO();
/*    */   
/*    */   InterruptedIOException interruptedIO(@Field int paramInt);
/*    */   
/*    */   @Message(id = 815, value = "Worker is shut down: %s")
/*    */   ClosedWorkerException workerShutDown(NioXnioWorker paramNioXnioWorker);
/*    */   
/*    */   @Message(id = 900, value = "Method '%s' is not supported on this implementation")
/*    */   UnsupportedOperationException unsupported(String paramString);
/*    */   
/*    */   @Message(id = 1006, value = "Failed to invoke file watch callback")
/*    */   @LogMessage(level = Logger.Level.ERROR)
/*    */   void failedToInvokeFileWatchCallback(@Cause Throwable paramThrowable);
/*    */   
/*    */   @Message(id = 7000, value = "No threads configured")
/*    */   IllegalArgumentException noThreads();
/*    */   
/*    */   @Message(id = 7001, value = "Balancing token count must be greater than zero and less than thread count")
/*    */   IllegalArgumentException balancingTokens();
/*    */   
/*    */   @Message(id = 7002, value = "Balancing connection count must be greater than zero when tokens are used")
/*    */   IllegalArgumentException balancingConnectionCount();
/*    */   
/*    */   @Message(id = 7003, value = "Buffer is too large")
/*    */   IllegalArgumentException bufferTooLarge();
/*    */   
/*    */   @Message(id = 7004, value = "No functional selector provider is available")
/*    */   IllegalStateException noSelectorProvider();
/*    */   
/*    */   @Message(id = 7005, value = "Unexpected exception opening a selector")
/*    */   IllegalStateException unexpectedSelectorOpenProblem(@Cause Throwable paramThrowable);
/*    */   
/*    */   @Message(id = 7006, value = "XNIO IO factory is from the wrong provider")
/*    */   IllegalArgumentException notNioProvider();
/*    */   
/*    */   @Message(id = 7007, value = "Thread is terminating")
/*    */   RejectedExecutionException threadExiting();
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 8000, value = "Received an I/O error on selection: %s")
/*    */   void selectionError(IOException paramIOException);
/*    */   
/*    */   @LogMessage(level = Logger.Level.WARN)
/*    */   @Message(id = 8001, value = "Socket accept failed, backing off for %2$d milliseconds: %1$s")
/*    */   void acceptFailed(IOException paramIOException, int paramInt);
/*    */   
/*    */   @LogMessage(level = Logger.Level.TRACE)
/*    */   @Message("Starting up with selector provider %s")
/*    */   void selectorProvider(@Transform({Transform.TransformType.GET_CLASS}) SelectorProvider paramSelectorProvider);
/*    */   
/*    */   @LogMessage(level = Logger.Level.TRACE)
/*    */   @Message("Using %s for main selectors and %s for temp selectors")
/*    */   void selectors(Object paramObject1, Object paramObject2);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\xnio\nio\Log.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */