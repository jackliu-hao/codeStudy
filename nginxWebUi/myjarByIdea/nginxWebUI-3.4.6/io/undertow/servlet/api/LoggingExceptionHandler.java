package io.undertow.servlet.api;

import io.undertow.UndertowLogger;
import io.undertow.server.HttpServerExchange;
import io.undertow.servlet.ExceptionLog;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;

public class LoggingExceptionHandler implements ExceptionHandler {
   public static final LoggingExceptionHandler DEFAULT = new LoggingExceptionHandler(Collections.emptyMap());
   private final Map<Class<? extends Throwable>, ExceptionDetails> exceptionDetails;

   public LoggingExceptionHandler(Map<Class<? extends Throwable>, ExceptionDetails> exceptionDetails) {
      this.exceptionDetails = exceptionDetails;
   }

   public boolean handleThrowable(HttpServerExchange exchange, ServletRequest request, ServletResponse response, Throwable t) {
      ExceptionDetails details = null;
      if (!this.exceptionDetails.isEmpty()) {
         for(Class c = t.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            details = (ExceptionDetails)this.exceptionDetails.get(c);
            if (details != null) {
               break;
            }
         }
      }

      ExceptionLog log = (ExceptionLog)t.getClass().getAnnotation(ExceptionLog.class);
      Logger.Level level;
      Logger.Level stackTraceLevel;
      String category;
      if (details != null) {
         level = details.level;
         stackTraceLevel = details.stackTraceLevel;
         category = details.category;
         this.handleCustomLog(exchange, t, level, stackTraceLevel, category);
      } else if (log != null) {
         level = log.value();
         stackTraceLevel = log.stackTraceLevel();
         category = log.category();
         this.handleCustomLog(exchange, t, level, stackTraceLevel, category);
      } else if (t instanceof IOException) {
         UndertowLogger.REQUEST_IO_LOGGER.debugf(t, "Exception handling request to %s", exchange.getRequestURI());
      } else {
         UndertowLogger.REQUEST_LOGGER.exceptionHandlingRequest(t, exchange.getRequestURI());
      }

      return false;
   }

   private void handleCustomLog(HttpServerExchange exchange, Throwable t, Logger.Level level, Logger.Level stackTraceLevel, String category) {
      BasicLogger logger = UndertowLogger.REQUEST_LOGGER;
      if (!category.isEmpty()) {
         logger = Logger.getLogger(category);
      }

      boolean stackTrace = true;
      if (stackTraceLevel.ordinal() > level.ordinal() && !((BasicLogger)logger).isEnabled(stackTraceLevel)) {
         stackTrace = false;
      }

      if (stackTrace) {
         ((BasicLogger)logger).logf(level, (Throwable)t, (String)"Exception handling request to %s", (Object)exchange.getRequestURI());
      } else {
         ((BasicLogger)logger).logf(level, (String)"Exception handling request to %s: %s", (Object)exchange.getRequestURI(), (Object)t.getMessage());
      }

   }

   public static Builder builder() {
      return new Builder();
   }

   public static final class Builder {
      private final Map<Class<? extends Throwable>, ExceptionDetails> exceptionDetails = new HashMap();

      Builder() {
      }

      public Builder add(Class<? extends Throwable> exception, String category, Logger.Level level) {
         this.exceptionDetails.put(exception, new ExceptionDetails(level, Logger.Level.FATAL, category));
         return this;
      }

      public Builder add(Class<? extends Throwable> exception, String category) {
         this.exceptionDetails.put(exception, new ExceptionDetails(Logger.Level.ERROR, Logger.Level.FATAL, category));
         return this;
      }

      public Builder add(Class<? extends Throwable> exception, String category, Logger.Level level, Logger.Level stackTraceLevel) {
         this.exceptionDetails.put(exception, new ExceptionDetails(level, stackTraceLevel, category));
         return this;
      }

      public LoggingExceptionHandler build() {
         return new LoggingExceptionHandler(this.exceptionDetails);
      }
   }

   private static class ExceptionDetails {
      final Logger.Level level;
      final Logger.Level stackTraceLevel;
      final String category;

      private ExceptionDetails(Logger.Level level, Logger.Level stackTraceLevel, String category) {
         this.level = level;
         this.stackTraceLevel = stackTraceLevel;
         this.category = category;
      }

      // $FF: synthetic method
      ExceptionDetails(Logger.Level x0, Logger.Level x1, String x2, Object x3) {
         this(x0, x1, x2);
      }
   }
}
