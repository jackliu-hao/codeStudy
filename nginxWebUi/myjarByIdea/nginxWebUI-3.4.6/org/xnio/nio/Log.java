package org.xnio.nio;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.nio.channels.spi.SelectorProvider;
import java.util.concurrent.RejectedExecutionException;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Field;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.Transform;
import org.jboss.logging.annotations.Transform.TransformType;
import org.xnio.ClosedWorkerException;
import org.xnio.channels.ReadTimeoutException;
import org.xnio.channels.WriteTimeoutException;

@MessageLogger(
   projectCode = "XNIO"
)
interface Log extends BasicLogger {
   Log log = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio");
   Log socketLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.socket");
   Log selectorLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.selector");
   Log tcpServerLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.tcp.server");
   Log tcpServerConnectionLimitLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.tcp.server.connection-limit");
   Log udpServerChannelLog = (Log)Logger.getMessageLogger(Log.class, "org.xnio.nio.udp.server.channel");

   @LogMessage(
      level = Logger.Level.INFO
   )
   @Message("XNIO NIO Implementation Version %s")
   void greeting(String var1);

   @LogMessage(
      level = Logger.Level.ERROR
   )
   @Message(
      id = 11,
      value = "Task %s failed with an exception"
   )
   void taskFailed(Runnable var1, @Cause Throwable var2);

   @Message(
      id = 15,
      value = "Parameter '%s' is out of range"
   )
   IllegalArgumentException parameterOutOfRange(String var1);

   @Message(
      id = 39,
      value = "Value for option '%s' is out of range"
   )
   IllegalArgumentException optionOutOfRange(String var1);

   @Message(
      id = 800,
      value = "Read timed out"
   )
   ReadTimeoutException readTimeout();

   @Message(
      id = 801,
      value = "Write timed out"
   )
   WriteTimeoutException writeTimeout();

   @Message(
      id = 808,
      value = "I/O operation was interrupted"
   )
   InterruptedIOException interruptedIO();

   InterruptedIOException interruptedIO(@Field int var1);

   @Message(
      id = 815,
      value = "Worker is shut down: %s"
   )
   ClosedWorkerException workerShutDown(NioXnioWorker var1);

   @Message(
      id = 900,
      value = "Method '%s' is not supported on this implementation"
   )
   UnsupportedOperationException unsupported(String var1);

   @Message(
      id = 1006,
      value = "Failed to invoke file watch callback"
   )
   @LogMessage(
      level = Logger.Level.ERROR
   )
   void failedToInvokeFileWatchCallback(@Cause Throwable var1);

   @Message(
      id = 7000,
      value = "No threads configured"
   )
   IllegalArgumentException noThreads();

   @Message(
      id = 7001,
      value = "Balancing token count must be greater than zero and less than thread count"
   )
   IllegalArgumentException balancingTokens();

   @Message(
      id = 7002,
      value = "Balancing connection count must be greater than zero when tokens are used"
   )
   IllegalArgumentException balancingConnectionCount();

   @Message(
      id = 7003,
      value = "Buffer is too large"
   )
   IllegalArgumentException bufferTooLarge();

   @Message(
      id = 7004,
      value = "No functional selector provider is available"
   )
   IllegalStateException noSelectorProvider();

   @Message(
      id = 7005,
      value = "Unexpected exception opening a selector"
   )
   IllegalStateException unexpectedSelectorOpenProblem(@Cause Throwable var1);

   @Message(
      id = 7006,
      value = "XNIO IO factory is from the wrong provider"
   )
   IllegalArgumentException notNioProvider();

   @Message(
      id = 7007,
      value = "Thread is terminating"
   )
   RejectedExecutionException threadExiting();

   @LogMessage(
      level = Logger.Level.WARN
   )
   @Message(
      id = 8000,
      value = "Received an I/O error on selection: %s"
   )
   void selectionError(IOException var1);

   @LogMessage(
      level = Logger.Level.WARN
   )
   @Message(
      id = 8001,
      value = "Socket accept failed, backing off for %2$d milliseconds: %1$s"
   )
   void acceptFailed(IOException var1, int var2);

   @LogMessage(
      level = Logger.Level.TRACE
   )
   @Message("Starting up with selector provider %s")
   void selectorProvider(@Transform({TransformType.GET_CLASS}) SelectorProvider var1);

   @LogMessage(
      level = Logger.Level.TRACE
   )
   @Message("Using %s for main selectors and %s for temp selectors")
   void selectors(Object var1, Object var2);
}
